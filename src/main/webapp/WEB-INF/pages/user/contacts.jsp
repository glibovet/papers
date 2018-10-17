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
            <jsp:include page="../common/error_field.jsp"/>
            <div class="my_container">
                <jsp:include page="../common/navigation.jsp"/>
                <div class="inner cover">

                    <main>

                        <input id="tab1" type="radio" name="tabs" checked class="tab1">
                        <label for="tab1">Мої контакти</label>

                        <input id="tab2" type="radio" name="tabs" class="tab2">
                        <label for="tab2">Запрошення</label>

                        <input id="tab3" type="radio" name="tabs" class="tab3">
                        <label for="tab3">Пошук контактів</label>

                        <section id="content1">
                            <div id="search_result" class="search_result">
                                <c:forEach items="${contacts}" var="contact">
                                    <div class="folder">
                                        <div class="user_img">
                                            <img class="user_ph" src="/users/image/${contact.id}"/>
                                        </div>
                                        <div class="info">
                                            <a href="/users/${contact.id}">${contact.name} ${contact.lastName}</a>
                                            <div class="buttons">
                                                <button class="btn btn-danger btn-block">Видалити</button>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </section>

                        <section id="content2">
                            <div class="search_result">
                                <c:forEach items="${receivedContactRequests}" var="contact">
                                    <div class="folder">
                                        <div class="user_img">
                                            <img class="user_ph" src="/users/image/${contact.userFrom.id}"/>
                                        </div>
                                        <div class="info">
                                            <a href="/users/${contact.userFrom.id}">${contact.userFrom.name} ${contact.userFrom.lastName}</a>
                                            <div>
                                                <a href="/users/attachment/${contact.id}">${contact.attachment}</a>
                                            </div>
                                                ${contact.message}
                                            <div class="buttons">
                                                <button class="btn btn-success btn-block">Прийняти</button>
                                                <button class="btn btn-danger btn-block">Відхилити</button>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </section>

                        <section id="content3">

                            <spring:form method="post" modelAttribute="searchUsersView" action="/users/contacts">
                                <div class="col-md-4 form-group">
                                    <label>
                                        <spring:input path="name" type="text" class="form-control" placeholder="Ім'я"
                                                      value="${searchUsersView.name}"/>
                                    </label>
                                </div>
                                <div class="col-md-4 form-group">
                                    <label>
                                        <spring:input path="lastName" type="text" class="form-control"
                                                      placeholder="Прізвище" value="${searchUsersView.lastName}"/>
                                    </label>
                                </div>
                                <div class="col-md-4 form-group">
                                    <spring:button class="btn btn-success">Шукати</spring:button>
                                </div>
                            </spring:form>
                            <input type="hidden" id = "searchResultsSize" value="${fn:length(searchResults)}">
                            <div class="search_result">
                                <c:forEach items="${searchResults}" var="contact">
                                    <div class="folder">
                                        <div class="user_img">
                                            <img class="user_ph" src="/users/image/${contact.id}"/>
                                        </div>
                                        <div class="info">
                                            <a href="/users/${contact.id}">${contact.name} ${contact.lastName}</a>
                                            <div class="buttons">
                                                <button class="btn btn-success btn-block">Додати в контакти</button>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>

                        </section>

                    </main>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>
<script src="/resources/js/main/index.js"></script>
</body>
</html>