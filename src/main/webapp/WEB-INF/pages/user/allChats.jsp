<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Контакти</title>
    <jsp:include page="../common/include_resources.jsp"/>
    <script src="/resources/js/user/contacts.js"></script>
</head>


<body>
<div class="site-wrapper">
    <div class="site-wrapper-inner">
        <div class="cover-container">
            <jsp:include page="../common/header.jsp"/>
            <c:if test="${error != null}">
                <div class="alert-danger">
                    <strong>Помилка! </strong> ${error}
                </div>
            </c:if>
            <div class="my_container">
                <u class="nav masthead-nav my_nav">
                    <li><a href="/users/edit">Редагувати інформацію</a></li>
                    <li><a href="/users/contacts">Контакти</a></li>
                    <li><a href="/users/add-contact/${user.id}">Встановити контакт</a></li>
                </u>
                <div class="inner cover">


                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>
<script src="/resources/js/main/index.js"></script>
</body>
</html>