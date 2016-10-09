<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link href="/resources/icons/favicon.png" rel="shortcut icon" type="image/png" />

<jsp:include page="../common/bootstrap_include.jsp" />
<link rel="stylesheet" href="/resources/css/css/font-awesome.min.css">

<script src="/resources/js/ejs.min.js"></script>
<script src="/resources/js/ajax.js"></script>
<script>
    Ajax.setCSRF('${_csrf.headerName}', '${_csrf.token}');
</script>
<script src="/resources/js/auth/sign_in.js"></script>
