<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<!DOCTYPE html>
<html ng-app="author_edit">
<head>
    <title><spring:message code="admin.authors.edit" /> #${id}</title>
    <jsp:include page="../include.jsp" />
</head>
<body>
<jsp:include page="../header.jsp"/>
<div class="main-container" ng-controller="author_controller">
    <div class="row" ng-controller="author_master_edit_controller">
        <h3><spring:message code="admin.authors.master" /></h3>
        <form ng-submit="authorMasterSave()">
            <div class="form-group">
                <label><spring:message code="admin.authors.last_name" /></label>
                <input type="text" ng-model="author.last_name" class="form-control">
            </div>

            <div class="form-group">
                <label><spring:message code="admin.authors.initials" /></label>
                <input type="text" ng-model="author.initials" class="form-control">
            </div>

            <input type="submit" class="btn btn-success" value="<spring:message code="admin.save" />">
        </form>
    </div>
    <hr role="separator" class="divider" />
    <div ng-controller="sub_authors" style="{{!author.id ? 'display: none;' : ''}}">
        <div class="row" ng-controller="sub_author_edit">
            <h3><spring:message code="admin.authors.sub_authors" /></h3>
            <form ng-submit="subAuthorSave()">
                <div class="form-group">
                    <label><spring:message code="admin.authors.last_name" /></label>
                    <input type="text" ng-model="author_edit.last_name" class="form-control">
                </div>

                <div class="form-group">
                    <label><spring:message code="admin.authors.initials" /></label>
                    <input type="text" ng-model="author_edit.initials" class="form-control">
                </div>

                <div class="form-group">
                    <label><spring:message code="admin.authors.original" /></label>
                    <input type="text" ng-model="author_edit.original" class="form-control">
                </div>

                <input type="submit" class="btn btn-success" value="<spring:message code="admin.save" />">
            </form>
        </div>
    </div>
</div>
<script src="/resources/messages/admin.js"></script>
<script src="/resources/js/admin/authors/edit_author.js?id=${id}" id="loader"></script>

</body>
</html>
