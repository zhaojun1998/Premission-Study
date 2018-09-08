package im.zhaojun.interceptor;

import im.zhaojun.annotation.RequiredPermission;
import im.zhaojun.annotation.RequiredRole;
import im.zhaojun.model.Permission;
import im.zhaojun.model.Role;
import im.zhaojun.model.User;
import im.zhaojun.service.RoleService;
import im.zhaojun.service.UserService;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

public class PermissionHandlerInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        response.setHeader("Content-type", "text/html;charset=UTF-8");

        Method method = ((HandlerMethod) handler).getMethod();
        RequiredRole requiredRole = method.getAnnotation(RequiredRole.class);
        RequiredPermission requiredPermission = method.getAnnotation(RequiredPermission.class);
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            response.getWriter().write("未登录");
            return false;
        }


        List<Role> userRoles = userService.getUserRoles(user.getId());


        if (requiredRole != null) {
            for (Role role : userRoles) {
                if (role.getName().equals(requiredRole.value())) {
                    return true;
                }
            }
        }

        if (requiredPermission != null) {
            for (Role role : userRoles) {
                List<Permission> permissions = roleService.getPermissions(role.getId());
                for (Permission persission : permissions) {
                    if (requiredPermission.value().equals(persission.getName())) {
                        return true;
                    }
                }
            }
        }
        response.getWriter().println("权限不足");
        return false;
    }
}
