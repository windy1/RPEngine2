package tv.twitch.moonmoon.rpengine2.data.attribute;

import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.di.PluginLogger;
import tv.twitch.moonmoon.rpengine2.model.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeArgs;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeType;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
public class AttributeRepoImpl implements AttributeRepo {

    private final AttributeDbo attributeDbo;
    private final RpPlayerRepo playerRepo;
    private final SelectRepo selectRepo;
    private final Logger log;
    private Map<String, Attribute> attributes;

    @Inject
    public AttributeRepoImpl(
        AttributeDbo attributeDbo,
        RpPlayerRepo playerRepo,
        SelectRepo selectRepo,
        @PluginLogger Logger log
    ) {
        this.attributeDbo = Objects.requireNonNull(attributeDbo);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.selectRepo = Objects.requireNonNull(selectRepo);
        this.log = Objects.requireNonNull(log);
    }

    @Override
    public Set<Attribute> getAttributes() {
        return Collections.unmodifiableSet(new HashSet<>(attributes.values()));
    }

    @Override
    public Optional<Attribute> getAttribute(String name) {
        return Optional.ofNullable(attributes.get(name));
    }

    @Override
    public void createAttributeAsync(
        String name,
        AttributeType type,
        String display,
        String defaultValue,
        Consumer<Result<Void>> callback
    ) {
        Result<AttributeArgs> a = new AttributeArgs(
            name, type, display, defaultValue, selectRepo
        ).clean();

        Optional<String> err = a.getError();
        if (err.isPresent()) {
            callback.accept(Result.error(err.get()));
            return;
        }

        AttributeArgs args = a.get();
        String def = args.getDefaultValue().orElse(null);
        String typeId = args.getType().getId();

        attributeDbo.insertAttributeAsync(args.getName(), args.getDisplay(), typeId, def, r ->
            callback.accept(handleCreateAttribute(name, def, r))
        );
    }

    @Override
    public void createAttribute(
        String name,
        AttributeType type,
        String display,
        String defaultValue
    ) {
        Result<AttributeArgs> a = new AttributeArgs(
            name, type, display, defaultValue, selectRepo
        ).clean();

        Optional<String> err = a.getError();
        if (err.isPresent()) {
            log.warning(err.get());
            return;
        }

        AttributeArgs args = a.get();
        String def = args.getDefaultValue().orElse(null);
        String typeId = args.getType().getId();
        String newDisplay = args.getDisplay();

        Result<Long> r = attributeDbo.insertAttribute(args.getName(), newDisplay, typeId, def);
        handleCreateAttribute(name, def, r).getError().ifPresent(log::warning);
    }

    @Override
    public void removeAttributeAsync(String name, Consumer<Result<Void>> callback) {
        Attribute attribute = attributes.get(name);
        if (attribute == null) {
            callback.accept(Result.error("Attribute not found"));
            return;
        }

        int attributeId = attribute.getId();

        playerRepo.removeAttributesAsync(attributeId, r ->
            callback.accept(handleRemoveAttribute(attributeId, name, r))
        );
    }

    @Override
    public void load() {
        Result<Set<Attribute>> r = attributeDbo.selectAttributes();

        Optional<String> err = r.getError();
        if (err.isPresent()) {
            log.warning(err.get());
        } else {
            attributes = Collections.synchronizedMap(r.get().stream()
                .collect(Collectors.toMap(Attribute::getName, Function.identity())));
        }
    }

    private Result<Attribute> reloadAttribute(String name) {
        Result<Attribute> updatedAttribute = attributeDbo.selectAttribute(name);

        Optional<String> err = updatedAttribute.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        Attribute a = updatedAttribute.get();
        attributes.put(a.getName(), a);

        return Result.ok(a);
    }

    private Result<Void> handleRemoveAttribute(
        int attributeId,
        String attributeName,
        Result<Void> r
    ) {
        Result<Void> deleteResult = handleResult(() -> r).getError()
            .<Result<Void>>map(Result::error)
            .orElseGet(() -> handleResult(playerRepo::reloadPlayers).getError()
                .<Result<Void>>map(Result::error)
                .orElseGet(() ->
                    handleResult(() -> attributeDbo.deleteAttribute(attributeId)).getError()
                        .<Result<Void>>map(Result::error)
                        .orElseGet(() -> Result.ok(null))
                )
            );

        Optional<String> err = deleteResult.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        } else {
            attributes.remove(attributeName);
            return Result.ok(null);
        }
    }

    private Result<Void> handleCreateAttribute(
        String name,
        String def,
        Result<Long> r
    ) {
        Result<Long> result = handleResult(() -> r);

        Optional<String> err = result.getError();
        if (err.isPresent()) {
            // insertion failed
            return Result.error(err.get());
        }

        long attributeId = result.get();
        if (attributeId == 0) {
            // attribute exists already
            return Result.error("Attribute already exists");
        }

        // load new attribute
        err = handleResult(() -> reloadAttribute(name)).getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        // add new attribute to each player
        playerRepo.flushJoinedPlayers();

        for (RpPlayer player : playerRepo.getPlayers()) {
            playerRepo.setAttributeAsync(player, (int) attributeId, def, s ->
                s.getError().ifPresent(log::warning)
            );
        }

        return Result.ok(null);
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}