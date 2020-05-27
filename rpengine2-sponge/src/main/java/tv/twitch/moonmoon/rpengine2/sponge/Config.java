package tv.twitch.moonmoon.rpengine2.sponge;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class Config {

    private final ConfigurationNode root;

    private Config(ConfigurationNode root) {
        this.root = Objects.requireNonNull(root);
    }

    public static Config load(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            InputStream defaultConfig = Config.class.getResourceAsStream("/config.yml");
            System.out.println("path " + path);
            System.out.println("defaultConfig " + defaultConfig);
            Files.copy(defaultConfig, path, StandardCopyOption.REPLACE_EXISTING);
        }

        return new Config(YAMLConfigurationLoader.builder().setPath(path).build().load());
    }

    public ConfigurationNode getRoot() {
        return root;
    }
}
