<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <div class="card">
        <h5 class="card-header bg-primary">
            403: Forbidden
        </h5>
        <div class="card-body">
            <p class="card-text">
                Visiting this page requires the signed in user to be assigned to <strong>the correct group(s)</strong>
                as defined in the authentication.properties file.
                <br><br>
                Click the Groups button to check if you are a member of any security groups that have been emitted in
                the ID token or obtained via Microsoft Graph. If you you do not see any groups, use the Readme
                instructions to add your user to one or more security
                groups. Follow the guidance in the Readme to make sure that the groups will be emitted in the ID token.
                <br><br>
                If you do see group memberships, copy some values and enter them into the relevant fields in the
                authentication.properties file.
                Use "mvn clean package" and then restart the app.
                <br><br>
                <a class="btn btn-success" href="<c:url value="/groups"></c:url>">Groups</a>
                <a class="btn btn-success" href="<c:url value="/admin_only"></c:url>">Admins Only</a>
                <a class="btn btn-success" href="<c:url value="/regular_user"></c:url>">Regular Users</a>
            </p>
        </div>
    </div>