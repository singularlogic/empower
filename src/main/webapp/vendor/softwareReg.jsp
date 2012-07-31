<%@page import="java.rmi.server.Operation"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="../style/menu_style.css"/>
        <link rel="stylesheet" type="text/css" href="../style/layout4_setup.css"/>
        <link rel="stylesheet" type="text/css" href="../style/layout4_text.css"/>
        <link rel="stylesheet" type="text/css" href="../style/container.css"/>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">        
        <title>Vendor Menu</title>
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
                        <li><a href="./" title="Go to Start page">Home</a></li>
                        <li><a href="./" title="Get in touch with us">Contact</a></li>
                    </ul>
                </div>
            </div><!-- A.2 HEADER MIDDLE -->
            <div class="header-middle"><!-- Site message --><div class="sitemessage"><h1 style="float:right">Empower PROJECT</h1><h2 style="width:450px">A Empower Service-Oriented Enterprise Application Integration Middleware Addressing the Needs of the European SMEs</h2></div></div>
            <div class="header-bottom"><!-- Navigation Level 2 (Drop-down menus) --><div class="nav2"><ul><li id="current" class="last"><a href="actions.jsp?action=tabSelect&amp;tabIndex=0&amp;menuitemId=tabA">Home</a></li></ul></div></div>
            <div class="header-breadcrumbs"><ul></ul></div>
        </div>   
        </div>
        <div class="main">
        <div class="main-navigation">
            <div class="round-border-topright"></div>
            <h1 class="first">Menu</h1>
            <div class='glossymenu'>
                <a class='menuitem submenuheader1'>Vendor Menu</a>
                    <div class='submenu1'>
                        <a class='menuitem' href='./softwareReg.jsp'>Register new software</a>
                    </div><!--submenuENDdiv-->
                    <div class='submenu1'>
                        <a class='menuitem' href='./showSoftwareComponent.jsp'>Show software components</a>
                    </div><!--submenuENDdiv-->                
                    <div class='submenu1'>
                        <a class='menuitem' href='/Controller?op=signout'>Logout</a>
                    </div><!--submenuENDdiv--> 
            </div>
            </div>
            <div class="main-content">
                <p><br><br><h2><b>Register Software Component</b></h2>
                <br>
                <% 
                    String operation = request.getParameter("operation");
                    String softwareName = request.getParameter("software_name");
                    String softwareVersion = request.getParameter("software_version");
                            
                    if(operation==null)
                        operation = new String("DIController?op=software_reg");
                    
                    if(softwareName==null)
                        softwareName = new String("");
                    
                    if(softwareVersion==null)
                        softwareVersion = new String("");
                %>
                <form method="POST" action='../<%= operation.toString() %>' name="soft_registration">
                    <table>
                        <tbody>
                            <tr>
                                <td align="right">Name:</td><td align="left"><input type="text" name="software_name" size="20" value='<%= softwareName.toString() %>'></td>
                            </tr>
                            <tr>
                                <td align="right">Version:</td><td align="left"><input type="text" name="software_version" size="20" value='<%= softwareVersion.toString() %>'></td>
                            </tr>
                            <tr>
                                <td align="right"><input type="submit" value="Submit" name="submit_button"></td>
                            </tr>
                        </tbody>
                    </table>
                    <input type='hidden' name='software_id' value='<%= request.getParameter("software_id") %>'>
                </form>
            </div>
        </div>
            <div class="footer"><p>Copyright &copy; 2012 Empower Consortium | All Rights Reserved</p></div>

</center>
</body>
</html>