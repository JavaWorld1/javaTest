<jsp:useBean id="resume" scope="request" type="jdk.internal.jimage.ImageLocation"/>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Резюме: ${resume.fullName}</title>
</head>
<body>
<h1>${resume.fullName}</h1>

<h2>Контакты</h2>
<ul>
    <c:forEach var="entry" items="${resume.contacts}">
        <li><strong>${entry.key}:</strong> ${entry.value}</li>
    </c:forEach>
</ul>

<h2>Секции</h2>
<ul>
    <c:forEach var="entry" items="${resume.sections}">
        <li>
            <strong>${entry.key}:</strong>
            <c:choose>
                <c:when test="${entry.value.class.simpleName == 'TextSection'}">
                    ${entry.value.text}
                </c:when>
                <c:when test="${entry.value.class.simpleName == 'ListSection'}">
                    <ul>
                        <c:forEach var="item" items="${entry.value.items}">
                            <li>${item}</li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:when test="${entry.value.class.simpleName == 'OrganizationSection'}">
                    <ul>
                        <c:forEach var="org" items="${entry.value.organizations}">
                            <li>
                                <strong>${org.name}</strong><br/>
                                <c:forEach var="pos" items="${org.positions}">
                                    <div>
                                            ${pos.startDate} - ${pos.endDate}: <strong>${pos.title}</strong><br/>
                                        <em>${pos.description}</em>
                                    </div>
                                </c:forEach>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
            </c:choose>
        </li>
    </c:forEach>
</ul>

<a href="resume">Назад к списку</a>

</body>
</html>