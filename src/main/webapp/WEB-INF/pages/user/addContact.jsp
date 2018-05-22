<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Додати контакт</title>
    <jsp:include page="../common/include_resources.jsp" />
</head>


<body>
<div class="site-wrapper">
    <div class="site-wrapper-inner">
        <div class="cover-container">
            <jsp:include page="../common/header.jsp"/>
            <div class="inner cover">
                <form method="post"  action="/users/send-request?${_csrf.parameterName}=${_csrf.token}"
                             enctype="multipart/form-data">
                    Прикріплення: <input type="file" name="attachment"/>
                    Повідомлення: <input type="text" name="message" class="form-control"> <br/>
                    <input type="hidden" name="id" value="${user.id}"> <br/>
                    <button class="btn btn-lg btn-primary btn-block">Надіслати</button>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
<script src="/resources/js/main/index.js"></script>
</body>
</html>