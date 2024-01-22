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
            Click here to see your <a class="btn btn-success" href="sign_in_status">Sign-in Status</a>
        </p>
    </div>
</div>
