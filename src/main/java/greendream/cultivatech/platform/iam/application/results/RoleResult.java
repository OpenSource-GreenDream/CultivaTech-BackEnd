package greendream.cultivatech.platform.iam.application.results;

/**
 * Application-level representation of an IAM role.
 *
 * @param id          persisted role identity
 * @param name        immutable role name
 * @param description editable role description
 */
public record RoleResult(Long id, String name, String description) {
}
