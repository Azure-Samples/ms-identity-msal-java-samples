<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <a class="navbar-brand" href="index">Microsoft Identity Platform</a>
    <span class="navbar-text">Authentication: Use MSAL Java to sign in users in your Azure Active Directory tenant</span>
    <div class="btn-group ml-auto dropleft">
        <ul class="nav navbar-nav navbar-right">
            <c:if test = "${isAuthenticated}">
            <li class="nav-item">
                <a class="nav-link" href="token_details">Hello ${username}!</a>
            </li>
            <li>
                <a class="btn btn-warning" href="auth/sign_out">Sign Out</a>
            </li>
        </c:if>
        <c:if test = "${!isAuthenticated}">
            <li><a class="btn btn-success" href="auth/sign_in">Sign In</a></li>
        </c:if>
        </ul>
    </div>
</nav>
