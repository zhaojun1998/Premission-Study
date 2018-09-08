package im.zhaojun.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizationController {

    @RequestMapping("/role1")
    @RequiresAuthentication
    @RequiresRoles("user")
    public String role1() {
        Subject subject = SecurityUtils.getSubject();
        System.out.println(subject.isAuthenticated());
        System.out.println(subject.isRemembered());
        return "success";
    }

    @RequestMapping("/role2")
    @RequiresRoles("admin")
    public String role2() {
        Subject subject = SecurityUtils.getSubject();
        System.out.println(subject.isAuthenticated());
        System.out.println(subject.isRemembered());
        return "success2";
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String processUnauthorizedException(UnauthorizedException e) {
        return e.getMessage();
    }

}
