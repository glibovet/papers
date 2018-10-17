<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <title>Пошук наукових робіт</title>
    <jsp:include page="../common/include_resources.jsp" />
</head>
<body>
<div class="site-wrapper">
    <div class="site-wrapper-inner">
        <div class="cover-container">
            <jsp:include page="../common/header.jsp"/>

            <div class="inner cover" style="margin-top:100px;margin-bottom:20px">

                <div class="row">
                    <div class="col-sm-offset-1 col-sm-10">
                        <div class="row text-left">
                            <h3>Рекомендації на основі взаємодії користувача з системою</h3>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-sm-offset-1 col-sm-10">
                        <c:forEach var="recommendation" items="${recommendations}">
                            <div class="row text-left">
                                <h4>${recommendation.title}</h4>
                                <%--<p>--%>
                                    <%--Автор(и):--%>
                                        <%--&lt;%&ndash;${item.authors}&ndash;%&gt;--%>
                                    <%--<c:forEach var="author" items="${item.authors}">--%>
                                        <%--${author.lastName} ${author.initials}--%>
                                    <%--</c:forEach>--%>
                                <%--</p>--%>
                                <p>Видавництво: ${recommendation.publisher.title}</p>
                                <c:choose>
                                    <c:when test="${recommendation.link ne null}">
                                        <a href="${recommendation.link}" class="btn btn-default" target="_blank">Перейти на сторінку публікації</a>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn btn-success order-publication" data-id="${recommendation.id}">Замовити файл</button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
</body>
</html>
