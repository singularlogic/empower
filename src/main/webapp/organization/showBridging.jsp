<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
 <script type="text/javascript" src="./js/jquery.js"></script>  
 <script type="text/javascript">                                          
  $(document).ready(function() {
   $("#dobridging").click(function() {
           
    alert("hola");
    
     $.getJSON('./OrganizationManager?op=doBridging='+$('#fileinput').val(), function(data) {
      //alert(data.selections);
       target_tree = new dhtmlXTreeObject("target_box_tree", "100%", "100%", 0);
       target_tree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
       target_tree.enableCheckBoxes(true, false);
       //target_tree.loadXMLString("<?xml version='1.0' encoding='UTF-8'?><tree id='0'><item text='accountingEntriesComplexType' id='accountingEntriesComplexType'><item text='documentInfoComplexType' id='documentInfoComplexType'/><item text='entityInformationComplexType' id='entityInformationComplexType'/><item text='entryHeaderComplexType' id='entryHeaderComplexType\'><item text='entryDetailComplexType' id='entryDetailComplexType'/></item></item></tree>", null);               
       target_tree.loadXMLString(data.tree, null);   
       
       alert(data.target_xml);
   
    });

     
   });
 });
  
 </script>     
<title>Presenting service in tree form</title>
        <link rel="stylesheet" type="text/css" href="./style/menu_style.css"/>
        <link rel="stylesheet" type="text/css" href="./style/layout4_setup.css"/>
        <link rel="stylesheet" type="text/css" href="./style/layout4_text_simple.css"/> 
        <link rel="stylesheet" type="text/css" href="./style/container.css"/>
	<link rel="stylesheet" type="text/css" href="./js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxtree.css">
	<link rel="stylesheet" type="text/css" href="./style/XMLDisplay.css"/>
        <script src="./js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxcommon.js"></script>
	<script src="./js/dhtmlxSuite/dhtmlxTree/codebase/dhtmlxtree.js"></script>
        <script src="./js/XMLDisplay.js"></script>
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
                 menu_grid.setSkin("light");//set grid skin
                 menu_grid.setSkin("inverse");
                 menu_grid.init();//initialize grid
                 menu_grid.loadXML('./DIController?op=get_menu&level=1');
            </script>
            </div>    
        </div>
            <div class="main-content" style="width:550px;">
                <br>
                <p><h2>Do Bridging</h2>
                
                 
                <div id="sourcee_xml" style="float:left;width:250px; height:400px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;"/>
                <p><h2>Source XML</h2>
                 <script>
                    LoadXMLString('sourcee_xml', <%=session.getAttribute("source_xml")%>);
                </script> 
                </div>  
                
                
                <div id="target_xml" style="float:left;width:250px; height:400px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;"/>
                <p><h2>Target XML</h2>
                 <script>
                    LoadXMLString('target_xml', '<%=session.getAttribute("target_xml")%>');
                </script> 
                </div>  
                <br><br><br><br>
                <form method="post" name="do_bridge" action="./OrganizationManager?op=doBridging&cpa_id=<%=request.getParameter("cpa_id")%>" enctype="multipart/form-data">
                    <input type="file" name="source_xml" value="" id="fileinput"/>
                    <input type="submit" value="Show target xml"  id="do_bridging"/>
                </form>
                <br>
            </div> 
    </div>
            <div class="footer"><p>Copyright &copy; 2012 Empower Consortium | All Rights Reserved</p></div>
    </center>                
</body>
</html>
