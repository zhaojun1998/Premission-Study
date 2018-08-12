package im.zhaojun.chapter3.realm;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by zhaojun on 2018/7/23 .
 */
public class AuthenticatorTest {


    /**
     * 所有Realm验证成功才算成功, 且返回所有Realm身份验证成功的认证信息.
     */
    @Test
    public void testAllSuccessfulStrategyWithSuccess() {
        login("classpath:chapter3/shiro-authenticator-all-success.ini");
        Subject subject = SecurityUtils.getSubject();

        //得到一个身份集合，其包含了Realm验证成功的身份信息
        PrincipalCollection principalCollection = subject.getPrincipals();
        Assert.assertEquals(2, principalCollection.asList().size());
    }

    /**
     * 只要有一个Realm验证成功即可, 返回第一个 Realm 身份验证成功的认证信息.
     */
    @Test
    public void testFirstSuccessfulStrategyWithSuccess() {
        login("classpath:chapter3/shiro-authenticator-first-success.ini");
        Subject subject = SecurityUtils.getSubject();

        //得到一个身份集合，其包含了Realm验证成功的身份信息
        PrincipalCollection principalCollection = subject.getPrincipals();
        System.out.println(principalCollection);
        Assert.assertEquals(2, principalCollection.asList().size());
    }

    /**
     * 只要有一个Realm验证成功即可, 返回所有Realm身份验证成功的认证信息.
     */
    @Test
    public void testLastSuccessfulStrategyWithSuccess() {
        login("classpath:chapter3/shiro-authenticator-least-success.ini");
        Subject subject = SecurityUtils.getSubject();

        //得到一个身份集合，其包含了Realm验证成功的身份信息
        PrincipalCollection principalCollection = subject.getPrincipals();
        System.out.println(principalCollection);
        Assert.assertEquals(2, principalCollection.asList().size());
    }

    private void login(String configFile) {
        //1、获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager
        Factory<SecurityManager> factory =
                new IniSecurityManagerFactory(configFile);

        //2、得到SecurityManager实例 并绑定给SecurityUtils
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);

        //3、得到Subject及创建用户名/密码身份验证Token（即用户身份/凭证）
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken("zhang", "123");
        subject.login(token);
    }

}
