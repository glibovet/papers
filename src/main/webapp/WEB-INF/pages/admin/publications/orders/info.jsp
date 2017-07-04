<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<!DOCTYPE html>
<html ng-app="publication_order">
<head>
    <title>замовлення публікації #${id}</title>
    <jsp:include page="../../include.jsp" />
</head>
<body>
<jsp:include page="../../header.jsp"/>
<div class="main-container" ng-controller="publication_order">
    <div class="row">
        <p>імейл: {{order.email}}</p>
        <p>дата: {{order.date}}</p>
        <p>статус: {{order.status}}</p>
        <p>публікація: <a href="/admin/publications/{{order.publication_id}}/edit" target="_blank">{{order.publication_name}}</a></p>
        <p>причина: {{order.reason}}</p>
        <p>відмова: {{order.answer}}</p>

        <hr />

        <button class="btn btn-success" ng-click="acceptOrder()">підтвердити замовлення</button>
        <button class="btn btn-danger" ng-click="rejectOrder()">відхилити замовлення</button>
        <div class="form-group">
            <label>причина відхилення</label>
            <textarea class="form-control" ng-model="answer"></textarea>
        </div>
    </div>
</div>
<script src="/resources/js/utils/date.format.min.js"></script>
<script src="/resources/messages/admin.js"></script>
<script src="/resources/js/admin/publications/orders/info_order.js?id=${id}" id="loader"></script>
</body>
</html>
