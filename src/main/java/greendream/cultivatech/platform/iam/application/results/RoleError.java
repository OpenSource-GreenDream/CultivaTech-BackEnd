package greendream.cultivatech.platform.iam.application.results;

/**
 * Application-level error returned by role use cases.
 *
 * @param messageKey i18n message key describing the failure
 */
public record RoleError(String messageKey) {
}
