<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link href="/resources/icons/favicon.png" rel="shortcut icon" type="image/png" />

<jsp:include page="../common/bootstrap_include.jsp" />
<link rel="stylesheet" href="/resources/css/css/font-awesome.min.css">

<script src="/resources/js/ejs.min.js"></script>
<script>
    var HEADERS = {
        '${_csrf.headerName}': '${_csrf.token}',
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
</script>
<script src="/resources/js/auth/sign_in.js"></script>
<script src="/resources/js/utils/parse_url.js"></script>

<script src="/resources/js/angular.min.js"></script>
<script src="/resources/js/angular_plugins/notification/angular-ui-notification.min.js"></script>
<link rel="stylesheet" href="/resources/js/angular_plugins/notification/angular-ui-notification.min.css" />

