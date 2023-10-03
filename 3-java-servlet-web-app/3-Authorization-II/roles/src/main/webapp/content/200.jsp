<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="card">
    <h5 class="card-header bg-primary">
        200: OK!
    </h5>
    <div class="card-body">
        <p class="card-text">
            Excellent! You are a member of the role(s) that are allowed to visit this page!
            <br>
            <a class="btn btn-success" href="<c:url value="/token_details"></c:url>">ID Token Details</a>
            <a class="btn btn-success" href="<c:url value="/admin_only"></c:url>">Admins Only</a>
            <a class="btn btn-success" href="<c:url value="/regular_user"></c:url>">Regular Users</a>
        </p>
    </div>
</div>
