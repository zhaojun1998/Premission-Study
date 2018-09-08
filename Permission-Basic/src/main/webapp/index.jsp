<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>首页</title>
</head>
<body>
<a href="listUser">用户管理</a>
<a href="listRole">角色管理</a>
<a href="listPermission">权限管理</a>
<hr>
<a href="login.jsp">登录</a>

<hr>
<ul>
    <li><a href="api/add">/api/add</a></li>
    <li><a href="api/delete">/api/delete</a></li>
    <li><a href="api/get">/api/get</a></li>
    <li><a href="api/employee">/api/employee</a></li>
    <li><a href="api/boss">/api/boss</a></li>
</ul>
</body>
</html>
