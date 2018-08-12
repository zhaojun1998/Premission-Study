## Shiro Realm

Realm: 域，Shiro 从 Realm 中获取用户，角色，权限信息。可以把 Relam 看成 DataSource，即安全数据源。



>  在前两章的认证和授权中，我们也使用到了 `SimpleAccountRealm`，并通过其 `addAccount(username, password, roles)` 来预设用户和角色信息。

## IniRealm

IniRealm 顾名思义，即通过读取 `.ini` 文件来获取用户，角色，权限信息。



配置用户名/密码及其角色, 格式: "用户名=密码，角色1，角色2"，如:

```ini
[users]
zhao = 123456, admin, user
wang = 123456, user
```



配置角色及权限之间的关系, 格式: “角色=权限1, 权限2", 如:

```ini
[roles]
admin = user:delete
user = user:select
```



结合起来，即 :

```ini
[users]
zhao = 123456, admin, user
wang = 123456, user

[roles]
admin = user:delete
user = user:select
```



然后进行测试 :

```java
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

import java.util.Arrays;

public class IniRealmTest {

    @Test
    public void testIniRealm() {
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        Realm iniRealm = new IniRealm("classpath:shiro.ini");
        defaultSecurityManager.setRealm(iniRealm);
        SecurityUtils.setSecurityManager(defaultSecurityManager);

        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("zhao", "123456");

        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            System.out.println("登陆失败");
        }

        System.out.println("--------------------认证--------------------");
        System.out.println("是否具备 admin 权限: " + subject.hasRole("admin"));
        System.out.println("是否具备 user 权限: " + subject.hasRole("user"));
        System.out.println("是否同时具备 admin 和 user 权限: " + subject.hasAllRoles(Arrays.asList("admin", "user")));

        System.out.println("--------------------授权--------------------");
        System.out.println("是否具备 user:delete 权限" + subject.isPermitted("user:delete"));
        System.out.println("是否具备 user:select 权限" + subject.isPermitted("user:select"));
        System.out.println("是否同时具备 user:delete 和 user:select 权限" + subject.isPermittedAll("user:delete", "user:select"));
    }
}
```



> 跟前两章的代码没有什么不同，只是将 SimpleAccountRealm 换成了 IniRealm。



## JdbcRelam

JdbcRelam 顾名思义，即通过通过访问数据库来获取用户，角色，权限信息。

首先需要导入 `mysql` 的驱动包和 `druid` 数据库连接池的包:

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.32</version>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.10</version>
</dependency>
```

创建数据库表和初始化数据 :

```sql
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for roles_permissions
-- ----------------------------
DROP TABLE IF EXISTS `roles_permissions`;
CREATE TABLE `roles_permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) DEFAULT NULL,
  `permission` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_roles_permissions` (`role_name`,`permission`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of roles_permissions
-- ----------------------------
INSERT INTO `roles_permissions` VALUES ('1', 'admin', 'user:delete');
INSERT INTO `roles_permissions` VALUES ('2', 'user', 'user:select');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `password_salt` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_users_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('1', 'zhao', '123456', null);

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) DEFAULT NULL,
  `role_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_roles` (`username`,`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_roles
-- ----------------------------
INSERT INTO `user_roles` VALUES ('1', 'zhao', 'admin');
INSERT INTO `user_roles` VALUES ('2', 'zhao', 'user');
```

测试：

```java
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * Shiro JdbcRealm 测试
 */
public class JdbcRealmTest {
    private DruidDataSource dataSource = new DruidDataSource();

    /**
     * 初始化 DataSource
     */
    @Before
    public void before() {
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/shiro");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
    }

    @Test
    public void testJdbcRealm() {
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();

        // 构建 JdbcRelam
        JdbcRealm jdbcRealm = new JdbcRealm();
        // 为 JdbcRelam 设置数据源
        jdbcRealm.setDataSource(dataSource);
        // 设置启用权限查询, 默认为 false
        jdbcRealm.setPermissionsLookupEnabled(true);

        defaultSecurityManager.setRealm(jdbcRealm);
        SecurityUtils.setSecurityManager(defaultSecurityManager);

        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("zhao", "123456");

        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            System.out.println("登陆失败");
        }

        System.out.println("--------------------认证--------------------");
        System.out.println("是否具备 admin 权限: " + subject.hasRole("admin"));
        System.out.println("是否具备 user 权限: " + subject.hasRole("user"));
        System.out.println("是否同时具备 admin 和 user 权限: " + subject.hasAllRoles(Arrays.asList("admin", "user")));
        System.out.println("--------------------授权--------------------");
        System.out.println("是否具备 user:delete 权限" + subject.isPermitted("user:delete"));
        System.out.println("是否具备 user:select 权限" + subject.isPermitted("user:select"));
        System.out.println("是否同时具备 user:delete 和 user:select 权限" + subject.isPermittedAll("user:delete", "user:select"));
    }
}
```



这里也只是将 Relam 修改了一下，其他代码是一样的。

细心的朋友可能会有疑问，我们这里没有写一行查询语句，那么Shiro 怎么知道你的数据库结构的，它如何来查询角色和权限信息。

其实我们点开 `JdbcRelam` 的源码看看就知道了，它内置了默认的查询角色和权限的 SQL 语句：

```java
protected static final String DEFAULT_AUTHENTICATION_QUERY = "select password from users where username = ?";
protected static final String DEFAULT_SALTED_AUTHENTICATION_QUERY = "select password, password_salt from users where username = ?";
protected static final String DEFAULT_USER_ROLES_QUERY = "select role_name from user_roles where username = ?";
protected static final String DEFAULT_PERMISSIONS_QUERY = "select permission from roles_permissions where role_name = ?";
```

而上面我们建立的表结构是符合这些查询语句的。



但在实际项目开发中，我们不可能完全按照 Shiro 提供的这种方式来建表，我们可以通过修改 JdbcRealm 的默认查询语句来实现：

```java
jdbcRealm.setAuthenticationQuery(String authenticationQuery);
jdbcRealm.setPermissionsQuery(String permissionsQuery);
jdbcRealm.setUserRolesQuery(String userRolesQuery);
```



## 自定义 Relam

在真实项目开发中，我们往往会使用自定义 Realm 来实现一些自定义的功能，如判断账号锁定，账号登陆次数限制等。

我们需要创建一个类来继承 `AuthorizingRealm` ，并实现其抽象方法：

```java
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

        // 根据用户名
        Set<String> roles = selectRoles(username);
        Set<String> permissions = selectPermissions(username);

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
        User user = selectByUserName((String) authenticationToken.getPrincipal());

        if (user == null) {
            throw new UnknownAccountException("账号不存在");
        }

        return new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), super.getName());
    }


    private Set<String> selectPermissions(String username) {
        HashSet<String> permissions = new HashSet<>();
        // 假设只有 zhao 这个用户具备 select 权限
        if ("zhao".equals(username)) {
            permissions.add("select");
        }
        return permissions;
    }

    private Set<String> selectRoles(String username) {
        HashSet<String> roles = new HashSet<>();

        // 假设只有 zhao 这个用户具备 user 角色
        if ("zhao".equals(username)) {
            roles.add("user");
        }
        return roles;
    }

    private User selectByUserName(String username) {
        User user = null;

        // 假设当前只有 zhao - 123465 这个账户.
        if ("zhao".equals(username)) {
            user = new User("zhao", "123456");
        }
        return user;
    }
}
```

这里的代码也很简单，主要是实现了父类的抽象方法 :

* `doGetAuthenticationInfo` : 获取用户认证信息，在这里我们不需要校验密码是否正确，因为有专门的密码校验器来做这件事，我们只需要返回**认证信息**即可。 *(认证信息在这个示例中为 `SimpleAuthenticationInfo`， 即账号密码)*
  当然你也可以在返回认证信息前根据用户的状态，如冻结，锁定，或登陆次数来抛出相应的异常，以直接返回登陆失败，而不再进行密码校验。
* `doGetAuthorizationInfo` : 获取用户授权信息，授权信息包括所拥有的角色和权限信息，这里的逻辑很简单，只需要根据用户信息查询出角色和权限，配置到 `AuthorizationInfo` 中返回即可 。



测试：

```java
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
```