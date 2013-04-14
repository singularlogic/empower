<%@page import="net.sf.json.JSONObject"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../timedoutRedirect.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <script type="text/javascript" src="./js/jquery.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            tree.openAllItems(0);
            $("#softwareComp_source").change(function () {
                $('#box_tree').empty();
                    software_id = $("select[name='sourceSoftComp'] option:selected").val();
                    $.getJSON('./OrganizationManager?op=ws_installations&form=json&software_id='+software_id, function(data) {
                        tree = new dhtmlXTreeObject("box_tree", "100%", "100%", 0);
                        tree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                        tree.enableCheckBoxes(true, false);
                        tree.loadXMLString(data.tree, null);
                        tree.openAllItems(0);

                    });

            })

        });

        window.onload = function () {
            tree.openAllItems(0);
        }

    </script>
    <script>
        function validate_ws()
        {

            if(tree.getAllChecked().indexOf("WS")!== -1 && tree.getAllChecked().split("\\$").length==2  && isValidURL($("#url").val())) {
                document.forms['add_url_binding'].elements['add_url'].value  = tree.getAllChecked();
                return true;
            }  else if (tree.getAllChecked().indexOf("WS")== -1 || tree.getAllChecked().split("\\$").length>2 || !isValidURL($("#url").val()) ){
                alert("Please check one Web Service from the Tree and add one valid URL in order to ground the Web Service. Thank You!");
                return false;
            }

        }
        function validate_url()
        {

            if(tree.getAllChecked().indexOf("IB")!== -1 && tree.getAllChecked().indexOf(",")== -1){
                document.forms['delete_url_binding'].elements['delete_url'].value  = tree.getAllChecked();
                return true;
            }  else{
                alert("You have to check one Url binding you like to delete.Thank You!");
                return false;

            }
        }
        function isValidURL(url){
            var RegExp = /(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;

            if(RegExp.test(url)){
                return true;
            }else{
                return false;
            }
        }
    </script>
    <title>Presenting services in tree form</title>
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
            <div id="menu_grid" style="width:180px; height:240px" class='glossymenu'>
                <script>
                    menu_grid = new dhtmlXGridObject("menu_grid");
                    menu_grid.setImagePath("js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/");
                    menu_grid.setHeader("Menu");
                    menu_grid.setColTypes("link");
                    menu_grid.setSkin("light");
                    menu_grid.setSkin("inverse");
                    menu_grid.init();
                    menu_grid.loadXML('./DIController?op=get_menu&level=1');

                </script>
            </div>
        </div>
        <div class="main-content" style="width:600px;">
            <br>
            <div id="title"><h2>Manage installations of Exposed Web Services</h2></div>
            <div><p class="info_message">In this page you can define the specific installation of each Web Service.</p></div>
            <br>
            <div>
                <div style="width:600px; float: left;">
                    <div class="st_title"><p>Exposed Web Services</p></div>
                    <div>
                        <p class="info_message" style="float: left; margin-top: 18px;margin-right:20px;">Filter By Software Components: </p>
                    <select name="sourceSoftComp" id="softwareComp_source" style="margin:10px;float:left;">
                        <option value="-1">All</option>
                        <%
                            if (session.getAttribute("softwarecomponents") != "") {
                                JSONObject taxonomies = (JSONObject) session.getAttribute("softwarecomponents");
                                for (Object o : taxonomies.keySet()) {
                                    String key = (String) o;
                                    String value = taxonomies.getString(key);
                                    System.out.println("key: " + key + " value: " + value);
                        %> <option value="<%=key%>"><%=value%></option><%
                            }
                        }
                    %>
                    </select>
                    </div>
                </div>

            </div>
            <div id="box_tree" style="float:left;width:400px; height:500px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;margin: 10px;"/>
            <script>
                tree = new dhtmlXTreeObject("box_tree", "100%", "100%", 0);
                tree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                tree.enableCheckBoxes(true, false);
                tree.loadXML('./OrganizationManager?op=ws_installations&form=out&software_id=<%=request.getParameter("software_id")%>', null);
            </script>
        </div>
        <div style="border: 2px solid;border-color: #77A631;width:170px; float: right;margin: 20px 0px; padding-top: 20px; padding-bottom: 20px;">
            <p class="info_message">In order to ground a web service: 1)Please choose a web service 2) Add at the input field the specific url binding you wish to ground 3) and then press the "Add Url Binding" option so as to add a new soap location to an existing version of a web service.</p>

            <form method="post" name="add_url_binding" action="./OrganizationManager?op=addUrlBinding" onSubmit=" return validate_ws();">
            <input type='text' name='url' id="url"  value='please provide a url' style="width:150px;margin-top: 10px;margin-bottom: 10px;">
            <input type='hidden' name='add_url'  value='null'>
           <input TYPE="image" src="./img/addurl.png" id="addUrlBinding"/>
           <br>
           <br>
            </form>

        </div>
        <div style="border: 2px solid;border-color: #77A631;width:170px; float: right;margin: 20px 0px; padding-top: 20px; padding-bottom: 20px;">
            <p class="info_message">In order to delete an URL Binding of a web Service please select the url binding and press the " Delete Url Binding" option.</p>

            <form method="post" name="delete_url_binding" action="./OrganizationManager?op=deleteUrlBinding" onSubmit=" return validate_url();">
            <input type='hidden' name='delete_url'  value='null'>
            <input TYPE="image" src="./img/deleteurl.png" id="deleteUrlBinding" style="margin-top: 10px;"/>
            </form>

        </div>

        <div style="clear: both;">
        <p class="info_message"  style="margin: 10px;border:2px solid;"><%=request.getParameter("message")%></p>
        </div>

        <br>
        <br>


    </div>
    </div>
    <div class="footer"><p>Copyright &copy; 2011 - 2013 Empower Consortium | All Rights Reserved</p></div>
</center>
</body>
</html>
