package greendream.cultivatech.platform.iam.application.results;

/**
 * Temporary application-layer profile payload used by sign-up.
 *
 * This is intentionally not a domain type. The Profile bounded context will
 * replace it when it is implemented.
 */
public record TemporaryProfileResult(
        String fundoName,
        String contactPhone,
        double moistureThreshold,
        double tempThreshold
) {
}
