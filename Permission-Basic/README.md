# 手撸 Java Web RBAC 权限管理

代码对应博客地址: [http://www.zhaojun.im/java-permission-1/](http://www.zhaojun.im/java-permission-1/)


## RBAC 简介

RBAC (Role-Based Access Control) 基于角色的权限访问控制。

即**用户**拥有**角色**,**角色**拥有**权限**。具体关于 RBAC 的好处我就不再赘言，如感兴趣请自行查询。

## 数据库设计

共有五张表，分别为用户表、角色表、权限表、用户-角色关系表、角色-权限关系表。其中用户表于角色表是多对多的关系，角色表于权限表也是多对多关系。具体每个字段的含义请查看相应的注释。

```sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限名称',
  `description` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限描述表',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限名称',
  `description` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for role_premission
-- ----------------------------
DROP TABLE IF EXISTS `role_premission`;
CREATE TABLE `role_premission`  (
  `role_id` int(11) NULL DEFAULT NULL,
  `permission_id` int(11) NULL DEFAULT NULL,
  INDEX `role_premission_uid_fk`(`role_id`) USING BTREE,
  INDEX `role_premission_pid_fk`(`permission_id`) USING BTREE,
  CONSTRAINT `role_premission_pid_fk` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `role_premission_uid_fk` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `user_id` int(11) NULL DEFAULT NULL,
  `role_id` int(11) NULL DEFAULT NULL,
  INDEX `user_role_uid_fk`(`user_id`) USING BTREE,
  INDEX `user_role_rid_fk`(`role_id`) USING BTREE,
  CONSTRAINT `user_role_rid_fk` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_role_uid_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户角色表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
```

## 环境配置

本次基于的环境是 Spring + SpringMVC + MyBatis，不过即使你不会这几个框架也无所谓，因为权限管理没有涉及到太多这些框架的特性，用普通的 Servlet + JDBC 同样也可以实现。

因篇幅原因，框架的配置文件我这里就不再贴出，但我会将源码发到 Github，你可以去下载本实例完整代码。

## 实体类
首先需要创建三个与数据库对应的实体类

```java
public class User {
    private Integer id;

    private String username;

    private String password;

    // getter setter 略
}
```

```java
public class Role {
    private Integer id;

    private String name;

    private String description;

    // getter setter 略
}
```

```java
public class Permission {
    private Integer id;

    private String name;

    private String description;

    // getter setter 略
}
```

## DAO 数据操作层

### UserMapper
```java
public interface UserMapper {
    int insert(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(User record);

    List<User> selectALL();

    /**
     * 查询用户拥有的角色列表
     * @param id 用户 id
     * @return 角色列表
     */
    List<Role> selectRolesByPrimaryKey(Integer id);

    /**
     * 删除用户所有角色
     * @param id 用户id
     * @return 删除成功的条数
     */
    int deleteRoles(Integer id);

    /**
     * 为用户赋予角色
     * @param userId 用户 id
     * @param roleId 授予的角色 id
     * @return 插入成功的条数
     */
    int insertUserRole(@Param("user_id") Integer userId, @Param("role_id") Integer roleId);


    /**
     * 根据用户名密码查询账号是否存在
     * @param username  用户名
     * @param password  密码
     * @return  查询到的账号
     */
    User selectUserByUsernameAndPassword(@Param("username")String username,@Param("password")String password);
}
```

### RoleMapper

```java
public interface RoleMapper {
    int insert(Role record);

    Role selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(Role record);

    List<Role> selectAll();

    /**
     * 查询角色拥有的权限列表
     * @param id 角色 id
     * @return 权限列表
     */
    List<Permission> selectPermissionsByPrimaryKey(Integer id);

    /**
     * 删除角色所有的权限
     * @param id 角色 id
     * @return 删除成功的条数
     */
    int deletePermissions(Integer id);

    /**
     * 为角色添加一个权限
     * @param roleId 角色 id
     * @param permissionId 权限 id
     * @return 插入成功的条数
     */
    int insertRolePermission(@Param("role_id")Integer roleId, @Param("permission_id") Integer permissionId);
}
```

### PermissionMapper

```java
public interface PermissionMapper {
    int insert(Permission record);

    Permission selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(Permission record);

    List<Permission> selectAll();
}
```

> 只需要一些简单的 SQL 操作，如需要对应的 mapper.xml 可去 Github 查看。

## 用户管理

### 用户添加

HTML 页面:
```html
<form action="addUser" method="post">
    用户名:<input type="text" name="username">
    密 码:<input type="password" name="password">
    <input type="submit" value="提交">
</form>
```

Controller:
```java
public String addUserSubmit(User user) {
    return userService.add(user) > 0 ? "success" : "error";
}
```

> service 和 dao 略.

### 用户登陆

HTML 页面:

```html
<form action="login" method="post">
    用户名 :<input type="text" name="username"><br>
    密  码:<input type="password" name="password"><br>
    <input type="submit" value="登陆">
</form>
```

Controller:

```java
public String login(String username, String password, HttpSession httpSession) {
    User user = userService.selectUserByUsernameAndPassword(username, password);
    if (user != null) {
        httpSession.setAttribute("user", user);
        return "登陆成功";
    }
    return "登陆失败";
}
```

> service 和 dao 略.

### 查看用户列表

HTML 页面:
```java
<table border="1">
    <tr>
        <td>用户名</td>
        <td>密码</td>
        <td>操作</td>
    </tr>
    <c:forEach items="${users}" var="user">
        <tr>
            <td>${user.username}</td>
            <td>${user.password}</td>
            <td>
                <a href="grantRoleView?id=${user.id}">赋予角色</a>
            </td>
        </tr>
    </c:forEach>
</table>
```

Controller:
```java
public ModelAndView listUser() {
    return new ModelAndView("user.jsp").addObject("users", userService.getAllUser());
}
```

![](https://cdn.jun6.net/201807251719_361.png)

> service 和 dao 略.

### 为用户赋予角色
为用户赋予角色需要先添加角色，请先看下面的添加角色后再来操作。

HTML 页面:
```html
<form action="grantRole">
    <table border="1">
        <tr>
            <td>当前用户</td>
            <td>
                ${user.username}
                <input type="hidden" name="id" value="${user.id}">
            </td>
        </tr>
        <tr>
            <td>已拥有角色</td>
            <td>
                <c:forEach items="${grantRole}" var="role">
                    <span>${role.name}</span>
                </c:forEach>
            </td>
        </tr>
        <tr>
            <td>所有角色</td>
            <td>
                <c:forEach items="${roles}" var="role">
                    <input type="checkbox" name="roleId" value="${role.id}"> ${role.name}
                </c:forEach>
            </td>
        </tr>
        <tr>
            <td></td>
            <td><input type="submit" value="提交"></td>
        </tr>
    </table>
</form>
```

![](https://cdn.jun6.net/201807251651_861.png)

Controller:
```java
public String grantRole(int id, int[] roleId) {
    userService.updateRoles(id, roleId);
    return "success";
}
```

Service:

```java
public void updateRoles(Integer id, int[] roleIds) {
    userMapper.deleteRoles(id);
    if (roleIds != null) {
        for (int roleId : roleIds) {
            userMapper.insertUserRole(id, roleId);
        }
    }
}
```

> 其实这里的修改授权角色只是将原来它拥有的所有角色删除，再分配给它提交的所有角色。

## 角色管理

### 添加角色
HTML 页面:

```html
<form action="addRole" method="post">
    角色名称:<input type="text" name="name">
    角色描述:<input type="text" name="description">
    <input type="submit" value="提交">
</form>
```

Controller:

```java
public String addRole(Role role) {
    return roleService.add(role) > 0 ? "success" : "error";
}
```

> service 和 dao 略.

### 角色列表

HTML 页面:
```html
<table border="1">
    <tr>
        <td>角色名称</td>
        <td>角色描述</td>
        <td>操作</td>
    </tr>
    <c:forEach items="${roles}" var="role">
        <tr>
            <td>${role.name}</td>
            <td>${role.description}</td>
            <td>
                <a href="grantPermissionView?id=${role.id}">赋予权限</a>
            </td>
        </tr>
    </c:forEach>
</table>
```
Controller:
```java
public ModelAndView listRole() {
    return new ModelAndView("role.jsp").addObject("roles", roleService.getAll());
}
```

![](https://cdn.jun6.net/201807251725_998.png)

> service 和 dao 略.

### 为角色赋予权限

HTML 页面:
```html
<form action="grantPermission">
    <table border="1">
        <tr>
            <td>当前角色</td>
            <td>
                ${role.name}
                <input type="hidden" name="id" value="${role.id}">
            </td>
        </tr>
        <tr>
            <td>已拥有权限</td>
            <td>
                <c:forEach items="${grantPermission}" var="permission">
                    <span>${permission.name}</span>
                </c:forEach>
            </td>
        </tr>
        <tr>
            <td>所有权限</td>
            <td>
                <c:forEach items="${permissions}" var="permission">
                    <input type="checkbox" name="premissionId" value="${permission.id}"> ${permission.name}
                </c:forEach>
            </td>
        </tr>
        <tr>
            <td></td>
            <td><input type="submit" value="提交"></td>
        </tr>
    </table>
</form>
```

Controller:
```java
public String grantPermission(int id, int[] premissionId) {
    roleService.updatePermission(id, premissionId);
    return "success";
}
```

Service:
```java
public void updatePermission(Integer roleId, int[] permissionsIds) {
    roleMapper.deletePermissions(roleId);
    if (permissionsIds != null) {
        for (int permissionId : permissionsIds) {
            roleMapper.insertRolePermission(roleId, permissionId);
        }
    }
}
```

![](https://cdn.jun6.net/201807251726_81.png)

> 这里的为角色赋予权限同样也是先删除角色所拥有的权限，再添加表单提交的所有权限。

## 权限管理

### 添加权限

HTML 页面:
```html
<form action="addPermission" method="post">
    权限名称:<input type="text" name="name">
    权限描述:<input type="text" name="description">
    <input type="submit" value="提交">
</form>
```
Controller:
```java
public String add(Permission permission) {
    return permissionService.add(permission) > 0 ? "success" : "error";
}
```

> service 和 dao 略.

### 权限列表

HTML 页面:
```html
<form action="addPermission" method="post">
    权限名称:<input type="text" name="name">
    权限描述:<input type="text" name="description">
    <input type="submit" value="提交">
</form>
```

Controller:

```java
public String add(Permission permission) {
    return permissionService.add(permission) > 0 ? "success" : "error";
}
```

![](https://cdn.jun6.net/201807251726_658.png)

> service 和 dao 略.

## 权限拦截
既然已经分配好用户，角色以及权限之间的关系了，那么我们就可以设置一些需要权限才能访问的资源了。

我设置了 5 个 url, 并标注了需要何权限或何角色才可访问:

```java
/api/add        # add 权限
/api/delete     # delete 权限
/api/get        # get 权限
/api/employee   # employee 角色
/api/boss       # boos 角色
```

我们可以用拦截器来拦截 `/api/*` 下的所有请求，那么如何区分不同请求分别需要什么权限呢？

这里我参考了 Shiro 的设计，即采用注解的方式，在相应的方法上用 `@RequiredRole` 和 `@RequiredPremission` 来标注相应的请求需要某个角色或某个权限才可访问。

```java
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
```

拦截器获取拦截的方法上的注解即可得知需要什么权限，以便来进行相应的判断，Spring 拦截器：
```java
public class PermissionHandlerInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        response.setHeader("Content-type", "text/html;charset=UTF-8");

        Method method = ((HandlerMethod)handler).getMethod();
        RequiredRole requiredRole = method.getAnnotation(RequiredRole.class);
        RequiredPermission requiredPermission = method.getAnnotation(RequiredPermission.class);
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            response.getWriter().write("未登录");
            return false;
        }


        List<Role> userRoles = userService.getUserRoles(user.getId());


        if (requiredRole != null) {
            for (Role role : userRoles) {
                if (role.getName().equals(requiredRole.value())) {
                    return true;
                }
            }
        }

        if (requiredPermission != null) {
            for (Role role : userRoles) {
                List<Permission> permissions = roleService.getPermissions(role.getId());
                for (Permission persission : permissions) {
                    if (requiredPermission.value().equals(persission.getName())) {
                        return true;
                    }
                }
            }
        }
        response.getWriter().println("权限不足");
        return false;
    }
}
```

## 总结
基本实现就这些，其实没有很复杂的东西，只是将 RBAC 这个思想运用了起来。

那么反观我们这个权限管理有什么缺陷呢？

我来列举几点：

* 对密码没有进行加密处理, 应对密码进行加盐并散列。
* 每次请求都会去获取所对应的权限数据和角色数据，太耗费资源，应该进行缓存。
* 不支持多凭证登陆，如可用邮箱也可用手机号登陆。

这些问题我会在后续的 shiro 的笔记中一一讲到。