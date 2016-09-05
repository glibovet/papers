<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link href="/resources/icons/favicon.png" rel="shortcut icon" type="image/png" />

<jsp:include page="bootstrap_include.jsp" />
<link rel="stylesheet" href="/resources/css/css/font-awesome.min.css">
<link rel="stylesheet" href="/resources/css/style.css">

<script src="/resources/js/ejs.min.js"></script>

<script>
    $(function () {
        var token = '${_csrf.parameterName}';
        var header = '${_csrf.token}';
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $('form').append('<input type="hidden"' +
                'name="${_csrf.parameterName}"' +
                'value="${_csrf.token}"/>');
    });
</script>