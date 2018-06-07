<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:if test="${error != null}">
    <div class="alert-danger">
        <strong>Помилка! </strong> ${error}
    </div>
</c:if>
