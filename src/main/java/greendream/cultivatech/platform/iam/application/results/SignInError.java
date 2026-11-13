package greendream.cultivatech.platform.iam.application.results;

/**
 * Application-level error returned by the sign-in use case.
 *
 * @param messageKey i18n message key describing the failure
 */
public record SignInError(String messageKey) {
}
