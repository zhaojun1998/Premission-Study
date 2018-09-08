<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>为角色授予权限</title>
</head>
<body>
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
</body>
</html>
