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
</head>


<body>
<div class="site-wrapper">
    <div class="site-wrapper-inner">
        <div class="cover-container">
            <jsp:include page="../common/header.jsp"/>
            <jsp:include page="../common/error_field.jsp"/>
            <div class="my_container">
                <jsp:include page="../common/navigation.jsp"/>
                <div id="search_result" class="search_result">
                    <c:forEach items="${chats}" var="chat">
                        <c:forEach items="${chat.members}" var="member">
                            <c:set var = "salary"/>
                            <c:if test="${currentUser.id != member.id}">
                                <c:set var = "withUser" value="${member}"/>
                            </c:if>
                        </c:forEach>
                        <div class="folder" style="margin-left: 50px;">
                            <div class="user_img">
                                <img class="user_ph" src="/users/image/${withUser.id}"/>
                            </div>
                            <div class="info" style="display: flex; flex-direction: column; text-align: left; justify-content:flex-start;">
                                <a href="/chat/${chat.id}">${withUser.name} ${withUser.lastName}</a>
                                <%--<div class="buttons">--%>
                                    <%--<button class="btn btn-danger btn-block">udalit</button>--%>
                                <%--</div>--%>
                                <%--<div class="m_user" style="margin-left: 25px;">--%>
                                    <%--Добрий день!--%>
                                <%--</div>--%>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>
<script src="/resources/js/main/index.js"></script>
</body>
</html>