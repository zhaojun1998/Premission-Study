package im.zhaojun;

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
