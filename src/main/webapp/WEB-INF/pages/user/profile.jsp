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
            <jsp:include page="../common/error_field.jsp"/>
            <div class="my_container">
                <jsp:include page="../common/navigation.jsp"/>
                <div class="photo_block">
                    <div class="user_img">
                        <img class="user_ph" src="/users/image/${user.id}"/>
                    </div>
                    <u class="nav masthead-nav my_nav">
                        <c:set var="currentUserId">
                            <security:authentication property="principal.id"/>
                        </c:set>
                        <c:if test="${(currentUser.id != user.id && currentUser.roleEntity.name == 'scientist') ||
                        (contact != null && contact.accepted)}">
                            <li><a href="/chat/message/${user.id}">Написати повідомлення</a></li>
                        </c:if>
                        <c:if test="${contact != null}">
                            <c:if test="${contact.accepted}">
                                <form id="delete-contact" action="/users/delete-contact" method="post" class="nav masthead-nav my_nav">
                                    <input type="hidden" name="contactId" value="${contact.id}" />
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <li><a href="#" onclick="document.getElementById('delete-contact').submit();">Видалити контакт</a></li>
                                </form>
                            </c:if>
                            <c:if test="${!contact.accepted && contact.userFrom.id == currentUserId}">
                                <form id="delete-contact" action="/users/delete-contact" method="post" class="nav masthead-nav my_nav">
                                    <input type="hidden" name="contactId" value="${contact.id}" />
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <li><a href="#" onclick="document.getElementById('delete-contact').submit();">Відмінити запрошення</a></li>
                                </form>
                            </c:if>
                            <c:if test="${!contact.accepted && contact.userTo.id == currentUserId}">
                                <form id="accept-contact" action="/users/accept-contact" method="post" class="nav masthead-nav my_nav">
                                    <input type="hidden" name="contactId" value="${contact.id}" />
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <li><a href="#" onclick="document.getElementById('accept-contact').submit();">Прийняти запрошення</a></li>
                                </form>
                                <form id="reject-contact" action="/users/delete-contact" method="post" class="nav masthead-nav my_nav">
                                    <input type="hidden" name="contactId" value="${contact.id}" />
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <li><a href="#" onclick="document.getElementById('reject-contact').submit();">Відхилити запрошення</a></li>
                                </form>
                            </c:if>
                        </c:if>
                        <c:if test="${user.id != currentUserId && contact == null}">
                            <form id="add-contact" action="/users/add-contact" method="post" class="nav masthead-nav my_nav">
                                <input type="hidden" name="userId" value="${user.id}" />
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                <li><a href="#" onclick="document.getElementById('add-contact').submit();">Встановити контакт</a></li>
                            </form>

                        </c:if>
                    </u>
                </div>

                <div class="my_content">
                    <h2 class="user_name">${user.name} ${user.lastName}</h2>
                    <c:if test="${currentUser.id == user.id ||
                                    currentUser.roleEntity.name == 'scientist' ||
                                    (contact != null && contact.accepted)}">
                        <p class="user_email">${user.email}</p>
                    </c:if>
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