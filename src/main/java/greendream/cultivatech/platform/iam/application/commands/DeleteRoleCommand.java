package greendream.cultivatech.platform.iam.application.commands;

/**
 * Command to delete an IAM role.
 *
 * @param roleId identity of the role to delete
 */
public record DeleteRoleCommand(Long roleId) {
}
