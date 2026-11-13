package greendream.cultivatech.platform.iam.application.internal.queryservices;

import greendream.cultivatech.platform.iam.application.queries.GetAllRolesQuery;
import greendream.cultivatech.platform.iam.application.queries.GetRoleByIdQuery;
import greendream.cultivatech.platform.iam.application.querieservices.RoleQueryService;
import greendream.cultivatech.platform.iam.application.results.RoleError;
import greendream.cultivatech.platform.iam.application.results.RoleResult;
import greendream.cultivatech.platform.iam.domain.model.aggregates.Role;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleId;
import greendream.cultivatech.platform.iam.domain.repositories.RoleRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of RoleQueryService.
 */
@Service
public class RoleQueryServiceImpl implements RoleQueryService {

    private final RoleRepository roleRepository;

    public RoleQueryServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<RoleResult>, RoleError> getAllRoles(GetAllRolesQuery query) {
        if (query == null) {
            return Result.failure(new RoleError("error.command.required"));
        }

        List<RoleResult> roles = roleRepository.findAll().stream()
                .map(this::toResult)
                .toList();
        return Result.success(roles);
    }

    @Override
    @Transactional(readOnly = true)
    public Result<RoleResult, RoleError> getRoleById(GetRoleByIdQuery query) {
        if (query == null) {
            return Result.failure(new RoleError("error.command.required"));
        }

        RoleId roleId;
        try {
            roleId = new RoleId(query.roleId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new RoleError(exception.getMessage()));
        }

        Role role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            return Result.failure(new RoleError("error.role.not-found"));
        }

        return Result.success(toResult(role));
    }

    private RoleResult toResult(Role role) {
        Long id = role.id()
                .map(RoleId::value)
                .orElseThrow(() -> new IllegalArgumentException("error.role.id.not-assigned"));
        return new RoleResult(id, role.name().value(), role.description());
    }
}
