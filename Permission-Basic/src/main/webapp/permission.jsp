<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <title>权限列表</title>
</head>
<body>
    <table border="1">
        <tr>
            <td>权限名称</td>
            <td>权限描述</td>
        </tr>
        <c:forEach items="${permissions}" var="permission">
            <tr>
                <td>${permission.name}</td>
                <td>${permission.description}</td>
            </tr>
        </c:forEach>
    </table>
    <a href="add-permission.jsp">添加权限</a>
</body>
</html>
