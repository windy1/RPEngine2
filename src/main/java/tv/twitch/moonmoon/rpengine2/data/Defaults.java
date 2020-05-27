package tv.twitch.moonmoon.rpengine2.data;

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
                "Caste", "Serf", "Serf", "GRAY"
            );
            selectRepo.createOption(
                "Caste", "Brother", "Brother", "BLUE"
            );
            selectRepo.createOption(
                "Caste", "Acolyte", "Acolyte", "GREEN"
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
