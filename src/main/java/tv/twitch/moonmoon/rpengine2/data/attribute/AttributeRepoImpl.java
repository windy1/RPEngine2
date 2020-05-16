package tv.twitch.moonmoon.rpengine2.data.attribute;

import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.di.PluginLogger;
import tv.twitch.moonmoon.rpengine2.model.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeArgs;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeType;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
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
    private Attribute identity;

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
    public Optional<Attribute> getIdentity() {
        return Optional.ofNullable(identity);
    }

    @Override
    public void createAttributeAsync(
        String name,
        AttributeType type,
        String display,
        String defaultValue,
        Callback<Void> callback
    ) {
        Result<AttributeArgs> a = new AttributeArgs(
            name, type, defaultValue, null, selectRepo,this
        ).canCreate();

        Optional<String> err = a.getError();
        if (err.isPresent()) {
            callback.accept(Result.error(err.get()));
            return;
        }

        AttributeArgs args = a.get();
        String def = args.getDefaultValue().orElse(null);
        String typeId = args.getType().getId();

        attributeDbo.insertAttributeAsync(args.getName(), display, typeId, def, r ->
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
            name, type, defaultValue, null, selectRepo, this
        ).canCreate();

        Optional<String> err = a.getError();
        if (err.isPresent()) {
            log.warning(err.get());
            return;
        }

        AttributeArgs args = a.get();
        String def = args.getDefaultValue().orElse(null);
        String typeId = args.getType().getId();

        Result<Long> r = attributeDbo.insertAttribute(args.getName(), display, typeId, def);
        handleCreateAttribute(name, def, r).getError().ifPresent(log::warning);
    }

    @Override
    public void removeAttributeAsync(String name, Callback<Void> callback) {
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
    public void setDefaultAsync(String name, String defaultValue, Callback<Void> callback) {
        Attribute attribute = attributes.get(name);
        if (attribute == null) {
            callback.accept(Result.error("Attribute not found"));
            return;
        }

        Result<AttributeArgs> a = new AttributeArgs(
            name, attribute.getType(), defaultValue, null, selectRepo, this
        ).canUpdate();

        Optional<String> err = a.getError();
        if (err.isPresent()) {
            callback.accept(Result.error(err.get()));
            return;
        }

        String def = a.get().getDefaultValue().orElse(null);

        attributeDbo.updateDefaultAsync(attribute.getId(), def, r -> {
            Optional<String> updateErr = handleResult(() -> r).getError();
            if (updateErr.isPresent()) {
                callback.accept(Result.error(updateErr.get()));
            } else {
                callback.accept(reloadAttribute(name).mapOk(v -> null));
            }
        });
    }

    @Override
    public void setDisplayAsync(String name, String display, Callback<Void> callback) {
        Attribute attribute = attributes.get(name);
        if (attribute == null) {
            callback.accept(Result.error("Attribute not found"));
            return;
        }

        attributeDbo.updateDisplayAsync(attribute.getId(), display, r -> {
            Optional<String> updateErr = handleResult(() -> r).getError();
            if (updateErr.isPresent()) {
                callback.accept(Result.error(updateErr.get()));
            } else {
                callback.accept(reloadAttribute(name).mapOk(v -> null));
            }
        });
    }

    @Override
    public void setFormatAsync(String name, String formatString, Callback<Void> callback) {
        Attribute attribute = attributes.get(name);
        if (attribute == null) {
            callback.accept(Result.error("Attribute not found"));
            return;
        }

        String def = attribute.getDefaultValue()
            .map(Object::toString)
            .orElse(null);

        // validate format string
        Result<AttributeArgs> a = new AttributeArgs(
            name, attribute.getType(), def, formatString, selectRepo, this
        ).canUpdate();

        Optional<String> err = a.getError();
        if (err.isPresent()) {
            callback.accept(Result.error(err.get()));
            return;
        }

        AttributeArgs args = a.get();
        String fmt = args.getFormatString().orElse(null);

        attributeDbo.updateFormatAsync(attribute.getId(), fmt, r -> {
            Optional<String> updateErr = handleResult(() -> r).getError();
            if (updateErr.isPresent()) {
                callback.accept(Result.error(updateErr.get()));
            } else {
                callback.accept(reloadAttribute(name).mapOk(v -> null));
            }
        });
    }

    @Override
    public void setIdentityAsync(String name, Callback<Void> callback) {
        Attribute attribute = attributes.get(name);
        if (attribute == null) {
            callback.accept(Result.error("Attribute not found"));
            return;
        }

        Optional<String> check = checkIdent(attribute);
        if (check.isPresent()) {
            callback.accept(Result.error(check.get()));
            return;
        }

        attributeDbo.setIdentityAsync(attribute.getId(), r -> {
            Optional<String> err = handleIdentityUpdate(r).getError();
            if (err.isPresent()) {
                callback.accept(Result.error(err.get()));
                return;
            }

            Result<Attribute> newIdent = reloadAttribute(name);

            err = newIdent.getError();
            if (err.isPresent()) {
                callback.accept(Result.error(err.get()));
            } else {
                identity = newIdent.get();
                callback.accept(Result.ok(null));
            }
        });
    }

    @Override
    public void setIdentity(String name) {
        Attribute attribute = attributes.get(name);
        if (attribute == null) {
            return;
        }

        Optional<String> check = checkIdent(attribute);
        if (check.isPresent()) {
            return;
        }

        Result<Void> update = attributeDbo.setIdentity(attribute.getId());

        Optional<String> err = handleIdentityUpdate(update).getError();
        if (err.isPresent()) {
            log.warning(err.get());
            return;
        }

        Result<Attribute> newIdent = reloadAttribute(name);

        err = newIdent.getError();
        if (err.isPresent()) {
            log.warning(err.get());
        } else {
            identity = newIdent.get();
        }
    }

    private Optional<String> checkIdent(Attribute attribute) {
        if (attribute.getType() != AttributeType.String) {
            String message = "The identity attribute may only be on attributes of type: string";
            return Optional.of(message);
        }
        return Optional.empty();
    }

    @Override
    public void clearIdentityAsync(Callback<Void> callback) {
        attributeDbo.clearIdentityAsync(r -> {
            Optional<String> err = handleIdentityUpdate(r).getError();
            if (err.isPresent()) {
                callback.accept(Result.error(err.get()));
            } else {
                identity = null;
                callback.accept(Result.ok(null));
            }
        });
    }

    private Result<Void> handleIdentityUpdate(Result<Void> r) {
        Optional<String> err = handleResult(() -> r).getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        if (identity != null) {
            err = reloadAttribute(identity.getName()).getError();
            if (err.isPresent()) {
                return Result.error(err.get());
            }
        }

        return Result.ok(null);
    }

    @Override
    public Result<Void> load() {
        Result<Set<Attribute>> r = attributeDbo.selectAttributes();

        Optional<String> err = r.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        } else {
            attributes = Collections.synchronizedMap(r.get().stream()
                .collect(Collectors.toMap(Attribute::getName, Function.identity())));

            identity = attributes.values().stream()
                .filter(Attribute::isIdentity)
                .findFirst()
                .orElse(null);

            return Result.ok(null);
        }
    }

    private Result<Attribute> reloadAttribute(String name) {
        Result<Attribute> updatedAttribute = handleResult(() ->
            attributeDbo.selectAttribute(name)
        );

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
        err = reloadAttribute(name).getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        // add new attribute to each player
        playerRepo.flushJoinedPlayers();

        for (RpPlayer player : playerRepo.getPlayers()) {
            // TODO: bad
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
