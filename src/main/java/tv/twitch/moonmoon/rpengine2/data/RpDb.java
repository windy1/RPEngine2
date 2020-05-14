package tv.twitch.moonmoon.rpengine2.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.Result;
import tv.twitch.moonmoon.rpengine2.di.DbPath;
import tv.twitch.moonmoon.rpengine2.model.RpPlayer;
import tv.twitch.moonmoon.rpengine2.model.RpPlayerAttribute;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

@Singleton
public class RpDb {

    private final Plugin plugin;
    private final Path path;
    private final Logger log;

    private Connection conn;

    @Inject
    public RpDb(Plugin plugin, @DbPath Path path) {
        this.plugin = Objects.requireNonNull(plugin);
        this.path = Objects.requireNonNull(path);
        log = plugin.getLogger();
    }

    public void connectAsync(Consumer<Result<Void>> callback) {
        Logger log = plugin.getLogger();

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (IOException e) {
                String message = "error creating database file: `%s`";
                callback.accept(Result.error(String.format(message, e.getMessage())));
                return;
            }
            log.info(String.format("Created empty database file at `%s`", path));
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Optional<String> err = connect().getError();
            if (err.isPresent()) {
                callback.accept(Result.error(err.get()));
                return;
            }

            for (String query : Migrations.MIGRATIONS) {
                err = migrate(query).getError();
                if (err.isPresent()) {
                    callback.accept(Result.error(err.get()));
                    return;
                }
            }

            callback.accept(Result.ok(null));
        });
    }

    public void selectPlayersAsync(Consumer<Result<Set<RpPlayer>>> callback) {
        Bukkit.getScheduler()
            .runTaskAsynchronously(plugin, () -> callback.accept(selectPlayers()));
    }

    public Result<Void> insertPlayer(OfflinePlayer player) {
        if (player.getName() == null || !player.hasPlayedBefore()) {
            return Result.error("player not yet seen on server");
        }

        final String query =
            "INSERT OR IGNORE INTO rp_player (created, username, uuid) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, Instant.now().toString());
            stmt.setString(2, player.getName());
            stmt.setString(3, player.getUniqueId().toString());

            stmt.executeUpdate();

            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error inserting player into database: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    public Result<RpPlayer> selectPlayer(UUID playerId) {
        final String query = "SELECT id, created, username, uuid FROM rp_player WHERE uuid = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, playerId.toString());

            try (ResultSet results = stmt.executeQuery()) {
                return Result.ok(readRpPlayer(results));
            }
        } catch (SQLException e) {
            String message = "error reading player from database: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private Result<Void> connect() {
        String url = "jdbc:sqlite:" + path.toString();

        try {
            conn = DriverManager.getConnection(url);
            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error connecting to database: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private Result<Void> migrate(String query) {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error updating database schema: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private Result<Set<RpPlayer>> selectPlayers() {
        final String query = "SELECT id, created, username, uuid FROM rp_player";

        Set<RpPlayer> players = new HashSet<>();

        try (Statement stmt = conn.createStatement();
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

    private RpPlayer readRpPlayer(ResultSet results) throws SQLException {
        int playerId = results.getInt("id");
        Set<Integer> groupIds;
        Set<RpPlayerAttribute> attributes;

        Result<Set<Integer>> groupIdsResult = selectPlayerGroupIds(playerId);

        Optional<String> err = groupIdsResult.getError();
        if (err.isPresent()) {
            log.warning(err.get());
            groupIds = new HashSet<>();
        } else {
            groupIds = groupIdsResult.get();
        }

        Result<Set<RpPlayerAttribute>> attributesResult = selectPlayerAttributes(playerId);

        err = attributesResult.getError();
        if (err.isPresent()) {
            log.warning(err.get());
            attributes = new HashSet<>();
        } else {
            attributes = attributesResult.get();
        }

        return new RpPlayer(
            playerId,
            Instant.parse(results.getString("created")),
            results.getString("username"),
            UUID.fromString(results.getString("uuid")),
            attributes,
            groupIds
        );
    }

    private Result<Set<Integer>> selectPlayerGroupIds(int playerId) {
        final String query = "SELECT group_id FROM rp_player_group WHERE player_id = ?";

        Set<Integer> groupIds = new HashSet<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, playerId);

            try (ResultSet results = stmt.executeQuery()) {
                while (results.next()) {
                    groupIds.add(results.getInt("group_id"));
                }
            }

            return Result.ok(groupIds);
        } catch (SQLException e) {
            String message = "error reading player groups: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private Result<Set<RpPlayerAttribute>> selectPlayerAttributes(int playerId) {
        final String query =
            "SELECT " +
                "A.id, " +
                "A.name, " +
                "A.display, " +
                "A.type, " +
                "P.id AS instance_id, " +
                "P.created, " +
                "P.value " +
            "FROM rp_attribute A " +
            "LEFT JOIN rp_player_attribute P " +
            "ON A.id = P.attribute_id " +
            "WHERE P.player_id = ?";

        Set<RpPlayerAttribute> attributes = new HashSet<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, playerId);

            try (ResultSet results = stmt.executeQuery()) {
                while (results.next()) {

                    String value = results.getString("value");
                    String type = results.getString("type");

                    Result<Object> r = parseAttributeValue(value, type);

                    Optional<String> err = r.getError();
                    if (err.isPresent()) {
                        log.warning(err.get());
                        continue;
                    }

                    Object parsedValue = r.get();

                    attributes.add(new RpPlayerAttribute(
                        results.getInt("instance_id"),
                        Instant.parse(results.getString("created")),
                        results.getString("display"),
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

    private Result<Object> parseAttributeValue(String value, String type) {
        switch (type) {
            case "string":
                return Result.ok(value);
            case "number": {
                try {
                    return Result.ok(Float.parseFloat(value));
                } catch (NumberFormatException e) {
                    return Result.error("invalid number value on attribute");
                }
            }
            case "group":
                try {
                    return Result.ok(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    return Result.error("invalid group value on attribute");
                }
            default:
                return Result.error(String.format("unknown type: `%s`", type));
        }
    }
}
