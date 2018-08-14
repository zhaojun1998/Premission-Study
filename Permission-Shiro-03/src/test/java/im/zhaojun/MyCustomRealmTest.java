package im.zhaojun;

import im.zhaojun.realm.MyCustomRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * 自定义 Realm 测试
 */
public class MyCustomRealmTest {

    @Test
    public void testCustomRealm() {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        MyCustomRealm realm = new MyCustomRealm();
        securityManager.setRealm(realm);

        SecurityUtils.setSecurityManager(securityManager);

        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("zhao", "123456");

        subject.login(token);

        System.out.println("是否具备 user 角色: " + subject.hasRole("user"));
        System.out.println("是否具备 select 权限: " + subject.isPermitted("select"));
    }
}