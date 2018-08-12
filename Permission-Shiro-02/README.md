授权在 Shiro 中被称为 **Authorization**，用来**验证用户是否具备某个角色**。

<!-- more -->

首先需要导包，这里我们通过 maven 坐标的方式进行引用, 导入 shrio 和 junit 的包:


```xml
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-all</artifactId>
    <version>1.4.0</version>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
</dependency>
```


然后创建一个测试类来简单认识一下 Shiro 的授权流程:

```java
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;

public class AuthorizationTest {
    // 创建一个 Realm
    private SimpleAccountRealm simpleAccountRealm = new SimpleAccountRealm();

    /**
     * 为 Realm 添加一个账户, 并赋予 admin 角色
     */
    @Before
    public void before() {
        simpleAccountRealm.addAccount("zhao", "123456", "admin");
    }

    /**
     * 测试用户授权(是否具备某个角色)
     */
    @Test
    public void testAuthentication() {
        // 构建 SecurityManager 环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();

        // 为 SecurityManager 设置 Realm
        defaultSecurityManager.setRealm(simpleAccountRealm);

        // 将 SecurityManager 放入 SecurityUtils 这个工具类中
        SecurityUtils.setSecurityManager(defaultSecurityManager);

        // 获取一个 Subject
        Subject subject = SecurityUtils.getSubject();

        // 创建一个账号密码
        UsernamePasswordToken token = new UsernamePasswordToken("zhao", "123456");

        // 进行登陆操作
        subject.login(token);

        // 验证是否为登陆状态
        System.out.println("是否登陆: " + subject.isAuthenticated());

        // 验证是否具备某个角色
        System.out.println("是否具备admin角色: " + subject.hasRole("admin"));
    }
}
```

若登陆的用户具备 admin 角色的话，`subject.hasRole("admin")` 会返回 true，反之返回 false。

也可以使用 `subject.checkRole("admin")` 来校验，此方法没有返回值，但如账户不具备此角色，会抛出 `UnauthorizedException` 异常。

还有 `subject.hasAllRoles(Collection<String> collection)` 及 `subject.CheckRoles(String... strings)` 等 API 用来判断用来进行其他方式的校验角色。




本章代码地址 : https://github.com/zhaojun1998/Premission-Study/tree/master/Permission-Shiro-02/