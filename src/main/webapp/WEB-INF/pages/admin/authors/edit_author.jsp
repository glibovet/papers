<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html ng-app="author_edit">
<head>
    <title>Редагувати автора</title>
    <jsp:include page="../include.jsp" />
    <script src="/resources/js/admin/authors/edit_author.js?id=${id}" id="loader"></script>
</head>
<body>
<jsp:include page="../header.jsp"/>
<div class="main-container" ng-controller="author_controller">
    <div class="row" ng-controller="author_master_edit_controller">
        <form ng-submit="authorMasterSave()">
            <div class="form-group">
                <label>Прізвище</label>
                <input type="text" ng-model="author.last_name" class="form-control">
            </div>

            <div class="form-group">
                <label>Ініціали</label>
                <input type="text" ng-model="author.initials" class="form-control">
            </div>

            <input type="submit" class="btn btn-success">
        </form>
    </div>
    <hr role="separator" class="divider" />
</div>
</body>
</html>
