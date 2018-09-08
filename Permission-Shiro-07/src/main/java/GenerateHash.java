import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.util.ByteSource;

/**
 * 将现在的密码: "123456", 用 md5 加密, 并以 "TestSalt"
 */
public class GenerateHash {
    public static void main(String[] args) {
        Md5Hash md5Hash = new Md5Hash("123456", ByteSource.Util.bytes("TestSalt"));
        System.out.println(md5Hash.toString());
    }
}
