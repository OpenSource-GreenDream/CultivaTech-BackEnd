package greendream.cultivatech.platform.iam.application.internal.queryservices;

import greendream.cultivatech.platform.iam.application.queries.GetAllUsersQuery;
import greendream.cultivatech.platform.iam.application.queries.GetUserByIdQuery;
import greendream.cultivatech.platform.iam.application.querieservices.UserQueryService;
import greendream.cultivatech.platform.iam.application.results.UserError;
import greendream.cultivatech.platform.iam.application.results.UserResult;
import greendream.cultivatech.platform.iam.domain.model.aggregates.Role;
import greendream.cultivatech.platform.iam.domain.model.aggregates.User;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleId;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleName;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.iam.domain.repositories.RoleRepository;
import greendream.cultivatech.platform.iam.domain.repositories.UserRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of UserQueryService.
 */
@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserQueryServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<UserResult>, UserError> getAllUsers(GetAllUsersQuery query) {
        if (query == null) {
            return Result.failure(new UserError("error.command.required"));
        }

        List<UserResult> users = userRepository.findAll().stream()
                .map(this::toResult)
                .toList();
        return Result.success(users);
    }

    @Override
    @Transactional(readOnly = true)
    public Result<UserResult, UserError> getUserById(GetUserByIdQuery query) {
        if (query == null) {
            return Result.failure(new UserError("error.command.required"));
        }

        UserId userId;
        try {
            userId = new UserId(query.userId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new UserError(exception.getMessage()));
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return Result.failure(new UserError("error.user.not-found"));
        }

        return Result.success(toResult(user.get()));
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
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }
}
