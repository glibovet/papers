<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %>

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
                            <h3>${publication.title}</h3>
                            <p>
                                Автор(и):
                                <c:forEach var="author" items="${publication.authors}">
                                    ${author.lastName} ${author.initials}
                                </c:forEach>
                            </p>
                            <p>Видавництво: ${publication.publisher.title}</p>
                            <c:choose>
                                <c:when test="${publication.link ne null}">
                                    <a href="${publication.link}" class="btn btn-default" target="_blank">Перейти на сторінку публікації</a>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn btn-success order-publication" data-id="${publication.id}">Замовити файл</button>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-sm-offset-1 col-sm-10">
                        <c:choose>
                            <c:when test="${recommendations.size() == 0}">
                                <h3>До цієї публікації поки що немає рекомендацій :(</h3>
                            </c:when>
                            <c:otherwise>
                                <h3>Рекомендації</h3>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="row">
                    <div class="col-sm-offset-1 col-sm-10">
                        <c:forEach var="recommendation" items="${recommendations}">
                            <c:choose>
                                <c:when test="${recommendation.publication1.id == publication.id}">
                                    <c:set var = "item" scope = "session" value = "${recommendation.publication2}"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var = "item" scope = "session" value = "${recommendation.publication1}"/>
                                </c:otherwise>
                            </c:choose>

                            <div class="row text-left">
                                <h4>${item.title}</h4>
                                <p>
                                    Автор(и):
                                    <c:forEach var="author" items="${publication.authors}">
                                        ${author.lastName} ${author.initials}
                                    </c:forEach>
                                </p>
                                <p>Видавництво: ${item.publisher.title}</p>
                                <c:choose>
                                    <c:when test="${item.link ne null}">
                                        <a href="${item.link}" class="btn btn-default" target="_blank">Перейти на сторінку публікації</a>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn btn-success order-publication" data-id="${item.id}">Замовити файл</button>
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
