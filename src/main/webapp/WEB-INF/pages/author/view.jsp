<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Автор ${author.last_name} ${author.initials}</title>
    <jsp:include page="../common/include_resources.jsp" />
</head>
<body>
<div class="site-wrapper">
    <div class="site-wrapper-inner">
        <div class="cover-container">
            <jsp:include page="../common/header.jsp"/>

            <div class="inner cover">
                <h1>${author.last_name} ${author.initials}</h1>
                <hr />
                <h2>Публікацїї автора</h2>
                <div style="text-align: left;">
                <c:choose>
                    <c:when test="${author.publications ne null}">
                        <c:forEach var="item" items="${author.publications}" varStatus="i">
                            <div>
                                <p>
                                    Назва: ${item.title}
                                </p>
                                <c:if test="${item.annotation ne null}">
                                    <p>
                                        Анотації: ${item.annotation}
                                    </p>
                                </c:if>
                                <c:if test="${item.link ne null}">
                                    <p>
                                        <a href="${item.link}" target="_blank">Сторінка публікації</a>
                                    </p>
                                </c:if>
                            </div>
                            <c:if test="${!i.last}">
                                <hr />
                            </c:if>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>У автора досі немає жодних публікацій</c:otherwise>
                </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
</body>
</html>
