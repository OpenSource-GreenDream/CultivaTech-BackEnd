package greendream.cultivatech.platform.iam.infrastructure.seeding;

import greendream.cultivatech.platform.iam.domain.model.aggregates.Role;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleName;
import greendream.cultivatech.platform.iam.domain.repositories.RoleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Idempotently seeds the IAM roles required at startup.
 */
@Component
public class RoleSeeder implements ApplicationRunner {

    private static final List<RoleSeed> SEEDS = List.of(
            new RoleSeed("ADMIN", "Platform administrator"),
            new RoleSeed("FARMER", "Default farmer role")
    );

    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (RoleSeed seed : SEEDS) {
            RoleName roleName = new RoleName(seed.name());
            if (!roleRepository.existsByName(roleName)) {
                roleRepository.save(Role.create(roleName, seed.description()));
            }
        }
    }

    private record RoleSeed(String name, String description) {
    }
}
