<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="card">
    <h5 class="card-header bg-primary">
        403: Forbidden
    </h5>
    <div class="card-body">
        <p class="card-text">
            Visiting this page requires the signed in user to be assigned to <strong>the correct role(s)</strong> as defined in the authentication.properties file.
            <br>
            <br>
            <a class="btn btn-success" href="<c:url value="/token_details"></c:url>">ID Token Details</a>
            <a class="btn btn-success" href="<c:url value="/admin_only"></c:url>">Admins Only</a>
            <a class="btn btn-success" href="<c:url value="/regular_user"></c:url>">Regular Users</a>
        </p>
    </div>
</div>
