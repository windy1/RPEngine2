package tv.twitch.moonmoon.rpengine2.sponge;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import tv.twitch.moonmoon.rpengine2.Config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class SpongeConfig implements Config {

    private final ConfigurationNode root;

    private SpongeConfig(ConfigurationNode root) {
        this.root = Objects.requireNonNull(root);
    }

    public static SpongeConfig load(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            InputStream defaultConfig = SpongeConfig.class.getResourceAsStream("/config.yml");
            Files.copy(defaultConfig, path, StandardCopyOption.REPLACE_EXISTING);
        }

        return new SpongeConfig(YAMLConfigurationLoader.builder().setPath(path).build().load());
    }

    public ConfigurationNode getRoot() {
        return root;
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return getNode(path).getBoolean(def);
    }

    @Override
    public int getInt(String path, int def) {
        return getNode(path).getInt(def);
    }

    @Override
    public String getString(String path, String def) {
        return getNode(path).getString(def);
    }

    @Override
    public double getDouble(String path, double def) {
        return getNode(path).getDouble(def);
    }

    private ConfigurationNode getNode(String path) {
        Object[] args = path.split("\\.");
        return getRoot().getNode(args);
    }
}
