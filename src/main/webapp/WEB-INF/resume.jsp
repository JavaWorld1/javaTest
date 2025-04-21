<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Резюме ${resume.fullName}</title>
    <style>
        table { border-collapse: collapse; width: 100%; }
        th, td { padding: 8px; text-align: left; border-bottom: 1px solid #ddd; }
        .section { margin-bottom: 20px; }
    </style>
</head>
<body>
<section>
    <h2>${resume.fullName}</h2>
    <p><strong>UUID:</strong> ${resume.uuid}</p>

    <h3>Контакты:</h3>
    <table>
        <c:forEach var="contactEntry" items="${resume.contacts}">
            <tr>
                <td><strong>${contactEntry.key.title}:</strong></td>
                <td>${contactEntry.value}</td>
            </tr>
        </c:forEach>
    </table>

    <h3>Секции:</h3>
    <c:forEach var="sectionEntry" items="${resume.sections}">
        <div class="section">
            <h4>${sectionEntry.key.title}</h4>
            <c:set var="sectionType" value="${sectionTypes[sectionEntry.key]}"/>

            <c:choose>
                <c:when test="${sectionType == 'TextSection'}">
                    <p>${sectionEntry.value.content}</p>
                </c:when>

                <c:when test="${sectionType == 'ListSection'}">
                    <ul>
                        <c:forEach var="item" items="${sectionEntry.value.items}">
                            <li>${item}</li>
                        </c:forEach>
                    </ul>
                </c:when>

                <c:when test="${sectionType == 'OrganizationSection'}">
                    <c:forEach var="org" items="${sectionEntry.value.organizations}">
                        <div style="margin-bottom: 15px;">
                            <strong>
                                <c:choose>
                                    <c:when test="${not empty org.linkHomePage and not empty org.linkHomePage.url}">
                                        <a href="${org.linkHomePage.url}">${org.linkHomePage.name}</a>
                                    </c:when>
                                    <c:otherwise>
                                        ${org.linkHomePage.name}
                                    </c:otherwise>
                                </c:choose>
                            </strong>
                            <c:forEach var="position" items="${org.positions}">
                                <div style="margin-top: 5px;">
                                        ${position.startDate} - ${position.endDate}
                                    <br/><strong>${position.title}</strong>
                                    <c:if test="${not empty position.description}">
                                        <br/>${position.description}
                                    </c:if>
                                </div>
                            </c:forEach>
                        </div>
                    </c:forEach>
                </c:when>
            </c:choose>
        </div>
    </c:forEach>

    <button onclick="window.history.back()">Назад</button>
</section>
</body>
</html>