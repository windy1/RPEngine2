package tv.twitch.moonmoon.rpengine2.spigot.data;

import org.bukkit.ChatColor;
import tv.twitch.moonmoon.rpengine2.data.Defaults;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeType;
import tv.twitch.moonmoon.rpengine2.spigot.data.select.SpigotSelectRepo;

import javax.inject.Inject;
import java.util.Objects;

public class SpigotDefaults implements Defaults {

    private final AttributeRepo attributeRepo;
    private final SpigotSelectRepo selectRepo;

    @Inject
    public SpigotDefaults(AttributeRepo attributeRepo, SelectRepo selectRepo) {
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        this.selectRepo = (SpigotSelectRepo) Objects.requireNonNull(selectRepo);
    }

    @Override
    public void saveDefaults() {
        if (selectRepo.getSelects().isEmpty()) {
            selectRepo.createSelect("Caste");

            selectRepo.createOption(
                "Caste", "Serf", "Serf", ChatColor.GRAY
            );
            selectRepo.createOption(
                "Caste", "Brother", "Brother", ChatColor.BLUE
            );
            selectRepo.createOption(
                "Caste", "Acolyte", "Acolyte", ChatColor.GREEN
            );
        }

        if (attributeRepo.getAttributes().isEmpty()) {
            attributeRepo.createAttribute(
                "Caste", AttributeType.Select, "Caste", "Serf"
            );

            attributeRepo.setMarker("Caste");

            attributeRepo.createAttribute(
                "Name", AttributeType.String, "Name", null
            );

            attributeRepo.setIdentity("Name");

            attributeRepo.createAttribute(
                "Age", AttributeType.Number, "Age", null
            );
            attributeRepo.createAttribute(
                "Gender", AttributeType.String, "Gender", null
            );
            attributeRepo.createAttribute(
                "Description", AttributeType.String, "Description", null
            );

            attributeRepo.createAttribute(
                "Job", AttributeType.String, "Job", null
            );

            attributeRepo.setTitle("Job");
        }
    }
}
