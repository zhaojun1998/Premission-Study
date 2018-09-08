package im.zhaojun.controller;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class AuthorizationController {

    @Resource
    private LoginController loginController ;

    @RequestMapping("/role1")
    @RequiresRoles("user")
    public String role1() {
        return "success";
    }

    @RequestMapping("/role2")
    @RequiresRoles("admin")
    public String role2() {
        return "success2";
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String processUnauthorizedException(UnauthorizedException e) {
        return e.getMessage();
    }

}
