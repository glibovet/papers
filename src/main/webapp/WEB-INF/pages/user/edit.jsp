<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Редагувати інформацію</title>
    <jsp:include page="../common/include_resources.jsp" />
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
                    <%--<form>--%>
                        <%--<input type="file">--%>
                    <%--</form>--%>
                </div>

                <div class="my_content">
                    <spring:form method="post" class="user_form"  modelAttribute="userView" action="/users/update?${_csrf.parameterName}=${_csrf.token}"
                                 enctype="multipart/form-data">
                        <label>
                            <input type="file" name="photo" accept="image/*"/>
                        </label>
                        <label>
                            <span>Ім'я:</span>
                            <spring:input class="form-control" path="name" value="${user.name}"/>
                        </label>
                        <label>
                            <span>Прізвище:</span>
                            <spring:input class="form-control" path="lastName" value="${user.lastName}"/>
                        </label>
                        <label>
                            <span>Електронна адреса:</span>
                            <spring:input class="form-control" path="email" value="${user.email}"/>
                        </label>
                        <label>
                            <span>Я</span>
                            <spring:select class="form-control" path="role">
                                <c:if test="${user.roleEntity.name == 'student'}" >
                                    <spring:option selected="true" value="student">студент</spring:option>
                                    <spring:option value="scientist">науковець</spring:option>
                                </c:if>
                                <c:if test="${user.roleEntity.name == 'scientist'}" >
                                    <spring:option value="student">студент</spring:option>
                                    <spring:option selected="true" value="scientist">науковець</spring:option>
                                </c:if>
                            </spring:select>
                        </label>

                        <spring:button class="btn btn-success btn-block">Зберегти</spring:button>
                    </spring:form>
                </div>
            </div>

        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
<script src="/resources/js/main/index.js"></script>
</body>
</html>