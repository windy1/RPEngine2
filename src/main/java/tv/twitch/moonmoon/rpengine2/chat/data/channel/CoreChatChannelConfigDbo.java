package tv.twitch.moonmoon.rpengine2.chat.data.channel;

import tv.twitch.moonmoon.rpengine2.chat.model.ChatChannelConfig;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.task.Callback;
import tv.twitch.moonmoon.rpengine2.task.TaskExecutor;
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

public class CoreChatChannelConfigDbo implements ChatChannelConfigDbo {

    private final RpDb db;
    private final TaskExecutor taskExecutor;

    @Inject
    public CoreChatChannelConfigDbo(RpDb db, TaskExecutor taskExecutor) {
        this.db = Objects.requireNonNull(db);
        this.taskExecutor = Objects.requireNonNull(taskExecutor);
    }

    @Override
    public void setMutedAsync(
        int playerId,
        String channelName,
        boolean muted,
        Callback<Void> callback
    ) {
        taskExecutor.execute(() ->
            callback.accept(setMuted(playerId, channelName, muted))
        );
    }

    @Override
    public Result<Set<ChatChannelConfig>> selectConfigs() {
        final String query =
            "SELECT id, created, channel_name, player_id, muted FROM rp_chat_channel_config";

        Set<ChatChannelConfig> configs = new HashSet<>();

        try (Statement stmt = db.getConnection().createStatement();
             ResultSet results = stmt.executeQuery(query)) {
            while (results.next()) {
                configs.add(readConfig(results));
            }

            return Result.ok(configs);
        } catch (SQLException e) {
            String message = "error reading chat channel configs: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    @Override
    public Result<ChatChannelConfig> selectConfig(int playerId, String channelName) {
        final String query =
            "SELECT id, created, channel_name, player_id, muted " +
                "FROM rp_chat_channel_config " +
                "WHERE player_id = ? " +
                "AND channel_name = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, playerId);
            stmt.setString(2, channelName);

            try (ResultSet results = stmt.executeQuery()) {
                if (results.next()) {
                    return Result.ok(readConfig(results));
                } else {
                    return Result.error("config not found");
                }
            }
        } catch (SQLException e) {
            String message = "error reading channel config: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    @Override
    public Result<Long> createConfig(int playerId, String channelName) {
        final String query =
            "INSERT OR IGNORE INTO rp_chat_channel_config (" +
                "created, " +
                "channel_name, " +
                "player_id" +
                ") " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement stmt =
                 db.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, Instant.now().toString());
            stmt.setString(2, channelName);
            stmt.setInt(3, playerId);

            stmt.executeUpdate();

            try (ResultSet results = stmt.getGeneratedKeys()) {
                if (results.next()) {
                    return Result.ok(results.getLong(1));
                } else {
                    return Result.ok(0L);
                }
            }
        } catch (SQLException e) {
            String message = "error inserting chat channel config: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    protected Result<Void> setMuted(int playerId, String channelName, boolean muted) {
        int mutedValue = muted ? 1 : 0;
        final String query =
            "UPDATE rp_chat_channel_config " +
                "SET muted = ? " +
                "WHERE player_id = ? " +
                "AND channel_name = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, mutedValue);
            stmt.setInt(2, playerId);
            stmt.setString(3, channelName);

            stmt.executeUpdate();

            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error updating channel config: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private ChatChannelConfig readConfig(ResultSet results) throws SQLException {
        return new ChatChannelConfig(
            results.getInt("id"),
            Instant.parse(results.getString("created")),
            results.getString("channel_name"),
            results.getInt("player_id"),
            results.getInt("muted") > 0
        );
    }
}
