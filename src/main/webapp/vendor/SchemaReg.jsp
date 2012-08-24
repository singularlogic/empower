<%@page import="net.sf.json.JSONObject"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="./style/menu_style.css"/>
        <link rel="stylesheet" type="text/css" href="./style/layout4_setup.css"/>
        <link rel="stylesheet" type="text/css" href="./style/layout4_text.css"/>
        <link rel="stylesheet" type="text/css" href="./style/container.css"/>
        <link rel="stylesheet" type="text/css" href="../style/menu_style.css"/>
        <link rel="stylesheet" type="text/css" href="../style/layout4_setup.css"/>
        <link rel="stylesheet" type="text/css" href="../style/layout4_text.css"/>
        <link rel="stylesheet" type="text/css" href="../style/container.css"/>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">        
        <title>Service registration</title>
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
                <div class="round-border-topright"></div>
                <h1 class="first">Menu</h1>

                <div class='glossymenu'>
                    <a class='menuitem submenuheader1'>Vendor Menu</a>
                    <%if (request.getParameter("jsp").equalsIgnoreCase("true")) { %>
                    <div class='submenu1'>
                        <a class='menuitem' href='./softwareReg.jsp'>Register new software</a>
                    </div><!--submenuENDdiv-->
                    <div class='submenu1'>
                        <a class='menuitem' href='./showSoftwareComponent.jsp'>Show software components</a>
                    </div><!--submenuENDdiv-->                
                    <div class='submenu1'>
                        <a class='menuitem' href='/empower/DIController?op=signout'>Logout</a>
                    </div><!--submenuENDdiv--> 
                    <% } else {%>
                    <div class='submenu1'>
                        <a class='menuitem' href='vendor/softwareReg.jsp'>Register new software</a>
                    </div><!--submenuENDdiv-->
                    <div class='submenu1'>
                        <a class='menuitem' href='vendor/showSoftwareComponent.jsp'>Show software components</a>
                    </div><!--submenuENDdiv-->                
                    <div class='submenu1'>
                        <a class='menuitem' href='/DIController?op=signout'>Logout</a>
                    </div><!--submenuENDdiv--> 
                    <% }%>
                </div>
            </div>
            <div class="main-content">
                <br><h2><b>Register service details</b></h2>
                <br>
                <%if (request.getParameter("jsp").equalsIgnoreCase("true")) { %>
                <form method="POST" action="/empower/DIController?op=schema_reg" name="ws_registration" enctype="multipart/form-data">
                <%}else{%>
                <form method="POST" action="./DIController?op=schema_reg" name="ws_registration" enctype="multipart/form-data">
                <%}%>
                <input type='hidden' name='software_id' value='<%= request.getParameter("software_id")%>'>
                    <table>
                        <tbody>
                            <tr>
                                <td align="right">Web Service Description:</td>
                                <td align="left">
                                    <form>
                                        <label for="existing_web_service_name">                        
                                            <input name="web_service_name" type="radio" value=""/>New Service
                                            <input name="new_web_service_name"  type="text" value="" /><br/>
                                            <%
                                            System.out.println("holaaaaaaaa");
                                            if (session.getAttribute("services")!=""){ 
                                            System.out.println("holaaaaaaaa1");   
                                            JSONObject services = (JSONObject) session.getAttribute("services");
                                                for (Object o : services.keySet()) {
                                                    String key = (String) o;
                                                    String value = services.getString(key);
                                                    System.out.println("key: " + key + " value: " + value);
                                            %>     
                                            <input name="web_service_name" type="radio" value="<%=key%>" /><%=value%> <br />
                                            <%}}%>
                                        </label>
                                        <br />
                                    </form>

                                </td>
                            </tr>
                            <tr>
                                <td align="right">Operation Description:</td>
                                <td align="left">
                                    <input type="text" name="operation_name" size="20">
                                </td>
                            </tr>
                            <tr>
                                <td align="right">Choose accion:</td>
                                <td align="left">
                                    <select name="inputoutput" size="2">
                                        <option>input</option>
                                        <option>output</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td align="right">Schema Name:</td>
                                <td align="left">
                                    <input type="text" name="schema_name" size="20">
                                </td>
                            </tr>
                            <tr>
                                <td align="right">File:</td>
                                <td align="left">
                                    <input type="file" name="schema_file" size="20">
                                </td>
                            </tr>
                            <tr>
                                <td align="right"><input type="submit" value="Submit" name="submit_button"></td>
                            </tr>
                        </tbody>
                    </table>
                </form>
            </div>
        </div>
        <div class="footer"><p>Copyright &copy; 2012 Empower Consortium | All Rights Reserved</p></div>                    
    </center>
</body>
</html>

