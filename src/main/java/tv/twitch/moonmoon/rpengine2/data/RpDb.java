package tv.twitch.moonmoon.rpengine2.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tv.twitch.moonmoon.rpengine2.di.DbPath;
import tv.twitch.moonmoon.rpengine2.di.PluginLogger;
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
import java.util.logging.Logger;

@Singleton
public class RpDb {

    private final Path path;
    private final Logger log;

    private Connection conn;

    @Inject
    public RpDb(@DbPath Path path, @PluginLogger Logger log) {
        this.path = Objects.requireNonNull(path);
        this.log = log;
    }

    public Connection getConnection() {
        return Optional.ofNullable(conn)
            .orElseThrow(() -> new IllegalStateException("not connected"));
    }

    public Result<Void> connect() {
        return createFiles().getError()
            .<Result<Void>>map(Result::error)
            .orElseGet(() -> _connect().getError()
                .<Result<Void>>map(Result::error)
                .orElseGet(() -> Result.ok(null))
            );
    }

    private Result<Void> _connect() {
        String url = "jdbc:sqlite:" + path.toString();

        try {
            conn = DriverManager.getConnection(url);
            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error connecting to database: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private Result<Void> createFiles() {
        if (Files.exists(path)) {
            return Result.ok(null);
        }

        try {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
            log.info(String.format("Created empty database file at `%s`", path));
            return Result.ok(null);
        } catch (IOException e) {
            String message = "error creating database file: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }
}
