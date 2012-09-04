<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <script type="text/javascript" src="./js/jquery.js"></script>  
        <script type="text/javascript">                                          
            $(document).ready(function() {
                $("#get_target_schemas").click(function() {

                    $.getJSON('./OrganizationManager?op=showPossibleTargets&selections='+tree.getAllChecked(), function(data) {
     
                        target_tree = new dhtmlXTreeObject("target_box_tree", "100%", "100%", 0);
                        target_tree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                        target_tree.enableCheckBoxes(true, false);
                        //target_tree.loadXMLString("<?xml version='1.0' encoding='UTF-8'?><tree id='0'><item text='accountingEntriesComplexType' id='accountingEntriesComplexType'><item text='documentInfoComplexType' id='documentInfoComplexType'/><item text='entityInformationComplexType' id='entityInformationComplexType'/><item text='entryHeaderComplexType' id='entryHeaderComplexType\'><item text='entryDetailComplexType' id='entryDetailComplexType'/></item></item></tree>", null);               
                        target_tree.loadXMLString(data.tree, null);               
                    });
                });
              
            });
 
            jQuery(document).ready(function(){
                dhtmlxAjax.get("./DIController?op=showAvailableSources_title&software_id=<%=request.getParameter("software_id")%>", putTitle);
            });

            function putTitle(loader) {
                if (loader.xmlDoc.responseText != null)
                    $('#title').html(loader.xmlDoc.responseText);
            }

  
        </script>    
        <script>
            function replaceValue()
            {
                document.forms['annotationf'].elements['selections'].value  = tree.getAllChecked();
                document.forms['annotationf'].elements['centraltree'].value = centralTree.getAllChecked();                
            }
        
            function assign_selections()
            {
                document.forms['create_bridge'].elements['selections_source'].value  = tree.getAllChecked();
                document.forms['create_bridge'].elements['selections_target'].value = target_tree.getAllChecked();                
            }
        </script>
        <title>Presenting service in tree form</title>
        <link rel="stylesheet" type="text/css" href="./style/menu_style.css"/>
        <link rel="stylesheet" type="text/css" href="./style/layout4_setup.css"/>
        <link rel="stylesheet" type="text/css" href="./style/container.css"/>
        <link rel="stylesheet" type="text/css" href="./js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxtree.css">
        <script src="./js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxcommon.js"></script>
        <script src="./js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxtree.js"></script>
        <link rel="stylesheet" type="text/css" href="./js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgrid.css">
        <link rel="stylesheet" type="text/css" href="./js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgrid_skins.css">
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
            <div class="header">
                <div class="header-top">
                    <div class="sitename" style="margin-left:10px">
                    </div><!-- Navigation Level 1 top menu links-->
                    <div class="nav1">
                        <ul>
                            <li><a href="./" title="Go to Start page">Home</a></li>
                            <li><a href="./" title="Get in touch with us">Contact</a></li>
                        </ul>
                    </div>
                </div>
                <div class="header-middle"><!-- Site message --><div class="sitemessage"><h1 style="float:right">Empower PROJECT</h1><h2 style="width:450px">A Empower Service-Oriented Enterprise Application Integration Middleware Addressing the Needs of the European SMEs</h2></div></div>
                <div class="header-bottom"><!-- Navigation Level 2 (Drop-down menus) --><div class="nav2"><ul><li id="current" class="last"><a href="actions.jsp?action=tabSelect&amp;tabIndex=0&amp;menuitemId=tabA">Home</a></li></ul></div></div>
                <div class="header-breadcrumbs"><ul></ul></div>
            </div>
        </div>
        <div class="main">
            <div class="main-navigation">
                <div class="round-border-topright"></div>
                <!--<h1 class="first">Organization</h1>-->
                <div id="menu_grid" style="width:180px; height:120px" class='glossymenu'>
                    <script>
                        menu_grid = new dhtmlXGridObject("menu_grid");
                        menu_grid.setImagePath("js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/");
                        menu_grid.setHeader("Menu");//set column names
                        menu_grid.setColTypes("link");
                        menu_grid.setSkin("light");//set grid skin
                        menu_grid.setSkin("inverse");
                        menu_grid.init();//initialize grid
                        menu_grid.loadXML('./DIController?op=get_menu&level=1');

                    </script>
                </div>
            </div>
            <div class="main-content" style="width:550px;">
                <br>
                <div id="title"> </div>
                <br>
                <div id="box_tree" style="float:left;width:250px; height:400px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;"/>
                <script>
                    tree = new dhtmlXTreeObject("box_tree", "100%", "100%", 0);
                    tree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                    tree.enableCheckBoxes(true, false);
                    tree.loadXML('./OrganizationManager?op=show_bridging_schemas&software_id=<%=request.getParameter("software_id")%>', null);
                </script>
            </div>   

            <div id="target_box_tree" style="width:250px; height:400px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;"/>
        </div>

        <input type="submit" value="Get Semantically coherent Target Schemas" name="get_target_schemas" id="get_target_schemas"/>
        <br>
        <br>
        <form method="post" name="create_bridge" action="./OrganizationManager?op=createBridging" onclick="assign_selections();">
            <input type='hidden' name='selections_source'  value='null'>
            <input type='hidden' name='selections_target'  value='null'>
            <input type="submit" value="Create Bridging" name="create_bridging" id="create_bridging"/>
        </form>
    </div>
</div>
<div class="footer"><p>Copyright &copy; 2012 Empower Consortium | All Rights Reserved</p></div>
</center>                
</body>
</html>
