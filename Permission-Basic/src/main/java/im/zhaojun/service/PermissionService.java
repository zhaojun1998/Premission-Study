package im.zhaojun.service;

import im.zhaojun.mapper.PermissionMapper;
import im.zhaojun.mapper.UserMapper;
import im.zhaojun.model.Permission;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PermissionService {

    @Resource
    private PermissionMapper permissionMapper;

    public Integer add(Permission permission){
        return permissionMapper.insert(permission);
    }

    public List<Permission> getAll() {
        return permissionMapper.selectAll();
    }
}
