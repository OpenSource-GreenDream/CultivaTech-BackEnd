package greendream.cultivatech.platform.iam.application.commands;

/**
 * Command to register a new IAM user.
 *
 * @param email    raw email address supplied by the caller
 * @param password raw password supplied by the caller
 */
public record SignUpCommand(String email, String password) {
}
