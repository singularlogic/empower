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
        <link rel="stylesheet" type="text/css" href="http://localhost:8080/DIEmpower/style/menu_style.css"/>
        <link rel="stylesheet" type="text/css" href="http://localhost:8080/DIEmpower/style/layout4_setup.css"/>        
        <link rel="stylesheet" type="text/css" href="http://localhost:8080/DIEmpower/style/container.css"/>
	<link rel="stylesheet" type="text/css" href="http://localhost:8080/DIEmpower/js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxtree.css">
	<script src="http://localhost:8080/DIEmpower/js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxcommon.js"></script>
	<script src="http://localhost:8080/DIEmpower/js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxtree.js"></script>
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
                        <a class='menuitem' href='softwareReg.jsp'>Register new software</a>
                    </div><!--submenuENDdiv-->
                    <div class='submenu1'>
                        <a class='menuitem' href='showSoftwareComponent.jsp'>Show software components</a>
                    </div><!--submenuENDdiv-->                
                    <div class='submenu1'>
                        <a class='menuitem' href='../DIController?op=signout'>Logout</a>
                    </div><!--submenuENDdiv--> 
            </div>
        </div>    
            <div class="main-content">
                <p><h2>Function Taxonomy</h2>
                <br>
                <div id="box_tree_func" style="width:500px; height:300px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;"/>
                <script>
                        treeFunc = new dhtmlXTreeObject("box_tree_func", "100%", "100%", 0);
                        treeFunc.setImagePath("http://localhost:8080/Semantix/js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                        treeFunc.enableCheckBoxes(true, false);
                        treeFunc.loadXML('../ontologies/functional_tax_facet.xml', null);                        
//                        treeFunc.loadXML('../VendorManager?op=present_ftaxonomy', null);
//                        tree.loadXML('ManageServices?op=show_serv_bind&software_name=<%= request.getParameter("software_name") %>', null);
                </script>
                </div>  
                <br>
                <p><h2>Service messages</h2>
                <br>
                <div id="box_tree" style="width:500px; height:400px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;"/>
                <script>
                        tree = new dhtmlXTreeObject("box_tree", "100%", "100%", 0);
                        tree.setImagePath("http://localhost:8080/DIEmpower/js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                        tree.enableCheckBoxes(true, false);                      
                        tree.loadXML('../VendorManager?op=present_service&service_id=<%= request.getParameter("service_id") %>', null);
//                        tree.loadXML('ManageServices?op=show_serv_bind&software_name=<%= request.getParameter("software_name") %>', null);
                </script>
                </div>
                <br>
                <p><h2>Select data facet</h2>
                <br>
                <div id="central_tree" style="width:500px; height:200px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;"/>
                <script>
                    centralTree = new dhtmlXTreeObject("central_tree", "100%", "100%", 0);
                    centralTree.setImagePath("http://localhost:8080/DIEmpower/js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                    centralTree.enableCheckBoxes(true, false);
                    centralTree.loadXML('../VendorManager?op=present_central_trees', null);
                </script>                
                </div>                
                <br>
                <form method="post" name="annotationf" action="../VendorManager?op=annotate" onClick="replaceValue();">
                    <input type='hidden' name='service_id' value='<%= request.getParameter("service_id") %>'>
                    <input type='hidden' name='exposed'  value='<%= request.getParameter("exposed") %>'>
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
