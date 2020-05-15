package tv.twitch.moonmoon.rpengine2.data.attribute;

import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.di.PluginLogger;
import tv.twitch.moonmoon.rpengine2.model.AttributeType;
import tv.twitch.moonmoon.rpengine2.model.RpPlayer;
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
    private final Logger log;
    private Map<String, Attribute> attributes;

    @Inject
    public AttributeRepoImpl(
        AttributeDbo attributeDbo,
        RpPlayerRepo playerRepo,
        @PluginLogger Logger log
    ) {
        this.attributeDbo = Objects.requireNonNull(attributeDbo);
        this.playerRepo = Objects.requireNonNull(playerRepo);
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
        attributeDbo.insertAttributeAsync(name, display, type.getId(), defaultValue, r -> {

            Result<Long> result = handleResult(() -> r);

            Optional<String> err = result.getError();
            if (err.isPresent()) {
                // insertion failed
                callback.accept(Result.error(err.get()));
                return;
            }

            long attributeId = result.get();
            if (attributeId == -1) {
                // attribute exists already
                callback.accept(Result.error("Attribute already exists"));
                return;
            }

            // load new attribute
            err = handleResult(() -> reloadAttribute(name)).getError();
            if (err.isPresent()) {
                callback.accept(Result.error(err.get()));
                return;
            }

            // add new attribute to each player
            playerRepo.flushJoinedPlayers();

            for (RpPlayer player : playerRepo.getPlayers()) {
                playerRepo.setAttributeAsync(player, (int) attributeId, defaultValue, s ->
                    s.getError().ifPresent(log::warning)
                );
            }

            callback.accept(Result.ok(null));
        });
    }

    @Override
    public void removeAttributeAsync(String name, Consumer<Result<Void>> callback) {
        Attribute attribute = attributes.get(name);
        if (attribute == null) {
            callback.accept(Result.error("Attribute not found"));
            return;
        }

        int attributeId = attribute.getId();

        playerRepo.removeAttributesAsync(attributeId, r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                callback.accept(Result.error(err.get()));
                return;
            }

            err = attributeDbo.deleteAttribute(attributeId).getError();
            if (err.isPresent()) {
                callback.accept(Result.error(err.get()));
            } else {
                callback.accept(Result.ok(null));
            }
        });
    }

    @Override
    public void load() {
        attributeDbo.selectAttributesAsync(r -> {
            Optional<String> err = r.getError();
            if (err.isPresent()) {
                log.warning(err.get());
                return;
            }

            attributes = r.get().stream()
                .collect(Collectors.toMap(Attribute::getName, Function.identity()));

            log.info("loaded attributes " + attributes);
        });
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

    @Override
    public Logger getLogger() {
        return log;
    }
}
