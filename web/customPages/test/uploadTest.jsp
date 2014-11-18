<html>
<head>
<title>File Uploading Form</title>
</head>
<body>
<h3>File Upload:</h3>
Select a file to upload for preview: <br />
<form action="/Preview" method="post" enctype="multipart/form-data">

    <input type="hidden" name="session" value="DummySessionToken"/>
    <input type="file" name="file" size="50" />
<br />
<input type="submit" value="Preview File" />
</form>
</body>
</html>