<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
    <title>Резюме</title>
</head>
<body>
<h2>${resume.fullName}</h2>
<p><strong>UUID:</strong> ${resume.uuid}</p>

<hr/>

<h3>Контакты</h3>
<ul>
    <c:forEach var="contact" items="${resume.contacts}">
        <li>
            <strong>${contact.key}:</strong>
            <c:choose>
                <c:when test="${fn:startsWith(contact.value, 'http')}">
                    <a href="${contact.value}" target="_blank">${contact.value}</a>
                </c:when>
                <c:otherwise>
                    ${contact.value}
                </c:otherwise>
            </c:choose>
        </li>
    </c:forEach>
</ul>

<hr/>

<h3>Секции</h3>
<c:forEach var="entry" items="${resume.sections}">
    <c:set var="type" value="${sectionTypes[entry.key]}" />

    <h4>${entry.key}</h4>

    <c:choose>
        <c:when test="${type == 'TextSection'}">
            <p>${entry.value.content}</p>
        </c:when>

        <c:when test="${type == 'ListSection'}">
            <ul>
                <c:forEach var="item" items="${entry.value.items}">
                    <li>${item}</li>
                </c:forEach>
            </ul>
        </c:when>

        <c:when test="${type == 'OrganizationSection'}">
            <c:forEach var="org" items="${entry.value.organizations}">
                <p>
                    <strong>${org.name}</strong>
                    <c:if test="${not empty org.url}">
                        (<a href="${org.url}" target="_blank">${org.url}</a>)
                    </c:if><br/>
                    <c:forEach var="pos" items="${org.positions}">
                        <small>${pos.startDate} - ${pos.endDate}</small><br/>
                        <b>${pos.title}</b><br/>
                        <c:if test="${not empty pos.description}">
                            ${pos.description}<br/>
                        </c:if>
                    </c:forEach>
                </p>
            </c:forEach>
        </c:when>

        <c:otherwise>
            <em>Неизвестный тип секции: ${type}</em>
        </c:otherwise>
    </c:choose>
    <hr/>
</c:forEach>

<p><a href="resume">Назад</a></p>
</body>
</html>
