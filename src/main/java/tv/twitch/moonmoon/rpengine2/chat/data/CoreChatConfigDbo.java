package tv.twitch.moonmoon.rpengine2.chat.data;

import tv.twitch.moonmoon.rpengine2.chat.Chat;
import tv.twitch.moonmoon.rpengine2.chat.ChatChannel;
import tv.twitch.moonmoon.rpengine2.chat.model.ChatConfig;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.util.AsyncExecutor;
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

public class CoreChatConfigDbo implements ChatConfigDbo {

    private final RpDb db;
    private final Chat chat;
    private final AsyncExecutor asyncExecutor;

    @Inject
    public CoreChatConfigDbo(RpDb db, Chat chat, AsyncExecutor asyncExecutor) {
        this.db = Objects.requireNonNull(db);
        this.chat = Objects.requireNonNull(chat);
        this.asyncExecutor = Objects.requireNonNull(asyncExecutor);
    }

    @Override
    public void updateChannelAsync(int playerId, String channelName, Callback<Void> callback) {
        asyncExecutor.execute(() ->
            callback.accept(updateChannel(playerId, channelName))
        );
    }

    @Override
    public Result<Set<ChatConfig>> selectConfigs() {
        final String query = "SELECT id, created, player_id, channel FROM rp_chat_config";

        Set<ChatConfig> configs = new HashSet<>();

        try (Statement stmt = db.getConnection().createStatement();
             ResultSet results = stmt.executeQuery(query)) {
            while (results.next()) {
                configs.add(readConfig(results));
            }

            return Result.ok(configs);
        } catch (SQLException e) {
            String message = "error reading chat configs: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    @Override
    public Result<ChatConfig> selectConfig(int playerId) {
        final String query =
            "SELECT id, created, player_id, channel " +
                "FROM rp_chat_config " +
                "WHERE player_id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, playerId);

            try (ResultSet results = stmt.executeQuery()) {
                if (results.next()) {
                    return Result.ok(readConfig(results));
                } else {
                    return Result.error("config not found");
                }
            }
        } catch (SQLException e) {
            String message = "error reading chat config: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    @Override
    public Result<Long> createConfig(int playerId) {
        final String query = "INSERT OR IGNORE INTO rp_chat_config (" +
            "created, " +
            "player_id, " +
            "channel" +
            ") " +
            "VALUES (?, ?, ?)";

        String channelName = chat.getDefaultChannel()
            .map(ChatChannel::getName)
            .orElse(null);

        try (PreparedStatement stmt =
                 db.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, Instant.now().toString());
            stmt.setInt(2, playerId);
            stmt.setString(3, channelName);

            stmt.executeUpdate();

            try (ResultSet results = stmt.getGeneratedKeys()) {
                if (results.next()) {
                    return Result.ok(results.getLong(1));
                } else {
                    return Result.ok(0L);
                }
            }
        } catch (SQLException e) {
            String message = "error inserting chat config: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    protected Result<Void> updateChannel(int playerId, String channelName) {
        final String query = "UPDATE rp_chat_config SET channel = ? WHERE player_id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, channelName);
            stmt.setInt(2, playerId);

            stmt.executeUpdate();

            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error updating player chat channel: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private ChatConfig readConfig(ResultSet results) throws SQLException {
        String channelName = results.getString("channel");
        ChatChannel channel = null;
        if (channelName != null) {
            channel = chat.getChannel(channelName).orElse(null);
        }

        return new ChatConfig(
            results.getInt("id"),
            Instant.parse(results.getString("created")),
            results.getInt("player_id"),
            channel
        );
    }
}
