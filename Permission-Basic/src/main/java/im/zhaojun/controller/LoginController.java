package im.zhaojun.controller;

import im.zhaojun.model.User;
import im.zhaojun.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Resource
    private UserService userService;

    @RequestMapping("/login")
    @ResponseBody
    public String login(String username, String password, HttpSession httpSession) {
        User user = userService.selectUserByUsernameAndPassword(username, password);
        if (user != null) {
            httpSession.setAttribute("user", user);
            return "登录成功";
        }
        return "登录失败";
    }

}
