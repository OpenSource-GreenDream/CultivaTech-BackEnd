package greendream.cultivatech.platform.monitoring.application.results;

/**
 * Application-level error returned by the device use cases.
 *
 * @param messageKey i18n message key describing the failure
 */
public record DeviceError(String messageKey) {
}
