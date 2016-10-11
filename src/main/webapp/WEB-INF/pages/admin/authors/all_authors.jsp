<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html ng-app="all_authors">
<head>
    <title>Всі автори</title>
    <jsp:include page="../include.jsp" />
    <script src="/resources/js/admin/authors/all_authors.js"></script>
</head>
<body>
    <jsp:include page="../header.jsp"/>
    <div style="width: 90%; margin: auto;" ng-controller="all_authors_ctrl">
        <hr role="separator" class="divider" />
        <div class="row">
            <div class="col-md-3">
                filters here later
            </div>
            <div class="col-md-9">
                <div class="row" ng-repeat="author in authors">
                    {{author.last_name}} {{author.initials}}
                    <br />
                    <a href="/admin/authors/{{author.id}}/edit" class="btn btn-success"><spring:message code="admin.edit" /></a>
                    <hr role="separator" class="divider" />
                </div>
            </div>
        </div>
    </div>
</body>
</html>
