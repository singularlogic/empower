<%@ page import="net.sf.json.JSONObject" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="timedoutRedirect.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="./style/menu_style.css"/>
        <link rel="stylesheet" type="text/css" href="./style/layout4_setup.css"/>
        <link rel="stylesheet" type="text/css" href="./style/container.css"/>
	<link rel="stylesheet" type="text/css" href="./js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgrid.css">
	<link rel="stylesheet" type="text/css" href="./js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgrid_skins.css">
        <script src="./js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxcommon.js"></script>
	<script src="./js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgrid.js"></script>
        <script src="./js/dhtmlxSuite/dhtmlxGrid/codebase/dhtmlxgridcell.js"></script>
        <script src="./js/dhtmlxSuite/dhtmlxGrid/codebase/excells/dhtmlxgrid_excell_link.js"></script>
        <script type="text/javascript" src="./js/jquery.js"></script>
        <style type="text/css">
            div.gridbox_inverse table.hdr td {background-color:#A0D651; color:white; font-weight:bold;}
            div.gridbox_inverse table.obj td{background-color: #D9EFB9;text-align: center;}
            div.gridbox_inverse table.obj tr{height: 30px;}
        </style>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">        
        <title>C Show Schemas</title>
        <script type="text/javascript">
            jQuery(document).ready(function(){
            $("#createANewCPP").click(function() {
                $("#createCPPDIV").show();

                $.getJSON('./OrganizationManager?op=getCPPsPerSoftCompPerOrg&software_id=<%= request.getParameter("software_id") %>&serviceORschema=schema', function(data) {
                    for (var cpp in data) {
                        $('#CPPsPerSoftCompPerOrg').append('<option value="'+cpp+'" >'+data[cpp]+'</option>');
                    }
                });
            });
            });

            function deleteschema(schema_id){
                var r=confirm("Are you sure you want to delete?");
                if (r==true)
                {
                    document.location.href='./VendorManager?op=delete_schema&schema_id='+schema_id;             
                }       
            }
        </script>        
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
            <div class="header-middle"><!-- Site message --><div class="sitemessage"><h1 style="float:right">Empower PROJECT</h1><h2 style="width:450px">A Semantic Service-Oriented Enterprise Application Integration Middleware Addressing the Needs of the European SMEs</h2></div></div>                        
            <div class="header-bottom"><!-- Navigation Level 2 (Drop-down menus) --><div class="nav2"><ul><li id="current" class="last"><a href="actions.jsp?action=tabSelect&amp;tabIndex=0&amp;menuitemId=tabA"></a></li></ul></div></div>
            <div class="header-breadcrumbs"><ul></ul></div>
        </div>   
        </div>
        <div class="main">
        <div class="main-navigation">
            <div id="menu_grid" style="width:180px; height:<%=(session.getAttribute("userType").equals("organization"))?240:150%>px" class='glossymenu'>
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
            <div id="box_grid" style="width:600px; height:200px"></div>
                <script>
                        grid = new dhtmlXGridObject("box_grid");
                        grid.setImagePath("js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/");
                        if(<%=session.getAttribute("userType").equals("organization")%>){
                            grid.setHeader("Schema name,CPP Name,Functional Annotation,Data Annotation,Delete CPP, UsedByCPA ");//set column names
                            grid.setColTypes("ro,ro,link,link,link,ro");
                        }else{
                            grid.setHeader("Schema name,Functional Annotation,Data Annotation ,Delete Schema");//set column names
                            grid.setColTypes("ro,link,link,link");
                        }
                //        grid.attachEvent("onRowSelect", doOnRowSelected);
                        grid.setSkin("light");//set grid skin
                        grid.init();//initialize grid
                        grid.loadXML('DIController?op=show_schemas&software_id=<%= request.getParameter("software_id") %>');

                </script>
                <br>
                <% if (session.getAttribute("userType").equals("vendor")){ %>
                <p><a href='./vendor/SchemaReg.jsp?software_id=<%= request.getParameter("software_id")%>&jsp=true'>Add new schema to the current Software Component</a>
                <%}else{ %>

            <div>
                <h2><a id="createANewCPP">Create a New CPP</a></h2>
            <div id="createCPPDIV" style="display: none;border-bottom: 2px solid;">
            <form method="POST" action="./OrganizationManager?op=cpp_reg&software_id=<%= request.getParameter("software_id") %>&serviceORschema=schema" name="cpp_registration"  id="cpp_registration" >

                    <p class="info_message">Step 1: Choose The CPP you want to specialize:</p>

                    <select name="CPPid" id="CPPsPerSoftCompPerOrg" style="margin:10px;">
                        <%
                         //      if (session.getAttribute("CPPsPerSoftCompPerOrg") != "") {
                            //       JSONObject cpps = (JSONObject) session.getAttribute("CPPsPerSoftCompPerOrg");
                            //       for (Object o : cpps.keySet()) {
                            //            String key = (String) o;
                            //           String value = cpps.getString(key);
                            //            System.out.println("key: " + key + " value: " + value);
                        //% > <option value="< %=//key% >">< %=//value% >< /option> <%
                            //   }
                        // }
                    %>
                    </select>
                    <p class="info_message">Step 2: Name the New CPP:</p>
                    <input type="text" name="cpp_name" size="20">
                    <input type="submit" value="Create CPP" name="submit_button"></td>
                </form>
            </div>
            <br><br>
            <div id="createDeleteCppMessage"><p class="info_message"  style="margin: 10px; border:2px solid;"><%=request.getParameter("message")%></p></div>

            <% }%>
        </div>
        </div>
            <div class="footer"><p>Copyright &copy; 2011 - 2013 Empower Consortium | All Rights Reserved</p></div>
    </center>
</body>
</html>
