<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html ng-app="all_publishers">
<head>
    <title><spring:message code="admin.publishers.all_publishers" /></title>
    <jsp:include page="../include.jsp" />
</head>
<body>
<jsp:include page="../header.jsp"/>
<div style="width: 90%; margin: auto;" ng-controller="all_publishers">
    <hr role="separator" class="divider" />
    <div class="row">
        <div class="col-md-3">
            <form ng-submit="filterPublishers()">

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
            <table class="table">
                <thead>
                <tr>
                    <th><spring:message code="publishers.title" /></th>
                    <th><spring:message code="publishers.url" /></th>
                    <th><spring:message code="admin.actions" /></th>
                </tr>
                </thead>
                <tbody>
                <tr ng-repeat="publisher in publishers">
                    <td>
                        {{publisher.title}}
                    </td>
                    <td>
                        {{publisher.url}}
                    </td>
                    <td>
                        <a href="/admin/publishers/{{publisher.id}}/edit" class="btn btn-success"><spring:message code="admin.edit" /></a>
                        <br />
                        <button class="btn btn-danger" ng-click="deletePublisher(publisher)"><spring:message code="admin.delete" /></button>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>
<script src="/resources/messages/admin.js"></script>
<script src="/resources/js/admin/publishers/all_publishers.js"></script>
</body>
</html>
