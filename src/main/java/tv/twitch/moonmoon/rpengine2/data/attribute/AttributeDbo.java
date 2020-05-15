package tv.twitch.moonmoon.rpengine2.data.attribute;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.model.AttributeType;
import tv.twitch.moonmoon.rpengine2.util.Result;

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

public class AttributeDbo {

    private final Plugin plugin;
    private final RpDb db;
    private final Logger log;

    @Inject
    public AttributeDbo(Plugin plugin, RpDb db) {
        this.plugin = Objects.requireNonNull(plugin);
        this.db = Objects.requireNonNull(db);
        log = plugin.getLogger();
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

    public void selectAttributesAsync(Consumer<Result<Set<Attribute>>> callback) {
        Bukkit.getScheduler()
            .runTaskAsynchronously(plugin, () -> callback.accept(selectAttributes()));
    }

    public Result<Attribute> selectAttribute(String name) {
        final String query =
            "SELECT " +
                "id, " +
                "created, " +
                "name, " +
                "display, " +
                "type, " +
                "default_value " +
            "FROM rp_attribute " +
            "WHERE name = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, name);

            try (ResultSet results = stmt.executeQuery()) {
                if (results.next()) {
                    return Result.ok(readAttribute(results));
                } else {
                    return Result.error("attribute not found");
                }
            }
        } catch (SQLException e) {
            String message = "error reading attribute: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private Result<Set<Attribute>> selectAttributes() {
        final String query =
            "SELECT id, created, name, display, type, default_value FROM rp_attribute";

        Set<Attribute> attributes = new HashSet<>();

        try (Statement stmt = db.getConnection().createStatement();
                ResultSet results = stmt.executeQuery(query)) {
            while (results.next()) {
                attributes.add(readAttribute(results));
            }

            return Result.ok(attributes);
        } catch (SQLException e) {
            String message = "error reading attributes: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private Attribute readAttribute(ResultSet results) throws SQLException {
        Optional<AttributeType> t = AttributeType.findById(results.getString("type"));
        AttributeType type;
        if (!t.isPresent()) {
            log.warning("invalid attribute type, defaulting to string");
            type = AttributeType.String;
        } else {
            type = t.get();
        }

        String rawDefault = results.getString("default_value");
        Result<Object> d = parseAttributeValue(rawDefault, type.getId());

        Optional<String> err = d.getError();
        Object defaultValue = null;
        if (err.isPresent()) {
            log.warning(err.get());
        } else {
            defaultValue = d.get();
        }

        return new Attribute(
            results.getInt("id"),
            Instant.parse(results.getString("created")),
            results.getString("name"),
            results.getString("display"),
            type,
            defaultValue
        );
    }

    private Result<Long> insertAttribute(
        String name,
        String display,
        String type,
        String defaultValue
    ) {
        final String query =
            "INSERT OR IGNORE INTO rp_attribute (" +
                "created, " +
                "name, " +
                "display, " +
                "type, " +
                "default_value" +
            ") " +
            "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt =
                 db.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, Instant.now().toString());
            stmt.setString(2, name);
            stmt.setString(3, display);
            stmt.setString(4, type);
            stmt.setString(5, defaultValue);

            stmt.executeUpdate();

            try (ResultSet results = stmt.getGeneratedKeys()) {
                if (results.next()) {
                    return Result.ok(results.getLong(1));
                } else {
                    return Result.ok(0L);
                }
            }
        } catch (SQLException e) {
            String message = "error inserting new attribute: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    public static Result<Object> parseAttributeValue(String value, String type) {
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
