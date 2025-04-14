<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Список резюме</title>
</head>
<body>
<h1>Все резюме</h1>

<table border="1">
    <tr>
        <th>Имя</th>
        <th>Действия</th>
    </tr>
    <c:forEach var="resume" items="${resumes}">
        <tr>
            <td>${resume.fullName}</td>
            <td>
                <a href="resume?action=view&uuid=${resume.uuid}">Просмотр</a> |
                <a href="resume?action=delete&uuid=${resume.uuid}">Удалить</a>
            </td>
        </tr>
    </c:forEach>
</table>

<h3>Добавить резюме</h3>
<form method="post" action="resume">
    <input type="text" name="fullName" placeholder="Введите имя"/>
    <button type="submit">Сохранить</button>
</form>

<form method="get" action="resume">
    <button type="submit" name="action" value="clear">Очистить все</button>
</form>

</body>
</html>
