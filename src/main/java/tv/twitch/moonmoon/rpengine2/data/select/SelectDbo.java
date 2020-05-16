package tv.twitch.moonmoon.rpengine2.data.select;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.model.select.Option;
import tv.twitch.moonmoon.rpengine2.model.select.Select;
import tv.twitch.moonmoon.rpengine2.util.Result;
import tv.twitch.moonmoon.rpengine2.util.StringUtils;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class SelectDbo {

    private final Plugin plugin;
    private final RpDb db;
    private final Logger log;

    @Inject
    public SelectDbo(Plugin plugin, RpDb db) {
        this.plugin = Objects.requireNonNull(plugin);
        this.db = Objects.requireNonNull(db);
        log = plugin.getLogger();
    }

    public void insertSelectAsync(String name, Consumer<Result<Long>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(insertSelect(name))
        );
    }

    public void insertOptionAsync(
        int selectId,
        String name,
        String display,
        ChatColor color,
        Consumer<Result<Void>> callback
    ) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(insertOption(selectId, name, display, color))
        );
    }

    public Result<Select> selectSelect(int selectId) {
        final String query = "SELECT id, created, name FROM rp_select WHERE id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, selectId);

            try (ResultSet results = stmt.executeQuery()) {
                if (results.next()) {
                    return Result.ok(readSelect(results));
                } else {
                    return Result.error("Select not found");
                }
            }
        } catch (SQLException e) {
            String message = "error reading select: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    public Result<Long> insertSelect(String name) {
        final String query = "INSERT OR IGNORE INTO rp_select (created, name) VALUES (?, ?)";

        try (PreparedStatement stmt =
                 db.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, Instant.now().toString());
            stmt.setString(2, name);

            stmt.executeUpdate();

            try (ResultSet results = stmt.getGeneratedKeys()) {
                if (results.next()) {
                    return Result.ok(results.getLong(1));
                } else {
                    return Result.ok(0L);
                }
            }
        } catch (SQLException e) {
            String message = "error inserting select: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    public Result<Void> insertOption(int selectId, String name, String display, ChatColor color) {
        final String query =
            "INSERT OR IGNORE INTO rp_select_option (" +
                "select_id, " +
                "created, " +
                "name, " +
                "display, " +
                "color" +
                ") " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, selectId);
            stmt.setString(2, Instant.now().toString());
            stmt.setString(3, name);
            stmt.setString(4, display);
            stmt.setString(5, color != null ? color.name() : null);

            stmt.executeUpdate();

            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error inserting option: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    public Result<Set<Select>> selectSelects() {
        final String query = "SELECT id, created, name FROM rp_select";

        Set<Select> selects = new HashSet<>();

        try (Statement stmt = db.getConnection().createStatement();
                ResultSet results = stmt.executeQuery(query)) {
            while (results.next()) {
                selects.add(readSelect(results));
            }

            return Result.ok(selects);
        } catch (SQLException e) {
            String error = "error reading selects: `%s`";
            return Result.error(String.format(error, e.getMessage()));
        }
    }

    public void deleteSelectAsync(int selectId, Consumer<Result<Void>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(deleteSelect(selectId))
        );
    }

    public Result<Void> deleteSelect(int selectId) {
        final String query = "DELETE FROM rp_select WHERE id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, selectId);
            stmt.executeUpdate();
            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error deleting select: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private Select readSelect(ResultSet results) throws SQLException {
        int selectId = results.getInt("id");
        Result<Set<Option>> o = selectOptions(selectId);
        Set<Option> options;

        Optional<String> err = o.getError();
        if (err.isPresent()) {
            log.warning(err.get());
            options = new HashSet<>();
        } else {
            options = o.get();
        }

        return new Select(
            selectId,
            Instant.parse(results.getString("created")),
            results.getString("name"),
            options
        );
    }

    private Result<Set<Option>> selectOptions(int selectId) {
        final String query =
            "SELECT id, created, name, display, color FROM rp_select_option WHERE select_id = ?";

        Set<Option> options = new HashSet<>();

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, selectId);

            try (ResultSet results = stmt.executeQuery()) {
                while (results.next()) {
                    options.add(new Option(
                        results.getInt("id"),
                        selectId,
                        Instant.parse(results.getString("created")),
                        results.getString("name"),
                        results.getString("display"),
                        StringUtils.getChatColor(results.getString("color")).orElse(null)
                    ));
                }

                return Result.ok(options);
            }
        } catch (SQLException e) {
            String message = "error reading options: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }
}
