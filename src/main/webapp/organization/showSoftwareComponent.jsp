<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
            <div class="header-middle"><!-- Site message --><div class="sitemessage"><h1 style="float:right">EMPOWER PROJECT</h1><h2 style="width:450px">A Empower Service-Oriented Enterprise Application Integration Middleware Addressing the Needs of the European SMEs</h2></div></div>            
            <div class="header-bottom"><!-- Navigation Level 2 (Drop-down menus) --><div class="nav2"><ul><li id="current" class="last"><a href="actions.jsp?action=tabSelect&amp;tabIndex=0&amp;menuitemId=tabA">Home</a></li></ul></div></div>
            <div class="header-breadcrumbs"><ul></ul></div>
        </div>   
        </div>
        <div class="main">
        <div class="main-navigation">
            <div class="round-border-topright"></div>
            <h1 class="first">Menu</h1>
            <div class='glossymenu'>
                <a class='menuitem submenuheader1'>Organization Menu</a>
                    <div class='submenu1'>
                        <a class='menuitem' href='showSoftwareComponent.jsp'>Show software components</a>
                    </div><!--submenuENDdiv-->  
 
                    <div class='submenu1'>
                        <a class='menuitem' href='showMyBridges.jsp'>Show My Bridges</a>
                    </div><!--submenuENDdiv-->                  
                    <div class='submenu1'>
                        <a class='menuitem' href='../DIController?op=signout'>Logout</a>
                    </div><!--submenuENDdiv-->                     
            </div>
        </div>    
            <div class="main-content">
                <br>
                <% 
                    String bridge = request.getParameter("bridge"); 
                    if(bridge==null)
                        bridge = new String("no");
                %>                   
                <div id="box_grid" style="width:500px; height:200px"/>
                <script>
                        grid = new dhtmlXGridObject("box_grid");
                        grid.setImagePath("/js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/");
                        grid.setHeader("ID,Component name,Version,Schemas, ");//set column names
//                        grid.attachEvent("onRowSelect", doOnRowSelected);
                        grid.setColTypes("ro,link,ro,link,link");
                        grid.setSkin("light");//set grid skin
                        grid.init();//initialize grid
                        grid.loadXML("../DIController?op=show_components");

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
