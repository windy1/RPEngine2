package tv.twitch.moonmoon.rpengine2.data;

import org.bukkit.ChatColor;
import tv.twitch.moonmoon.rpengine2.data.attribute.AttributeRepo;
import tv.twitch.moonmoon.rpengine2.data.select.SelectRepo;
import tv.twitch.moonmoon.rpengine2.model.attribute.AttributeType;

import javax.inject.Inject;
import java.util.Objects;

public class Defaults {

    private final AttributeRepo attributeRepo;
    private final SelectRepo selectRepo;

    @Inject
    public Defaults(AttributeRepo attributeRepo, SelectRepo selectRepo) {
        this.attributeRepo = Objects.requireNonNull(attributeRepo);
        this.selectRepo = Objects.requireNonNull(selectRepo);
    }

    public void saveDefaults() {
        if (selectRepo.getSelects().isEmpty()) {
            selectRepo.createSelect("Caste");

            selectRepo.createOption(
                "Caste", "Serf", "Serf", ChatColor.GRAY
            );
            selectRepo.createOption(
                "Caste", "Brother", "Brother", ChatColor.GRAY
            );
            selectRepo.createOption(
                "Caste", "Acolyte", "Acolyte", ChatColor.GREEN
            );
        }

        if (attributeRepo.getAttributes().isEmpty()) {
            attributeRepo.createAttribute(
                "Caste", AttributeType.Select, "Caste", "Serf", null
            );

            attributeRepo.createAttribute(
                "Name", AttributeType.String, "Name", null, null
            );
            attributeRepo.createAttribute(
                "Age", AttributeType.Number, "Age", null, "%d"
            );
            attributeRepo.createAttribute(
                "Gender", AttributeType.String, "Gender", null, null
            );
            attributeRepo.createAttribute(
                "Description",
                AttributeType.String,
                "Description",
                null, null
            );
        }
    }
}
