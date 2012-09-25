<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>    

<title>Annotation result</title>
        <link rel="stylesheet" type="text/css" href="./style/menu_style.css"/>
        <link rel="stylesheet" type="text/css" href="./style/layout4_setup.css"/>        
        <link rel="stylesheet" type="text/css" href="./style/layout4_text_simple.css"/>
        <link rel="stylesheet" type="text/css" href="./style/container.css"/>
	<link rel="stylesheet" type="text/css" href="./js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxtree.css">
	<script src="./js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxcommon.js"></script>
	<script src="./js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxtree.js"></script>
        <script src="./js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxcommon.js"></script>
	<script src="./js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgrid.js"></script>
        <script src="./js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgridcell.js"></script>
        <script src="./js/dhtmlxSuite/dhtmlxGrid/codebase/excells/dhtmlxgrid_excell_link.js"></script>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"> 
        <style type="text/css">
            div.gridbox_inverse table.hdr td {background-color:#A0D651; color:white; font-weight:bold;}
            div.gridbox_inverse table.obj td{background-color: #D9EFB9;text-align: center;}
            div.gridbox_inverse table.obj tr{height: 30px;}
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
          <div id="menu_grid" style="width:180px; height:<%=(session.getAttribute("userType").equals("organization"))?150:120%>px" class='glossymenu'>
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
                <br>
                <p><h2>Action was successful</h2>
                <br>
                <% //if (((String)session.getAttribute("xsd")).toString() == "true") 
                if (request.getParameter("dataannotation").equalsIgnoreCase("true"))   {%>
                <p>Data Annotation was successful.
                <% } else { %>
                <p>Annotation for service operation was successful.
                <% } %>
                <% //if (((String)session.getAttribute("xsd")).toString() == "true") 
                if (!request.getParameter("schema_id").equalsIgnoreCase("-1"))   {%>
                <p>Return back to <a href='DIController?op=show_schema&xsd=<%= ((String)session.getAttribute("xsd")).toString() %>'>schema</a>                    
                <% } else { %>
                <p>Return back to <a href='DIController?op=show_service&service=<%= ((String)session.getAttribute("service")).toString() %>'>service</a>                    
                <% } %>
            </div>
    </div>
            <div class="footer"><p>Copyright &copy; 2012 Empower Consortium | All Rights Reserved</p></div>
    </center>                
</body>
</html>
