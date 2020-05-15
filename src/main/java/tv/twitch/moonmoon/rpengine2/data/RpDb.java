package tv.twitch.moonmoon.rpengine2.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.di.DbPath;
import tv.twitch.moonmoon.rpengine2.util.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Optional;
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

    public Connection getConnection() {
        return Optional.ofNullable(conn)
            .orElseThrow(() -> new IllegalStateException("not connected"));
    }

    public void connectAsync(Consumer<Result<Void>> callback) {
        createFiles(callback);

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

    private void createFiles(Consumer<Result<Void>> callback) {
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
}
