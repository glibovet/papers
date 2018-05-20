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
                Name = ${user.name} <br>
                Last Name = ${user.lastName} <br>
                Email = ${user.email} <br>
                <c:set var="currentUserId">
                    <security:authentication property = "principal.id"/>
                </c:set>
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