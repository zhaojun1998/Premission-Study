package im.zhaojun.controller;

import im.zhaojun.annotation.RequiredPermission;
import im.zhaojun.annotation.RequiredRole;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class APIController {

    @RequiredPermission("add")
    @RequestMapping("/add")
    public String add() {
        return "添加数据成功";
    }

    @RequiredPermission("delete")
    @RequestMapping("/delete")
    public String delete() {
        return "删除数据成功";
    }

    @RequiredPermission("get")
    @RequestMapping("/get")
    public String select() {
        return "查询数据成功";
    }

    @RequiredRole("boss")
    @RequestMapping("/boss")
    public String boss() {
        return "此数据为 Boss 专用数据, 你是 boss, 你可以查看";
    }

    @RequiredRole("employee")
    @RequestMapping("/employee")
    public String employee() {
        return "此数据为员工专用数据, 你是员工, 可以查看";
    }
}
