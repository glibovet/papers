<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<!DOCTYPE html>
<html ng-app="publication_edit">
<head>
    <title><spring:message code="admin.publications.edit" /> #${id}</title>
    <jsp:include page="../include.jsp" />
    <script src="/resources/js/angular_plugins/autocomplete/angular-autocomplete.min.js"></script>
    <link rel="stylesheet" href="/resources/js/angular_plugins/autocomplete/angular-autocomplete.min.css" />
    <style>
        .delete-button {
            width: 22px;
            height: 22px;
            padding: 1px;
        }
    </style>
</head>
<body>
<jsp:include page="../header.jsp"/>
<div class="main-container" ng-controller="publication_controller">
    <div class="row">
        <form ng-submit="editPublicationForm()">
            <div class="form-group">
                <label>назва</label>
                <input type="text" ng-model="publication.title" class="form-control">
            </div>

            <div class="form-group">
                <label>анотації</label>
                <textarea ng-model="publication.annotation" class="form-control"></textarea>
            </div>

            <div class="form-group">
                <label>тип</label>
                <select ng-model="publication.type" class="form-control">
                    <option value="">--- select ---</option>
                    <option value="MASTER_WORK">дипломна</option>
                    <option value="COURSE_WORK">курсова</option>
                    <option value="ARTICLE">стаття</option>
                    <option value="THESIS">тези</option>
                </select>
            </div>

            <div class="form-group">
                <label>лінк</label>
                <input type="text" ng-model="publication.link" class="form-control">
            </div>

            <hr />

            <div class="form-group">
                <label>видавець</label>
                <autocomplete ng-model="publisher.title" data="publishers_autocompete" on-type="publisherType" attr-placeholder="" attr-input-class="form-control" on-select="publisherSelect"></autocomplete>
            </div>

            <hr />

            <div>
                <h3 style="margin-bottom: 4px;">Автори:</h3>
                <div class="form-group">
                    <label><spring:message code="publishers.url" /></label>
                    <autocomplete data="authors_autocompete" on-type="authorType" attr-placeholder="" attr-input-class="form-control" on-select="authorSelect"></autocomplete>
                </div>
                <div>
                    <span ng-repeat="author in authors">
                        {{author.last_name}} {{author.initials}}
                        <span class="btn btn-danger delete-button" ng-click="deleteAuthorToPublication(author.id)">X</span>
                    </span>
                </div>
            </div>

            <input type="submit" class="btn btn-success" value="<spring:message code="admin.save" />">
        </form>
    </div>
    <div class="row" ng-show="publication.id">
        <hr />

    </div>
</div>
<script src="/resources/messages/admin.js"></script>
<script src="/resources/messages/publication.js"></script>
<script src="/resources/js/admin/publications/edit_publication.js?id=${id}" id="loader"></script>
</body>
</html>
