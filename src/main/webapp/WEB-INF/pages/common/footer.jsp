<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script src="/resources/js/ejs.min.js"></script>
<script src="/resources/js/ajax.js"></script>
<script>
    Ajax.setCSRF('${_csrf.headerName}', '${_csrf.token}');
</script>
<script src="/resources/js/md5.min.js"></script>
<script src="/resources/js/auth/sign_in.js"></script>
