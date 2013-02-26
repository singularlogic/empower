<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="timedoutRedirect.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="./style/menu_style.css"/>
        <link rel="stylesheet" type="text/css" href="./style/layout4_setup.css"/>        
        <link rel="stylesheet" type="text/css" href="./style/layout4_text.css"/>   
        <link rel="stylesheet" type="text/css" href="./style/container.css"/>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">        
        <title>EMPOWER Signin page</title>
        <meta http-equiv="refresh" content="0;URL='./DIController?op=signout'" />  
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
            <div class="header-middle"><!-- Site message --><div class="sitemessage"><h1 style="float:right">EMPOWER PROJECT</h1><h2 style="width:450px">A Empower Service-Oriented Enterprise Application Integration Middleware Addressing the Needs of the European SMEs</h2></div></div>
            <div class="header-bottom"><!-- Navigation Level 2 (Drop-down menus) --><div class="nav2"><ul><li id="current" class="last"><a href="actions.jsp?action=tabSelect&amp;tabIndex=0&amp;menuitemId=tabA"></a></li></ul></div></div>
            <div class="header-breadcrumbs"><ul></ul></div>
        </div>   
        </div>
        <div class="main" style="clear:both; width:900px; padding-bottom:30px; background:transparent url(./img/bg_main_withoutnav.jpg) top left repeat-y;">
        <!---<div class="main-navigation"></div> --->
        <div class="main-content">
              <br>
                <h2>User login</h2>
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
                
            </div>
            <br>
        </div>
        <div class="footer"><p>Copyright &copy; 2011 - 2013 EMPOWER Consortium | All Rights Reserved</p></div>
      </center>
    </body>
</html>
   