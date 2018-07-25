<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
    <title>为用户授予角色</title>
</head>
<body>
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
</body>
</html>
