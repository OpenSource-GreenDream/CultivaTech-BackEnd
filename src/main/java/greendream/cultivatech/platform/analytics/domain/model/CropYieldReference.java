package greendream.cultivatech.platform.analytics.domain.model;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public record CropYieldReference(
        String cropIdentifier,
        String spanishName,
        double referenceYieldTonsPerHectare,
        String measurementUnit,
        int referenceYear,
        String geographicScope,
        String source
) {
    private static final Map<String, CropYieldReference> CATALOG = Map.of(
            "potato", new CropYieldReference("POTATO", "Papa", 19.5, "t/ha", 2024, "Peru - National average", "MIDAGRI"),
            "papa", new CropYieldReference("POTATO", "Papa", 19.5, "t/ha", 2024, "Peru - National average", "MIDAGRI"),
            "patata", new CropYieldReference("POTATO", "Papa", 19.5, "t/ha", 2024, "Peru - National average", "MIDAGRI")
    );

    public static Optional<CropYieldReference> find(String cropType) {
        if (cropType == null || cropType.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(CATALOG.get(cropType.trim().toLowerCase(Locale.ROOT)));
    }
}
