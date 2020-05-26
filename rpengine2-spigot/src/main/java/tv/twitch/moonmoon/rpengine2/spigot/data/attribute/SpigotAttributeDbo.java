package tv.twitch.moonmoon.rpengine2.spigot.data.attribute;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import tv.twitch.moonmoon.rpengine2.data.RpDb;
import tv.twitch.moonmoon.rpengine2.data.attribute.AbstractAttributeDbo;
import tv.twitch.moonmoon.rpengine2.util.Callback;

import javax.inject.Inject;
import java.util.Objects;

public class SpigotAttributeDbo extends AbstractAttributeDbo {

    private final Plugin plugin;

    @Inject
    public SpigotAttributeDbo(Plugin plugin, RpDb db) {
        super(db, plugin.getLogger());
        this.plugin = Objects.requireNonNull(plugin);
    }

    @Override
    public void insertAttributeAsync(
            String name,
            String display,
            String type,
            String defaultValue,
            Callback<Long> callback
    ) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(insertAttribute(name, display, type, defaultValue))
        );
    }

    @Override
    public void updateDefaultAsync(int attributeId, String defaultValue, Callback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(updateDefault(attributeId, defaultValue))
        );
    }

    @Override
    public void updateDisplayAsync(int attributeId, String display, Callback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(updateDisplay(attributeId, display))
        );
    }

    @Override
    public void updateFormatAsync(int attributeId, String formatString, Callback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(updateFormat(attributeId, formatString))
        );
    }

    @Override
    public void setIdentityAsync(int attributeId, Callback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(setIdentity(attributeId))
        );
    }

    @Override
    public void clearIdentityAsync(Callback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(clearIdentity())
        );
    }

    @Override
    public void setMarkerAsync(int attributeId, Callback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(setMarker(attributeId))
        );
    }

    @Override
    public void clearMarkerAsync(Callback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> callback.accept(clearMarker()));
    }

    @Override
    public void setTitleAsync(int attributeId, Callback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
            callback.accept(setTitle(attributeId))
        );
    }

    @Override
    public void clearTitleAsync(Callback<Void> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> callback.accept(clearTitle()));
    }
}
