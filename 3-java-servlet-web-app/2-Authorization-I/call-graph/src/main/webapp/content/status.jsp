<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="card">
    <h5 class="card-header bg-primary">
        <c:if test = "${isAuthenticated}">You're signed in!</c:if>
        <c:if test = "${!isAuthenticated}">You're not signed in.</c:if>
    </h5>
    <div class="card-body">
        <p class="card-text">
            <c:if test = "${isAuthenticated}">
                Click here to get your <a class="btn btn-success" href="token_details">ID Token Details</a>
                or <a class="btn btn-success" href="call_graph">Call Graph</a>
            </c:if>
            <c:if test = "${!isAuthenticated}">
                Use the button on the top right to sign in.
                Attempts to get your <a href="token_details">ID Token Details</a>
                or <a href="call_graph">Call Graph</a> will result in a 401 error.
            </c:if>
        </p>
    </div>
</div>
