<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
var messages_${var} = {};
<c:forEach var="key" items="${keys}">
    messages_${var}['${key}'] = "<spring:message code='${key}' javaScriptEscape='true' />";
</c:forEach>