<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>hello WebSocket</title>
    <script src="/resources/js/chat/sockjs-0.3.4.js"></script>
    <script src="/resources/js/chat/stomp.js"></script>
    <script type="text/javascript">
        var stompClient = null;

        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
            document.getElementById('response').innerHTML = '';
        }

        function connect() {
            var socket = new SockJS('/papers');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function (frame) {
                setConnected(true);
                console.log('Connected: ' + frame);
                stompClient.subscribe('/topic/papers/' +${chatId}, function (greeting) {
                    showGreeting(JSON.parse(greeting.body).text);
                });
            });
        }

        function disconnect() {
            stompClient.disconnect();
            setConnected(false);
            console.log("Disconnected");
        }

        function sendName() {
            var name = document.getElementById('name').value;
            stompClient.send("/app/papers/" +${chatId}, {}, JSON.stringify({
                'userId': '1',
                'chatId': ${chatId},
                'text': name,
                'date': new Date(),
                'attachment': 'attachment'
            }));
        }

        function showGreeting(message) {
            var response = document.getElementById('response');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(message));
            response.appendChild(p);
        }
    </script>
    <jsp:include page="../common/include_resources.jsp"/>
</head>
<body>
<noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websocket relies on Javascript being
    enabled. Please enable
    Javascript and reload this page!</h2></noscript>


<div class="site-wrapper">
    <div class="site-wrapper-inner">
        <div class="cover-container">
            <jsp:include page="../common/header.jsp"/>
            <div class="alert-danger">
                <strong>Помилка!</strong> щось сталось.
            </div>
            <div class="my_container">
                <u class="nav masthead-nav my_nav">
                    <li><a href="/users/edit">Редагувати профіль</a></li>
                    <li><a href="/users/contacts">Контакти</a></li>
                    <li><a href="/chat/1">Повідомлення</a></li>
                </u>
                <div style="width: 100%">
                    <div class="chat">
                        <div class="chat_container">
                            <%--<c:forEach items="${messages}" var="message">--%>
                            <div class="mess odd">
                                <div class="user_ph">
                                    <img class="user_ph" src="/users/image/2"/>
                                </div>
                                <div class="mess">
                                    <div class="name"><h3>Vlad noviko</h3> <span> 12:33 01/01/1994</span></div>
                                    <p>messs messs msdadad adsad</p>
                                    <a class="att" href="">sdadasdasd</a>
                                </div>
                            </div>
                            <div class="mess even">
                                <div class="user_ph">
                                    <img class="user_ph" src="/users/image/1"/>
                                </div>
                                <div class="mess">
                                    <div class="name"><h3>Vlad noviko</h3> <span> 12:33 01/01/1994</span></div>
                                    <p> messs messs msdadad adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss
                                        messs msdadad adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss messs
                                        msdadad adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss messs
                                        msdadad
                                        adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss messs msdadad
                                        adsad</p>
                                    <a class="att" href="">sdadasdasd</a>
                                </div>
                            </div>
                            <div class="mess even">
                                <div class="user_ph">
                                    <img class="user_ph" src="/users/image/1"/>
                                </div>
                                <div class="mess">
                                    <div class="name"><h3>Vlad noviko</h3> <span> 12:33 01/01/1994</span></div>
                                    <p>messs messs msdadad adsad</p>
                                </div>
                            </div>
                            <div class="mess even">
                                <div class="user_ph">
                                    <img class="user_ph" src="/users/image/1"/>
                                </div>
                                <div class="mess">
                                    <div class="name"><h3>Vlad noviko</h3> <span> 12:33 01/01/1994</span></div>
                                    <p>messs messs msdadad adsad</p>
                                </div>
                            </div>
                            <div class="mess odd">
                                <div class="user_ph">
                                    <img class="user_ph" src="/users/image/2"/>
                                </div>
                                <div class="mess">
                                    <div class="name"><h3>Vlad noviko</h3> <span> 12:33 01/01/1994</span></div>
                                    <p>messs messs msdadad adsad</p>h
                                </div>
                            </div>
                            <div class="mess odd">
                                <div class="user_ph">
                                    <img class="user_ph" src="/users/image/2"/>
                                </div>
                                <div class="mess">
                                    <div class="name"><h3>Vlad noviko</h3> <span> 12:33 01/01/1994</span></div>
                                    <p> messs messs msdadad adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss
                                        messs msdadad adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss messs
                                        msdadad adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss messs
                                        msdadad
                                        adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss messs msdadad
                                        adsad</p>
                                </div>
                            </div>
                            <div class="mess odd">
                                <div class="user_ph">
                                    <img class="user_ph" src="/users/image/2"/>
                                </div>
                                <div class="mess">
                                    <div class="name"><h3>Vlad noviko</h3> <span> 12:33 01/01/1994</span></div>
                                    <p> messs messs msdadad adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss
                                        messs msdadad adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss messs
                                        msdadad adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss messs
                                        msdadad
                                        adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss messs msdadad
                                        adsad</p>
                                </div>
                            </div>
                            <div class="mess odd">
                                <div class="user_ph">
                                    <img class="user_ph" src="/users/image/2"/>
                                </div>
                                <div class="mess">
                                    <div class="name"><h3>Vlad noviko</h3> <span> 12:33 01/01/1994</span></div>
                                    <p> messs messs msdadad adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss
                                        messs msdadad adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss messs
                                        msdadad adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss messs
                                        msdadad
                                        adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss messs msdadad
                                        adsad</p>
                                </div>
                            </div>
                            <div class="mess odd">
                                <div class="user_ph">
                                    <img class="user_ph" src="/users/image/2"/>
                                </div>
                                <div class="mess">
                                    <div class="name"><h3>Vlad noviko</h3> <span> 12:33 01/01/1994</span></div>
                                    <p> messs messs msdadad adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss
                                        messs msdadad adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss messs
                                        msdadad adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss messs
                                        msdadad
                                        adsadmesss messs msdadad adsadmesss messs msdadad adsadmesss messs msdadad
                                        adsad</p>
                                </div>
                            </div>

                            <%--</c:forEach>--%>
                        </div>
                    </div>
                    <div>
                        <%--<div>--%>
                        <%--<button id="connect" onclick="connect();">Connect</button>--%>
                        <%--<button id="disconnect" disabled="disabled" onclick="disconnect();">Disconnect</button>--%>
                        <%--</div>--%>
                        <%--<div id="conversationDiv">--%>

                        <%--<label><input type="text" id="name" placeholder="sadasd"/></label>--%>
                        <%--<button id="sendName" onclick="sendName();">Send</button>--%>
                        <%--<p id="response"></p>--%>
                        <%--</div>--%>

                        <form id="form_chat">
                            <label>
                                <textarea></textarea>
                            </label>
                            <button class="btn btn-primary" style="width: 100px; margin: 0 auto">Выдправити</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>