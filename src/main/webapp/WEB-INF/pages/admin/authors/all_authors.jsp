<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html ng-app="all_authors">
<head>
    <title>Всі автори</title>
    <jsp:include page="../include.jsp" />
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
                <div class="row">
                    <table class="table">
                        <thead>
                        <tr>
                            <th><spring:message code="authors.last_name" /></th>
                            <th><spring:message code="authors.initials" /></th>
                            <th><spring:message code="admin.actions" /></th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr ng-repeat="author in authors">
                            <td>
                                {{author.last_name}}
                            </td>
                            <td>
                                {{author.initials}}
                            </td>
                            <td>
                                <a href="/admin/authors/{{author.id}}/edit" class="btn btn-success"><spring:message code="admin.edit" /></a>
                                <br />
                                <button class="btn btn-danger" ng-click="deleteAuthor(author)"><spring:message code="admin.delete" /></button>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <script src="/resources/messages/admin.js"></script>
    <script src="/resources/js/admin/authors/all_authors.js"></script>
</body>
</html>
