package greendream.cultivatech.platform.iam.application.commands;

/**
 * Command to authenticate an IAM user.
 *
 * @param email    raw email address supplied by the caller
 * @param password raw password supplied by the caller
 */
public record SignInCommand(String email, String password) {
}
