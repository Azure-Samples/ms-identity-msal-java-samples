<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="card">
    <h5 class="card-header bg-primary">
        ID Token Details
    </h5>
    <div class="card-body">
        <!-- <h5 class="card-title"></h5> -->
        <p class="card-text">
            <c:forEach items="${claims}" var="claim">
                <b> ${claim.key} :</b> ${claim.value} <br/>
            </c:forEach>
            Click here to see <a class="btn btn-success" href="<c:url value="./auth_sign_in_status"></c:url>">Sign-in Status</a>
            or <a class="btn btn-success" href="<c:url value="./auth_edit_profile"></c:url>">Edit Your Profile</a>
        </p>
        <!-- <div class="card-footer"></div> -->
    </div>
</div>
