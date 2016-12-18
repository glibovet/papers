<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html ng-app="all_publications">
<head>
    <title><spring:message code="admin.publications.publications" /></title>
    <jsp:include page="../include.jsp" />
</head>
<body>
<jsp:include page="../header.jsp"/>
<div ng-controller="all_publications">
    <hr role="separator" class="divider" />
    <div class="row">
        <div class="col-md-3">
            <form ng-submit="filterPublications()">

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
                    <th><spring:message code="publication.title" /></th>
                    <th>annotations</th>
                    <th>authors</th>
                    <th><spring:message code="publication.type" /></th>
                    <th>indexed</th>
                    <th>status</th>
                    <th><spring:message code="admin.actions" /></th>
                </tr>
                </thead>
                <tbody>
                <tr ng-repeat="publication in publications">
                    <td>
                        {{publication.title}}
                    </td>
                    <td>
                        {{publication.annotation}}
                    </td>
                    <td>

                        <span ng-repeat="author in publication.authors">
                            <span>{{author.last_name}} {{author.initials}}</span>
                            <hr />
                        </span>
                    </td>
                    <td>
                        {{publication.type}}
                    </td>
                    <td>
                        <input type="checkbox" disabled ng-checked="publication.in_index">
                        <div ng-show="!publication.in_index">
                            <br />
                            <button class="btn btn-success" ng-click="indexPublication(publication)">index publication</button>
                        </div>
                    </td>
                    <td>
                        {{publication.status_m}}
                        <hr />
                        <div class="form-group">
                            <label>status</label>
                            <select ng-model="publication.status" class="form-control">
                                <option value="">--- select ---</option>
                                <option value="IN_PROCESS">in process</option>
                                <option value="ACTIVE">active</option>
                                <option value="DELETED">deleted</option>
                            </select>
                        </div>
                        <button class="btn btn-success" ng-click="setStatus(publication)">set status</button>
                    </td>
                    <td>
                        <a href="/admin/publications/{{publication.id}}/edit" class="btn btn-success"><spring:message code="admin.edit" /></a>
                        <%--<br />
                        <button class="btn btn-danger" ng-click="deletePublication(publication)"><spring:message code="admin.delete" /></button>--%>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>
<script src="/resources/messages/admin.js"></script>
<script src="/resources/js/admin/publications/all_publications.js"></script>
</body>
</html>