package greendream.cultivatech.platform.iam.application.results;

/**
 * Application-level error returned by user use cases.
 *
 * @param messageKey i18n message key describing the failure
 */
public record UserError(String messageKey) {
}
