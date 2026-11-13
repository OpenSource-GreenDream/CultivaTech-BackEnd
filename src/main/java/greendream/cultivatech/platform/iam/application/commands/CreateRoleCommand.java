package greendream.cultivatech.platform.iam.application.commands;

/**
 * Command to create an IAM role.
 *
 * @param name        role name; normalized and validated by the domain
 * @param description optional human-readable description
 */
public record CreateRoleCommand(String name, String description) {
}
