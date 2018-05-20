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
                user.id = ${user.name} <br>
                user.email = ${user.lastName} <br>
                <c:set var="currentUserId">
                    <security:authentication property = "principal.id"/>
                </c:set>
                currentUserId = ${currentUserId}
                <c:if test="${user.id == currentUserId}" >
                    <li><a href="/users/edit">Редагувати</a></li>
                </c:if>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
<script src="/resources/js/main/index.js"></script>
</body>
</html>