package tv.twitch.moonmoon.rpengine2.data;

import java.util.ArrayList;
import java.util.List;

public final class Migrations {
    static final List<String> MIGRATIONS = new ArrayList<>();

    static {
        MIGRATIONS.add(
            "CREATE TABLE IF NOT EXISTS rp_player (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "created TEXT NOT NULL, " +
                "username VARCHAR(255) NOT NULL UNIQUE, " +
                "uuid VARCHAR(255) NOT NULL UNIQUE" +
            "); " +

            "CREATE TABLE IF NOT EXISTS rp_group (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "created TEXT NOT NULL, " +
                "name VARCHAR(255) NOT NULL UNIQUE" +
            "); " +

            "CREATE TABLE IF NOT EXISTS rp_attribute (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "created TEXT NOT NULL, " +
                "name VARCHAR(255) NOT NULL UNIQUE, " +
                "display VARCHAR(255) NOT NULL, " +
                "type VARCHAR(255) NOT NULL, " +
                "default_value VARCHAR(255)" +
            ");"
        );

        MIGRATIONS.add(
            "CREATE TABLE IF NOT EXISTS rp_player_group (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "created TEXT NOT NULL, " +
                "player_id INTEGER NOT NULL, " +
                "group_id INTEGER NOT NULL, " +
                "FOREIGN KEY (player_id) REFERENCES rp_player, " +
                "FOREIGN KEY (group_id) REFERENCES rp_group, " +
                "UNIQUE (player_id, group_id) ON CONFLICT IGNORE" +
            "); " +

            "CREATE TABLE IF NOT EXISTS rp_player_attribute (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "created TEXT NOT NULL, " +
                "player_id INTEGER NOT NULL, " +
                "attribute_id INTEGER NOT NULL, " +
                "value VARCHAR(255), " +
                "FOREIGN KEY (player_id) REFERENCES rp_player, " +
                "FOREIGN KEY (attribute_id) REFERENCES rp_attribute, " +
                "UNIQUE (player_id, attribute_id) ON CONFLICT IGNORE" +
            ");"
        );
    }
}
