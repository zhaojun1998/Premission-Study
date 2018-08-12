package im.zhaojun.chapter6;

import org.apache.shiro.crypto.hash.Md5Hash;

/**
 * Created by zhaojun on 2018/7/23 .
 */
public class Test {

    @org.junit.Test
    public void test() {
        String str = "hello";
        String salt = "12113";
        String md5 = new Md5Hash(str, salt).toString();
        System.out.println(md5);

        System.out.println(new Md5Hash("12113hello"));
    }


}
