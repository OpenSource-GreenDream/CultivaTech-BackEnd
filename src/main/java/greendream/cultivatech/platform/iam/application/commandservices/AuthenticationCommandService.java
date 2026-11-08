package greendream.cultivatech.platform.iam.application.commandservices;

import greendream.cultivatech.platform.iam.application.commands.SignUpCommand;
import greendream.cultivatech.platform.iam.application.results.SignUpError;
import greendream.cultivatech.platform.iam.application.results.SignUpResult;
import greendream.cultivatech.platform.iam.application.results.TemporaryProfileResult;
import greendream.cultivatech.platform.iam.domain.model.aggregates.Role;
import greendream.cultivatech.platform.iam.domain.model.aggregates.User;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.EmailAddress;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.PasswordHash;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleName;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.iam.domain.ports.PasswordHashing;
import greendream.cultivatech.platform.iam.domain.repositories.RoleRepository;
import greendream.cultivatech.platform.iam.domain.repositories.UserRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Application service for authentication use cases.
 */
@Service
public class AuthenticationCommandService {

    private static final RoleName DEFAULT_ROLE_NAME = new RoleName("FARMER");
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 128;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHashing passwordHashing;

    public AuthenticationCommandService(UserRepository userRepository,
                                        RoleRepository roleRepository,
                                        PasswordHashing passwordHashing) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordHashing = passwordHashing;
    }

    /**
     * Registers a user, assigns the default FARMER role and returns the
     * temporary application-layer profile payload.
     *
     * <p>The user, its role assignment and the audit data are written in a single
     * transaction. Profile data is mocked locally because the Profile context does
     * not exist yet.</p>
     *
     * @param command sign-up command
     * @return successful sign-up result or an application error
     */
    @Transactional
    public Result<SignUpResult, SignUpError> signUp(SignUpCommand command) {
        if (command == null) {
            return Result.failure(new SignUpError("error.command.required"));
        }

        Result<EmailAddress, SignUpError> emailResult = parseEmail(command.email());
        if (emailResult.isFailure()) {
            return Result.failure(emailResult.failure().orElseThrow());
        }
        EmailAddress email = emailResult.success().orElseThrow();

        Result<String, SignUpError> passwordResult = validatePassword(command.password());
        if (passwordResult.isFailure()) {
            return Result.failure(passwordResult.failure().orElseThrow());
        }
        String plainPassword = passwordResult.success().orElseThrow();

        if (userRepository.existsByEmail(email)) {
            return Result.failure(new SignUpError("error.user.email.already-exists"));
        }

        Role defaultRole = roleRepository.findByName(DEFAULT_ROLE_NAME).orElse(null);
        if (defaultRole == null || defaultRole.id().isEmpty()) {
            return Result.failure(new SignUpError("error.role.default-not-found"));
        }

        PasswordHash passwordHash = passwordHashing.hash(plainPassword);
        User user = User.register(email, passwordHash);
        user.assignRole(defaultRole.id()
                .orElseThrow(() -> new IllegalStateException("error.role.id.not-assigned")));

        User savedUser = userRepository.save(user);
        Long userId = savedUser.id().map(UserId::value).orElse(null);
        if (userId == null) {
            return Result.failure(new SignUpError("error.user.id.not-assigned"));
        }

        // TODO: Replace hardcoded profile defaults with ProfileContext integration
        TemporaryProfileResult profile = new TemporaryProfileResult(
                "Fundo Principal (Default)",
                "+51000000000",
                30.0,
                25.0
        );

        return Result.success(new SignUpResult(
                userId,
                savedUser.email().value(),
                Set.of(defaultRole.name().value()),
                profile
        ));
    }

    private Result<EmailAddress, SignUpError> parseEmail(String email) {
        try {
            return Result.success(new EmailAddress(email));
        } catch (IllegalArgumentException exception) {
            return Result.failure(new SignUpError(exception.getMessage()));
        }
    }

    private Result<String, SignUpError> validatePassword(String password) {
        if (password == null || password.isBlank()) {
            return Result.failure(new SignUpError("error.user.password.required"));
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return Result.failure(new SignUpError("error.user.password.too-short"));
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            return Result.failure(new SignUpError("error.user.password.too-long"));
        }
        return Result.success(password);
    }
}
