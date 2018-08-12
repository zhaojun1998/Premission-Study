package im.zhaojun.chapter4;

import im.zhaojun.BaseTest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * 基于角色的访问控制
 */
public class RoleTest extends BaseTest {
    /**
     * 判断用户是否具备某个角色
     */
    @Test
    public void testHasRole() {
        login("classpath:chapter4/shiro-role.ini", "zhang", "123");

        // 是否拥有 role1 角色
        Assert.assertTrue(SecurityUtils.getSubject().hasRole("role1"));

        // 是否同时拥有 role1 和 role2 角色
        Assert.assertTrue(SecurityUtils.getSubject().hasAllRoles(Arrays.asList("role1", "role1")));

        // 是否同时拥有 role1, role2, role3 这三个角色, 返回 boolean[] 数组, 分别表示是否拥有
        boolean[] result = SecurityUtils.getSubject().hasRoles(Arrays.asList("role1", "role2", "role3"));

        Assert.assertEquals(true, result[0]);
        Assert.assertEquals(true, result[1]);
        Assert.assertEquals(false, result[2]);
    }


    /**
     * CheckRole/CheckRoles/CheckXXX 系列方法没有返回值, 且当判断为假的情况下, 会抛出 UnauthorizedException 异常
     */
    @Test(expected = UnauthorizedException.class)
    public void testCheckRole() {
        login("classpath:chapter4/shiro-role.ini", "zhang", "123");
        // 是否拥有 role1 角色
        SecurityUtils.getSubject().checkRole("role3");
    }
}
