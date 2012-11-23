<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="timedoutRedirect.jsp" %>
<!DOCTYPE html>
<html>
<head>
<title>Browse components</title>
        <link rel="stylesheet" type="text/css" href="../style/menu_style.css"/>
        <link rel="stylesheet" type="text/css" href="../style/layout4_setup.css"/>        
        <link rel="stylesheet" type="text/css" href="../style/layout4_text_simple.css"/>        
        <link rel="stylesheet" type="text/css" href="../style/container.css"/>
        <link rel="stylesheet" type="text/css" href="../js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgrid.css">
	<link rel="stylesheet" type="text/css" href="../js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgrid_skins.css">        
        <script src="../js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxcommon.js"></script>
	<script src="../js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgrid.js"></script>
        <script src="../js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgridcell.js"></script>
        <script src="../js/dhtmlxSuite/dhtmlxGrid/codebase/excells/dhtmlxgrid_excell_link.js"></script>
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
        <div class="main">
        <div class="main-navigation">
             <div id="menu_grid" style="width:180px; height:180px" class='glossymenu <%=session.getAttribute("userType")%>'>
                    <script>
                        menu_grid = new dhtmlXGridObject("menu_grid");
                        menu_grid.setImagePath("js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/");
                        menu_grid.setHeader("Menu");//set column names
                        menu_grid.setColTypes("link");
                        menu_grid.setSkin("light");//set grid skin
                        menu_grid.setSkin("inverse");
                        menu_grid.init();//initialize grid
                        menu_grid.loadXML('../DIController?op=get_menu&level=2');

                    </script>
              </div>
        </div>    
            <div class="main-content">
                <br>
                <% 
                    String bridge = request.getParameter("bridge"); 
                    if(bridge==null)
                        bridge = new String("no");
                %> 
                
                <% if (request.getParameter("bridging").equalsIgnoreCase("true")){%>
                <p id="title"><h2>Create My Bridges at Schema or Service Level</h2></p>
                <%}else{%>
                <p id="title"><h2>Do Data and Functional Annotation at a Schema or Service Level</h2></p>
                <%}%>
                <div id="box_grid" style="width:580px; height:600px"/>
                <script>
                        grid = new dhtmlXGridObject("box_grid");
                        grid.setImagePath("/js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/");
                        grid.setHeader("ID,Component name,Version,Schemas,Services");//set column names
//                        grid.attachEvent("onRowSelect", doOnRowSelected);
                        grid.setColTypes("ro,ro,ro,link,link");
                        grid.setSkin("light");//set grid skin
                        grid.init();//initialize grid
                        grid.setColWidth(0, '20');
                        grid.setColWidth(1, '30');
                        grid.setColWidth(2, '20');
                        grid.setColWidth(3, '15');
                        grid.setColWidth(4, '15');
                        grid.loadXML("../DIController?op=show_components&bridging="+<%=request.getParameter("bridging")%>);

//                        function doOnRowSelected(rowID,celInd){
//                            location.replace('showServices.jsp?bridge=<%= bridge.toString() %>&software_name=' + rowID);
//                        }

                </script>
            </div>
        </div>
    </div>
            <div class="footer"><p>Copyright &copy; 2012 EMPOWER Consortium | All Rights Reserved</p></div>                                                
        
    </center>                                
</body>
</html>
