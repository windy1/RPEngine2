package tv.twitch.moonmoon.rpengine2.duel.data;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.duel.model.DuelConfig;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DuelConfigDbo {

    private final Plugin plugin;
    private final RpDb db;

    @Inject
    public DuelConfigDbo(Plugin plugin, RpDb db) {
        this.plugin = Objects.requireNonNull(plugin);
        this.db = Objects.requireNonNull(db);
    }

    public Result<Set<DuelConfig>> selectConfigs() {
        final String query = "SELECT id, created, player_id, read_rules FROM rp_duel_config";

        Set<DuelConfig> configs = new HashSet<>();

        try (Statement stmt = db.getConnection().createStatement();
                ResultSet results = stmt.executeQuery(query)) {
            while (results.next()) {
                configs.add(readConfig(results));
            }

            return Result.ok(configs);
        } catch (SQLException e) {
            String message = "error reading duel configs: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    public Result<DuelConfig> selectConfig(int playerId) {
        final String query =
            "SELECT id, created, player_id, read_rules " +
            "FROM rp_duel_config " +
            "WHERE player_id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, playerId);

            try (ResultSet results = stmt.executeQuery()) {
                if (results.next()) {
                    return Result.ok(readConfig(results));
                } else {
                    return Result.error("duel config not found");
                }
            }
        } catch (SQLException e) {
            String message = "error reading duel config: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    public Result<Long> insertConfig(int playerId) {
        final String query = "INSERT INTO rp_duel_config (created, player_id) VALUES (?, ?)";

        try (PreparedStatement stmt =
                 db.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, Instant.now().toString());
            stmt.setInt(2, playerId);

            stmt.executeUpdate();

            try (ResultSet results = stmt.getGeneratedKeys()) {
                if (results.next()) {
                    return Result.ok(results.getLong(1));
                } else {
                    return Result.ok(0L);
                }
            }
        } catch (SQLException e) {
            String message = "error inserting config: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    public void setReadRulesAsync(int playerId, Callback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(setReadRules(playerId))
        );
    }

    private Result<Void> setReadRules(int playerId) {
        final String query = "UPDATE rp_duel_config SET read_rules = 1 WHERE player_id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, playerId);
            stmt.executeUpdate();
            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error updating duel config read_rules: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private DuelConfig readConfig(ResultSet results) throws SQLException {
        return new DuelConfig(
            results.getInt("id"),
            Instant.parse(results.getString("created")),
            results.getInt("player_id"),
            results.getInt("read_rules") > 0
        );
    }
}
