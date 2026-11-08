package greendream.cultivatech.platform.shared.infrastructure.persistence.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA auditing for auditable persistence entities.
 */
@Configuration
@EnableJpaAuditing
@Profile("!webmvc")
public class JpaAuditingConfiguration {
}
