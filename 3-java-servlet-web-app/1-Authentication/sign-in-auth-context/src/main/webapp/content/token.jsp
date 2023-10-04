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
            <c:if test="${isAutheticatedAndMfa}">
                <br/>
                Auth Context claim is present.
                <br>
            </c:if>
            <c:if test="${not isAutheticatedAndMfa}">
                <br/>
                Auth Context claim (acrs) is NOT present. Click <a href="token_details_mfa">here</a> to try to access a protected route and you will be challenged to complete MFA.
                <br>
            </c:if>
            <br>
            Click here to see your <a class="btn btn-success" href="sign_in_status">Sign-in Status</a>
        </p>
    </div>
</div>
