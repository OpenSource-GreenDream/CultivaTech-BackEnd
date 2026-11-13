package greendream.cultivatech.platform.iam.application.commands;

/**
 * Command to delete an IAM user.
 *
 * @param userId identity of the user to delete
 */
public record DeleteUserCommand(Long userId) {
}
