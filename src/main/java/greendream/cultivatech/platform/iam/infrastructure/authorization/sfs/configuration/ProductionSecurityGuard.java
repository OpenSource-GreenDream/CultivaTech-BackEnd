package greendream.cultivatech.platform.iam.infrastructure.authorization.sfs.configuration;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Fails fast when the application starts with the {@code prod} profile.
 *
 * <p>The current iteration deliberately leaves non-IAM endpoints open and applies RBAC
 * only to the IAM administration reads. That posture is appropriate for development and
 * academic demonstrations but not for a public production deployment, so the production
 * profile is blocked until a complete authorization matrix is implemented.</p>
 */
@Component
@Profile("prod")
public class ProductionSecurityGuard implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        throw new IllegalStateException(
                "The partial RBAC configuration is not production-safe. "
                        + "Implement authorization for every bounded context before starting with the 'prod' profile.");
    }
}
