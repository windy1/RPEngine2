package tv.twitch.moonmoon.rpengine2.data;

import tv.twitch.moonmoon.rpengine2.util.Result;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Migrations {

    private final RpDb db;

    @Inject
    public Migrations(RpDb db) {
        this.db = Objects.requireNonNull(db);
    }

    public Result<Void> migrate() {
        Result<Queue<Migration>> m = load();

        Optional<String> err = m.getError();
        if (err.isPresent()) {
            return Result.error(err.get());
        }

        for (Migration migration : m.get()) {
            err = migrate(migration.query).getError();
            if (err.isPresent()) {
                return Result.error(err.get());
            }
        }

        return Result.ok(null);
    }

    private Result<Void> migrate(String query) {
        try (Statement stmt = db.getConnection().createStatement()) {
            stmt.executeUpdate(query);
            return Result.ok(null);
        } catch (SQLException e) {
            String message = "error updating database schema: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    private Result<Queue<Migration>> load() {
        try {
            URI uri = Migrations.class.getResource("/migrations").toURI();
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());

            return Result.ok(Files
                .list(fileSystem.getPath("/migrations"))
                .map(this::readMigration)
                .collect(Collectors.toCollection(PriorityQueue::new))
            );
        } catch (URISyntaxException | IOException e) {
            String message = "error loading migrations: `%s`";
            return Result.error(String.format(message, e.getMessage()));
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private Migration readMigration(Path path) {
        String fileName = path.getFileName().toString();
        String baseName = com.google.common.io.Files.getNameWithoutExtension(fileName);
        int sep = baseName.indexOf("_");
        int priority;

        Supplier<IllegalArgumentException> err = () ->
            new IllegalArgumentException(String.format("invalid migration file: `%s`", path));

        if (sep == -1 || sep == baseName.length() - 1) {
            throw err.get();
        }

        try {
            priority = Integer.parseInt(baseName.substring(sep + 1));
        } catch (NumberFormatException e) {
            throw err.get();
        }

        try {
            return new Migration(priority, new String(Files.readAllBytes(path)));
        } catch (IOException e) {
            throw new RuntimeException("failed to read migration file", e);
        }
    }

    static class Migration implements Comparable<Migration> {

        final int priority;
        final String query;

        Migration(int priority, String query) {
            this.priority = priority;
            this.query = Objects.requireNonNull(query);
        }

        @Override
        public int compareTo(Migration migration) {
            return Integer.compare(priority, migration.priority);
        }
    }
}
