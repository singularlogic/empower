<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../timedoutRedirect.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <script type="text/javascript" src="./js/jquery.js"></script>  
        <script type="text/javascript">                                          
            $(document).ready(function() {
                $("#get_target_schemas").click(function() {   
                    $('#target_box_tree').empty();  
                    if(tree.getAllChecked().split("--").length!=9) alert("You have to check only one input schema! Thank You!");
                    else{    $.getJSON('./OrganizationManager?op=showPossibleTargets&selections='+tree.getAllChecked(), function(data) {
                            target_tree = new dhtmlXTreeObject("target_box_tree", "100%", "100%", 0);
                            target_tree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                            target_tree.enableCheckBoxes(true, false);
                            target_tree.loadXMLString(data.tree, null);               
                        }); 
                    }
                });
               
              
            });
 
            jQuery(document).ready(function(){
                dhtmlxAjax.get("./DIController?op=showAvailableSources_title&software_id=<%=request.getParameter("software_id")%>", putTitle);
            });

            function putTitle(loader) {
                if (loader.xmlDoc.responseText != null)
                    $('#title').html(loader.xmlDoc.responseText);
            }
            
            function compare_schemas() {
                if(tree.getAllChecked().split("--").length!=9 || target_tree.getAllChecked().split("--").length!=9){
                    alert("You have to check only one input and one output schema! Thank You!");
                    return false;  
                }  else{          
                    $.getJSON('./OrganizationManager?op=getModelsForComparationSchema&source_service='+tree.getAllChecked()+'&target_service='+target_tree.getAllChecked(), function(data) {
                            
                        var response =   jQuery.ajax ({
                            url: "http://54.247.114.191/sensapp/mediator",
                            type: "POST",
                            data: JSON.stringify({algorithm:"syntactic",source:data.source_schema,target:data.target_schema}),
                            dataType: "json",
                            contentType: "application/json; charset=utf-8",
                            async:    false,
                            success: function (res) {
                                var text = res.responseText;
                                return text;
                            }
                        }).responseText;                
                       if (response.substring(0, 9) == "/sensapp/"){
                       $("#response").html("<p>In case your Browser does not permit pop-up functionality you can see the \n\
                        mapping results in <a  target=\"_blank\" href=\"http://54.247.114.191/net.modelbased.mediation.gui-0.0.1-SNAPSHOT/repositories.html\">Mediator Portal</a> (Mappings section) looking for the following code <i>"+response.split("/")[5]+"</i></p>");
                        var caracteristicas = "height=500,width=750,scrollTo,resizable=1,scrollbars=1,location=0";
                        nueva=window.open('./organization/matchingResult.jsp?mediator_mapping='+response, 'Popup', caracteristicas);
                       }else{
                         alert("The input schema can not be compared with XBRL Taxonomy by Mediator Portal due to it's excessive depth! Please proceed with the Annotation proccess!");       
                       }
                    }); 
                }  
            }

  
        </script>    
        <script>
            function assign_selections()
            {   
                 if(tree.getAllChecked().split("--").length!=9 || target_tree.getAllChecked().split("--").length!=9){ 
                     alert("You have to check only one input and one output schema! Thank You!");
                     return false;
                 }else{
                    document.forms['create_bridge'].elements['selections_source'].value  = tree.getAllChecked();
                    document.forms['create_bridge'].elements['selections_target'].value = target_tree.getAllChecked();  
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
                            <li><a href="./" title="Go to Start page"></a></li>
                            <li><a href="./" title="Get in touch with us"></a></li>
                        </ul>
                    </div>
                </div>
                <div class="header-middle"><!-- Site message --><div class="sitemessage"><h1 style="float:right">Empower PROJECT</h1><h2 style="width:450px">A Empower Service-Oriented Enterprise Application Integration Middleware Addressing the Needs of the European SMEs</h2></div></div>
                <div class="header-bottom"><!-- Navigation Level 2 (Drop-down menus) --><div class="nav2"><ul><li id="current" class="last"><a href="actions.jsp?action=tabSelect&amp;tabIndex=0&amp;menuitemId=tabA"></a></li></ul></div></div>
                <div class="header-breadcrumbs"><ul></ul></div>
            </div>
        </div>
        <div class="main">
            <div class="main-navigation">
                <div class="round-border-topright"></div>
                <!--<h1 class="first">Organization</h1>-->
                <div id="menu_grid" style="width:180px; height:240px" class='glossymenu'>
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
                <div><p class="info_message">For Every input schema you choose you get all the available output schemas that belong at the same category with the input selection and have been annotated at the same data facet of the XBRL Taxonomy.</p></div>
                <br>
                <div id="box_tree" style="float:left;width:200px; height:300px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;"/>
                <script>
                    tree = new dhtmlXTreeObject("box_tree", "100%", "100%", 0);
                    tree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                    tree.enableCheckBoxes(true, false);
                    tree.loadXML('./OrganizationManager?op=show_bridging_schemas&software_id=<%=request.getParameter("software_id")%>', null);
                </script>
            </div>   

            <div id="target_box_tree" style="width:200px; height:300px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;"/>
        </div>
        <div style="width: 38px; float: right; margin-top: -280px;">
        <div style="float:left;margin-bottom: 20px;"><input TYPE="image" src="./img/getTargetSchemas.png" id="get_target_schemas"/></div>
        <div style="float:left;margin-bottom: 20px;"><input TYPE="image" src="./img/searchMatches.png" id="search_matches" onclick="compare_schemas();"/></div>    
        <div style="float:left;">
        <form method="post" name="create_bridge" action="./OrganizationManager?op=createBridging" onSubmit="return assign_selections();">
            <input type='hidden' name='selections_source'  value='null'>
            <input type='hidden' name='selections_target'  value='null'>
            <div style="float:left;"><input TYPE="image" src="./img/createBridge.png" id="create_bridging"/></div>    
        
        </form></div>
        </div>
        <div id="response" style="clear: both; width: 500px;font-size: 10px;padding-top: 20px; padding-left: 50px;"></div>    
          
    </div>
</div>
<div class="footer"><p>Copyright &copy; 2012 Empower Consortium | All Rights Reserved</p></div>
</center>                
</body>
</html>
