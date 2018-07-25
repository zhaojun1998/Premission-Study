package im.zhaojun.service;

import im.zhaojun.mapper.RoleMapper;
import im.zhaojun.model.Permission;
import im.zhaojun.model.Role;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoleService {

    @Resource
    private RoleMapper roleMapper;

    public Integer add(Role role) {
        return roleMapper.insert(role);
    }
    public Role get(Integer roleId) {
        return roleMapper.selectByPrimaryKey(roleId);
    }

    public List<Role> getAll() {
        return roleMapper.selectAll();
    }

    public List<Permission> getPermissions(Integer roleId) {
        return roleMapper.selectPermissionsByPrimaryKey(roleId);
    }

    public void updatePermission(Integer roleId, int[] permissionsIds) {
        roleMapper.deletePermissions(roleId);
        if (permissionsIds != null) {
            for (int permissionId : permissionsIds) {
                roleMapper.insertRolePermission(roleId, permissionId);
            }
        }
    }

}
