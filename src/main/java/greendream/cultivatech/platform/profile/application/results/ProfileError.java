package greendream.cultivatech.platform.profile.application.results;

/**
 * Application-level error returned by the profile use cases.
 *
 * @param messageKey i18n message key describing the failure
 */
public record ProfileError(String messageKey) {
}
