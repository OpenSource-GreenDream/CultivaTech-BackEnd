package greendream.cultivatech.platform.profile.application.results;

/**
 * Application-level error returned by the field use cases.
 *
 * @param messageKey i18n message key describing the failure
 */
public record FieldError(String messageKey) {
}
