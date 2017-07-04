<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html ng-app="all_orders">
<head>
    <title>замовлення публікацій</title>
    <jsp:include page="../../include.jsp" />

    <script src="/resources/js/angular_plugins/autocomplete/angular-autocomplete.min.js"></script>
    <link rel="stylesheet" href="/resources/js/angular_plugins/autocomplete/angular-autocomplete.min.css" />
</head>
<body>
<jsp:include page="../../header.jsp"/>
<div ng-controller="all_orders">
    <hr role="separator" class="divider" />
    <div class="row">
        <div class="col-md-3">
            <form ng-submit="filterOrders()">

                <div class="form-group">
                    <label><spring:message code="admin.page" /></label>
                    <select class="form-control" ng-model="filters.page" id="pages">
                    </select>
                </div>

                <div class="form-group">
                    <label><spring:message code="admin.query" /></label>
                    <input ng-model="filters.query" class="form-control">
                </div>

                <div class="form-group">
                    <label>статус</label>
                    <select ng-model="filters.status" class="form-control">
                        <option value="">--- select ---</option>
                        <option value="NEW">new</option>
                        <option value="REJECTED">rejected</option>
                        <option value="APPLIED">applied</option>
                    </select>
                </div>

                <button type="submit" class="btn btn-success"><spring:message code="admin.search" /></button>
            </form>
        </div>
        <div class="col-md-9">
            <table class="table">
                <thead>
                <tr>
                    <th>імейл користувача</th>
                    <th>дата</th>
                    <th>статус</th>
                    <th><spring:message code="admin.actions" /></th>
                </tr>
                </thead>
                <tbody>
                <tr ng-repeat="order in orders">
                    <td>
                        {{order.email}}
                    </td>
                    <td>
                        {{order.date}}
                    </td>
                    <td>
                        {{order.status}}
                    </td>
                    <td>
                        <a href="/admin/publications/orders/{{order.id}}/info" class="btn btn-success">Інформація</a>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>
<script src="/resources/js/utils/date.format.min.js"></script>
<script src="/resources/messages/admin.js"></script>
<script src="/resources/js/admin/publications/orders/all_orders.js"></script>
</body>
</html>
