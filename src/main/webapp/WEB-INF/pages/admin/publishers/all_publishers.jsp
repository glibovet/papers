<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html ng-app="all_authors">
<head>
    <title><spring:message code="admin.publishers.all_publishers" /></title>
    <jsp:include page="../include.jsp" />
</head>
<body>
<jsp:include page="../header.jsp"/>
<div style="width: 90%; margin: auto;" ng-controller="all_authors_ctrl">
    <hr role="separator" class="divider" />
    <div class="row">
        <div class="col-md-3">
            <form ng-submit="filterAuthors()">

                <div class="form-group">
                    <label><spring:message code="admin.page" /></label>
                    <select class="form-control" ng-model="filters.page" id="pages">
                    </select>
                </div>

                <div class="form-group">
                    <label><spring:message code="admin.query" /></label>
                    <input ng-model="filters.query" class="form-control">
                </div>

                <button type="submit" class="btn btn-success"><spring:message code="admin.search" /></button>
            </form>
        </div>
        <div class="col-md-9">

        </div>
    </div>
</div>
<script src="/resources/messages/admin.js"></script>
</body>
</html>
