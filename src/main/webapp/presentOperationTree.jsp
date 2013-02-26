<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="timedoutRedirect.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <script type="text/javascript" src="./js/jquery.js"></script>  
        <script>
            
            $(document).ready(function() {
                $("#get_previousfuncannotation").click(function() {
                    if(tree.getAllChecked().split(",").length!=1 || tree.getAllChecked()=="" ) alert("You have to check only one input schema! Thank You!");
                    else{    
                         if(<%=request.getParameter("service_id")%>){
                            dhtmlxAjax.get("./DIController?op=getPreviousFuncAnnotation&service_id=<%= request.getParameter("service_id")%>&operation="+tree.getAllChecked(), placeInfo);
                            }else if (<%=request.getParameter("schema_id")%>){
                              dhtmlxAjax.get("./DIController?op=getPreviousFuncAnnotation&schema_id=<%= request.getParameter("schema_id")%>&operation_id="+tree.getAllChecked(), placeInfo);  
                            }             
                    }
                });
                if(<%=request.getParameter("service_id")%>){
                dhtmlxAjax.get("./DIController?op=showcurrentwebservice&service_id=<%=request.getParameter("service_id")%>", putSubTitle);
                }
            });

            window.onload = function () {
                treeFunc.openAllItems(0);
                tree.openAllItems(0);
            }

            function putSubTitle(loader) {
                if (loader.xmlDoc.responseText != null){
                    $("#subPageTitle").html("Functional Annotation of Web Service : "+loader.xmlDoc.responseText);
                }
            }

            function placeInfo(loader) {
                if (loader.xmlDoc.responseText != null)
                   alert(loader.xmlDoc.responseText);
            }
            
            
            
            
            function replaceValue()
            {
           
                if(tree.getAllChecked().split(",").length!=1 || tree.getAllChecked()=="" || treeFunc.getAllChecked().split(",").length!=1 || treeFunc.getAllChecked()==""){ 
                    alert("You have to check one Operation and only one Operation Taxonomy! Thank You!");
                    return false;
                }else{       
                    document.forms['annotationf'].elements['selections'].value     = tree.getAllChecked();
                    document.forms['annotationf'].elements['funcselections'].value = treeFunc.getAllChecked(); 
                    return true;
                }
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
                            <li><a href="./" title="Go to Start page"></a></li>
                            <li><a href="./" title="Get in touch with us"></a></li>
                        </ul>
                    </div>
                </div><!-- A.2 HEADER MIDDLE -->
                <div class="header-middle"><!-- Site message --><div class="sitemessage"><h1 style="float:right">EMPOWER PROJECT</h1><h2 style="width:450px">A Semantic Service-Oriented Enterprise Application Integration Middleware Addressing the Needs of the European SMEs</h2></div></div>                                    
                <div class="header-bottom"><!-- Navigation Level 2 (Drop-down menus) --><div class="nav2"><ul><li id="current" class="last"><a href="actions.jsp?action=tabSelect&amp;tabIndex=0&amp;menuitemId=tabA"></a></li></ul></div></div>
                <div class="header-breadcrumbs"><ul></ul></div>
            </div>   
        </div>
        <div class="main">
            <div class="main-navigation">
                <div id="menu_grid" style="width:180px; height:<%=(session.getAttribute("userType").equals("organization")) ? 210 : 150%>px" class='glossymenu'>
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
            <div class="main-content" style="width: 650px;">
                <h2 id="subPageTitle" style="width: 488px; float: left;"></h2><br><br>
                <p class="info_message" style="width: 488px; float: left;">Functional Annotation of the operations of the web service. Please select the  operation from the left side and
                    the Functional Taxonomy category from the right side. In order to see the existing functional annotation of an operation
                    click on "See existing Annotation" link.</p><br><br>
                <div style="float: left;width:240px;margin-right: 10px;">
                    <p><h2>Available Operations</h2>

                    <br>            
                    <div id="box_tree" style="width:230px; height:300px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;"/>
                    <script>
                        tree = new dhtmlXTreeObject("box_tree", "100%", "100%", 0);
                        tree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                        tree.enableCheckBoxes(true, false);                      
                        tree.load
                        if(<%=request.getParameter("service_id")%>){
                            tree.loadXML('./DIController?op=present_service_operations&service_id=<%= request.getParameter("service_id")%>', null); 
                        }else if (<%=request.getParameter("schema_id")%>){     
                            tree.loadXML('./DIController?op=present_service_operations&schema_id=<%= request.getParameter("schema_id")%>', null);
                        }
                    </script>
                </div>

            </div>
            <div style="float: left;width:240px;">
                <p><h2>Functional Taxonomy</h2>
                <br>
                <div id="box_tree_func" style="width:230px; height:300px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;"/>
                <script>
                    treeFunc = new dhtmlXTreeObject("box_tree_func", "100%", "100%", 0);
                    treeFunc.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                    treeFunc.enableCheckBoxes(true, false);
                    treeFunc.loadXML('./ontologies/functional_tax_facet.xml', null);
                    //                        treeFunc.loadXML('../VendorManager?op=present_ftaxonomy', null);
                    //                        tree.loadXML('ManageServices?op=show_serv_bind&software_name=<%= request.getParameter("software_name")%>', null);
                </script>
            </div>     

        </div>
        <div style="float:left;margin-bottom: 20px;margin-left: 15px; margin-top: 290px;">
           <form method="post" name="annotationf" action="./DIController?op=annotate_operations" onClick="return replaceValue();">

                <input type='hidden' name='schema_id' value='<%= request.getParameter("schema_id")%>'>
                <input type='hidden' name='service_id' value='<%= request.getParameter("service_id")%>'>
                <input type='hidden' name='selections' value='null'>
                <input type='hidden' name='funcselections' value='null'>

                <!--<input type="submit" value="Annotate" name="annotate_button">-->
                <input TYPE="image" src="./img/Annotate.png" name="annotate_button"/>
            </form>
            <a id="get_previousfuncannotation">See existing Annotation</a>
        </div>
    </div>
</div>
<div class="footer"><p>Copyright &copy; 2011 - 2013 EMPOWER Consortium | All Rights Reserved</p></div>
</center>                
</body>
</html>
