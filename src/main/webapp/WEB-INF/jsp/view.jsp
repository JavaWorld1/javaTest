<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="javatime" uri="http://sargue.net/jsptags/time" %>
<html>
<head>
    <title>Резюме ${resume.fullName}</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .section { margin-bottom: 30px; }
        .contact-item { margin-bottom: 10px; }
        .position { margin-left: 20px; padding-left: 10px; border-left: 2px solid #eee; }
        .action-btn {
            padding: 8px 15px;
            margin-right: 10px;
            text-decoration: none;
            color: white;
            border-radius: 4px;
        }
        .edit-btn { background-color: #2196F3; }
        .back-btn { background-color: #607D8B; }
    </style>
</head>
<body>
<h1>${resume.fullName}</h1>
<div style="margin-bottom: 20px;">
    <a href="resume?action=edit&uuid=${resume.uuid}" class="action-btn edit-btn">Редактировать</a>
    <a href="resume" class="action-btn back-btn">Назад к списку</a>
</div>

<h2>Контакты</h2>
<c:forEach var="contactEntry" items="${resume.contacts}">
    <div class="contact-item">
        <strong>${contactEntry.key.title}:</strong>
        <c:choose>
            <c:when test="${contactEntry.key == model.ContactType.MAIL}">
                <a href="mailto:${contactEntry.value}">${contactEntry.value}</a>
            </c:when>
            <c:when test="${contactEntry.key == model.ContactType.PHONE ||
                          contactEntry.key == model.ContactType.MOBILE ||
                          contactEntry.key == model.ContactType.HOME_PHONE}">
                <a href="tel:${contactEntry.value}">${contactEntry.value}</a>
            </c:when>
            <c:when test="${contactEntry.key == model.ContactType.LINKEDIN ||
                          contactEntry.key == model.ContactType.GITHUB ||
                          contactEntry.key == model.ContactType.STACKOVERFLOW ||
                          contactEntry.key == model.ContactType.HOME_PAGE}">
                <a href="${contactEntry.value}" target="_blank">${contactEntry.value}</a>
            </c:when>
            <c:otherwise>
                ${contactEntry.value}
            </c:otherwise>
        </c:choose>
    </div>
</c:forEach>

<h2>Секции</h2>
<c:forEach var="sectionEntry" items="${resume.sections}">
    <div class="section">
        <h3>${sectionEntry.key.title}</h3>
        <c:choose>
            <c:when test="${sectionEntry.value.getClass().simpleName == 'TextSection'}">
                <p>${sectionEntry.value.content}</p>
            </c:when>

            <c:when test="${sectionEntry.value.getClass().simpleName == 'ListSection'}">
                <ul>
                    <c:forEach var="item" items="${sectionEntry.value.items}">
                        <li>${item}</li>
                    </c:forEach>
                </ul>
            </c:when>

            <c:when test="${sectionEntry.value.getClass().simpleName == 'OrganizationSection'}">
                <c:forEach var="org" items="${sectionEntry.value.organizations}">
                    <div style="margin-bottom: 20px;">
                        <h4>
                            <c:choose>
                                <c:when test="${not empty org.linkHomePage.url}">
                                    <a href="${org.linkHomePage.url}" target="_blank">${org.linkHomePage.name}</a>
                                </c:when>
                                <c:otherwise>
                                    ${org.linkHomePage.name}
                                </c:otherwise>
                            </c:choose>
                        </h4>
                        <c:forEach var="pos" items="${org.positions}">
                            <div class="position">
                                <p>
                                    <javatime:format value="${pos.startDate}" pattern="MM/yyyy"/> -
                                    <javatime:format value="${pos.endDate}" pattern="MM/yyyy"/>
                                    <br/><strong>${pos.title}</strong>
                                    <c:if test="${not empty pos.description}">
                                        <br/>${pos.description}
                                    </c:if>
                                </p>
                            </div>
                        </c:forEach>
                    </div>
                </c:forEach>
            </c:when>
        </c:choose>
    </div>
</c:forEach>
</body>
</html>