<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>  
        <script type="text/javascript" src="./js/jquery.xdomainajax.js"></script>
        <script type="text/javascript" src="./js/jquery.js"></script>  
        

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
        <!-- add schema -->
        <script>
            jQuery(document).ready(function(){
                //jQuery.ajax ({
                //url: "http://54.247.114.191/sensapp/importer",
                //type: "POST",
                //data: JSON.stringify({modelId:"caca",description:"caca",format:"XSD",content:"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>   <xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">  <xs:complexType name=\"Absence\">  <xs:sequence>  <xs:element name=\"SeqNo\" nillable=\"true\" minOccurs=\"0\">  <xs:simpleType>  <xs:restriction base=\"xs:int\"> <xs:minInclusive value=\"-2147483648\" />  <xs:maxInclusive value=\"2147483647\" />  </xs:restriction></xs:simpleType> </xs:element> </xs:sequence> </xs:complexType> </xs:schema>"}),
                //dataType: "json",
                //contentType: "application/json; charset=utf-8",
                //success: function(){
                //    alert ("nomizo oti to ekane");
                //}
                //});
        
                //alert("can i delete caca  it now?");
                // jQuery.ajax ({
                // url: "http://54.247.114.191/sensapp/mediation/repositories/models/caca",
                // type: "DELETE",
                // dataType: "json",
                // contentType: "application/json; charset=utf-8",
                // success: function(){
                //    alert ("nomizo oti to ekane");
                // }
                // });
                //alert("can i compare samples - article now?");
       
        
                //var response =   jQuery.ajax ({
                 //   url: "http://54.247.114.191/sensapp/mediator",
                 //   type: "POST",
                 //   data: JSON.stringify({algorithm:"syntactic",source:"samples-article",target:"samples-article"}),
                 //   dataType: "json",
                 //   contentType: "application/json; charset=utf-8",
                 //   async:    false,
                 //   success: function (res) {
                 //      var text = res.responseText;
                 //       $("#response").html(text);
                 //       alert("this is the responsse: "+text);
                 //      return text;

                 //   }

               // }).responseText;
                
               // alert(response);
               //  $("#response").html(response);

        
               
       

            });
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
                            response                     <li><a href="./" title="Go to Start page">Home</a></li>
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
            <div class="main-content">    
                <br>
                <p><h2>Information</h2>
                <br>
                <div id="response"></div>
                <form method="post" id="callannotator" name="callannotator" action="http://54.247.114.191/sensapp/importer">
                    <p>content: </p><textarea name="content" rows="4" cols="20"></textarea>
                    <p>description:</p><input type="text" name='description' value=''>
                    <p>modelId:</p><input type="text"  name='modelId'  value=''>
                    <p>format:</p><input type="text"  name='format'  value=''>

                    submit:<input type="submit" value="Continue" name="A ver">
                </form>
                <input type="submit" value="do Ajax Call" onclick="" />

            </div>
        </div>              
    </body>
</html>

