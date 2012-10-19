<%@page import="net.sf.json.JSONObject"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="../style/menu_style.css"/>
        <link rel="stylesheet" type="text/css" href="../style/layout4_setup.css"/>
        <link rel="stylesheet" type="text/css" href="../style/layout4_text_simple.css"/> 
        <link rel="stylesheet" type="text/css" href="../style/container.css"/>
        <script type="text/javascript" src="../js/jquery.js"></script> 
        <script type="text/javascript">                                          
            jQuery(document).ready(function(){
                var comparation_result =   jQuery.ajax ({
                    url: "http://54.247.114.191<%=request.getParameter("mediator_mapping")%>",
                    type: "GET",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    async:    false,
                    success: function (res) {
                        var text = res.responseText;
                        return text;
                    }
                }).responseText;                
                //alert(comparation_result);
                //$("#response").append(comparation_result);
                        
                var obj = jQuery.parseJSON(comparation_result);
              
                $("#response").append('<h2 class="bb">Entries in Mapping <p id="uid">'+obj.uid+'</p></h2>');
                $("#response").append('<div>');
                $("#response").append('<p><b>Source Schema:</b> <i>'+obj.sourceId+'</i>        <b>Target Schema:</b> <i>'+obj.targetID+'</i></p>');
                $("#response").append('<table><tr class="bb"><td style="width:215px;"><p><b>Source Element</b></p></td><td style="width:200px;"><p><b>Target Element</b></p></td><td style="width:44px;"><p><b>Degree</b></p></td><td style="width:80px;"><p><b>Algorithm</b></p></td></tr></table>');
                   
                $("#response").append('<table>');
                var content = obj.content;
                $.each(obj.content, function(index) {
                    if (content[index].source.indexOf("anonymous") == -1 && content[index].target.indexOf("anonymous") == -1 ){
                        $("#response").append('<tr><td><p>'+content[index].source+'</p></td><td><p>'+content[index].target+'</p></td><td><p>'+Math.round(content[index].degree*100)/100+'</p></td><td><p>'+content[index].origin+'</p></td></tr>');   
                    }
                     });
                $("#response").append('</table></div>');  
                
                // delete the mapping here for chrome because he does not suppor onBeforeUnload
                var is_chrome = navigator.userAgent.toLowerCase().indexOf('chrome') > -1;
                if (is_chrome){
                var uid = $("#uid").html();
                jQuery.ajax ({
                        url: "http://54.247.114.191/sensapp/mediation/repositories/mappings/"+uid,
                        type: "DELETE",
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",
                        success: function(){
                           }
                    });
                    }
            });
            
              
            function loadOut()
            {
                var uid = $("#uid").html();
                alert(uid+" Mapping will be deleted from Mediator Portal.");
                jQuery.ajax ({
                        url: "http://54.247.114.191/sensapp/mediation/repositories/mappings/"+uid,
                        type: "DELETE",
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",
                        success: function(){
                           }
                    });
            }
        </script> 
        <style type="text/css">
            div.header-breadcrumbs { background:none;}
            div.main { background:none;}
            div.sitemessage {margin-left: 0 !important;}
            div.footer p { margin-left: 20px; text-align: left;}
            p { clear: both; color: #505050; font-family: "trebuchet ms",arial,sans-serif;font-size: 12px;font-weight: normal;margin: 1em 0 0.5em;}
            div#response{width: 700px;}
            .bb{background-color: #A0D651;}

        </style>


        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">        
        <title>Mediator Mapping</title>
    </head>
    <body class="yui-skin-sam" onBeforeUnload="loadOut()">
    <center>
        <div class="page_container">  
            <div class="header"><!-- A.1 HEADER TOP -->
                <!-- A.2 HEADER MIDDLE -->
                <div class="header-middle"><!-- Site message --><div class="sitemessage"><h1 style="float:right">Empower PROJECT</h1><h2 style="width:450px">A Semantic Service-Oriented Enterprise Application Integration Middleware Addressing the Needs of the European SMEs</h2></div></div>                                    
                <div class="header-bottom"><!-- Navigation Level 2 (Drop-down menus) --><div class="nav2"><ul><li id="current" class="last"><a href="actions.jsp?action=tabSelect&amp;tabIndex=0&amp;menuitemId=tabA"></a></li></ul></div></div>
                <div class="header-breadcrumbs"><ul></ul></div>
            </div>   
        </div>
        <div class="main">  
            <div class="main-content">
                <div id="response">


                </div>
            </div>
        </div>
        <div class="footer"><p>Copyright &copy; 2012 Empower Consortium | All Rights Reserved</p></div>        
    </center>
</body>
</html>

