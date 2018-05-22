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

            <div class="inner cover">


                <div class="row col-sm-offset-1 col-sm-10">
                    <form class="input-group" action="/search" method="get">
                        <input name="q" type="text" class="form-control" placeholder="Дослідження морських котиків" value="${query}">
                        <span class="input-group-btn">
                            <button type="submit" class="btn btn-default">Шукати! <i class="fa fa-search" aria-hidden="true"></i></button>
                        </span>
                    </form>
                </div>

                <c:if test="${publications.size() == 0 && offset == null}">
                    <div class="row">
                        <div class="col-sm-offset-1 col-sm-10">
                            <h1>Не можу нічого знайти :(</h1>
                            <p>Будь ласка спробуйте ще раз</p>
                        </div>
                    </div>
                </c:if>

                <c:if test="${publications.size() == 0 && offset != null}">
                    <div class="row">
                        <div class="col-sm-offset-1 col-sm-10">
                            <h1>Більше не можу нічого знайти :(</h1>
                        </div>
                    </div>
                </c:if>

                <div class="row">
                    <div class="col-sm-offset-1 col-sm-10">
                        <c:forEach var="publication" items="${publications}">
                            <div class="row text-left">
                                <h3>${publication.title}</h3>
                                <p>${publication.body}</p>
                                <p>Автор(и): ${publication.authors}</p>
                                <p>Видавництво: ${publication.publisher}</p>
                                <c:choose>
                                    <c:when test="${publication.link ne null}">
                                        <a href="${publication.link}" class="btn btn-default publicationLink" publication-id="${publication.id}" target="_blank">Перейти на сторінку публікації</a>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn btn-success order-publication" data-id="${publication.id}">Замовити файл</button>
                                    </c:otherwise>
                                </c:choose>
                                <a href="/recommendations/to-publication/${publication.id}" class="btn btn-default" target="_blank">Переглянути рекомендації</a>
                            </div>
                        </c:forEach>
                    </div>
                </div>
                <div class="row">
                    <div class="col-sm-offset-1 col-sm-10">
                        <c:if test="${publications.size() == 10}">
                            <form action="/search" method="get">
                                <input type="hidden" name="q" value="${query}">
                                <input type="hidden" name="offset" value="${offset != null ? offset+10:10}">
                                <button type="submit" class="btn btn-default" id="search">Знайти ще! <i class="fa fa-search" aria-hidden="true"></i></button>
                            </form>
                        </c:if>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
<script src="/resources/js/search/search.js"></script>
<script>
    $(document).ready(function() {
        $('.publicationLink').on('click', function(e) {
            e.preventDefault();
           var link = $(this).attr('href');
           var publicationId = $(this).attr('publication-id');

            $.ajax({
                type: "GET",
                url: "/redis/register-clicked-publication/" + publicationId,
            });

            window.open(link, '_blank');
        });
    });
</script>
</body>
</html>
