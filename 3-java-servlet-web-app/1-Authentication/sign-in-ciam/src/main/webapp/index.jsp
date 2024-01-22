<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <link rel="stylesheet" href="./static/style.css">
    <link rel="icon" type="image/x-icon" href="./static/favicon.ico">
    <title>Authentication: Use MSAL Java to sign in users in your Azure Active Directory tenant</title>
</head>
<body>

<%@ include file="navbar.jsp" %>

<div class="container body-content">
        <jsp:include page="${bodyContent}" ></jsp:include>
</div>

<footer style="text-align: center;">
    <c:if test = "${!isAuthenticated}">
        <svg id="check-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" width="16" height="16"><path fill-rule="evenodd" d="M1.5 8a6.5 6.5 0 1113 0 6.5 6.5 0 01-13 0zM0 8a8 8 0 1116 0A8 8 0 010 8zm11.78-1.72a.75.75 0 00-1.06-1.06L6.75 9.19 5.28 7.72a.75.75 0 00-1.06 1.06l2 2a.75.75 0 001.06 0l4.5-4.5z"></path></svg>
        &nbsp;Have you updated your app's <em>redirect URI</em> on <a target="_blank" href=https://portal.azure.com>Azure Portal</a>?
    </c:if>
    <c:if test = "${isAuthenticated}">
        <a class="nostyle" href="survey">
        <svg id="smiley" viewBox="0 0 16 16" class="msportalfx-svg-palette-inherit" role="presentation" focusable="false" xmlns:svg="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" id="FxSymbol0-01e" data-type="319"><g><title></title><path d="M8 0a7.6 7.6 0 012.1.3 6.4 6.4 0 011.9.8 10.4 10.4 0 011.7 1.2A10.4 10.4 0 0114.9 4a6.4 6.4 0 01.8 1.9 7.5 7.5 0 010 4.2 6.4 6.4 0 01-.8 1.9 10.4 10.4 0 01-1.2 1.7 10.4 10.4 0 01-1.7 1.2 6.4 6.4 0 01-1.9.8 7.5 7.5 0 01-4.2 0 6.4 6.4 0 01-1.9-.8 10.4 10.4 0 01-1.7-1.2A10.4 10.4 0 011.1 12a6.4 6.4 0 01-.8-1.9 7.5 7.5 0 010-4.2A6.4 6.4 0 011.1 4a10.4 10.4 0 011.2-1.7A10.4 10.4 0 014 1.1 6.4 6.4 0 015.9.3 7.6 7.6 0 018 0zm0 15l1.9-.2 1.6-.8a4.9 4.9 0 001.4-1.1 4.9 4.9 0 001.1-1.4 8 8 0 00.8-1.6A12.3 12.3 0 0015 8a12.3 12.3 0 00-.2-1.9 8 8 0 00-.8-1.6 4.9 4.9 0 00-1.1-1.4A4.9 4.9 0 0011.5 2a4.6 4.6 0 00-1.6-.7 6.2 6.2 0 00-3.8 0 4.6 4.6 0 00-1.6.7 4.9 4.9 0 00-1.4 1.1A4.9 4.9 0 002 4.5a4.6 4.6 0 00-.7 1.6 6.2 6.2 0 000 3.8 4.6 4.6 0 00.7 1.6 4.9 4.9 0 001.1 1.4A4.9 4.9 0 004.5 14l1.6.8zm0-3l1.1-.2 1-.4a2.7 2.7 0 00.8-.7l.7-.9.9.4-.8 1.2-1.1.9-1.2.5L8 13l-1.4-.2-1.2-.5-1.1-.9-.8-1.2.9-.4.7.9a2.7 2.7 0 00.8.7l1 .4zM5 7h-.4l-.3-.2-.2-.3A.6.6 0 014 6a.6.6 0 01.1-.4l.2-.3.3-.2h.8l.3.2.2.3A.6.6 0 016 6a.6.6 0 01-.1.4l-.2.3-.3.2zm6 0h-.4l-.3-.2-.2-.3A.6.6 0 0110 6a.6.6 0 01.1-.4l.2-.3.3-.2h.8l.3.2.2.3a.6.6 0 01.1.4.6.6 0 01-.1.4l-.2.3-.3.2z"></path></g></svg>
        <svg id="smiley" viewBox="0 0 16 16" class="msportalfx-svg-palette-inherit" role="presentation" focusable="false" xmlns:svg="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" id="FxSymbol0-01e" data-type="319"><g><title></title><path d="M8 0a7.6 7.6 0 012.1.3 6.4 6.4 0 011.9.8 10.4 10.4 0 011.7 1.2A10.4 10.4 0 0114.9 4a6.4 6.4 0 01.8 1.9 7.5 7.5 0 010 4.2 6.4 6.4 0 01-.8 1.9 10.4 10.4 0 01-1.2 1.7 10.4 10.4 0 01-1.7 1.2 6.4 6.4 0 01-1.9.8 7.5 7.5 0 01-4.2 0 6.4 6.4 0 01-1.9-.8 10.4 10.4 0 01-1.7-1.2A10.4 10.4 0 011.1 12a6.4 6.4 0 01-.8-1.9 7.5 7.5 0 010-4.2A6.4 6.4 0 011.1 4a10.4 10.4 0 011.2-1.7A10.4 10.4 0 014 1.1 6.4 6.4 0 015.9.3 7.6 7.6 0 018 0zm0 15l1.9-.2 1.6-.8a4.9 4.9 0 001.4-1.1 4.9 4.9 0 001.1-1.4 8 8 0 00.8-1.6A12.3 12.3 0 0015 8a12.3 12.3 0 00-.2-1.9 8 8 0 00-.8-1.6 4.9 4.9 0 00-1.1-1.4A4.9 4.9 0 0011.5 2a4.6 4.6 0 00-1.6-.7 6.2 6.2 0 00-3.8 0 4.6 4.6 0 00-1.6.7 4.9 4.9 0 00-1.4 1.1A4.9 4.9 0 002 4.5a4.6 4.6 0 00-.7 1.6 6.2 6.2 0 000 3.8 4.6 4.6 0 00.7 1.6 4.9 4.9 0 001.1 1.4A4.9 4.9 0 004.5 14l1.6.8zm0-3l1.1-.2 1-.4a2.7 2.7 0 00.8-.7l.7-.9.9.4-.8 1.2-1.1.9-1.2.5L8 13l-1.4-.2-1.2-.5-1.1-.9-.8-1.2.9-.4.7.9a2.7 2.7 0 00.8.7l1 .4zM5 7h-.4l-.3-.2-.2-.3A.6.6 0 014 6a.6.6 0 01.1-.4l.2-.3.3-.2h.8l.3.2.2.3A.6.6 0 016 6a.6.6 0 01-.1.4l-.2.3-.3.2zm6 0h-.4l-.3-.2-.2-.3A.6.6 0 0110 6a.6.6 0 01.1-.4l.2-.3.3-.2h.8l.3.2.2.3a.6.6 0 01.1.4.6.6 0 01-.1.4l-.2.3-.3.2z"></path></g></svg>
        <svg id="smiley" viewBox="0 0 16 16" class="msportalfx-svg-palette-inherit" role="presentation" focusable="false" xmlns:svg="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" id="FxSymbol0-01e" data-type="319"><g><title></title><path d="M8 0a7.6 7.6 0 012.1.3 6.4 6.4 0 011.9.8 10.4 10.4 0 011.7 1.2A10.4 10.4 0 0114.9 4a6.4 6.4 0 01.8 1.9 7.5 7.5 0 010 4.2 6.4 6.4 0 01-.8 1.9 10.4 10.4 0 01-1.2 1.7 10.4 10.4 0 01-1.7 1.2 6.4 6.4 0 01-1.9.8 7.5 7.5 0 01-4.2 0 6.4 6.4 0 01-1.9-.8 10.4 10.4 0 01-1.7-1.2A10.4 10.4 0 011.1 12a6.4 6.4 0 01-.8-1.9 7.5 7.5 0 010-4.2A6.4 6.4 0 011.1 4a10.4 10.4 0 011.2-1.7A10.4 10.4 0 014 1.1 6.4 6.4 0 015.9.3 7.6 7.6 0 018 0zm0 15l1.9-.2 1.6-.8a4.9 4.9 0 001.4-1.1 4.9 4.9 0 001.1-1.4 8 8 0 00.8-1.6A12.3 12.3 0 0015 8a12.3 12.3 0 00-.2-1.9 8 8 0 00-.8-1.6 4.9 4.9 0 00-1.1-1.4A4.9 4.9 0 0011.5 2a4.6 4.6 0 00-1.6-.7 6.2 6.2 0 00-3.8 0 4.6 4.6 0 00-1.6.7 4.9 4.9 0 00-1.4 1.1A4.9 4.9 0 002 4.5a4.6 4.6 0 00-.7 1.6 6.2 6.2 0 000 3.8 4.6 4.6 0 00.7 1.6 4.9 4.9 0 001.1 1.4A4.9 4.9 0 004.5 14l1.6.8zm0-3l1.1-.2 1-.4a2.7 2.7 0 00.8-.7l.7-.9.9.4-.8 1.2-1.1.9-1.2.5L8 13l-1.4-.2-1.2-.5-1.1-.9-.8-1.2.9-.4.7.9a2.7 2.7 0 00.8.7l1 .4zM5 7h-.4l-.3-.2-.2-.3A.6.6 0 014 6a.6.6 0 01.1-.4l.2-.3.3-.2h.8l.3.2.2.3A.6.6 0 016 6a.6.6 0 01-.1.4l-.2.3-.3.2zm6 0h-.4l-.3-.2-.2-.3A.6.6 0 0110 6a.6.6 0 01.1-.4l.2-.3.3-.2h.8l.3.2.2.3a.6.6 0 01.1.4.6.6 0 01-.1.4l-.2.3-.3.2z"></path></g></svg>
        <svg id="smiley" viewBox="0 0 16 16" class="msportalfx-svg-palette-inherit" role="presentation" focusable="false" xmlns:svg="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" id="FxSymbol0-01e" data-type="319"><g><title></title><path d="M8 0a7.6 7.6 0 012.1.3 6.4 6.4 0 011.9.8 10.4 10.4 0 011.7 1.2A10.4 10.4 0 0114.9 4a6.4 6.4 0 01.8 1.9 7.5 7.5 0 010 4.2 6.4 6.4 0 01-.8 1.9 10.4 10.4 0 01-1.2 1.7 10.4 10.4 0 01-1.7 1.2 6.4 6.4 0 01-1.9.8 7.5 7.5 0 01-4.2 0 6.4 6.4 0 01-1.9-.8 10.4 10.4 0 01-1.7-1.2A10.4 10.4 0 011.1 12a6.4 6.4 0 01-.8-1.9 7.5 7.5 0 010-4.2A6.4 6.4 0 011.1 4a10.4 10.4 0 011.2-1.7A10.4 10.4 0 014 1.1 6.4 6.4 0 015.9.3 7.6 7.6 0 018 0zm0 15l1.9-.2 1.6-.8a4.9 4.9 0 001.4-1.1 4.9 4.9 0 001.1-1.4 8 8 0 00.8-1.6A12.3 12.3 0 0015 8a12.3 12.3 0 00-.2-1.9 8 8 0 00-.8-1.6 4.9 4.9 0 00-1.1-1.4A4.9 4.9 0 0011.5 2a4.6 4.6 0 00-1.6-.7 6.2 6.2 0 00-3.8 0 4.6 4.6 0 00-1.6.7 4.9 4.9 0 00-1.4 1.1A4.9 4.9 0 002 4.5a4.6 4.6 0 00-.7 1.6 6.2 6.2 0 000 3.8 4.6 4.6 0 00.7 1.6 4.9 4.9 0 001.1 1.4A4.9 4.9 0 004.5 14l1.6.8zm0-3l1.1-.2 1-.4a2.7 2.7 0 00.8-.7l.7-.9.9.4-.8 1.2-1.1.9-1.2.5L8 13l-1.4-.2-1.2-.5-1.1-.9-.8-1.2.9-.4.7.9a2.7 2.7 0 00.8.7l1 .4zM5 7h-.4l-.3-.2-.2-.3A.6.6 0 014 6a.6.6 0 01.1-.4l.2-.3.3-.2h.8l.3.2.2.3A.6.6 0 016 6a.6.6 0 01-.1.4l-.2.3-.3.2zm6 0h-.4l-.3-.2-.2-.3A.6.6 0 0110 6a.6.6 0 01.1-.4l.2-.3.3-.2h.8l.3.2.2.3a.6.6 0 01.1.4.6.6 0 01-.1.4l-.2.3-.3.2z"></path></g></svg>
        <svg id="smiley" viewBox="0 0 16 16" class="msportalfx-svg-palette-inherit" role="presentation" focusable="false" xmlns:svg="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" id="FxSymbol0-01e" data-type="319"><g><title></title><path d="M8 0a7.6 7.6 0 012.1.3 6.4 6.4 0 011.9.8 10.4 10.4 0 011.7 1.2A10.4 10.4 0 0114.9 4a6.4 6.4 0 01.8 1.9 7.5 7.5 0 010 4.2 6.4 6.4 0 01-.8 1.9 10.4 10.4 0 01-1.2 1.7 10.4 10.4 0 01-1.7 1.2 6.4 6.4 0 01-1.9.8 7.5 7.5 0 01-4.2 0 6.4 6.4 0 01-1.9-.8 10.4 10.4 0 01-1.7-1.2A10.4 10.4 0 011.1 12a6.4 6.4 0 01-.8-1.9 7.5 7.5 0 010-4.2A6.4 6.4 0 011.1 4a10.4 10.4 0 011.2-1.7A10.4 10.4 0 014 1.1 6.4 6.4 0 015.9.3 7.6 7.6 0 018 0zm0 15l1.9-.2 1.6-.8a4.9 4.9 0 001.4-1.1 4.9 4.9 0 001.1-1.4 8 8 0 00.8-1.6A12.3 12.3 0 0015 8a12.3 12.3 0 00-.2-1.9 8 8 0 00-.8-1.6 4.9 4.9 0 00-1.1-1.4A4.9 4.9 0 0011.5 2a4.6 4.6 0 00-1.6-.7 6.2 6.2 0 00-3.8 0 4.6 4.6 0 00-1.6.7 4.9 4.9 0 00-1.4 1.1A4.9 4.9 0 002 4.5a4.6 4.6 0 00-.7 1.6 6.2 6.2 0 000 3.8 4.6 4.6 0 00.7 1.6 4.9 4.9 0 001.1 1.4A4.9 4.9 0 004.5 14l1.6.8zm0-3l1.1-.2 1-.4a2.7 2.7 0 00.8-.7l.7-.9.9.4-.8 1.2-1.1.9-1.2.5L8 13l-1.4-.2-1.2-.5-1.1-.9-.8-1.2.9-.4.7.9a2.7 2.7 0 00.8.7l1 .4zM5 7h-.4l-.3-.2-.2-.3A.6.6 0 014 6a.6.6 0 01.1-.4l.2-.3.3-.2h.8l.3.2.2.3A.6.6 0 016 6a.6.6 0 01-.1.4l-.2.3-.3.2zm6 0h-.4l-.3-.2-.2-.3A.6.6 0 0110 6a.6.6 0 01.1-.4l.2-.3.3-.2h.8l.3.2.2.3a.6.6 0 01.1.4.6.6 0 01-.1.4l-.2.3-.3.2z"></path></g></svg>

        &nbsp; Does this sample address your <strong>learning objective</strong>?
        </a>
    </c:if>

    <br><br>
    <p>&copy; 2020<p>
</footer>
<!-- jQuery first, then Popper.js, then Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>  </body>
</html>
