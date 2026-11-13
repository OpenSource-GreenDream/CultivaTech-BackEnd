package greendream.cultivatech.platform.iam.application.commands;

import java.util.Set;

/**
 * Command to update the editable data of an IAM user.
 *
 * @param userId   identity of the user to update
 * @param email    new email address; required
 * @param password new raw password; blank or null means unchanged
 * @param roleIds  complete set of role ids; null means unchanged
 */
public record UpdateUserCommand(Long userId, String email, String password, Set<Long> roleIds) {
}
