package greendream.cultivatech.platform.iam.application.internal.commandservices;

import greendream.cultivatech.platform.iam.application.commands.DeleteUserCommand;
import greendream.cultivatech.platform.iam.application.commands.UpdateUserCommand;
import greendream.cultivatech.platform.iam.application.commandservices.UserCommandService;
import greendream.cultivatech.platform.iam.application.results.UserError;
import greendream.cultivatech.platform.iam.application.results.UserResult;
import greendream.cultivatech.platform.iam.domain.model.aggregates.Role;
import greendream.cultivatech.platform.iam.domain.model.aggregates.User;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.EmailAddress;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleId;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleName;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.iam.domain.ports.PasswordHashing;
import greendream.cultivatech.platform.iam.domain.repositories.RoleRepository;
import greendream.cultivatech.platform.iam.domain.repositories.UserRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of UserCommandService.
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 128;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHashing passwordHashing;

    public UserCommandServiceImpl(UserRepository userRepository,
                                  RoleRepository roleRepository,
                                  PasswordHashing passwordHashing) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordHashing = passwordHashing;
    }

    @Override
    @Transactional
    public Result<UserResult, UserError> update(UpdateUserCommand command) {
        if (command == null) {
            return Result.failure(new UserError("error.command.required"));
        }

        UserId userId;
        try {
            userId = new UserId(command.userId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new UserError(exception.getMessage()));
        }

        EmailAddress email;
        try {
            email = new EmailAddress(command.email());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new UserError(exception.getMessage()));
        }

        String password = command.password();
        if (password != null && !password.isBlank()) {
            if (password.length() < MIN_PASSWORD_LENGTH) {
                return Result.failure(new UserError("error.user.password.too-short"));
            }
            if (password.length() > MAX_PASSWORD_LENGTH) {
                return Result.failure(new UserError("error.user.password.too-long"));
            }
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Result.failure(new UserError("error.user.not-found"));
        }

        if (!email.equals(user.email()) && userRepository.existsByEmail(email)) {
            return Result.failure(new UserError("error.user.email.already-exists"));
        }

        Set<RoleId> requestedRoles = null;
        if (command.roleIds() != null) {
            requestedRoles = new LinkedHashSet<>();
            for (Long roleId : command.roleIds()) {
                RoleId parsedRoleId;
                try {
                    parsedRoleId = new RoleId(roleId);
                } catch (IllegalArgumentException exception) {
                    return Result.failure(new UserError(exception.getMessage()));
                }
                if (roleRepository.findById(parsedRoleId).isEmpty()) {
                    return Result.failure(new UserError("error.role.not-found"));
                }
                requestedRoles.add(parsedRoleId);
            }
        }

        user.changeEmail(email);
        if (password != null && !password.isBlank()) {
            user.changePassword(passwordHashing.hash(password));
        }
        if (requestedRoles != null) {
            Set<RoleId> currentRoles = new LinkedHashSet<>(user.roleIds());
            for (RoleId roleId : currentRoles) {
                if (!requestedRoles.contains(roleId)) {
                    user.revokeRole(roleId);
                }
            }
            requestedRoles.forEach(user::assignRole);
        }

        User savedUser = userRepository.update(user);
        return Result.success(toResult(savedUser));
    }

    @Override
    @Transactional
    public Result<Void, UserError> delete(DeleteUserCommand command) {
        if (command == null) {
            return Result.failure(new UserError("error.command.required"));
        }

        UserId userId;
        try {
            userId = new UserId(command.userId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new UserError(exception.getMessage()));
        }

        if (userRepository.findById(userId).isEmpty()) {
            return Result.failure(new UserError("error.user.not-found"));
        }

        try {
            userRepository.deleteById(userId);
        } catch (IllegalArgumentException exception) {
            return Result.failure(new UserError(exception.getMessage()));
        }

        return Result.success(null);
    }

    private UserResult toResult(User user) {
        Long id = user.id()
                .map(UserId::value)
                .orElseThrow(() -> new IllegalArgumentException("error.user.id.not-assigned"));
        return new UserResult(id, user.email().value(), resolveRoleNames(user.roleIds()), user.isActive());
    }

    private Set<String> resolveRoleNames(Set<RoleId> roleIds) {
        return roleIds.stream()
                .map(roleRepository::findById)
                .flatMap(Optional::stream)
                .map(Role::name)
                .map(RoleName::value)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
