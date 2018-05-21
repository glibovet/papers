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
            <div class="inner cover">
                <spring:form method="post"  modelAttribute="userView" action="/users/update?${_csrf.parameterName}=${_csrf.token}"
                             enctype="multipart/form-data">
                    <input type="file" name="photo" accept="image/*"/>
                    Ім'я: <spring:input class="form-control" path="name" value="${user.name}"/> <br/>
                    Прізвище: <spring:input class="form-control" path="lastName" value="${user.lastName}"/> <br/>
                    Електронна адреса: <spring:input class="form-control" path="email" value="${user.email}"/> <br/>
                    Я
                    <spring:select class="form-control" path="role">
                        <c:if test="${user.roleEntity.name == 'student'}" >
                            <spring:option selected="true" value="student">студент</spring:option>
                            <spring:option value="scientist">науковець</spring:option>
                        </c:if>
                        <c:if test="${user.roleEntity.name == 'scientist'}" >
                            <spring:option value="student">студент</spring:option>
                            <spring:option selected="true" value="scientist">науковець</spring:option>
                        </c:if>
                    </spring:select> <br/>
                    <spring:button class="btn btn-lg btn-primary btn-block">Зберегти</spring:button>

                </spring:form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
<script src="/resources/js/main/index.js"></script>
</body>
</html>