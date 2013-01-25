<%@page import="net.sf.json.JSONObject"%>
<%@include file="../timedoutRedirect.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <script type="text/javascript" src="./js/jquery.js"></script>  
        <script type="text/javascript">                                          
            $(document).ready(function() {
                $("#a").click(function() {
                    //var str='< %=session.getAttribute("source_xml")%>';
                    var lala =  $("#sourcee_xml_textarea").html();
                    var csv=replaceAll(lala,"&lt;","<"); 
                    var csv=replaceAll(csv,"&gt;",">");
                    var a = document.getElementById('a');
                    a.href='data:text/xml;base64,' + btoa(csv);
                });
                
                $("#target").click(function() {
                    var target_xml =  $("#target_xml_textarea").html();
                    var xml=replaceAll(target_xml,"&lt;","<"); 
                    var xml=replaceAll(xml,"&gt;",">");
                    var target = document.getElementById('target');
                    target.href='data:text/xml;base64,' + btoa(xml);
                });
            
                function replaceAll(txt, replace, with_this) {
                    return txt.replace(new RegExp(replace, 'g'),with_this);
                }
                
                $("#target_xml_textarea").hide();
                $("#sourcee_xml_textarea").hide();
                
                
                if($("#target_xml_styled").html()==""){
                    $("#target_xml_textarea").show();
                }else{
                     $("#target_xml_textarea").hide();
                }
                
                if($("#sourcee_xml_styled").html()==""){
                    $("#sourcee_xml_textarea").show();
                }else{
                     $("#sourcee_xml_textarea").hide();
                }
              
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
                <div id="menu_grid" style="width:180px; height:210px" class='glossymenu'>
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
                <p><h2>Do Bridging - Results</h2>


                <div id="sourcee_xml" style="float:left;width:260px;margin-right:23px;height:400px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;"/>
                <p><h2>Source XML</h2>
                <div id="sourcee_xml_styled"></div>
                <script>
                    LoadXMLString('sourcee_xml_styled', <%=session.getAttribute("source_xml")%>);
                </script>

                <textarea rows="130" cols="50" style="border:none;" id="sourcee_xml_textarea">
                    <%=session.getAttribute("source_xml")%>
                </textarea>
                </div> 

            <div id="target_xml" style="float:left;width:260px; height:400px;background-color:#f5f5f5;border :1px solid Silver;; overflow:auto;"/>
            <p><h2>Target XML</h2>
             <div id="target_xml_styled"></div>
            <script>
             LoadXMLString('target_xml_styled', '<%=session.getAttribute("target_xml")%>');            
            </script> 
            <textarea rows="130" cols="50" style="border:none;" id="target_xml_textarea">
                    <%=session.getAttribute("target_xml")%>
            </textarea>
            </div>  
        <br><br><br><br>
        
        <div> 
            <div style="float:left;width:260px;"><a id="a" target="_blank">Open in browser source xml</a></div>
            <div style="float:left;width:260px;"><a id="target" target="_blank">Open in browser target xml</a></div>
        
        
        </div>
        
        <div style="clear:both;"></div>

        <%if (request.getParameter("type").equalsIgnoreCase("schema")) {%>
        <form method="post" name="do_bridge" style="margin-top: 25px;" action="./OrganizationManager?op=doBridging&cpa_id=<%=request.getParameter("cpa_id")%>" enctype="multipart/form-data">
            <input type="file" name="source_xml" value="" id="fileinput"/>
            <input type="submit" value="Show target xml"  id="do_bridging"/>
        </form>
        <br>
        <%}%>
        <div class="st_title"><h2>Info about the Process</h2></div>
        <%
            if (session.getAttribute("infoBridgingProcess") != "") {
                JSONObject infoProcess = (JSONObject) session.getAttribute("infoBridgingProcess");
                for (Object o : infoProcess.keySet()) {
                    String key = (String) o;
                    String value = infoProcess.getString(key);
                    //if (key.equalsIgnoreCase("FirstSoapRequest"))         
%> 
        <div id="<%=key%>" style="border:2px;width:250px;"></div>
        <h2><%=key%></h2>       
        <textarea rows="7" cols="60" style="border:none;">
            <%=value%>
        </textarea>
        <%}
            }%>


    </div> 
</div>
<div class="footer"><p>Copyright &copy; 2012 Empower Consortium | All Rights Reserved</p></div>
</center>                
</body>
</html>
