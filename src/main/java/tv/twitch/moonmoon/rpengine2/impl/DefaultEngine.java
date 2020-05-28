package tv.twitch.moonmoon.rpengine2.impl;

import tv.twitch.moonmoon.rpengine2.Engine;
import tv.twitch.moonmoon.rpengine2.RpModule;
import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.combatlog.CombatLog;
import tv.twitch.moonmoon.rpengine2.data.Defaults;
import tv.twitch.moonmoon.rpengine2.data.Migrations;
import tv.twitch.moonmoon.rpengine2.data.Repo;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.player.RpPlayerRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.duel.Duels;
import tv.twitch.moonmoon.rpengine2.util.Messenger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.*;

public class DefaultEngine implements Engine {

    protected final RpDb db;
    protected final Migrations migrations;
    protected final Defaults defaults;
    protected final RpPlayerRepo playerRepo;
    protected final AttributeRepo attributeRepo;
    protected final SelectRepo selectRepo;
    protected final Chat chat;
    protected final Duels duels;
    protected final CombatLog combatLog;
    protected final Messenger messenger;

    protected final List<Repo> repos;
    protected final List<RpModule> modules = new ArrayList<>();

    protected DefaultEngine(
        RpDb db,
        Migrations migrations,
        Defaults defaults,
        RpPlayerRepo playerRepo,
        AttributeRepo attributeRepo,
        SelectRepo selectRepo,
        Optional<Chat> chat,
        Optional<Duels> duels,
        Optional<CombatLog> combatLog,
        Messenger messenger
    ) {
        this.db = Objects.requireNonNull(db);
        this.migrations = Objects.requireNonNull(migrations);
        this.defaults = Objects.requireNonNull(defaults);
        this.playerRepo = Objects.requireNonNull(playerRepo);
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        this.selectRepo = Objects.requireNonNull(selectRepo);
        this.chat = chat.orElse(null);
        this.duels = duels.orElse(null);
        this.combatLog = combatLog.orElse(null);
        this.messenger = Objects.requireNonNull(messenger);

        repos = Arrays.asList(playerRepo, attributeRepo, selectRepo);

        if (this.chat != null) {
            modules.add(this.chat);
        }

        if (this.duels != null) {
            modules.add(this.duels);
        }

        if (this.combatLog != null) {
            modules.add(this.combatLog);
        }
    }

    protected void onFailure() {
    }

    protected boolean onStart() {
        return true;
    }

    protected boolean onStarted() {
        return true;
    }

    @Override
    public void start() {
        if (onStart() && handleResult(initDb()) && handleResult(initModules()) && onStarted()) {
            messenger.info("Done");
        }
    }

    @Override
    public void shutdown() {
        playerRepo.shutdown();
    }

    protected Result<Void> initDb() {
        messenger.info("Connecting to database");

        Optional<String> err = db.connect().getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        err = migrations.migrate().getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        for (Repo repo : repos) {
            err = repo.load().getError();
            if (err.isPresent()) {
                return Result.error(err.get());
            }
        }

        defaults.saveDefaults();

        return Result.ok(null);
    }

    protected Result<Void> initModules() {
        Optional<String> err;
        for (RpModule module : modules) {
            err = module.init().getError();
            if (err.isPresent()) {
                return Result.error(err.get());
            }
        }

        return Result.ok(null);
    }

    private <T> boolean handleResult(Result<T> r) {
        Optional<String> err = r.getError();
        if (err.isPresent()) {
            onFailure();
            messenger.warn(err.get());
            return false;
        }
        return true;
    }

    @Override
    public RpPlayerRepo getPlayerRepo() {
        return playerRepo;
    }

    @Override
    public AttributeRepo getAttributeRepo() {
        return attributeRepo;
    }

    @Override
    public SelectRepo getSelectRepo() {
        return selectRepo;
    }

    @Override
    public Optional<Chat> getChatModule() {
        return Optional.ofNullable(chat);
    }

    @Override
    public Optional<Duels> getDuelsModule() {
        return Optional.ofNullable(duels);
    }

    @Override
    public Optional<CombatLog> getCombatLogModule() {
        return Optional.ofNullable(combatLog);
    }
}
