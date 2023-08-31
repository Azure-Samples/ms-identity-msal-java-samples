<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="card">
    <h5 class="card-header bg-primary">
        Call Graph /me Endpoint
    </h5>
    <div class="card-body">
        <p class="card-text">
            <c:forEach items="${user}" var="user">
                <strong>${user.key}:</strong> ${user.value}
                <br>
            </c:forEach>
            <br>
            Click here to see your <a class="btn btn-success" href="sign_in_status">Sign-in Status</a>
            or <a class="btn btn-success" href="token_details">Token Details</a>
        </p>
    </div>
</div>
