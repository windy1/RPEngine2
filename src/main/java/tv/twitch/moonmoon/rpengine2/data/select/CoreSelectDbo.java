package tv.twitch.moonmoon.rpengine2.data.select;

import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.model.select.Option;
import tv.twitch.moonmoon.rpengine2.model.select.OptionFactory;
import tv.twitch.moonmoon.rpengine2.model.select.Select;
import tv.twitch.moonmoon.rpengine2.model.select.impl.DefaultSelect;
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

public class CoreSelectDbo implements SelectDbo {

    private final RpDb db;
    private final AsyncExecutor asyncExecutor;
    private final OptionFactory optionFactory;
    private final Messenger log;

    @Inject
    public CoreSelectDbo(
        RpDb db,
        AsyncExecutor asyncExecutor,
        OptionFactory optionFactory,
        Messenger log
    ) {
        this.db = Objects.requireNonNull(db);
        this.asyncExecutor = Objects.requireNonNull(asyncExecutor);
        this.optionFactory = Objects.requireNonNull(optionFactory);
        this.log = Objects.requireNonNull(log);
    }

    @Override
    public void insertOptionAsync(
        int selectId,
        String name,
        String display,
        String color,
        Callback<Void> callback
    ) {
        asyncExecutor.execute(() ->
            callback.accept(insertOption(selectId, name, display, color))
        );
    }

    @Override
    public void insertSelectAsync(String name, Callback<Long> callback) {
        asyncExecutor.execute(() ->
            callback.accept(insertSelect(name))
        );
    }

    @Override
    public Result<Void> insertOption(int selectId, String name, String display, String color) {
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
            stmt.setString(5, color);

            stmt.executeUpdate();

            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error inserting option: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    @Override
    public void deleteSelectAsync(int selectId, Callback<Void> callback) {
        asyncExecutor.execute(() ->
            callback.accept(deleteSelect(selectId))
        );
    }

    @Override
    public void deleteOptionAsync(int optionId, Callback<Void> callback) {
        asyncExecutor.execute(() ->
            callback.accept(deleteOption(optionId))
        );
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public Result<Void> deleteOption(int optionId) {
        final String query = "DELETE FROM rp_select_option WHERE id = ?";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, optionId);
            stmt.executeUpdate();
            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error deleting option: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    @Override
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
            log.warn(err.get());
            options = new HashSet<>();
        } else {
            options = o.get();
        }

        return new DefaultSelect(
            selectId,
            Instant.parse(results.getString("created")),
            results.getString("name"),
            options
        );
    }

    @Override
    public Result<Set<Option>> selectOptions(int selectId) {
        final String query =
            "SELECT id, created, name, display, color FROM rp_select_option WHERE select_id = ?";

        Set<Option> options = new HashSet<>();

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, selectId);

            try (ResultSet results = stmt.executeQuery()) {
                while (results.next()) {
                    options.add(readOption(results, selectId));
                }

                return Result.ok(options);
            }
        } catch (SQLException e) {
            String message = "error reading options: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private Option readOption(ResultSet results, int selectId) throws SQLException {
        return optionFactory.newInstance(
            results.getInt("id"),
            selectId,
            Instant.parse(results.getString("created")),
            results.getString("name"),
            results.getString("display"),
            results.getString("color")
        );
    }
}
