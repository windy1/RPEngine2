package tv.twitch.moonmoon.rpengine2;

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
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractEngine implements Engine {

    protected final RpDb db;
    protected final Migrations migrations;
    protected final Defaults defaults;
    protected final RpPlayerRepo playerRepo;
    protected final AttributeRepo attributeRepo;
    protected final SelectRepo selectRepo;
    protected final Chat chat;
    protected final Duels duels;
    protected final CombatLog combatLog;

    private final List<Repo> repos;

    protected AbstractEngine(
        RpDb db,
        Migrations migrations,
        Defaults defaults,
        RpPlayerRepo playerRepo,
        AttributeRepo attributeRepo,
        SelectRepo selectRepo,
        Optional<Chat> chat,
        Optional<Duels> duels,
        Optional<CombatLog> combatLog
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
        repos = Arrays.asList(playerRepo, attributeRepo, selectRepo);
    }

    protected Result<Void> initDb() {
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
        Result<Void> r;
        if (chat != null && (r = chat.init()).getError().isPresent()) {
            return r;
        }

        if (duels != null && (r = duels.init()).getError().isPresent()) {
            return r;
        }

        if (combatLog != null && (r = combatLog.init()).getError().isPresent()) {
            return r;
        }

        return Result.ok(null);
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

    public void shutdown() {
        playerRepo.shutdown();
    }
}
