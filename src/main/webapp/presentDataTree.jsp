<%@page import="net.sf.json.JSONObject"%>
<%@include file="timedoutRedirect.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>  
    <script type="text/javascript" src="./js/jquery.js"></script> 
<script>
    $(document).ready(function() {
        if(<%=request.getParameter("service_id")%>){
            dhtmlxAjax.get("./DIController?op=showcurrentwebservice&service_id=<%=request.getParameter("service_id")%>", putSubTitle);
        }
    });

        function putSubTitle(loader) {
            if (loader.xmlDoc.responseText != null){
             $("#subPageTitle").html("Data Annotation of Web Service : "+loader.xmlDoc.responseText);
             }
        }

        function replaceValue()
        {
           if(<%=request.getParameter("service_id")%>){
               split_var="\\$";
               lenght_var =4; 
           }else{
               split_var="--";
               lenght_var =9; 
           }
           
           if(tree.getAllChecked().split(split_var).length!=lenght_var || tree.getAllChecked()=="" || centralTree.getAllChecked().split(",").length!=1 || centralTree.getAllChecked()==""){ 
              alert("You have to check one schema and only one XBRL Taxonomy! Thank You!");
              return false;
            }else{
            document.forms['annotationf'].elements['selections'].value  = tree.getAllChecked();
            document.forms['annotationf'].elements['centraltree'].value = centralTree.getAllChecked();                
//            alert(centraltree.getAllChecked());
            return true;
            }
        }
        
         function compare_schemas() {
             
             if(<%=request.getParameter("service_id")%>){
               split_var="\\$";
               lenght_var =4; 
           }else{
               split_var="--";
               lenght_var =9; 
           }
             
           if(tree.getAllChecked().split(split_var).length!=lenght_var || tree.getAllChecked()=="" || centralTree.getAllChecked().split(",").length!=1 || centralTree.getAllChecked()==""){
                alert("You have to check one schema and only one XBRL Taxonomy! Thank You!");
                return false;  
             }  else{ 

                      $.getJSON('./OrganizationManager?op=getModelsForComparationSchemaXBRL&source_service='+tree.getAllChecked()+'&schema_id='+<%=request.getParameter("schema_id")%>, function(data) {
                   var response =   jQuery.ajax ({
                    url: "http://54.247.114.191/sensapp/mediator",
                    type: "POST",
                    data: JSON.stringify({algorithm:"syntactic",source:(<%=request.getParameter("service_id")%>)?<%=request.getParameter("service_id")%>+'_'+tree.getAllChecked().split(split_var)[1]:data.source_schema,target:centralTree.getAllChecked()+"MP"}),
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    async:    false,
                    success: function (res) {
                    var text = res.responseText;
                    return text;
                    }
                    }).responseText;  
                   if (!response){
                     alert("Please proceed with the annotation proccess so as to import the input schema to Mediator before comparing it with the XBRL taxonomy!");       
                   } else if (response.substring(0, 9) == "/sensapp/"){
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
            <div class="header-middle"><!-- Site message --><div class="sitemessage"><h1 style="float:right">Empower PROJECT</h1><h2 style="width:450px">A Empower Service-Oriented Enterprise Application Integration Middleware Addressing the Needs of the European SMEs</h2></div></div>
            <div class="header-bottom"><!-- Navigation Level 2 (Drop-down menus) --><div class="nav2"><ul><li id="current" class="last"><a href="actions.jsp?action=tabSelect&amp;tabIndex=0&amp;menuitemId=tabA"></a></li></ul></div></div>
            <div class="header-breadcrumbs"><ul></ul></div>
        </div>
        </div>
        <div class="main">
        <div class="main-navigation">
          <div id="menu_grid" style="width:180px; height:<%=(session.getAttribute("userType").equals("organization"))?210:150%>px" class='glossymenu'>
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
           <div class="main-content" style="width: 650px;">
               <h2 id="subPageTitle" style="width: 488px; float: left;"></h2><br><br>
               <p class="info_message" style="width: 488px; float: left;">Data Annotation of the operations of the web service. Please select the  operation from the left side and
                   the Part of Canonical Model from the right side. The "accountingEntriesComplexType" option of Canonical Model represents all the available fields, so in case you are not familiar with the
                   specialization of the offered nodes of the canonical model, is better to choose the most generic annotation node(accountingEntriesComplexType).
                   </p><br><br>
               <p class="info_message" style="width: 488px; float: left;margin-top: 20px;"> By clicking on "Search for Matches" is shown the syntactic matching between the
                   operation fields and the canonical Model. The aformentioned matching is provided by the Mediator Portal.
               </p><br><br>
                <div style="float: left;width:240px;margin-right: 10px;">
                <p><h2>Service Argument Structure</h2>
                <br>
                <div id="box_tree" style="width:240px; height:250px;background-color:#f5f5f5;border :1px solid Silver;overflow:auto;"/>
                <script>
                        tree = new dhtmlXTreeObject("box_tree", "100%", "100%", 0);
                        tree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                        tree.enableCheckBoxes(true, false);
                        if(<%=request.getParameter("service_id")%>){
                         tree.loadXML('./DIController?op=present_service&service_id=<%= request.getParameter("service_id") %>', null); 
                        }else if (<%=request.getParameter("schema_id")%>){  
                        tree.loadXML('./DIController?op=present_service_schema&schema_id=<%= request.getParameter("schema_id") %>', null);
                        }
                       // tree.loadXML('../VendorManager?op=present_service&service_id=<%= request.getParameter("service_id") %>', null);
                </script>
                </div>
                </div>
                <div style="float: left;width:240px;">
                <p><h2>Canonical Model</h2>
                <br>
                <div id="central_tree" style="width:240px; height:250px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;"/>
                <script>
                    centralTree = new dhtmlXTreeObject("central_tree", "100%", "100%", 0);
                    centralTree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                    centralTree.enableCheckBoxes(true, false);
                    centralTree.loadXML('./DIController?op=present_central_trees', null);
                </script>                
                </div>                
                </div>
                
                <div style="float:left;margin-bottom: 20px;margin-top: 90px;margin-left: 15px;"><input TYPE="image" src="./img/searchMatches.png" id="search_matches" onclick="compare_schemas();"/></div>    
       
                 <div style="float:left;margin-bottom: 20px;margin-left: 15px;">
                <form method="post" name="annotationf" action="./DIController?op=annotate" onClick="return replaceValue();">
                    <input type='hidden' name='schema_id' value='<%= request.getParameter("schema_id") %>'>
                    <input type='hidden' name='service_id' value='<%= request.getParameter("service_id") %>'>
                    <input type='hidden' name='selections'  value='null'>
                    <input type='hidden' name='centraltree'  value='null'>
                    <input type='hidden' name='cpp_id'  value='<%= request.getParameter("cpp_id") %>'>
                    <input TYPE="image" src="./img/Annotate.png" name="annotate_button"/>
                </form>
                 </div>
                    
                       
            </div>
            <div id="response" style="clear: both; width: 500px;font-size: 10px;padding-top: 20px; padding-left: 50px;"></div>    
          
    </div>
            <div class="footer"><p>Copyright &copy; 2012 Empower Consortium | All Rights Reserved</p></div>
    </center>                
</body>
</html>
