<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Чат</title>
    <jsp:include page="../common/include_resources.jsp"/>
    <script src="/resources/js/chat/sockjs-0.3.4.js"></script>
    <script src="/resources/js/chat/stomp.js"></script>
    <script src="/resources/js/chat/chat.js"></script>
</head>
<body>
<input type="hidden" id="userId" value="${currentUser.id}">
<input type="hidden" id="chatId" value="${chat.id}">
<div class="site-wrapper">
    <div class="site-wrapper-inner">
        <div class="cover-container">
            <jsp:include page="../common/header.jsp"/>
            <jsp:include page="../common/error_field.jsp"/>
            <div class="my_container">
                <jsp:include page="../common/navigation.jsp"/>
                <div style="width: 100%">
                    <div id = "chat" class="chat">
                        <div id = "chat_container" class="chat_container">
                            <c:forEach items="${messages}" var="message">
                                <c:if test="${currentUser.id == message.user.id}">
                                    <div class="mess odd">
                                </c:if>
                                <c:if test="${currentUser.id != message.user.id}">
                                    <div class="mess even">
                                </c:if>
                                    <div class="user_ph">
                                        <img class="user_ph" src="/users/image/${message.user.id}"/>
                                    </div>
                                    <div class="mess">
                                        <div class="name"><h3>${message.user.name} ${message.user.lastName}</h3> <span> ${message.date}</span></div>
                                        <p>${message.text}</p>
                                        <c:if test="${message.attachment != null}">
                                            <a class="att" href="">Документ</a>
                                        </c:if>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                    <div>
                        <div id="form_chat">
                            <label>
                                <textarea id = "text"></textarea>
                            </label>
                            <button id = "sendButton" class="btn btn-primary"  onclick="sendMessage();" style="width: 100px; margin: 0 auto">Відправити</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>