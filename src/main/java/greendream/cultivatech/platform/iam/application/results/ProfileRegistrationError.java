package greendream.cultivatech.platform.iam.application.results;

/**
 * Application-level error returned by the profile registration port.
 *
 * @param messageKey i18n message key describing the failure
 */
public record ProfileRegistrationError(String messageKey) {
}
