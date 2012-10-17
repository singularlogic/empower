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
                        $("#response").append(comparation_result);
                });
        </script> 
        <style type="text/css">
            div.header-breadcrumbs { background:none;}
            div.main { background:none;}
            div.sitemessage {margin-left: 0 !important;}
            div.footer p { margin-left: 20px; text-align: left;}
        </style>
        
       
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">        
        <title>Mediator Mapping</title>
    </head>
    <body class="yui-skin-sam">
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
              <div id="response"></div>
          </div>
        </div>
            <div class="footer"><p>Copyright &copy; 2012 Empower Consortium | All Rights Reserved</p></div>        
    </center>
    </body>
</html>

