<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link href="/resources/icons/favicon.png" rel="shortcut icon" type="image/png" />

<jsp:include page="bootstrap_include.jsp" />
<link rel="stylesheet" href="/resources/css/css/font-awesome.min.css">
<link rel="stylesheet" href="/resources/css/style.css">
<meta name="_csrf" content="${_csrf.token}"/>
<!-- default header name is X-CSRF-TOKEN -->
<meta name="_csrf_header" content="${_csrf.headerName}"/>

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