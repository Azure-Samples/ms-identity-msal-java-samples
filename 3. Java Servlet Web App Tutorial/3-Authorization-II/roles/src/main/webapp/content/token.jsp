<jsp:useBean id="msalAuth" scope="session" class="com.microsoft.azuresamples.msal4j.helpers.IdentityContextData"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="card">
    <h5 class="card-header bg-primary">
        ID Token Details
    </h5>
    <div class="card-body">
        <p class="card-text">
            <c:forEach items="${claims}" var="claim">
                <strong>${claim.key}:</strong> ${claim.value}
                <br>
            </c:forEach>
            <br>
            <a class="btn btn-success" href="<c:url value="/admin_only"></c:url>">Admins Only</a>
            <a class="btn btn-success" href="<c:url value="/regular_user"></c:url>">Regular Users</a>
        </p>
    </div>
</div>