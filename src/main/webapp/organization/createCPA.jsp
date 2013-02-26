<%@page import="net.sf.json.JSONObject"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../timedoutRedirect.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <script type="text/javascript" src="./js/jquery.js"></script>
    <script type="text/javascript">
        jQuery(document).ready(function(){
            $("#tax_target,#softwareComp_target").change(function () {
                var tax = "";
                $('#target_box_tree').empty();
                $("#tax_target option:selected").each(function () {
                    tax += $(this).text();
                    software_id = $("select[name='targetSoftComp'] option:selected").val();
                    $.getJSON('./OrganizationManager?op=showexposedServicesByTaxonomy&form=json&tax='+tax+'&software_id='+software_id, function(data) {
                        //target_tree = new dhtmlXTreeObject("target_box_tree", "100%", "100%", 0);
                        //target_tree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                        //target_tree.enableCheckBoxes(true, false);
                        //target_tree.loadXMLString(data.tree, null);

                        //activate wsdls select list
                        $('#wsdls_target').removeAttr('disabled');
                        $('#wsdls_target').html(data.listWS);

                        //empty and disable installations and cpp's
                        $('#installations_target,#CPPs_target').html("");
                        $('#installations_target,#CPPs_target').attr('disabled', 'disabled');


                    });
                });
            })

            $("#tax_source,#softwareComp_source").change(function () {
                var tax = "";
                $('#box_tree').empty();
                $("#tax_source option:selected").each(function () {
                    tax += $(this).text();
                    software_id = $("select[name='sourceSoftComp'] option:selected").val();
                    $.getJSON('./OrganizationManager?op=showexposedServicesByTaxonomy&form=json&tax='+tax+'&software_id='+software_id, function(data) {
                        //tree = new dhtmlXTreeObject("box_tree", "100%", "100%", 0);
                        //tree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                        //tree.enableCheckBoxes(true, false);
                        //tree.loadXMLString(data.tree, null);

                        //activate wsdls select list
                        $('#wsdls_source').removeAttr('disabled');
                        $('#wsdls_source').html(data.listWS);

                        //empty and disable installations and cpp's
                        $('#installations_source,#CPPs_source').html("");
                        $('#installations_source,#CPPs_source').attr('disabled', 'disabled');

                    });
                });
            })


            $("#wsdls_source").change(function () {
              service_id = $("select[name='wsdls_source'] option:selected").val();
                $.getJSON('./OrganizationManager?op=getCPPsInstallations&form=json&service_id='+service_id, function(data) {

                    //activate installations and cpps select list
                    $('#installations_source').removeAttr('disabled');
                    $('#installations_source').html(data.list_installations);
                    $('#CPPs_source').removeAttr('disabled');
                    $('#CPPs_source').html(data.list_CPP);

                });
            })

            $("#wsdls_target").change(function () {
                service_id = $("select[name='wsdls_target'] option:selected").val();
                $.getJSON('./OrganizationManager?op=getCPPsInstallations&form=json&service_id='+service_id, function(data) {


                    //activate installations and cpps select list
                    $('#installations_target').removeAttr('disabled');
                    $('#installations_target').html(data.list_installations);
                    $('#CPPs_target').removeAttr('disabled');
                    $('#CPPs_target').html(data.list_CPP);
                });

            })





            $("#CPPs_source").change(function () {
                service_id = $("select[name='wsdls_source'] option:selected").val();
                cpp_id = $("select[name='CPPs_source'] option:selected").val();
                $.getJSON('./OrganizationManager?op=showCppsForBridging&form=json&service_id='+service_id+"&cpp_id="+cpp_id, function(data) {
                    $('#box_tree').empty();
                    tree = new dhtmlXTreeObject("box_tree", "100%", "100%", 0);
                    tree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                    tree.enableCheckBoxes(true, false);
                    tree.loadXMLString(data.tree, null);

                });
            })


            $("#CPPs_target").change(function () {
                service_id = $("select[name='wsdls_target'] option:selected").val();
                cpp_id = $("select[name='CPPs_target'] option:selected").val();
                $.getJSON('./OrganizationManager?op=showCppsForBridging&form=json&service_id='+service_id+"&cpp_id="+cpp_id, function(data) {
                    $('#target_box_tree').empty();
                    target_tree = new dhtmlXTreeObject("target_box_tree", "100%", "100%", 0);
                    target_tree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                    target_tree.enableCheckBoxes(true, false);
                    target_tree.loadXMLString(data.tree, null);

                });
            })
        });

        function putTitle(loader) {
            if (loader.xmlDoc.responseText != null){
                $("#softwareComp_source option[value='<%=request.getParameter("software_id")%>']").remove();
                $("#softwareComp_source option[text='"+loader.xmlDoc.responseText+"']").remove();
                $("select[name='sourceSoftComp'] option:selected").text(loader.xmlDoc.responseText);
                $("select[name='sourceSoftComp'] option:selected").val('<%=request.getParameter("software_id")%>');
            }
        }

        function compare_schemas() {
            if(tree.getAllChecked().split("\\$").length!=2 || target_tree.getAllChecked().split("\\$").length!=2){
                alert("You have to check only one input and one output web service! Thank You!");
                return false;
            }  else{
                $.getJSON('./OrganizationManager?op=getModelsForComparation&source_service='+tree.getAllChecked()+'&target_service='+target_tree.getAllChecked(), function(data) {

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
                    if (response){
                        $("#response").html("<p>In case your Browser does not permit pop-up functionality you can see the \n\
                        mapping results in <a  target=\"_blank\" href=\"http://54.247.114.191/net.modelbased.mediation.gui-0.0.1-SNAPSHOT/repositories.html\">Mediator Portal</a> (Mappings section) looking for the following code <i>"+response.split("/")[5]+"</i></p>");
                        var caracteristicas = "height=500,width=750,scrollTo,resizable=1,scrollbars=1,location=0";
                        nueva=window.open('./organization/matchingResult.jsp?mediator_mapping='+response, 'Popup', caracteristicas);
                    }else{
                        alert("Please check that both schemata have been introduced to Mediator Portal!!");
                    }
                });
            }
        }


    </script>
    <script>
        function assign_selections()
        {

            if(tree.getAllChecked().split("\\$").length!=2 || target_tree.getAllChecked().split("\\$").length!=2){
                alert("You have to check only one input and one output web service! Thank You!");
                return false;
            }  else{
                document.forms['create_bridge'].elements['selections_source'].value  = tree.getAllChecked();
                document.forms['create_bridge'].elements['selections_target'].value = target_tree.getAllChecked();

                document.forms['create_bridge'].elements['installations_source'].value = $("select[name='installations_source'] option:selected").val();
                document.forms['create_bridge'].elements['installations_target'].value = $("select[name='installations_target'] option:selected").val();

                document.forms['create_bridge'].elements['CPPs_source'].value = $("select[name='CPPs_source'] option:selected").val();
                document.forms['create_bridge'].elements['CPPs_target'].value = $("select[name='CPPs_target'] option:selected").val();


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
            <div id="title"><h2>Create My Bridges at Service Level</h2></div>
            <div><p class="info_message">In this page you can see all the exposed web services (web services that are fully annotated).</p></div>
            <br>
            <div>
                <div style="width: 230px; float: left;">
                    <div class="st_title"><p>Source Web Service</p></div>
                    <div>
                        <p class="info_message">Step1: Choose Software Component:</p>
                        <select name="sourceSoftComp" id="softwareComp_source" style="margin:10px;">
                            <option value="All">All</option>
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
                        <p class="info_message">Step2: Choose Semantic taxonomies:</p>
                        <select name="Semantic taxonomies" id="tax_source" style="margin:10px;">
                            <option>All</option>

                            <%
                                if (session.getAttribute("taxonomies") != "") {
                                    JSONObject taxonomies = (JSONObject) session.getAttribute("taxonomies");
                                    for (Object o : taxonomies.keySet()) {
                                        String key = (String) o;
                                        String value = taxonomies.getString(key);
                                        System.out.println("key: " + key + " value: " + value);
                            %> <option><%=value%></option><%
                                }
                            }

                        %>
                        </select>

                        <p class="info_message">Step 3: Choose Web service:</p>
                        <select name="wsdls_source" id="wsdls_source" style="margin:10px;" disabled=""> </select>

                        <p class="info_message">Step 4: Choose Available Installation:</p>
                        <select name="installations_source" id="installations_source" style="margin:10px;width:226px" disabled=""></select>

                        <p class="info_message">Step 5: Choose Available CPP:</p>
                        <select name="CPPs_source" id="CPPs_source" style="margin:10px;" disabled=""></select>

                    </div>
                </div>
                <div style="width: 320px; float: left;">
                    <div class="st_title"><p>Target Web Service</p></div>
                    <p class="info_message">Step1: Choose Software Component:</p>
                    <select name="targetSoftComp" id="softwareComp_target" style="margin:10px;">
                        <option>All</option>

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
                    <p class="info_message">Step2: Choose Semantic taxonomies:</p>
                    <select name="Semantic taxonomies" id="tax_target" style="margin:10px;">
                        <option>All</option>

                        <%
                            if (session.getAttribute("taxonomies") != "") {
                                JSONObject taxonomies = (JSONObject) session.getAttribute("taxonomies");
                                for (Object o : taxonomies.keySet()) {
                                    String key = (String) o;
                                    String value = taxonomies.getString(key);
                                    System.out.println("key: " + key + " value: " + value);
                        %> <option><%=value%></option><%
                            }
                        }

                    %>
                    </select>

                    <p class="info_message">Step 3: Choose Web service:</p>
                    <select name="wsdls_target" id="wsdls_target" style="margin:10px;" disabled=""></select>

                    <p class="info_message">Step 4: Choose Available Installation:</p>
                    <select name="installations_target" id="installations_target" style="margin:10px;width:226px" disabled=""></select>

                    <p class="info_message">Step 5: Choose Available CPP:</p>
                    <select name="CPPs_target" id="CPPs_target" style="margin:10px;" disabled=""></select>



                </div>
            </div>
        <div>
            <p style="float:left;width:216px;" class="info_message">Step 6: Choose Source Operation:</p>
            <p style="float:left;width:300px;" class="info_message">Step 6: Choose Target Operation:</p>
        </div>
            <div id="box_tree" style="float:left;width:220px; height:155px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;margin: 10px;"/>
            <script>
                //tree = new dhtmlXTreeObject("box_tree", "100%", "100%", 0);
                //tree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                //tree.enableCheckBoxes(true, false);
                //tree.loadXML('./OrganizationManager?op=showexposedServicesByTaxonomy&tax=All&form=out&software_id=All', null);
            </script>
            </div>

        <div id="target_box_tree" style="width:220px; height:155px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;margin: 10px;">
            <script>
                //target_tree = new dhtmlXTreeObject("target_box_tree", "100%", "100%", 0);
                //target_tree.setImagePath("./js/dhtmlxSuite/dhtmlxTree/codebase/imgs/");
                //target_tree.enableCheckBoxes(true, false);
                //target_tree.loadXML('./OrganizationManager?op=showexposedServicesByTaxonomy&tax=All&form=out&software_id=All', null);
            </script>
        </div>

        <br>
        <br>
        <div>
            <div style="float:left;margin-bottom: 20px; margin-left: 100px;"><input TYPE="image" src="./img/searchMatches.png" id="search_matches" onclick="compare_schemas();"/></div>


            <form method="post" name="create_bridge" action="./OrganizationManager?op=createBridgingServices" onSubmit=" return assign_selections();">
                <input type='hidden' name='selections_source'  value='null'>
                <input type='hidden' name='selections_target'  value='null'>

                <input type='hidden' name='installations_source'  value='null'>
                <input type='hidden' name='installations_target'  value='null'>

                <input type='hidden' name='CPPs_source'  value='null'>
                <input type='hidden' name='CPPs_target'  value='null'>
                <div style="float:left; margin-left: 30px;"><input TYPE="image" src="./img/createBridge.png" id="create_bridging"/></div>

            </form>
        </div>


        <div id="response" style="margin-top: 30px;"></div>

    </div>
    </div>
    <div class="footer"><p>Copyright &copy; 2011 - 2013 Empower Consortium | All Rights Reserved</p></div>
</center>
</body>
</html>
