package im.zhaojun.mapper;

import im.zhaojun.model.Permission;
import im.zhaojun.model.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper {
    int insert(Role record);

    Role selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(Role record);

    List<Role> selectAll();

    /**
     * 查询角色拥有的权限列表
     * @param id 角色 id
     * @return 权限列表
     */
    List<Permission> selectPermissionsByPrimaryKey(Integer id);

    /**
     * 删除角色所有的权限
     * @param id 角色 id
     * @return 删除成功的条数
     */
    int deletePermissions(Integer id);

    /**
     * 为角色添加一个权限
     * @param roleId 角色 id
     * @param permissionId 权限 id
     * @return 插入成功的条数
     */
    int insertRolePermission(@Param("role_id")Integer roleId, @Param("permission_id") Integer permissionId);

}