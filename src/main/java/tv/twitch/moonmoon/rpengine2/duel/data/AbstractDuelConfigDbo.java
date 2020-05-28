package tv.twitch.moonmoon.rpengine2.duel.data;

import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.duel.model.DuelConfig;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractDuelConfigDbo implements DuelConfigDbo {

    protected final RpDb db;

    protected AbstractDuelConfigDbo(RpDb db) {
        this.db = Objects.requireNonNull(db);
    }

    @Override
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

    @Override
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

    @Override
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

    protected Result<Void> setReadRules(int playerId) {
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
