<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags/form" %>
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
                user.id = ${user.id} <br>
                user.email = ${user.email} <br>
                <spring:form method="post"  modelAttribute="userView" action="/users/update?${_csrf.parameterName}=${_csrf.token}"
                             enctype="multipart/form-data">
                    <%--<input type="file" name="photo" accept="image/*"/>--%>
                    Name: <spring:input class="form-control" path="name" value="${user.name}"/> <br/>
                    Last Name: <spring:input class="form-control" path="lastName" value="${user.lastName}"/> <br/>
                    <spring:button class="btn btn-lg btn-primary btn-block">Update</spring:button>

                </spring:form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
<script src="/resources/js/main/index.js"></script>
</body>
</html>