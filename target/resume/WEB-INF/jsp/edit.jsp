<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <title>Редактирование резюме</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            line-height: 1.6;
        }

        .form-group {
            margin-bottom: 15px;
        }

        label {
            display: inline-block;
            width: 150px;
            vertical-align: top;
        }

        input[type="text"],
        input[type="date"],
        textarea {
            width: 300px;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }

        textarea {
            height: auto;
            min-height: 100px;
            font-family: monospace;
            white-space: pre;
        }

        .section {
            margin-bottom: 20px;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            background-color: #f9f9f9;
        }

        button {
            padding: 8px 15px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        button:hover {
            background-color: #45a049;
        }

        a {
            margin-left: 10px;
            color: #337ab7;
            text-decoration: none;
        }

        a:hover {
            text-decoration: underline;
        }

        .form-text {
            display: block;
            margin-top: 5px;
            color: #666;
            font-size: 0.9em;
        }
    </style>
</head>
<body>
<h1>Редактирование резюме</h1>

<form method="post" action="resume">
    <input type="hidden" name="uuid" value="${not empty resume.uuid ? resume.uuid : ''}">
    <div class="form-group">
        <label for="fullName">ФИО:</label>
        <input type="text" id="fullName" name="fullName" value="${fn:escapeXml(resume.fullName)}" required>
    </div>

    <div class="section">
        <h2>Контакты</h2>
        <c:forEach var="type" items="${contactTypes}">
            <div class="form-group">
                <label>${type.title}:</label>
                <input type="text" name="${type.name()}" value="${fn:escapeXml(resume.getContact(type))}">
            </div>
        </c:forEach>
    </div>

    <div class="section">
        <h2>Секции</h2>
        <c:forEach var="type" items="${sectionTypes}">
            <div class="section">
                <h3>${type.title}</h3>
                <c:choose>
                    <c:when test="${type == 'PERSONAL' || type == 'OBJECTIVE'}">
                        <div class="form-group">
                            <textarea name="${type.name()}">${fn:escapeXml(resume.getSection(type).content)}</textarea>
                        </div>
                    </c:when>

                    <c:when test="${type == 'ACHIEVEMENT' || type == 'QUALIFICATIONS'}">
                        <div class="form-group">
                            <textarea name="${type.name()}" rows="5" class="form-control"><c:forEach var="item" items="${resume.getSection(type).items}">${fn:trim(item)}<%= "\n" %></c:forEach></textarea>
                        </div>
                        <small class="form-text text-muted">Каждый пункт с новой строки</small>
                    </c:when>

                    <c:when test="${type == 'EXPERIENCE' || type == 'EDUCATION'}">
                        <c:forEach var="org" items="${resume.getSection(type).organizations}" varStatus="orgStatus">
                            <div class="form-group">
                                <label>Название организации:</label>
                                <input type="text" name="${type.name()}" value="${fn:escapeXml(org.linkHomePage.name)}">
                            </div>
                            <div class="form-group">
                                <label>Сайт:</label>
                                <input type="text" name="${type.name()}url"
                                       value="${fn:escapeXml(org.linkHomePage.url)}">
                            </div>
                            <c:forEach var="pos" items="${org.positions}" varStatus="posStatus">
                                <div class="form-group">
                                    <label>Начало работы:</label>
                                    <input type="date" name="${type.name()}${orgStatus.index}startDate"
                                           value="${pos.startDate}">
                                </div>
                                <div class="form-group">
                                    <label>Окончание:</label>
                                    <input type="date" name="${type.name()}${orgStatus.index}endDate"
                                           value="${pos.endDate}">
                                </div>
                                <div class="form-group">
                                    <label>Должность:</label>
                                    <input type="text" name="${type.name()}${orgStatus.index}title"
                                           value="${fn:escapeXml(pos.title)}">
                                </div>
                                <div class="form-group">
                                    <label>Описание:</label>
                                    <textarea
                                            name="${type.name()}${orgStatus.index}description">${fn:escapeXml(pos.description)}</textarea>
                                </div>
                            </c:forEach>
                        </c:forEach>
                    </c:when>
                </c:choose>
            </div>
        </c:forEach>
    </div>

    <div class="form-group">
        <button type="submit">Сохранить</button>
        <a href="resume">Отмена</a>
    </div>
</form>
</body>
</html>