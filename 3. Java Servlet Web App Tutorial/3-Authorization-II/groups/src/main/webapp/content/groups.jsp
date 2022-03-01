<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <div class="card">
        <h5 class="card-header bg-primary">
            See Group Memberships
        </h5>
        <div class="card-body">
            <p class="card-text">
                <c:if test="${!groupsOverage}">
                    Your security groups memberships are fewer than the emittable group membership limits for ID tokens
                    (>200). If you have
                    any group memberships, you'll see them here and in the token details page, under the <strong>groups</strong> claim.
                    <br>
                </c:if>
                <c:if test="${groupsOverage}">
                    Your security groups memberships have exceeded the maximum number of groups allowed in the ID token
                    (>200). You will not
                    see your group memberships in the token details page. Note the <strong>_claim_names</strong> and
                    <strong>_claim_sources</strong> claims on the token details page.
                    <br><br>
                    Using the information in the token, the app has called Microsoft Graph to get your group
                    memberships.
                    <br>
                </c:if>

                The app found ${groupsNum} total groups. Displaying up to <strong>10</strong> of them below:
                <br><br>
                ${groups}
                <br>

                <a class="btn btn-success" href="<c:url value="/token_details"></c:url>">Token Details</a>
                <a class="btn btn-success" href="<c:url value="/admin_only"></c:url>">Admins Only</a>
                <a class="btn btn-success" href="<c:url value="/regular_user"></c:url>">Regular Users</a>
            </p>
        </div>
    </div>