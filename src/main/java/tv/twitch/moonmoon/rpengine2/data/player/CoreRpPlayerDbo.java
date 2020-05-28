package tv.twitch.moonmoon.rpengine2.data.player;

import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeType;
import tv.twitch.moonmoon.rpengine2.model.player.impl.DefaultRpPlayerAttribute;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayer;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerAttribute;
import tv.twitch.moonmoon.rpengine2.model.player.RpPlayerFactory;
import tv.twitch.moonmoon.rpengine2.util.AsyncExecutor;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.Messenger;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CoreRpPlayerDbo implements RpPlayerDbo {

    private final RpDb db;
    private final AsyncExecutor asyncExecutor;
    private final Messenger log;
    private final RpPlayerFactory playerFactory;

    @Inject
    public CoreRpPlayerDbo(
        RpDb db,
        AsyncExecutor asyncExecutor,
        Messenger log,
        RpPlayerFactory playerFactory
    ) {
        this.db = Objects.requireNonNull(db);
        this.asyncExecutor = Objects.requireNonNull(asyncExecutor);
        this.log = Objects.requireNonNull(log);
        this.playerFactory = Objects.requireNonNull(playerFactory);
    }

    @Override
    public void insertPlayerAttributeAsync(
        int playerId,
        int attributeId,
        Object value,
        Callback<Void> callback
    ) {
        asyncExecutor.execute(() ->
            callback.accept(insertPlayerAttribute(playerId, attributeId, value))
        );
    }

    @Override
    public void updatePlayerAttributeAsync(
        int playerId,
        int attributeId,
        Object value,
        Callback<Void> callback
    ) {
        asyncExecutor.execute(() ->
            callback.accept(updatePlayerAttribute(playerId, attributeId, value))
        );
    }

    @Override
    public void deletePlayerAttributesAsync(int attributeId, Callback<Void> callback) {
        asyncExecutor.execute(() ->
            callback.accept(deletePlayerAttributes(attributeId))
        );
    }

    @Override
    public void setSessionAsync(int playerId, Instant sessionStart, Callback<Void> callback) {
        asyncExecutor.execute(() ->
            callback.accept(setSession(playerId, sessionStart))
        );
    }

    @Override
    public Result<Void> clearSessions() {
        final String query = "UPDATE rp_player SET session_start = NULL";

        try (Statement stmt = db.getConnection().createStatement()) {
            stmt.executeUpdate(query);
            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error clearing player sessions: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    @Override
    public Result<Set<RpPlayer>> selectPlayers() {
        final String query =
            "SELECT id, created, username, uuid, played, session_start FROM rp_player";

        Set<RpPlayer> players = new HashSet<>();

        try (Statement stmt = db.getConnection().createStatement();
             ResultSet results = stmt.executeQuery(query)) {
            while (results.next()) {
                players.add(readRpPlayer(results));
            }

            return Result.ok(players);
        } catch (SQLException e) {
            String message = "error reading players: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    @Override
    public Result<Long> insertPlayer(String name, UUID id) {
        if (name == null) {
            return Result.error("player not yet seen on server");
        }

        final String query =
            "INSERT OR IGNORE INTO rp_player (created, username, uuid) VALUES (?, ?, ?)";

        try (PreparedStatement stmt =
                 db.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, Instant.now().toString());
            stmt.setString(2, name);
            stmt.setString(3, id.toString());

            stmt.executeUpdate();

            try (ResultSet results = stmt.getGeneratedKeys()) {
                if (results.next()) {
                    return Result.ok(results.getLong(1));
                } else {
                    return Result.ok(0L);
                }
            }
        } catch (SQLException e) {
            String message = "error inserting player into database: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    @Override
    public Result<RpPlayer> selectPlayer(UUID playerId) {
        final String query =
            "SELECT id, created, username, uuid, played, session_start " +
                "FROM rp_player " +
                "WHERE uuid = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, playerId.toString());

            try (ResultSet results = stmt.executeQuery()) {
                if (results.next()) {
                    return Result.ok(readRpPlayer(results));
                } else {
                    return Result.error("player not found");
                }
            }
        } catch (SQLException e) {
            String message = "error reading player from database: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    @Override
    public Result<Void> updatePlayerAttribute(int playerId, int attributeId, Object value) {
        String rawValue = value != null ? value.toString() : null;

        final String query =
            "UPDATE rp_player_attribute " +
                "SET value = ? " +
                "WHERE player_id = ? " +
                "AND attribute_id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, rawValue);
            stmt.setInt(2, playerId);
            stmt.setInt(3, attributeId);

            stmt.executeUpdate();

            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error updating player attribute: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    @Override
    public Result<Void> setSession(int playerId, Instant sessionStart) {
        final String query = "UPDATE rp_player SET session_start = ? WHERE id = ?";

        String value = sessionStart != null ? sessionStart.toString() : null;

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, value);
            stmt.setInt(2, playerId);

            stmt.executeUpdate();

            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error setting player session: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    @Override
    public Result<Void> setPlayed(int playerId, Duration played) {
        final String query = "UPDATE rp_player SET played = ? WHERE id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, (int) (played.toMillis() / 1000));
            stmt.setInt(2, playerId);

            stmt.executeUpdate();

            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error updated play time: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    protected Result<Void> deletePlayerAttributes(int attributeId) {
        final String query = "DELETE FROM rp_player_attribute WHERE attribute_id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, attributeId);

            stmt.executeUpdate();
            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error deleting player attributes: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    protected Result<Set<RpPlayerAttribute>> selectPlayerAttributes(int playerId) {
        final String query =
            "SELECT " +
                "A.id AS attribute_id, " +
                "A.name, " +
                "A.type, " +
                "P.id AS instance_id, " +
                "P.created, " +
                "P.value " +
                "FROM rp_attribute A " +
                "LEFT JOIN rp_player_attribute P " +
                "ON A.id = P.attribute_id " +
                "WHERE P.player_id = ?";

        Set<RpPlayerAttribute> attributes = new HashSet<>();

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, playerId);

            try (ResultSet results = stmt.executeQuery()) {
                while (results.next()) {
                    String value = results.getString("value");

                    String typeId = results.getString("type");
                    AttributeType type = AttributeType.findById(typeId)
                        .orElse(AttributeType.String);
                    Object parsedValue = null;

                    if (value != null) {
                        Result<Object> r = type.parse(value);

                        Optional<String> err = r.getError();
                        if (err.isPresent()) {
                            log.warn(err.get());
                            continue;
                        }

                        parsedValue = r.orElse(null);
                    }


                    attributes.add(new DefaultRpPlayerAttribute(
                        results.getInt("instance_id"),
                        results.getInt("attribute_id"),
                        Instant.parse(results.getString("created")),
                        type,
                        results.getString("name"),
                        parsedValue
                    ));
                }

                return Result.ok(attributes);
            }
        } catch (SQLException e) {
            String message = "error reading player attributes: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    protected Result<Void> insertPlayerAttribute(int playerId, int attributeId, Object value) {
        String rawValue = value != null ? value.toString() : null;

        final String query =
            "INSERT OR IGNORE INTO rp_player_attribute (" +
                "created, " +
                "player_id, " +
                "attribute_id, " +
                "value" +
                ") " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, Instant.now().toString());
            stmt.setLong(2, playerId);
            stmt.setLong(3, attributeId);
            stmt.setString(4, rawValue);

            stmt.executeUpdate();

            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error inserting player attribute: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private RpPlayer readRpPlayer(ResultSet results) throws SQLException {
        int playerId = results.getInt("id");
        Set<RpPlayerAttribute> attributes;

        Result<Set<RpPlayerAttribute>> attributesResult = selectPlayerAttributes(playerId);

        Optional<String> err = attributesResult.getError();
        if (err.isPresent()) {
            log.warn(err.get());
            attributes = new HashSet<>();
        } else {
            attributes = attributesResult.get();
        }

        return playerFactory.newInstance(
            playerId,
            Instant.parse(results.getString("created")),
            results.getString("username"),
            UUID.fromString(results.getString("uuid")),
            attributes,
            Duration.ofSeconds(results.getInt("played")),
            Optional.ofNullable(results.getString("session_start"))
                .map(Instant::parse)
                .orElse(null)
        );
    }
}
