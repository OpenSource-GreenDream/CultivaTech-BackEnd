package greendream.cultivatech.platform.iam.application.results;

import java.util.Set;

/**
 * Result of a successful sign-up.
 *
 * @param userId  persisted IAM user identity
 * @param email   normalized email address
 * @param roles   assigned role names
 * @param profile registered profile payload
 */
public record SignUpResult(
        Long userId,
        String email,
        Set<String> roles,
        ProfileRegistrationResult profile
) {

    public SignUpResult {
        roles = Set.copyOf(roles);
    }
}
