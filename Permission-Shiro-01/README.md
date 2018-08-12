## Shiro 认证

认证在 Shiro 中被称为 **Authentication**，用来验证用户是不是拥有相应的身份，也可以理解为登陆时校验登陆凭证的过程。


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

然后创建一个测试类来简单认识一下 Shiro 的认证流程:

```java
public class AuthenticateTest {
    // 创建一个 Realm
    private SimpleAccountRealm simpleAccountRealm = new SimpleAccountRealm();

    /**
     * 为 Realm 添加一个账户
     */
    @Before
    public void before() {
        simpleAccountRealm.addAccount("zhao", "123456");
    }


    /**
     * 测试用户认证(登陆)
     */
    @Test
    public void testAuthenticate() {
        // 构建 SecurityManager 环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();

        // 为 SecurityManager 设置 Realm
        defaultSecurityManager.setRealm(simpleAccountRealm);

        // 将 SecurityManager 放入 SecurityUtils 这个工具类中
        SecurityUtils.setSecurityManager(defaultSecurityManager);

        // 获取一个 Subject
        Subject subject = SecurityUtils.getSubject();

        // 创建一个账号密码, 在 web 应用中一般为表单上填写并传入后台.
        UsernamePasswordToken token = new UsernamePasswordToken("zhao", "123456");

        // 进行登陆操作
        subject.login(token);

        // 验证是否为登陆状态
        System.out.println("是否登陆: " + subject.isAuthenticated());
    }
}
```

登陆成功的话，`subject.isAuthenticated()`会返回 `true` 。

登陆失败的话，会抛出相应的异常，如：

```
UnknownAccountException             # 未知账户/没找到帐号
IncorrectCredentialsException       # 错误的凭证(密码)异常
```

本章代码地址 : https://github.com/zhaojun1998/Premission-Study/tree/master/Permission-Shiro-01/