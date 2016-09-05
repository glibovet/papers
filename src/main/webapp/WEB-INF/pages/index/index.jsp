<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Пошук наукових робіт</title>
    <jsp:include page="../common/include_resources.jsp" />
    <link rel="stylesheet" href="/resources/css/css/font-awesome.min.css">
    <link rel="stylesheet" href="/resources/css/style.css">
</head>
<body>
<div class="site-wrapper">
    <div class="site-wrapper-inner">
        <div class="cover-container">
            <div class="masthead clearfix">
                <div class="inner">
                    <h3 class="masthead-brand"><a href="/"><i class="fa fa-search fa-2x" aria-hidden="true"></i></a></h3>
                    <nav>
                        <ul class="nav masthead-nav">
                            <li class="active"><a href="#">Home</a></li>
                            <li><a href="#">Features</a></li>
                            <li><a href="#">Contact</a></li>
                        </ul>
                    </nav>
                </div>
            </div>

            <div class="inner cover">
                <h1 class="cover-heading">тут типу текст запрошення до пошуку</h1>
                <p class="lead">знайдіть будь-яку наукову роботу в два кліки</p>
                <p class="lead">
                    <div class="input-group">
                        <input type="text" class="form-control" placeholder="Дослідження морських котиків">
                        <span class="input-group-btn">
                            <button class="btn btn-default" id="search" type="button">Шукати! <i class="fa fa-search" aria-hidden="true"></i></button>
                        </span>
                    </div>
                </p>
            </div>
        </div>
    </div>
</div>
</body>
</html>
