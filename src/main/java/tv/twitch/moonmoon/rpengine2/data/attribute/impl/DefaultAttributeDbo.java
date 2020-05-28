package tv.twitch.moonmoon.rpengine2.data.attribute.impl;

import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeDbo;
import tv.twitch.moonmoon.rpengine2.model.attribute.Attribute;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeType;
import tv.twitch.moonmoon.rpengine2.model.attribute.impl.DefaultAttribute;
import tv.twitch.moonmoon.rpengine2.util.AsyncExecutor;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.Messenger;
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

public class DefaultAttributeDbo implements AttributeDbo {

    private final RpDb db;
    private final AsyncExecutor asyncExecutor;
    private final Messenger log;

    @Inject
    public DefaultAttributeDbo(RpDb db, AsyncExecutor asyncExecutor, Messenger log) {
        this.db = Objects.requireNonNull(db);
        this.asyncExecutor = Objects.requireNonNull(asyncExecutor);
        this.log = Objects.requireNonNull(log);
    }

    @Override
    public void insertAttributeAsync(
        String name,
        String display,
        String type,
        String defaultValue,
        Callback<Long> callback
    ) {
        asyncExecutor.execute(() ->
            callback.accept(insertAttribute(name, display, type, defaultValue))
        );
    }

    @Override
    public void updateDefaultAsync(int attributeId, String defaultValue, Callback<Void> callback) {
        asyncExecutor.execute(() ->
            callback.accept(updateDefault(attributeId, defaultValue))
        );
    }

    @Override
    public void updateDisplayAsync(int attributeId, String display, Callback<Void> callback) {
        asyncExecutor.execute(() ->
            callback.accept(updateDisplay(attributeId, display))
        );
    }

    @Override
    public void updateFormatAsync(int attributeId, String formatString, Callback<Void> callback) {
        asyncExecutor.execute(() ->
            callback.accept(updateFormat(attributeId, formatString))
        );
    }

    @Override
    public void setIdentityAsync(int attributeId, Callback<Void> callback) {
        asyncExecutor.execute(() ->
            callback.accept(setIdentity(attributeId))
        );
    }

    @Override
    public void clearIdentityAsync(Callback<Void> callback) {
        asyncExecutor.execute(() ->
            callback.accept(clearIdentity())
        );
    }

    @Override
    public void setMarkerAsync(int attributeId, Callback<Void> callback) {
        asyncExecutor.execute(() ->
            callback.accept(setMarker(attributeId))
        );
    }

    @Override
    public void clearMarkerAsync(Callback<Void> callback) {
        asyncExecutor.execute(() -> callback.accept(clearMarker()));
    }

    @Override
    public void setTitleAsync(int attributeId, Callback<Void> callback) {
        asyncExecutor.execute(() ->
            callback.accept(setTitle(attributeId))
        );
    }

    @Override
    public void clearTitleAsync(Callback<Void> callback) {
        asyncExecutor.execute(() -> callback.accept(clearTitle()));
    }

    @Override
    public Result<Attribute> selectAttribute(String name) {
        final String query =
            "SELECT " +
                "id, " +
                "created, " +
                "name, " +
                "display, " +
                "type, " +
                "default_value, " +
                "format, " +
                "identity," +
                "marker," +
                "title  " +
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

    @Override
    public Result<Void> deleteAttribute(int attributeId) {
        final String query = "DELETE FROM rp_attribute WHERE id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, attributeId);

            stmt.executeUpdate();
            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error deleting attribute: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    @Override
    public Result<Long> insertAttribute(
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

    @Override
    public Result<Set<Attribute>> selectAttributes() {
        final String query =
            "SELECT " +
                "id, " +
                "created, " +
                "name, " +
                "display, " +
                "type, " +
                "default_value, " +
                "format, " +
                "identity," +
                "marker," +
                "title  " +
                "FROM rp_attribute";

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

    @Override
    public Result<Void> setIdentity(int attributeId) {
        Optional<String> err = clearIdentity().getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        final String query = "UPDATE rp_attribute SET identity = 1 WHERE id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, attributeId);
            stmt.executeUpdate();
            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error setting identity attribute: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    @Override
    public Result<Void> setMarker(int attributeId) {
        Optional<String> err = clearMarker().getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        final String query = "UPDATE rp_attribute SET marker = 1 WHERE id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, attributeId);
            stmt.executeUpdate();
            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error setting marker attribute: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    @Override
    public Result<Void> setTitle(int attributeId) {
        Optional<String> err = clearTitle().getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        final String query = "UPDATE rp_attribute SET title = 1 WHERE id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, attributeId);
            stmt.executeUpdate();
            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error setting title attribute: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private Result<Void> clearTitle() {
        final String query = "UPDATE rp_attribute SET title = 0";

        try (Statement stmt = db.getConnection().createStatement()) {
            stmt.executeUpdate(query);
            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error clearing title: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private Result<Void> clearMarker() {
        final String query = "UPDATE rp_attribute SET marker = 0";

        try (Statement stmt = db.getConnection().createStatement()) {
            stmt.executeUpdate(query);
            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error clearing marker: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private Result<Void> clearIdentity() {
        final String query = "UPDATE rp_attribute SET identity = 0";

        try (Statement stmt = db.getConnection().createStatement()) {
            stmt.executeUpdate(query);
            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error clearing identity: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private Result<Void> updateFormat(int attributeId, String formatString) {
        final String query = "UPDATE rp_attribute SET format = ? WHERE id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, formatString);
            stmt.setInt(2, attributeId);

            stmt.executeUpdate();

            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error updating attribute format: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private Result<Void> updateDisplay(int attributeId, String display) {
        final String query = "UPDATE rp_attribute SET display = ? WHERE id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, display);
            stmt.setInt(2, attributeId);

            stmt.executeUpdate();

            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error updating attribute display: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private Result<Void> updateDefault(int attributeId, String defaultValue) {
        final String query = "UPDATE rp_attribute SET default_value = ? WHERE id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setString(1, defaultValue);
            stmt.setInt(2, attributeId);

            stmt.executeUpdate();

            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error updating attribute default: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private Attribute readAttribute(ResultSet results) throws SQLException {
        Optional<AttributeType> t = AttributeType.findById(results.getString("type"));
        AttributeType type;
        if (!t.isPresent()) {
            log.warn("invalid attribute type, defaulting to string");
            type = AttributeType.String;
        } else {
            type = t.get();
        }

        String def = results.getString("default_value");
        Object defaultValue = null;

        if (def != null) {
            Result<Object> d = type.parse(results.getString("default_value"));

            Optional<String> err = d.getError();
            if (err.isPresent()) {
                log.warn(err.get());
            } else {
                defaultValue = d.get();
            }
        }

        return new DefaultAttribute(
            results.getInt("id"),
            Instant.parse(results.getString("created")),
            results.getString("name"),
            results.getString("display"),
            type,
            defaultValue,
            results.getString("format"),
            results.getInt("identity") > 0,
            results.getInt("marker") > 0,
            results.getInt("title") > 0
        );
    }
}
