package greendream.cultivatech.platform.iam.application.internal.commandservices;

import greendream.cultivatech.platform.iam.application.commands.CreateRoleCommand;
import greendream.cultivatech.platform.iam.application.commands.DeleteRoleCommand;
import greendream.cultivatech.platform.iam.application.commands.UpdateRoleCommand;
import greendream.cultivatech.platform.iam.application.commandservices.RoleCommandService;
import greendream.cultivatech.platform.iam.application.results.RoleError;
import greendream.cultivatech.platform.iam.application.results.RoleResult;
import greendream.cultivatech.platform.iam.domain.model.aggregates.Role;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleId;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleName;
import greendream.cultivatech.platform.iam.domain.repositories.RoleRepository;
import greendream.cultivatech.platform.iam.domain.repositories.UserRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of RoleCommandService.
 */
@Service
public class RoleCommandServiceImpl implements RoleCommandService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleCommandServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Result<RoleResult, RoleError> create(CreateRoleCommand command) {
        if (command == null) {
            return Result.failure(new RoleError("error.command.required"));
        }

        RoleName name;
        try {
            name = new RoleName(command.name());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new RoleError(exception.getMessage()));
        }

        if (roleRepository.existsByName(name)) {
            return Result.failure(new RoleError("error.role.name.already-exists"));
        }

        Role savedRole = roleRepository.save(Role.create(name, command.description()));
        return Result.success(toResult(savedRole));
    }

    @Override
    @Transactional
    public Result<RoleResult, RoleError> update(UpdateRoleCommand command) {
        if (command == null) {
            return Result.failure(new RoleError("error.command.required"));
        }

        RoleId roleId;
        try {
            roleId = new RoleId(command.roleId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new RoleError(exception.getMessage()));
        }

        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            return Result.failure(new RoleError("error.role.not-found"));
        }

        role.updateDescription(command.description());
        Role savedRole = roleRepository.update(role);
        return Result.success(toResult(savedRole));
    }

    @Override
    @Transactional
    public Result<Void, RoleError> delete(DeleteRoleCommand command) {
        if (command == null) {
            return Result.failure(new RoleError("error.command.required"));
        }

        RoleId roleId;
        try {
            roleId = new RoleId(command.roleId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new RoleError(exception.getMessage()));
        }

        if (roleRepository.findById(roleId).isEmpty()) {
            return Result.failure(new RoleError("error.role.not-found"));
        }

        if (userRepository.existsByRoleId(roleId)) {
            return Result.failure(new RoleError("error.role.in-use"));
        }

        try {
            roleRepository.deleteById(roleId);
        } catch (IllegalArgumentException exception) {
            return Result.failure(new RoleError(exception.getMessage()));
        }

        return Result.success(null);
    }

    private RoleResult toResult(Role role) {
        Long id = role.id()
                .map(RoleId::value)
                .orElseThrow(() -> new IllegalArgumentException("error.role.id.not-assigned"));
        return new RoleResult(id, role.name().value(), role.description());
    }
}
