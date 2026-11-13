package greendream.cultivatech.platform.iam.application.internal.commandservices;

import greendream.cultivatech.platform.iam.application.commands.ProfileRegistrationCommand;
import greendream.cultivatech.platform.iam.application.commands.SignInCommand;
import greendream.cultivatech.platform.iam.application.commands.SignUpCommand;
import greendream.cultivatech.platform.iam.application.commandservices.AuthenticationCommandService;
import greendream.cultivatech.platform.iam.application.ports.ProfileRegistration;
import greendream.cultivatech.platform.iam.application.ports.TokenIssuer;
import greendream.cultivatech.platform.iam.application.results.ProfileRegistrationError;
import greendream.cultivatech.platform.iam.application.results.ProfileRegistrationResult;
import greendream.cultivatech.platform.iam.application.results.SignInError;
import greendream.cultivatech.platform.iam.application.results.SignInResult;
import greendream.cultivatech.platform.iam.application.results.SignUpError;
import greendream.cultivatech.platform.iam.application.results.SignUpResult;
import greendream.cultivatech.platform.iam.domain.model.aggregates.Role;
import greendream.cultivatech.platform.iam.domain.model.aggregates.User;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.EmailAddress;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.PasswordHash;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleId;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleName;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.iam.domain.ports.PasswordHashing;
import greendream.cultivatech.platform.iam.domain.repositories.RoleRepository;
import greendream.cultivatech.platform.iam.domain.repositories.UserRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of AuthenticationCommandService.
 */
@Service
public class AuthenticationCommandServiceImpl implements AuthenticationCommandService {

    private static final RoleName DEFAULT_ROLE_NAME = new RoleName("FARMER");
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 128;
    private static final String DEFAULT_FUNDO_NAME = "Fundo Principal (Default)";
    private static final String DEFAULT_CONTACT_PHONE = "+51000000000";
    private static final double DEFAULT_MOISTURE_THRESHOLD = 30.0;
    private static final double DEFAULT_TEMPERATURE_THRESHOLD = 25.0;

    private static final String INVALID_CREDENTIALS = "error.authentication.invalid-credentials";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHashing passwordHashing;
    private final ProfileRegistration profileRegistration;
    private final TokenIssuer tokenIssuer;

    public AuthenticationCommandServiceImpl(UserRepository userRepository,
                                            RoleRepository roleRepository,
                                            PasswordHashing passwordHashing,
                                            ProfileRegistration profileRegistration,
                                            TokenIssuer tokenIssuer) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordHashing = passwordHashing;
        this.profileRegistration = profileRegistration;
        this.tokenIssuer = tokenIssuer;
    }

    @Override
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

        Result<ProfileRegistrationResult, ProfileRegistrationError> profileResult =
                profileRegistration.register(new ProfileRegistrationCommand(
                        userId,
                        DEFAULT_FUNDO_NAME,
                        DEFAULT_CONTACT_PHONE,
                        DEFAULT_MOISTURE_THRESHOLD,
                        DEFAULT_TEMPERATURE_THRESHOLD
                ));

        if (profileResult.isFailure()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.failure(new SignUpError(profileResult.failure().orElseThrow().messageKey()));
        }

        return Result.success(new SignUpResult(
                userId,
                savedUser.email().value(),
                Set.of(defaultRole.name().value()),
                profileResult.success().orElseThrow()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public Result<SignInResult, SignInError> signIn(SignInCommand command) {
        if (command == null) {
            return Result.failure(new SignInError("error.command.required"));
        }

        EmailAddress email;
        try {
            email = new EmailAddress(command.email());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new SignInError(exception.getMessage()));
        }

        if (command.password() == null || command.password().isBlank()) {
            return Result.failure(new SignInError("error.user.password.required"));
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null
                || !user.isActive()
                || !passwordHashing.matches(command.password(), user.passwordHash())) {
            return Result.failure(new SignInError(INVALID_CREDENTIALS));
        }

        UserId userId = user.id().orElse(null);
        if (userId == null) {
            return Result.failure(new SignInError("error.user.id.not-assigned"));
        }

        Set<String> roles = resolveRoleNames(user.roleIds());
        TokenIssuer.IssuedToken issuedToken = tokenIssuer.issue(userId.value(), user.email().value(), roles);

        return Result.success(new SignInResult(
                issuedToken.accessToken(),
                BEARER_TOKEN_TYPE,
                issuedToken.expiresInSeconds(),
                userId.value(),
                user.email().value(),
                roles
        ));
    }

    private Set<String> resolveRoleNames(Set<RoleId> roleIds) {
        return roleIds.stream()
                .map(roleRepository::findById)
                .flatMap(java.util.Optional::stream)
                .map(Role::name)
                .map(RoleName::value)
                .collect(Collectors.toCollection(LinkedHashSet::new));
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
