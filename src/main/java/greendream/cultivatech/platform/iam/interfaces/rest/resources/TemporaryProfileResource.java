package greendream.cultivatech.platform.iam.interfaces.rest.resources;

import java.util.Set;

/**
 * Temporary HTTP profile payload returned by sign-up.
 */
public record TemporaryProfileResource(
        String fundoName,
        String contactPhone,
        double moistureThreshold,
        double tempThreshold
) {
}
