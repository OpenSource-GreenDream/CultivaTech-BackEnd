package greendream.cultivatech.platform.iam.application.commands;

/**
 * Command to update an IAM role. The role name is immutable by design; only the
 * description can change.
 *
 * @param roleId      identity of the role to update
 * @param description new description; blank clears it
 */
public record UpdateRoleCommand(Long roleId, String description) {
}
