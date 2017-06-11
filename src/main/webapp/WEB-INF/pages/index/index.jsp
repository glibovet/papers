<%@ page contentType="text/html;charset=UTF-8" language="java" %>

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
                <h1 class="cover-heading">Пошукова система українських наукових матеріалів</h1>
                <p class="lead">SciSearch - знайдіть будь-яку наукову роботу в два кліки</p>
                <p class="lead">
                    <form class="input-group" action="/search" method="get">
                        <input name="q" type="text" class="form-control" placeholder="Дослідження морських котиків">
                        <span class="input-group-btn">
                            <button type="submit" class="btn btn-default">Шукати! <i class="fa fa-search" aria-hidden="true"></i></button>
                        </span>
                    </form>
                </p>
                <br />
                <p>
                    Проіндексовано публікацій: ${publication_count}
                </p>
                <p>
                    В системі знаходиться ${authors_count} унікальних авторів
                </p>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
</body>
</html>
