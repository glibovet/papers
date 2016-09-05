<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<div class="masthead clearfix">
    <div class="inner">
        <h3 class="masthead-brand"><a href="/"><i class="fa fa-search fa-2x" aria-hidden="true"></i></a></h3>
        <nav>
            <ul class="nav masthead-nav">
                <security:authorize access="isAuthenticated()">
                    <li><a href="#">Профіль</a></li>
                    <li><a href="#">Вийти</a></li>
                </security:authorize>
                <security:authorize access="isAnonymous()">
                    <li><a href="/sign_up">Зареєструватись</a></li>
                    <li><a href="#">Увійти</a></li>
                </security:authorize>
            </ul>
        </nav>
    </div>
</div>
