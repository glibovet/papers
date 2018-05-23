<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Профіль</title>
    <jsp:include page="../common/include_resources.jsp" />
</head>


<body>
<div class="site-wrapper">
    <div class="site-wrapper-inner">
        <div class="cover-container">
            <jsp:include page="../common/header.jsp"/>
            <div class="inner cover">

                <img src="/users/image/${user.id}" style="width: 20%; height: 20%"/>
                <br>
                Ім'я: ${user.name} <br>
                Прізвище: ${user.lastName} <br>
                Електронна адреса: ${user.email} <br>
                <c:set var="currentUserId">
                    <security:authentication property = "principal.id"/>
                </c:set>
                Я
                <c:if test="${user.roleEntity.name == 'student'}" >
                    студент
                </c:if>
                <c:if test="${user.roleEntity.name == 'scientist'}" >
                    науковець
                </c:if>
                <c:if test="${user.id == currentUserId}" >
                    <li><a href="/users/edit">Редагувати інформацію</a></li>
                    <li><a href="/users/contacts">Контакти</a></li>
                </c:if>
                <c:if test="${contact != null}" >
                    <c:if test="${contact.accepted}" >
                        <li><a href="/users/delete-contact/${user.id}">Видалити контакт</a></li>
                    </c:if>
                    <c:if test="${!contact.accepted && contact.userFrom.id == currentUserId}" >
                        <li><a href="/users/delete-contact/${user.id}">Відмінити запрошення</a></li>
                    </c:if>
                    <c:if test="${!contact.accepted && contact.userTo.id == currentUserId}" >
                        <li><a href="/users/accept-contact/${contact.id}">Прийняти запрошення</a></li>
                    </c:if>
                </c:if>
                <c:if test="${user.id != currentUserId && contact == null}">
                    <li><a href="/users/add-contact/${user.id}">Встановити контакт</a></li>
                </c:if>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
<script src="/resources/js/main/index.js"></script>
</body>
</html>