<%-- 
    Document   : index
    Created on : Jul 26, 2012, 10:54:35 AM
    Author     : eleni
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <form method="POST" action="DIController?op=fileupload" name="signin" >
            <table>
                <tbody>
                    <tr>
                        <td align="right">File</td><td align="left"><input type="file" name="file" value="" /></td>
                    </tr>
                    <tr>
                        <td align="right"><input type="submit" value="Submit" name="submit_button"></td>
                    </tr>
                </tbody>
            </table>
        </form>
    </body>
</html>
