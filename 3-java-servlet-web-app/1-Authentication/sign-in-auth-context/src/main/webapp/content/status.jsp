<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="card">
    <h5 class="card-header bg-primary">
        <c:if test = "${isAuthenticated}">You're signed in!</c:if>
        <c:if test = "${!isAuthenticated}">You're not signed in.</c:if>
    </h5>
    <div class="card-body">
        <p class="card-text">

            <c:if test = "${isAuthenticated}">
                You are authenticated. Click here to get your <a class="btn btn-success" href="token_details">ID Token Details</a>
                <br/><br/>
                <c:if test = "${isAutheticatedAndMfa}">
                    You have also passed MFA. You can access <a href="token_details_mfa">this page</a>.
                </c:if>
                <c:if test = "${not isAutheticatedAndMfa}">
                    You are authenticated but you have not passed MFA. If you try to access <a href="token_details_mfa">this page</a> you will be challenged.
                </c:if>
            </c:if>
            <c:if test = "${not isAuthenticated}">
                Use the button on the top right to sign in.
                Attempts to get your <a href="token_details">ID Token Details</a> will result in a 401 error.
                Attempts to get to this <a href="token_details_mfa"> page </a> will result in an auth challenge + MFA.
            </c:if>
        </p>
    </div>
</div>
