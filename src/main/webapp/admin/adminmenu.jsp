<%@page import="javax.swing.JFileChooser"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../timedoutRedirect.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="style/menu_style.css"/>
        <link rel="stylesheet" type="text/css" href="style/layout4_setup.css"/>
        <link rel="stylesheet" type="text/css" href="style/layout4_text_simple.css"/>
        <link rel="stylesheet" type="text/css" href="style/container.css"/>
        <link rel="stylesheet" type="text/css" href="js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgrid.css">
	<link rel="stylesheet" type="text/css" href="js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgrid_skins.css">
        <script src="js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxcommon.js"></script>
	<script src="js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgrid.js"></script>
        <script src="js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgridcell.js"></script>
        <script src="js/dhtmlxSuite/dhtmlxGrid/codebase/excells/dhtmlxgrid_excell_link.js"></script>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"> 
        <style type="text/css">
            div.gridbox_inverse table.hdr td {background-color:#A0D651; color:white; font-weight:bold;}
            div.gridbox_inverse table.obj td{background-color: #D9EFB9;text-align: center;}
            div.gridbox_inverse table.obj tr{height: 30px;}
        </style>
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
                        <li><a href="./" title="Go to Start page"></a></li>
                        <li><a href="./" title="Get in touch with us"></a></li>
                    </ul>
                </div>
            </div><!-- A.2 HEADER MIDDLE -->
            <div class="header-middle"><!-- Site message --><div class="sitemessage"><h1 style="float:right">Empower PROJECT</h1><h2 style="width:450px">A Empower Service-Oriented Enterprise Application Integration Middleware Addressing the Needs of the European SMEs</h2></div></div>
            <div class="header-bottom"><!-- Navigation Level 2 (Drop-down menus) --><div class="nav2"><ul><li id="current" class="last"><a href="actions.jsp?action=tabSelect&amp;tabIndex=0&amp;menuitemId=tabA"></a></li></ul></div></div>
            <div class="header-breadcrumbs"><ul></ul></div>
        </div>   
        </div>
        <div class="main">
        <div class="main-navigation">
          <div id="menu_grid" style="width:180px; height:120px" class='glossymenu'>
           <script>
              menu_grid = new dhtmlXGridObject("menu_grid");
              menu_grid.setImagePath("js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/");
              menu_grid.setHeader("Menu");//set column names
              menu_grid.setColTypes("link");
              menu_grid.setSkin("light");//set grid skin
              menu_grid.setSkin("inverse");
              menu_grid.init();//initialize grid
              menu_grid.loadXML('DIController?op=get_menu&level=0');
           </script>
         </div>
        </div>    
            <div class="main-content">
               <p style="text-align: justify;"><span style="font-size: small; font-family: verdana,geneva;"><br>Service-Oriented Architectures (SOAs), business process management (BPM), composite applications, and other new application requirements have become the driving force in the market. Integration of enterprise applications (EAI) is the new direction of application development. Rather than create new, separate programs, vendors are starting to integrate existing applications to better use, share and allocate resources within an organization. </span></p>
               <p style="text-align: justify;"><span style="font-size: small; font-family: verdana,geneva;"><br>The Empower project <strong>proposes an innovative framework and the enabling technologies that will allow the European Software SMEs to create their next generation, loosely-coupled, interoperable and easy-to-integrate Commercial-off-the-Shelf software products</strong>, leveraging the quality of the application software and the integration services delivered to their customers. <br><br></span></p><hr class="clear-contentunit">            
            </div>
        </div>
            <div class="footer"><p>Copyright &copy; 2012 Empower Consortium | All Rights Reserved</p></div>
    </center>
    </body>
</html>

