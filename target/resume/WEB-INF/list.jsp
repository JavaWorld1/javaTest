<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Список Резюме</title>
</head>
<body>
<h2>Список Резюме</h2>

<!-- Таблица для отображения резюме -->
<table border="1">
    <thead>
    <tr>
        <th>Имя</th>
        <th>UUID</th>
        <th>Действия</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="resume" items="${resumes}">
        <tr>
            <td>${resume.fullName}</td>
            <td>${resume.uuid}</td>
            <td>
                <a href="resume?action=view&uuid=${resume.uuid}">Просмотр</a> |
                <a href="resume?action=delete&uuid=${resume.uuid}" onclick="return confirm('Вы уверены, что хотите удалить?')">Удалить</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<hr/>
<p><a href="addResume.jsp">Добавить новое резюме</a></p>

</body>
</html>
