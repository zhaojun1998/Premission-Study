package im.zhaojun;

import im.zhaojun.realm.MyCustomRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.junit.Test;

/**
 * Shiro 加密测试
 */
public class EncryptionTest {
    @Test
    public void testSimpleEncryption() {
        String password = "123456";
        Md5Hash md5Hash = new Md5Hash(password);
        System.out.println(md5Hash.toString());
    }

    @Test
    public void testSlatEncryption() {
        String password = "123456";
        String slat = "ShiroStudy";
        Md5Hash md5Hash = new Md5Hash(password, ByteSource.Util.bytes(slat));
        System.out.println(md5Hash.toString());
    }

    @Test
    public void testEncryptionRealm() {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();

        // 创建 Relam
        MyCustomRealm realm = new MyCustomRealm();

        // 创建密码匹配器
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher("md5");

        // 告诉 Realm 密码匹配方式
        realm.setCredentialsMatcher(credentialsMatcher);

        securityManager.setRealm(realm);

        SecurityUtils.setSecurityManager(securityManager);

        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("zhao", "123456");

        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            System.out.println("登录失败");
            e.printStackTrace();
        }
        System.out.println("当前登录状态: " + subject.isAuthenticated());
    }
}
