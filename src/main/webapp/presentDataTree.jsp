<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>  
<script>
        function replaceValue()
        {
            document.forms['annotationf'].elements['selections'].value  = tree.getAllChecked();
            document.forms['annotationf'].elements['centraltree'].value = centralTree.getAllChecked();                
//            alert(centraltree.getAllChecked());
        }
        
        function msg()
        {
            alert(centralTree.getAllChecked());
        }
        
       
        
</script>
<title>Presenting service in tree form</title>
        <link rel="stylesheet" type="text/css" href="./style/menu_style.css"/>
        <link rel="stylesheet" type="text/css" href="./style/layout4_setup.css"/>
        <link rel="stylesheet" type="text/css" href="./style/container.css"/>
	<link rel="stylesheet" type="text/css" href="./js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxtree.css">
	<script src="./js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxcommon.js"></script>
	<script src="./js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxtree.js"></script>
        
        <link rel="stylesheet" type="text/css" href="js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgrid.css">
	<link rel="stylesheet" type="text/css" href="js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgrid_skins.css">
        <script src="./js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxcommon.js"></script>
	<script src="./js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgrid.js"></script>
        <script src="./js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgridcell.js"></script>
        <script src="./js/dhtmlxSuite/dhtmlxGrid/codebase/excells/dhtmlxgrid_excell_link.js"></script>
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
            <div class="header-middle"><!-- Site message --><div class="sitemessage"><h1 style="float:right">Empower PROJECT</h1><h2 style="width:450px">A Empower Service-Oriented Enterprise Application Integration Middleware Addressing the Needs of the European SMEs</h2></div></div>
            <div class="header-bottom"><!-- Navigation Level 2 (Drop-down menus) --><div class="nav2"><ul><li id="current" class="last"><a href="actions.jsp?action=tabSelect&amp;tabIndex=0&amp;menuitemId=tabA">Home</a></li></ul></div></div>
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
              menu_grid.setSkin("inverse");
              menu_grid.init();//initialize grid
              menu_grid.loadXML('DIController?op=get_menu&level=0');
           </script>
         </div>
        </div>
            <div class="main-content">
                <br>
                <p><h2>Service messages</h2>
                <br>
                <div id="box_tree" style="width:500px; height:400px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;"/>
                <script>
                        tree = new dhtmlXTreeObject("box_tree", "100%", "100%", 0);
                        tree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                        tree.enableCheckBoxes(true, false);
                        tree.loadXML('./DIController?op=present_service_schema&schema_id=<%= request.getParameter("schema_id") %>', null);

                       // tree.loadXML('../VendorManager?op=present_service&service_id=<%= request.getParameter("service_id") %>', null);
                </script>
                </div>
                <br>
                <p><h2>Select data facet</h2>
                <br>
                <div id="central_tree" style="width:500px; height:200px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;"/>
                <script>
                    centralTree = new dhtmlXTreeObject("central_tree", "100%", "100%", 0);
                    centralTree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                    centralTree.enableCheckBoxes(true, false);
                    centralTree.loadXML('./DIController?op=present_central_trees', null);
                </script>                
                </div>                
                <br>
                
                <form method="post" name="annotationf" action="./DIController?op=annotate" onClick="replaceValue();">
                    <input type='hidden' name='schema_id' value='<%= request.getParameter("schema_id") %>'>
                    <!--<input type='hidden' name='exposed'  value='< %= request.getParameter("exposed") %>'>-->
                    <input type='hidden' name='selections'  value='null'>
                    <input type='hidden' name='centraltree'  value='null'>                                        
                    <input type="submit" value="Annotate" name="annotate_button">
                </form>
            </div>
    </div>
            <div class="footer"><p>Copyright &copy; 2012 Empower Consortium | All Rights Reserved</p></div>
    </center>                
</body>
</html>
