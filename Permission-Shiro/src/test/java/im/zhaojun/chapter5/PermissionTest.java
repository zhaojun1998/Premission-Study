package im.zhaojun.chapter5;

import im.zhaojun.BaseTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by zhaojun on 2018/7/23 .
 */
public class PermissionTest extends BaseTest {

    @Test
    public void testIsPermitted() {
        login("classpath:chapter5/shiro-permission.ini", "zhang", "123");
        Assert.assertTrue(subject().isPermitted("user:create"));
        Assert.assertTrue(subject().isPermittedAll("system:user:*", "system:user:update"));
    }
}
