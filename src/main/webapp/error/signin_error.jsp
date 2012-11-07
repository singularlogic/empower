<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>    

        <title>Error result</title>
        <link rel="stylesheet" type="text/css" href="./style/menu_style.css"/>
        <link rel="stylesheet" type="text/css" href="./style/layout4_setup.css"/>        
        <link rel="stylesheet" type="text/css" href="./style/layout4_text.css"/>   
        <link rel="stylesheet" type="text/css" href="./style/container.css"/>    

        <style type="text/css">
            .main {
                background: url("./img/bg_main_withoutnav.jpg") repeat-y scroll left top transparent;
        </style>
        </head> 
        <body class="yui-skin-sam">
        <center>
            <div class="page_container">  
                <div class="header"><!-- A.1 HEADER TOP -->
                    <div class="header-top">
                        <!-- Sitelogo and sitename -->
                        <div class="sitename" style="margin-left:10px">
                        </div><!-- Navigation Level 1 top menu links-->
                        <div class="nav1">
                            <ul>
                                <li><a href="./" title="Go to Start page"></a></li>
                                <li><a href="./" title="Get in touch with us"></a></li>
                            </ul>
                        </div>
                    </div><!-- A.2 HEADER MIDDLE -->
                    <div class="header-middle"><!-- Site message --><div class="sitemessage"><h1 style="float:right">Empower PROJECT</h1><h2 style="width:450px">A Semantic Service-Oriented Enterprise Application Integration Middleware Addressing the Needs of the European SMEs</h2></div></div>                                    
                    <div class="header-bottom"><!-- Navigation Level 2 (Drop-down menus) --><div class="nav2"><ul><li id="current" class="last"><a href="actions.jsp?action=tabSelect&amp;tabIndex=0&amp;menuitemId=tabA">Home</a></li></ul></div></div>
                    <div class="header-breadcrumbs"><ul></ul></div>
                </div>   
            </div>
            <div class="main">
                <div class="main-content">
                    <br>
                    <h2>User login</h2><div style="font-size: 8px;">if you don't have a user account please <a href="signup.jsp">signup</a></div>
                    <form method="POST" action="DIController?op=signin" name="signin" >
                        <table>
                            <tbody>
                                <tr>
                                    <td align="right">Name:</td><td align="left"><input type="text" name="name"></td>
                                </tr>
                                <tr>
                                    <td align="right">Password:</td><td align="left"><input type="password" name="password"></td>
                                </tr>
                                <tr>
                                    <td align="right"><input type="submit" value="Submit" name="submit_button"></td>
                                </tr>
                            </tbody>
                        </table>
                    </form>  
                    <div>
                        <br>
                        <p><h2>User Information</h2>
                        <br>
                        <p><%= ((String) request.getParameter("errormsg")).toString()%>
                    </div>
                </div>

            </div>
            <div class="footer"><p>Copyright &copy; 2011 Empower Consortium | All Rights Reserved</p></div>
        </center>                
    </body>
</html>
