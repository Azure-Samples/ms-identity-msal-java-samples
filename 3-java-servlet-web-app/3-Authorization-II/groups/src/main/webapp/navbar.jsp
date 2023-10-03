<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <a class="navbar-brand" href="<c:url value="/index"></c:url>">Microsoft Identity Platform</a>
    <span class="navbar-text">Authorization II: Use MSAL Java to sign users in and restrict access to routes based on Security Group membership</span>
    <div class="btn-group ml-auto dropleft">
        <ul class="nav navbar-nav navbar-right">
            <c:if test = "${msalAuth.getAuthenticated()}">
            <li class="nav-item">
                <a class="nav-link" href="<c:url value="/token_details"></c:url>">Hello <% out.print(msalAuth.getUsername()); %>!</a>
            </li>
            <li>
                <a class="btn btn-warning" href="<c:url value="/auth/sign_out"></c:url>">Sign Out</a>
            </li>
        </c:if>
        <c:if test = "${!msalAuth.getAuthenticated()}">
            <li><a class="btn btn-success" href="<c:url value="/auth/sign_in"></c:url>">Sign In</a></li>
        </c:if>
        </ul>
    </div>
</nav>