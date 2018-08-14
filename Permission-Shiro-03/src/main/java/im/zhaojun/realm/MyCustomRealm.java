package im.zhaojun.realm;

import im.zhaojun.pojo.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.HashSet;
import java.util.Set;

/**
 * 自定义 Realm
 */
public class MyCustomRealm extends AuthorizingRealm {


    /**
     * 根据用户凭证查询所用拥有的角色和权限
     * @param principalCollection 用户凭证
     * @return 返回授权信息，包含所拥有的角色和权限
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String)principalCollection.getPrimaryPrincipal();

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        // 根据用户名其所拥有的角色和权限
        Set<String> roles = selectRolesByUserName(username);
        Set<String> permissions = selectPermissionsByUserName(username);

        authorizationInfo.setRoles(roles);
        authorizationInfo.setStringPermissions(permissions);
        return authorizationInfo;
    }


    /**
     * 根据用户提交的凭证查询是否具有这个用户 (这里不判断密码是否正确)
     * @param authenticationToken 用户凭证 (账户密码)
     * @return 相应的用户信息
     * @throws AuthenticationException 当用户不存在或具备其他状态, 如被锁定, 等状态会抛出相应的异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        // 这个方法也可以使用 DAO 层的方法来查询数据库，返回 user 对象。
        User user = selectUserByUserName((String) authenticationToken.getPrincipal());

        if (user == null) {
            throw new UnknownAccountException("账号不存在");
        }

        return new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), super.getName());
    }


    private Set<String> selectPermissionsByUserName(String username) {
        HashSet<String> permissions = new HashSet<>();
        // 假设只有 zhao 这个用户具备 select 权限
        if ("zhao".equals(username)) {
            permissions.add("select");
        }
        return permissions;
    }

    private Set<String> selectRolesByUserName(String username) {
        HashSet<String> roles = new HashSet<>();

        // 假设只有 zhao 这个用户具备 user 角色
        if ("zhao".equals(username)) {
            roles.add("user");
        }
        return roles;
    }

    private User selectUserByUserName(String username) {
        User user = null;

        // 假设当前只有 zhao - 123465 这个账户.
        if ("zhao".equals(username)) {
            user = new User("zhao", "123456");
        }
        return user;
    }
}
