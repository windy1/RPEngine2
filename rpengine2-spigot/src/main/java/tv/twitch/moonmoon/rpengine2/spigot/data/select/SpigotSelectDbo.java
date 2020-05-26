package tv.twitch.moonmoon.rpengine2.spigot.data.select;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.data.select.AbstractSelectDbo;
import tv.twitch.moonmoon.rpengine2.model.select.Option;
import tv.twitch.moonmoon.rpengine2.model.select.Select;
import tv.twitch.moonmoon.rpengine2.spigot.model.select.SpigotOption;
import tv.twitch.moonmoon.rpengine2.util.Callback;
import tv.twitch.moonmoon.rpengine2.util.Result;
import tv.twitch.moonmoon.rpengine2.spigot.util.SpigotUtils;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public class SpigotSelectDbo extends AbstractSelectDbo {

    private final Plugin plugin;

    @Inject
    public SpigotSelectDbo(Plugin plugin, RpDb db) {
        super(db, plugin.getLogger());
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public void insertSelectAsync(String name, Callback<Long> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(insertSelect(name))
        );
    }

    public void insertOptionAsync(
        int selectId,
        String name,
        String display,
        ChatColor color,
        Callback<Void> callback
    ) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(insertOption(selectId, name, display, color))
        );
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

    @Override
    public void deleteSelectAsync(int selectId, Callback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(deleteSelect(selectId))
        );
    }

    @Override
    public void deleteOptionAsync(int optionId, Callback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(deleteOption(optionId))
        );
    }

    @Override
    protected Option readOption(ResultSet results, int selectId) throws SQLException {
        return new SpigotOption(
            results.getInt("id"),
            selectId,
            Instant.parse(results.getString("created")),
            results.getString("name"),
            results.getString("display"),
            SpigotUtils.getChatColor(results.getString("color")).orElse(null)
        );
    }
}
