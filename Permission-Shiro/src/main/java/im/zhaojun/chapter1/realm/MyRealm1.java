package im.zhaojun.chapter1.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;

/**
 * 这是一个自定义 Realm
 */
public class MyRealm1 implements Realm {

    /**
     * 返回一个唯一的Realm名字
     */
    @Override
    public String getName() {
        return "MyRealm1";
    }

    /**
     * 判断此 Realm 是否支持此 Token
     */
    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof UsernamePasswordToken;
    }

    /**
     * 根据 Token 获取认证信息, 并进行校验
     */
    @Override
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        String username = (String) authenticationToken.getPrincipal();
        String password = new String((char[]) authenticationToken.getCredentials());

        if (!"zhao".equals(username)) {
            throw new UnknownAccountException("无效的用户名");
        }
        if (!"123456".equals(password)) {
            throw new IncorrectCredentialsException("密码错误");
        }
        return new SimpleAuthenticationInfo(username, password, getName());
    }
}
