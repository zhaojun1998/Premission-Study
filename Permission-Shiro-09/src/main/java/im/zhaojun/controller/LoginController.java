package im.zhaojun.controller;

import im.zhaojun.pojo.User;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewResponseBodyAdvice;

import java.util.Collection;

/**
 * 登录 Controller
 */
@Controller
public class LoginController {

    private static final Logger logger = Logger.getLogger(LoginController.class);

    @RequestMapping("login")
    @ResponseBody
    public String login(User user) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            return e.getMessage();
        }
        return "login success";
    }

    @RequestMapping("session")
    @ResponseBody
    public String session() {
        System.out.println(SecurityUtils.getSubject().isAuthenticated());
        Session session = SecurityUtils.getSubject().getSession();
        logger.info("sessionId --" + session.getId());
        logger.info("创建时间 --" + session.getStartTimestamp());
        logger.info("最后访问时间 --" + session.getLastAccessTime());
        logger.info("session 超时时间 --" + session.getTimeout());
        for (Object key : session.getAttributeKeys()) {
            Object value = session.getAttribute(key);
            logger.info(key + " -- " + value);
        }
        return "Hello Session";
    }
}