<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Контакти</title>
    <jsp:include page="../common/include_resources.jsp" />
</head>


<body>
<div class="site-wrapper">
    <div class="site-wrapper-inner">
        <div class="cover-container">
            <jsp:include page="../common/header.jsp"/>
            <div class="my_container">
                <u class="nav masthead-nav my_nav">
                    <li><a href="/users/edit">Редагувати інформацію</a></li>
                    <li><a href="/users/contacts">Контакти</a></li>
                    <li><a href="/users/add-contact/${user.id}">Встановити контакт</a></li>
                    <li><a href="/users/edit">Редагувати інформацію</a></li>
                    <li><a href="/users/contacts">Контакти</a></li>
                    <li><a href="/users/add-contact/${user.id}">Встановити контакт</a></li>
                    <li><a href="/users/edit">Редагувати інформацію</a></li>
                </u>
                <div class="inner cover">
                    <li><a href="/users/received-contacts/">Отримані запити</a></li>
                    <div class="lead row">
                        <spring:form method="post"  modelAttribute="searchUsersView" action="/users/contacts">
                            <div class="col-md-4 form-group">
                                <label>Прізвище</label>
                                <spring:input path="lastName" type="text" class="form-control" placeholder="Іванов" value="${searchUsersView.lastName}"/>
                            </div>
                            <div class="col-md-4 form-group">
                                <label>І`мя</label>
                                <spring:input path="name" type="text" class="form-control" placeholder="Іван" value="${searchUsersView.name}"/>
                            </div>
                            <div class="col-md-4 form-group">
                                <spring:button class="btn btn-success">Шукати</spring:button>
                            </div>
                        </spring:form>
                    </div>
                    <div id="search_result">
                        <c:forEach items="${contacts}" var="contact">
                            <label><a href="/users/${contact.id}">${contact.name} ${contact.lastName}</a></label><br>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
<script src="/resources/js/main/index.js"></script>
</body>
</html>