<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Профіль</title>
    <jsp:include page="../common/include_resources.jsp"/>
</head>


<body>
<div class="site-wrapper">
    <div class="site-wrapper-inner">
        <div class="cover-container">
            <jsp:include page="../common/header.jsp"/>
            <div class="my_container">
                <u class="nav masthead-nav my_nav">
                    <li><a href="/users/edit">Редагувати профіль</a></li>
                    <li><a href="/users/contacts">Контакти</a></li>
                    <li><a href="/chat">Повідомлення</a></li>
                </u>

                <div class="photo_block">
                    <div class="user_img">
                        <img class="user_ph" src="/users/image/${user.id}"/>
                    </div>
                    <u class="nav masthead-nav my_nav">
                        <c:set var="currentUserId">
                            <security:authentication property="principal.id"/>
                        </c:set>
                        <c:if test="${contact != null}">
                            <c:if test="${contact.accepted}">
                                <li><a href="/users/delete-contact/${user.id}">Видалити контакт</a></li>
                            </c:if>
                            <c:if test="${!contact.accepted && contact.userFrom.id == currentUserId}">
                                <li><a href="/users/delete-contact/${user.id}">Відмінити запрошення</a></li>
                            </c:if>
                            <c:if test="${!contact.accepted && contact.userTo.id == currentUserId}">
                                <li><a href="/users/accept-contact/${contact.id}">Прийняти запрошення</a></li>
                                <li><a href="/users/delete-contact/${contact.id}">Відхилити запрошення</a></li>
                            </c:if>
                        </c:if>
                        <c:if test="${user.id != currentUserId && contact == null}">
                            <li><a href="/users/add-contact/${user.id}">Встановити контакт</a></li>
                        </c:if>
                    </u>
                </div>

                <div class="my_content">
                    <h2 class="user_name">${user.name} ${user.lastName}</h2>
                    <p class="user_email">${user.email}</p>
                    <p class="user_descr">
                        <c:if test="${user.roleEntity.name == 'student'}">
                            Студент
                        </c:if>
                        <c:if test="${user.roleEntity.name == 'scientist'}">
                            Науковець
                        </c:if>
                    </p>
                </div>

            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>
<script src="/resources/js/main/index.js"></script>
</body>
</html>