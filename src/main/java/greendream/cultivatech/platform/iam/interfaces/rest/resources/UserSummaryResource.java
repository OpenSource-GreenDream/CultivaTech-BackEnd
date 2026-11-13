package greendream.cultivatech.platform.iam.interfaces.rest.resources;

import java.util.Set;

/**
 * HTTP response summarizing an IAM user.
 */
public record UserSummaryResource(
        Long id,
        String email,
        Set<String> roles,
        boolean active
) {
}
