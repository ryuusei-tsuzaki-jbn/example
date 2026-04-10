<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Debug Login</title>
</head>
<body>
<h1>Debug Login</h1>
<form method="post" action="${pageContext.request.contextPath}/login">
    name: <input name="username"><br>
    password: <input name="password"><br>
    age: <input name="age"><br>
    <button type="submit">login</button>
</form>
<p style="color: red;">${errorMessage}</p>
</body>
</html>
