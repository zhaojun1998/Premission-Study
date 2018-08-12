package im.zhaojun;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Assert;
import org.junit.Test;

public class IniLoginLogoutTest {

    @Test
    public void testHelloWorld() {
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken("zhang", "123");

        try {
            subject.login(usernamePasswordToken);
        } catch (AuthenticationException e) {
            // 登陆失败, AuthenticationException 是总验证异常, 它还有一些子类来详细的描述登陆失败的原因.
            // 如:
            // DisabledAccountException (禁用的帐号)
            // LockedAccountException (锁定的帐号)
            // UnknownAccountException (错误的帐号)
            // ExcessiveAttemptsException (登录失败次数过多)
            // IncorrectCredentialsException (错误的凭证)
            // ExpiredCredentialsException (过期的凭证)
            e.printStackTrace();
        }

        // 校验是否为登陆状态
        Assert.assertEquals(true, subject.isAuthenticated());


        subject.logout();
    }
}
