package tv.twitch.moonmoon.rpengine2.data.attribute;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;

public class AttributeDbo {

    private final Plugin plugin;
    private final RpDb db;

    @Inject
    public AttributeDbo(Plugin plugin, RpDb db) {
        this.plugin = Objects.requireNonNull(plugin);
        this.db = Objects.requireNonNull(db);
    }

    public void insertAttributeAsync(
        String name,
        String display,
        String type,
        String defaultValue,
        Consumer<Result<Long>> callback
    ) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(insertAttribute(name, display, type, defaultValue))
        );
    }

    private Result<Long> insertAttribute(
        String name,
        String display,
        String type,
        String defaultValue
    ) {
        // TODO: default value

        final String query =
            "INSERT OR IGNORE INTO rp_attribute (" +
                "created, " +
                "name, " +
                "display, " +
                "type" +
                ") " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt =
                 db.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, Instant.now().toString());
            stmt.setString(2, name);
            stmt.setString(3, display);
            stmt.setString(4, type);

            stmt.executeUpdate();

            try (ResultSet results = stmt.getGeneratedKeys()) {
                if (results.next()) {
                    return Result.ok(results.getLong(1));
                } else {
                    return Result.ok(-1L);
                }
            }
        } catch (SQLException e) {
            String message = "error inserting new attribute: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }
}
