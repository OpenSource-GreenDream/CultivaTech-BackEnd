package greendream.cultivatech.platform.iam.application.results;

import java.util.Set;

/**
 * Application-level representation of an IAM user.
 *
 * @param id     persisted user identity
 * @param email  normalized email address
 * @param roles  assigned role names
 * @param active whether the account is active
 */
public record UserResult(Long id, String email, Set<String> roles, boolean active) {

    public UserResult {
        roles = Set.copyOf(roles);
    }
}
