package im.zhaojun.realm;

import im.zhaojun.pojo.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.HashSet;
import java.util.Set;

/**
 * 自定义 Relam
 */
public class MyRealm extends AuthorizingRealm {
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        System.out.println("MyRealm doGetAuthorizationInfo...");

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        // 拥有 admin 角色
        Set<String> roles = new HashSet<>();
        roles.add("user");
        authorizationInfo.setRoles(roles);

        // 拥有权限
        Set<String> permissions = new HashSet<>();
        permissions.add("select");
        authorizationInfo.setStringPermissions(permissions);
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {

        System.out.println("MyRealm doGetAuthenticationInfo...");

        String username = (String) authenticationToken.getPrincipal();
        User user = selectUserByUserName(username);
        if (user == null) {
            throw new UnknownAccountException("当前账户不存在");
        }
        return new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), ByteSource.Util.bytes("TestSalt"), super.getName());
    }

    /**
     * 模拟查询数据库, 假设数据库只有 zhao - 123456 这条数据
     *
     * @param username 用户名
     * @return 查询到的用户
     */
    private User selectUserByUserName(String username) {
        if ("zhao".equals(username)) {
            return new User(username, "e5f728a966d050296c428290c9160dda");
        }
        return null;
    }
}