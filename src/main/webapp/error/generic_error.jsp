<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>    

<title>Error result</title>
        <link rel="stylesheet" type="text/css" href="style/menu_style.css"/>
        <link rel="stylesheet" type="text/css" href="style/layout4_setup.css"/>
        <link rel="stylesheet" type="text/css" href="style/layout4_text.css"/>
        <link rel="stylesheet" type="text/css" href="style/container.css"/>
	    <link rel="stylesheet" type="text/css" href="js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxtree.css">
	    <script src="js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxcommon.js"></script>
	    <script src="js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxtree.js"></script>
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
            <div class="header-middle"><!-- Site message --><div class="sitemessage"><h1 style="float:right">Empower PROJECT</h1><h2 style="width:450px">A Semantic Service-Oriented Enterprise Application Integration Middleware Addressing the Needs of the European SMEs</h2></div></div>                                    
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
                        <a class='menuitem' href='softwareReg.jsp'>Register new software</a>
                    </div>
                    <div class='submenu1'>
                        <a class='menuitem' href='showSoftwareComponent.jsp'>Show software components</a>
                    </div>
                    <div class='submenu1'>
                        <a class='menuitem' href='../Controller?op=signout'>Logout</a>
                    </div>
            </div>

        </div>    

            <div class="main-content">
                <br>
                <p><h2>Error occured</h2>
                <br>
                <p><%= ((String)request.getParameter("errormsg")).toString()%>
            </div>
    </div>
            <div class="footer"><p>Copyright &copy; 2011 Empower Consortium | All Rights Reserved</p></div>
    </center>                
</body>
</html>
