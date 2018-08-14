## Shiro 加密

安全总是最重要的，对于用户的数据，我们一般都不会以原密码明文保存，而是经过加密后保存。

比较常见的是，当注册时使用 `MD5` 散列算法对初始密码处理后存入数据库，而后每次登陆请求，对用户输入的密码也进行 `MD5` 散列后与数据库中的散列后的密码进行匹配，已达到加密的目的。



## 基本使用

### MD5 加密

Shiro 对加密处理也提供了相应的 API，老规矩，先导入依赖：

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

先来试试最简单的，将密码进行 `md5` 散列后输出结果：

```java
@Test
public void testSimpleEncryption() {
    String password = "123456";
    Md5Hash md5Hash = new Md5Hash(password);
    System.out.println(md5Hash.toString());
}

// output result: e10adc3949ba59abbe56e057f20f883e
```



### 加盐

但上面这种加密其实安全性也不高，因为可以建立一个 `密码 - hash` 对应表，根据存储的海量数据，对 md5 进行"反解密"，如我们通过网站 : http://www.cmd5.com/，将密文输入进去，即可 "反解密"。

![](https://cdn.jun6.net/201808141103_26.png)

这时候就需要**盐**的存在，即我们对原始密码，加一些佐料：盐。

如刚才我们的密码 `123456`，盐为 `ShiroStudy`，我们可以将盐加入到密码前或密码后，结果就是: `123456ShiroStudy`，这时再进行 `md5` 加密：

```java
@Test
public void testSlatEncryption() {
    String password = "123456";
    String slat = "ShiroStudy";
    Md5Hash md5Hash = new Md5Hash(password, ByteSource.Util.bytes(slat));
    System.out.println(md5Hash.toString());
}

// output result : ac7351ab94f40504aab7ac7fdf4ddad3
```

`Md5Hash(source, slat)` 构造函数的第二个参数就是盐，但他需要的是 `ByteSource` 类型的盐，所以我们需要将字符串类型的盐通过他的工具类转换成 `ByteSource` 类型，即：`ByteSource.Util.bytes(slat)`。



这次我们再去尝试 "反解密" :

![](https://cdn.jun6.net/201808141119_352.png)



## 集成 Relam

使用加密后，我们需要告诉 Relam，我们使用了哪种加密算法。



首先自定义定义 Relam：

```java
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
 * 自定义 Realm, 使用了加密, 盐
 */
public class MyCustomRealm extends AuthorizingRealm {


    /**
     * 根据用户凭证查询所用拥有的角色和权限
     * @param principalCollection 用户凭证
     * @return 返回授权信息，包含所拥有的角色和权限
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 略
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
        // 告诉 Relam, 校验密码时需要加的盐.
        ByteSource slat = ByteSource.Util.bytes("ShiroStudy");
        return new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), slat, super.getName());
    }


    private Set<String> selectPermissionsByUserName(String username) {
        // 略
    }

    private Set<String> selectRolesByUserName(String username) {
        // 略
    }

    private User selectUserByUserName(String username) {
        // 略
    }
}
```

主要是 `doGetAuthenticationInfo` 方法，其他的代码与上一章的基本相同。

在 `doGetAuthenticationInfo` 中我们返回的 `AuthenticationInfo`  信息中，将盐加了进去。



我们来测试下这个 Realm :

```java
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
            System.out.println("登陆失败");
            e.printStackTrace();
        }
        System.out.println("当前登陆状态: " + subject.isAuthenticated());
    }
}
```

我们需要为 Relam 指定一个 `CredentialsMatcher`，这里我们使用他的一个是实现类 : `HashedCredentialsMatcher`，并设置加密算法为 `md5`。



> 基本的加密使用就这些，具体的细节上，还有更多的加密算法，加密次数等内容，本篇为不对新手造成更多的初学负担，便不再展开，如需要深入了解，请自行查询。