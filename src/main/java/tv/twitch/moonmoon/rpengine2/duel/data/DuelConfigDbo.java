package tv.twitch.moonmoon.rpengine2.duel.data;

import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.duel.model.DuelConfig;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DuelConfigDbo {

    private final RpDb db;

    @Inject
    public DuelConfigDbo(RpDb db) {
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

    private DuelConfig readConfig(ResultSet results) throws SQLException {
        return new DuelConfig(
            results.getInt("id"),
            Instant.parse(results.getString("created")),
            results.getInt("player_id"),
            results.getInt("read_rules") > 0
        );
    }
}
