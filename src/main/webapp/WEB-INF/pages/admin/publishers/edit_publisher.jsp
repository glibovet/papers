<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<!DOCTYPE html>
<html ng-app="publisher_edit">
<head>
    <title><spring:message code="admin.publishers.edit" /> #${id}</title>
    <jsp:include page="../include.jsp" />
</head>
<body>
<jsp:include page="../header.jsp"/>
<div class="main-container" ng-controller="publisher_controller">
    <div class="row" ng-controller="publisher_edit">
        <form ng-submit="editPublisherForm()">
            <div class="form-group">
                <label><spring:message code="publishers.title" /></label>
                <input type="text" ng-model="publisher.title" class="form-control">
            </div>

            <div class="form-group">
                <label><spring:message code="publishers.description" /></label>
                <textarea ng-model="publisher.description" class="form-control"></textarea>
            </div>

            <div class="form-group">
                <label><spring:message code="publishers.contacts" /></label>
                <input type="text" ng-model="publisher.contacts" class="form-control">
            </div>

            <div class="form-group">
                <label><spring:message code="publishers.url" /></label>
                <input type="text" ng-model="publisher.url" class="form-control">
            </div>

            <input type="submit" class="btn btn-success" value="<spring:message code="admin.save" />">
        </form>
    </div>
</div>
<script src="/resources/messages/admin.js"></script>
<script src="/resources/messages/publisher.js"></script>
<script src="/resources/js/admin/publishers/edit_publisher.js?id=${id}" id="loader"></script>
</body>
</html>
