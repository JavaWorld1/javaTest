<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Список резюме</title>
    <style>
        /* Общие стили для всех кнопок */
        .btn {
            display: inline-block;
            padding: 8px 16px;
            margin: 4px;
            border: none;
            border-radius: 4px;
            color: white;
            text-decoration: none;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.3s;
            text-align: center;
        }

        /* Специфичные стили для каждой кнопки */
        .btn-create {
            background-color: #6f42c1; /* Фиолетовый */
            margin-bottom: 20px;
        }
        .btn-create:hover {
            background-color: #5a32a3;
            transform: translateY(-2px);
        }

        .btn-view {
            background-color: #28a745; /* Зеленый */
        }
        .btn-view:hover {
            background-color: #218838;
        }

        .btn-edit {
            background-color: #17a2b8; /* Голубой */
        }
        .btn-edit:hover {
            background-color: #138496;
        }

        .btn-delete {
            background-color: #dc3545; /* Красный */
        }
        .btn-delete:hover {
            background-color: #c82333;
        }

        /* Контейнер для кнопок действий */
        .action-buttons {
            white-space: nowrap;
        }

        /* Дополнительные стили */
        .header-actions {
            margin-bottom: 20px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
    </style>
</head>
<body>

<section>
    <div class="header-actions">
        <a href="resume?action=edit" class="btn btn-create">
            <i class="fas fa-plus"></i> Создать резюме
        </a>
    </div>

    <table>
        <thead>
        <tr>
            <th>Имя</th>
            <th>Email</th>
            <th>Телефон</th>
            <th>Действия</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${resumes}" var="resume">
            <tr>
                <td>${resume.fullName}</td>
                <td>${resume.getContact('MAIL')}</td>
                <td>${resume.getContact('PHONE')}</td>
                <td class="action-buttons">
                    <a href="resume?action=view&uuid=${resume.uuid}" class="btn btn-view">
                        <i class="fas fa-eye"></i> Просмотр
                    </a>
                    <a href="resume?action=edit&uuid=${resume.uuid}" class="btn btn-edit">
                        <i class="fas fa-edit"></i> Редактировать
                    </a>
                    <a href="resume?action=delete&uuid=${resume.uuid}"
                       class="btn btn-delete"
                       onclick="return confirm('Удалить резюме ${resume.fullName}?')">
                        <i class="fas fa-trash-alt"></i> Удалить
                    </a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</section>

<!-- Подключение Font Awesome для иконок -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</body>
</html>