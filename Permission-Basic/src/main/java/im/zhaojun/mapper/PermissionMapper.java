package im.zhaojun.mapper;

import im.zhaojun.model.Permission;

import java.util.List;

public interface PermissionMapper {
    int insert(Permission record);

    Permission selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(Permission record);

    List<Permission> selectAll();
}