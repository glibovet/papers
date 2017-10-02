<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<nav class="navbar navbar-default">
    <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/"><i class="fa fa-search fa-lg" aria-hidden="true"></i></a>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li><a href="/admin/cabinet">Кабінет адміна</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Користувачі <span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <li><a href="/admin/users/all" class="active">Всі користувачі</a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><spring:message code="admin.authors.authors"/> <span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <li><a href="/admin/authors/all" class="active"><spring:message code="admin.authors.all_authors" /></a></li>
                        <li><a href="/admin/authors/create" class="active"><spring:message code="admin.authors.create" /></a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><spring:message code="admin.publishers.publishers"/> <span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <li><a href="/admin/publishers/all" class="active"><spring:message code="admin.publishers.all_publishers" /></a></li>
                        <li><a href="/admin/publishers/create" class="active"><spring:message code="admin.publishers.create" /></a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><spring:message code="admin.publications.publications"/> <span class="caret"></span></a>
                    <ul class="dropdown-menu">
                        <li><a href="/admin/publications/all" class="active"><spring:message code="admin.publications.all_publications" /></a></li>
                        <li><a href="/admin/publications/create" class="active"><spring:message code="admin.publications.create" /></a></li>
                        <li><a href="/admin/publications/orders/all" class="active">замовлення публікацій</a></li>
                    </ul>
                </li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="#" id="logout">Вийти</a></li>
            </ul>
        </div><!-- /.navbar-collapse -->
    </div><!-- /.container-fluid -->
</nav>

<script>
    $(function(){
        var li = $('a[href="'+location.pathname+'"]').parent().addClass('active');

        if(li.parent().hasClass('dropdown-menu')){
            li.parent().parent().addClass('active');
        }
    });
</script>

<style>
    .main-container {
        margin: auto;
        width: 70%;
    }
</style>