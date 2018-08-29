package im.zhaojun.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 自定义 Shiro 过滤器, 用来匹配参数中的任意一种角色
 */
public class AnyRolesFilter extends AuthorizationFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        Subject subject = this.getSubject(request, response);
        String[] rolesArray = (String[]) mappedValue;
        if (rolesArray != null && rolesArray.length != 0) {
            for (String role : rolesArray) {
                if (subject.hasRole(role)) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }
}
