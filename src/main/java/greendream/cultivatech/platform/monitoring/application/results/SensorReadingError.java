package greendream.cultivatech.platform.monitoring.application.results;

/**
 * Application-level error returned by the sensor reading use cases.
 *
 * @param messageKey i18n message key describing the failure
 */
public record SensorReadingError(String messageKey) {
}
