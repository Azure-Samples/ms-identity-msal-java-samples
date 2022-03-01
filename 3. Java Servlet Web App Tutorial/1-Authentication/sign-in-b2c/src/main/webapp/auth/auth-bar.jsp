<c:if test = "${msalAuth.getAuthenticated()}">
    <li class="nav-item">
        <a class="nav-link" href="<c:url value="./auth_token_details"></c:url>">Hello <% out.print(msalAuth.getUsername()); %>!</a>
    </li>
    <li>
        <a class="btn btn-warning" href="<c:url value="./auth_sign_out"></c:url>">Sign Out</a>
    </li>
</c:if>
<c:if test = "${!msalAuth.getAuthenticated()}">
    <li><a class="btn btn-success" href="<c:url value="./auth_sign_in"></c:url>">Sign In</a></li>
</c:if>