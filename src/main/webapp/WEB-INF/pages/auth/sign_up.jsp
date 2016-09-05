<%--
  Created by IntelliJ IDEA.
  User: oleh_kurpiak
  Date: 05.09.2016
  Time: 12:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Реєстрація</title>
    <jsp:include page="../common/include_resources.jsp" />
    <link rel="stylesheet" href="/resources/css/sign_up.css">
</head>
<body>
<div class="site-wrapper">
    <div class="site-wrapper-inner">
        <div class="cover-container">
            <jsp:include page="../common/header.jsp"/>

            <div class="inner cover">
                <form class="form-signin">
                    <label class="sr-only" for="sign_up_email">Електронна пошта</label>
                    <input type="email" name="email" class="form-control" placeholder="email@example.com" id="sign_up_email">
                    <label class="sr-only">Пароль</label>
                    <input type="password" name="password" class="form-control" placeholder="My super secret password">
                    <label class="sr-only">Повторіть пароль</label>
                    <input type="password" name="password_repeat" class="form-control" placeholder="My super secret password(again)">
                    <button class="btn btn-lg btn-primary btn-block" type="submit">Зареєструватись</button>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>

