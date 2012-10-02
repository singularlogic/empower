/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maincontrol;

import dataaccesslayer.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.xml.sax.SAXException;
import orgports.OrgDBConnector;
import vendorports.VendorDBConnector;
import xml.WSDLParser;
import xml.XSDParser;

/**
 *
 * @author elenithis.forwardToPage("/VendorManager?op=schema_reg&software_id=" + softwareID, request, response);
     
 */
public class DIController extends HttpServlet {

    private static String xml_rep_path = "/home/eleni/Documents/ubi/empower/empower-deliverable-september/empower";

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String operation;
        HttpSession session;

        session = request.getSession(true);
        operation = request.getParameter("op");
        try {
            System.out.println("operation: " + operation);

            if (operation != null) {
                if (operation.equals("signin")) {
                    this.manageSignIn(request, response, session);
                } else if (operation.equals("signup")) {
                    this.manageSignUp(request, response, session);
                } else if (operation.equals("signout")) {
                    this.manageSignOut(request, response, session);
                } else if (operation.equals("software_reg")) {
                    this.manageVendorSoftwareReg(request, response, session);
                } else if (operation.equals("show_components")) {
                    this.manageShowComponents(request, response, session);
                } else if (operation.equals("show_schema")) {
                    this.manageShowSchema(request, response, session);
                } else if (operation.equals("show_schemas")) {
                    this.showSchemas(request, response, session);
                } else if (operation.equals("schema_reg")) {
                    this.manageVendorSchemaReg(request, response, session);
                } else if (operation.equals("post_mappings")) {
                    this.managePostMappings(request, response, session);
                } else if (operation.equals("show_bridging_schemas")) {
                    this.manageBridgingSchemas(request, response, session);
                }else if (operation.equals("show_bridging_services")) {
                    this.manageBridgingServices(request, response, session);
                }else if (operation.equals("doBridging")) {
                    this.doBridging(request, response, session);
                }else if (operation.equals("showMyBridges")) {
                    this.showMyBridges(request, response, session);
                } else if (operation.equals("present_service_operations")) {
                    this.presentServiceOperationsTree(request, response, session);
                } else if (operation.equals("annotate_operations")) {
                    this.annotateOperations(request, response, session);
                } else if (operation.equals("present_service_schema")) {
                    this.presentServiceSchemaTree(request, response, session);
                } else if (operation.equals("present_service")) {
                    this.presentServiceTree(request, response, session);
                } else if (operation.equals("present_central_trees")) {
                    this.presentOptionTrees(request, response, session);
                } else if (operation.equals("annotate")) {
                    this.annotate(request, response, session);
                } else if (operation.equals("showAvailableSources_title")) {
                    this.showAvailableSources_title(request, response, session);
                } else if (operation.equals("showcurrentsoftcomp")) {
                    this.showcurrentsoftcomp(request, response, session);
                } else if (operation.equals("get_menu")) {
                    this.getMenu(request, response, session);
                } else if (operation.equals("show_service")) {
                    this.manageShowService(request, response, session);
                } else if (operation.equals("show_services")) {
                    this.showServices(request, response, session);
                } else if (operation.equals("service_reg")) {
                    this.manageVendorServiceReg(request, response, session);
                }



            }
        } catch (Throwable t) {
            t.printStackTrace();
            this.forwardToPage("/error/generic_error.jsp?errormsg= " + t.getMessage() + " " + operation, request, response);
        } finally {
            out.close();
        }
    }

    private void forwardToPage(String url, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        RequestDispatcher dis;

        dis = getServletContext().getRequestDispatcher(url);
        dis.forward(req, resp);
        return;
    }

    protected void manageSignIn(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        //String userType = request.getParameter("user_type");

        MainControlDB mainControlDB = new MainControlDB();
        String userType = mainControlDB.isLegalUser(name, password);

        if (userType != null) {
            session = request.getSession(true);
            session.setAttribute("userType", userType);
            session.setAttribute("name", name);
            String redirectionURL = new String("/" + userType + "/" + userType + "menu.jsp");
            this.forwardToPage(redirectionURL, request, response);
        } else {
            this.forwardToPage("/error/signin_error.jsp?errormsg=Error in Login. Try to Login again or create a new user.", request, response);
        }


    }

    protected void manageSignOut(HttpServletRequest req, HttpServletResponse resp, HttpSession session)
            throws IOException, ServletException {
        session.invalidate();
        session = req.getSession(true);
        this.forwardToPage("/signin.jsp", req, resp);
    }

    protected void manageSignUp(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        String userType = request.getParameter("user_type");

        MainControlDB mainControlDB = new MainControlDB();
        mainControlDB.insertUser(name, password, userType);

        session = request.getSession(true);
        session.setAttribute("userType", userType);
        session.setAttribute("name", name);
        String redirectionURL = new String("/" + userType + "/" + userType + "menu.jsp");
        this.forwardToPage(redirectionURL, request, response);
    }

    private boolean verifyUser(String userType, HttpSession session) {
        boolean isVerified = false;

        String type = (String) session.getAttribute("userType");

        if (type != null) {
            if (type.equals(userType)) {
                isVerified = true;
            }
        }

        return isVerified;
    }

    protected void manageVendorSoftwareReg(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session)
            throws ServletException, IOException {
        int software_id;

        if (!verifyUser("vendor", session)) {
            this.forwardToPage("/error/generic_error.jsp?errormsg=op_not_supported_for_you", request, response);
        }

        this.forwardToPage("/VendorManager?op=software_reg", request, response);
    }

    protected void manageShowComponents(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session)
            throws ServletException, IOException {
        if (verifyUser("vendor", session)) {
            this.forwardToPage("/VendorManager?op=show_components", request, response);
        } else if (verifyUser("organization", session)) {
            String bridging = request.getParameter("bridging");
            this.forwardToPage("/OrganizationManager?op=show_components&bridging="+bridging, request, response);
        } else {
            this.forwardToPage("/error/generic_error.jsp?errormsg=op_not_supported_for_you", request, response);
        }
    }

    protected void manageShowSchema(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        String xsd_num = ((String) request.getParameter("xsd")).split("_")[1];
        String software_id = ((String) request.getParameter("xsd")).split("_")[2];
        session.setAttribute("xsd", (String) request.getParameter("xsd"));


        MainControlDB mainControlDB = new MainControlDB();
        LinkedList<Service> services = (LinkedList<Service>) mainControlDB.getServices(software_id, false,"IS NULL");
        System.out.println("services: " + services + "lenght: " + services.size());
        if (services.size() > 0) {

            JSONObject json_services = new JSONObject();
            Iterator serv_iterator = services.iterator();
            while (serv_iterator.hasNext()) {
                Service serv = (Service) serv_iterator.next();
                json_services.put(serv.getService_id(), serv.getName());
            }
            session.setAttribute("services", json_services);
        } else {
            session.setAttribute("services", "");
        }

        if (verifyUser("vendor", session)) {
            // if software component without an xsd
            if (xsd_num.equals("0")) {
                this.forwardToPage("/vendor/SchemaReg.jsp?software_id=" + software_id + "&jsp=false", request, response);
            } else {
                //this.forwardToPage("/vendor/showSchemas.jsp?software_id=" + software_id, request, response);
                this.forwardToPage("/showSchemas.jsp?software_id=" + software_id, request, response);

            }
        }

        if (verifyUser("organization", session)) {

            this.forwardToPage("/showSchemas.jsp?software_id=" + software_id, request, response);

            //this.forwardToPage("/organization/registerBinding.jsp?service_id=" + serviceID, request, response);
        }
    }

    protected void showSchemas(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {
        Iterator XSDIterator;
        int rowID = 1;

        MainControlDB mainControlDB = new MainControlDB();

        XSDIterator = (verifyUser("vendor", session)) ? mainControlDB.getSchemas((String) request.getParameter("software_id"), false).iterator() : mainControlDB.getSchemas((String) request.getParameter("software_id"), true).iterator();

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("<rows>");

        while (XSDIterator.hasNext()) {
            Schema schema = (Schema) XSDIterator.next();
            out.write("<row id=\"" + schema.getSchema_id() + "\"><cell>" + schema.getName() + "</cell>"
                    + "<cell> Annotate Operations^./presentOperationTree.jsp?schema_id=" + schema.getSchema_id() + "^_self</cell>"
                    + "<cell> Annotate Data^./presentDataTree.jsp?schema_id=" + schema.getSchema_id() + "^_self</cell>"
                    //+ "<cell>Annotate Data^./vendor/presentServiceTree.jsp?service_id=" + schema.getSchema_id()+ "^_self</cell>"
                    //+ "<cell>Annotate Functions^./vendor/presentServiceFunctionTree.jsp?service_id=" + schema.getSchema_id()+ "^_self</cell>"
                    + "</row>");
        }

        out.write("</rows>");
        out.flush();

        return;
    }

    protected void manageShowService(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        String service_num = ((String) request.getParameter("service")).split("_")[1];
        String software_id = ((String) request.getParameter("service")).split("_")[2];
        session.setAttribute("service", (String) request.getParameter("service"));


        MainControlDB mainControlDB = new MainControlDB();
        LinkedList<Service> services = (LinkedList<Service>) mainControlDB.getServices(software_id, false,"IS NOT NULL");
        System.out.println("services: " + services + "lenght: " + services.size());
        if (services.size() > 0) {

            JSONObject json_services = new JSONObject();
            Iterator serv_iterator = services.iterator();
            while (serv_iterator.hasNext()) {
                Service serv = (Service) serv_iterator.next();
                json_services.put(serv.getService_id(), serv.getName());
            }
            session.setAttribute("services", json_services);
        } else {
            session.setAttribute("services", "");
        }
        // if software component with any service
        if (service_num.equals("0")) {
            this.forwardToPage("/vendor/serviceReg.jsp?software_id=" + software_id + "&jsp=false", request, response);
        } else {
            this.forwardToPage("/showServices.jsp?software_id=" + software_id, request, response);
        }

    }

    protected void showServices(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {
        Iterator servIterator;
        String img_link  ="";

        MainControlDB mainControlDB = new MainControlDB();

        servIterator = (verifyUser("vendor", session)) ? mainControlDB.getServices((String) request.getParameter("software_id"), false,"IS NOT NULL").iterator()
                : mainControlDB.getServices((String) request.getParameter("software_id"), true,"IS NOT NULL").iterator();

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("<rows>");
        

        while (servIterator.hasNext()) {
            Service service = (Service) servIterator.next();
            img_link = (service.isExposed())?"js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/green.gif":"js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/red.gif";
            
           
            System.out.println("img_link"+img_link);
            out.write("<row id=\"" + service.getService_id() + "\">"
                    + "<cell>" + service.getName() + "</cell>"
                    + "<cell>Annotate Functions^./presentOperationTree.jsp?service_id=" + service.getService_id() + "^_self</cell>"
                    + "<cell>Annotate Data^./presentDataTree.jsp?service_id=" + service.getService_id() + "^_self</cell>"
                    + "<cell type=\"img\">"+img_link+"</cell>"
                    + "</row>");
//                      "<cell>Delete^./VendorManager?op=delete_service&amp;service_id=" + service.getServiceID() +
//                      "^_self</cell>" +

//                      "^_self</cell>" +
//                      "<cell>Annotate Taxonomy^./vendor/presentServiceTaxonomy.jsp?service_id=" + service.getServiceID() +
//                      "^_self</cell>" +                 

        }

        out.write("</rows>");
        out.flush();

        return;
    }

    protected void presentServiceOperationsTree(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, WSDLException {

        MainControlDB mainControlDB = new MainControlDB();
        response.setContentType("text/xml; charset=UTF-8");

        if (request.getParameter("schema_id") != null) {
            int schema_id = Integer.parseInt(request.getParameter("schema_id"));
            LinkedList<Operation> operations = mainControlDB.getOperationsBySchema(schema_id);
            this.outputOperationsToXML(response.getWriter(), operations);

        }
        if (request.getParameter("service_id") != null) {
            int service_id = Integer.parseInt(request.getParameter("service_id"));
            Service service = mainControlDB.getService(service_id);
            WSDLParser wsdlParser = new WSDLParser(service.getWsdl(), service.getNamespace());
            wsdlParser.loadService(service.getName());
            wsdlParser.outputFunctionsToXML(response.getWriter());
        }

    }

    public void outputOperationsToXML(PrintWriter out, LinkedList<Operation> operations) {
        try {
            //work with collection. get services
            Map<Integer, String> services = new HashMap<Integer, String>();

            System.out.println("operations size " + operations.size());

            Iterator<Operation> op_it = operations.iterator();

            while (op_it.hasNext() == true) {

                Operation op = op_it.next();

                int service_id = op.getService_id();
                String web_service_name = op.getWeb_service_name();


                if (!services.containsKey(service_id)) {
                    services.put(service_id, web_service_name);
                }

            }

            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

            out.write("<tree id=\"0\">");

            //Get Map in Set interface to get key and value
            Iterator it = services.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry m = (Map.Entry) it.next();
                int key = (Integer) m.getKey();
                String value = (String) m.getValue();


                out.write("<item text=\"" + value
                        + "\" id=\"" + key + "\" nocheckbox=\"true\">");

                System.out.println("key: " + key);
                System.out.println("This is out: " + out);


                op_it = operations.iterator();
                while (op_it.hasNext()) {

                    Operation op = op_it.next();
                    if (op.getService_id() == key) {
                        out.write("<item text=\"" + op.getOperation_name() + "\" id=\"" + op.getOperation_id() + "\"/>");
                        System.out.println(" operationname " + op.getOperation_name() + " operationid " + op.getOperation_id());
                    }
                }
                out.write("</item>");
            }

            out.write("</tree>");
            out.close();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected void annotateOperations(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {

        int schema_id = -1;
        int operation_id = -1;
        int service_id = -1;
        String operation_name = "";
        String funcSelections = request.getParameter("funcselections");
        String name = (String) session.getAttribute("name");
        String userType =(verifyUser("organization", session)) ? "organization": "vendor"; 
        MainControlDB mainControlDB = new MainControlDB();


        if (!request.getParameter("schema_id").equalsIgnoreCase("null")) {
            schema_id = Integer.parseInt((String) request.getParameter("schema_id"));
            operation_id = Integer.parseInt((String) request.getParameter("selections"));
            this.annotateOperations_schema(schema_id,operation_id,funcSelections,name,userType);
            this.forwardToPage("/annotationResult.jsp?schema_id=" + schema_id+"&dataannotation=false", request, response);
        }
        if (!request.getParameter("service_id").equalsIgnoreCase("null")) {
            service_id = Integer.parseInt((String) request.getParameter("service_id"));
            operation_name = (String) request.getParameter("selections");
            this.annotateOperations_service(service_id,operation_name,funcSelections,name,userType);
            this.forwardToPage("/wsannotationResult.jsp?service_id=" + service_id, request, response);
        }
        //System.out.println(funcSelections + " " + schema_id + " " + operation_id + " " + name);

       
        //vendorDBConnector.insertCVPFunction(funcSelections, serviceID, selections, name);
    }
    
    protected boolean annotateOperations_schema(int schema_id, int operation_id, String funcSelections, String name,String userType){
        
        MainControlDB mainControlDB = new MainControlDB();
        
        try{
        
        if (userType.equalsIgnoreCase("organization")) {
            mainControlDB.insertCPP(schema_id,-1, name);
            mainControlDB.functionalAnnotationBySchema(operation_id, funcSelections, -1);
        } else {

            Integer cvpID = new Integer(mainControlDB.insertCVP(schema_id, -1, name));
           
                mainControlDB.functionalAnnotationBySchema(operation_id, funcSelections, cvpID);
        }
        
         } catch (Throwable t) {
            t.printStackTrace();
        }
        
        return true;
    }
    
      protected boolean annotateOperations_service(int service_id, String operation_name, String funcSelections, String name,String userType){
        
        boolean isFullyMatched = false;
        Integer cvpID = -1;
        MainControlDB mainControlDB = new MainControlDB();
        
      if (userType.equalsIgnoreCase("organization")) {
            mainControlDB.insertCPP(-1,service_id,name);
            mainControlDB.functionalAnnotationByService(service_id, operation_name,funcSelections, -1);
            cvpID = mainControlDB.getCVP(-1, service_id);
        } else {
            cvpID = new Integer(mainControlDB.insertCVP(-1, service_id, name));
            mainControlDB.functionalAnnotationByService(service_id, operation_name, funcSelections, cvpID);      
        }
        isFullyMatched = mainControlDB.isFullyMatched(0, service_id, cvpID);
        
        return true;
    }

    protected void presentServiceSchemaTree(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, ParserConfigurationException, SAXException {

        int schema_id = Integer.parseInt(request.getParameter("schema_id"));
        
        MainControlDB mainControlDB = new MainControlDB();
        Schema schema = mainControlDB.getSchema(schema_id);
        
        System.out.println(schema_id+"schema location: " + schema.getLocation()+schema.getInputoutput()+"--"+ schema.getOperation_id()+"--"+schema.getOp_taxonomy_id()+"--"+schema.getCvp_id()+"--"+schema.getService_id()+"--"+schema.getSchema_id()+"--"+schema.getSelections());

        XSDParser p = new XSDParser(schema);
        String xml_string = p.convertSchemaToXML();
        System.out.println("xml_string: " + xml_string);

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write(xml_string);
    }
    
    protected void presentServiceTree(HttpServletRequest request,
                                HttpServletResponse response,
                                HttpSession session)
    throws IOException, ServletException, WSDLException
    {
        int serviceID = Integer.parseInt(request.getParameter("service_id"));
        
        MainControlDB mainControlDB = new MainControlDB();
        Service service = mainControlDB.getService(serviceID);
        response.setContentType("text/xml; charset=UTF-8");
        
        WSDLParser wsdlParser = new WSDLParser(service.getWsdl(),
                                               service.getNamespace());
        wsdlParser.loadService(service.getName());
        wsdlParser.outputToXML(response.getWriter());
        
        System.out.println("outputXML: " +response.getWriter());
    }

    protected void presentOptionTrees(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {
        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.write("<?xml version='1.0' encoding='UTF-8'?><tree id='0'><item text='accountingEntriesComplexType' id='accountingEntriesComplexType'>"
                + "<item text='documentInfoComplexType' id='documentInfoComplexType'/><item text='entityInformationComplexType' id='entityInformationComplexType'/><item text='entryHeaderComplexType' id='entryHeaderComplexType\'>"
                + "<item text='entryDetailComplexType' id='entryDetailComplexType'/></item></item></tree>");

        out.flush();
        out.close();

    }

    protected void annotate(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, ParserConfigurationException, SAXException, XPathExpressionException, WSDLException {
        
        if  (!request.getParameter("schema_id").equalsIgnoreCase("null")) annotate_data_schema(request,response,session);
        if  (!request.getParameter("service_id").equalsIgnoreCase("null")) annotate_data_service(request,response,session);
 
    }
    
    protected void annotate_data_schema(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, ParserConfigurationException, SAXException, XPathExpressionException, WSDLException {
       
        int schema_id = Integer.parseInt((String) request.getParameter("schema_id"));    
        String schema_data = ((String) request.getParameter("selections")).split("--")[0];
        String inputoutput = ((String) request.getParameter("selections")).split("--")[1];
        String centralTree = request.getParameter("centraltree");
        String mapping = null;
        String xbrl_mismatch="false";

        MainControlDB mainControlDB = new MainControlDB();
        Schema schema = mainControlDB.getSchema(schema_id);
        String filename = ((String) schema.getLocation()).split("/xsd/")[1];

        DataAnnotations dataannotations = mainControlDB.getMapping(schema_id,-1, inputoutput+"$" + schema_data);
        mapping = dataannotations.getMapping();

        if (mapping != null) {
            mapping = new String(mapping.replace("\"", "\\\""));
            System.out.println("==== mapping=" + mapping.substring(0, 500));
        }

        request.setAttribute("mapping", mapping);
        if (verifyUser("vendor", session))   request.setAttribute("map_type", "cvp");
        else request.setAttribute("map_type", "cpp");
       
        
        request.setAttribute("selections", inputoutput+"$" + schema_data);
        request.setAttribute("xbrl", centralTree);


        //we pass to the annotator the schema id as service id until only for the first deliverable of the empower project, so as not to double change the annotator without reason
        if (inputoutput.equals("output")) {

            //response.sendRedirect(response.encodeRedirectURL("http://127.0.0.1:8080/annotator?input=xsd/XBRL-GL-PR-2010-04-12/plt/case-c-b-m/gl-cor-content-2010-04-12.xsd&output=xsd/" + filename + "&service_id=" + schema_id + "&map_type=cvp&outputType=" + schema_data +"&inputType=" + centralTree + "&mapping="+ mapping)); 
            request.setAttribute("output", "xsd/" + filename);
            request.setAttribute("input", "xsd/XBRL-GL-PR-2010-04-12/plt/case-c-b-m/gl-cor-content-2010-04-12.xsd");
            request.setAttribute("outputType", schema_data);
            request.setAttribute("inputType", centralTree);
           
            
        } else {
            //response.sendRedirect(response.encodeRedirectURL("http://127.0.0.1:8080/annotator?input=xsd/" + filename + "&output=xsd/XBRL-GL-PR-2010-04-12/plt/case-c-b-m/gl-cor-content-2010-04-12.xsd&service_id=" + schema_id + "&map_type=cvp&outputType=" + centralTree + "&inputType=" + schema_data + "&mapping="));
            request.setAttribute("input", "xsd/" + filename);
            request.setAttribute("output", "xsd/XBRL-GL-PR-2010-04-12/plt/case-c-b-m/gl-cor-content-2010-04-12.xsd");
            request.setAttribute("outputType", centralTree);
            request.setAttribute("inputType", schema_data);
            
            //request.setAttribute("selections", schema_data + "$" + centralTree);
        }
        if (!centralTree.equalsIgnoreCase(dataannotations.getXbrl())) xbrl_mismatch= dataannotations.getXbrl()+"$"+centralTree; 
        this.forwardToPage("/proceedDataTree.jsp?schema_id=" + schema_id+"&service_id=-1&xbrl_mismatch="+xbrl_mismatch, request, response);
        
    }
    
    protected void annotate_data_service(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, ParserConfigurationException, SAXException, XPathExpressionException, WSDLException {
   
        int service_id = Integer.parseInt((String)request.getParameter("service_id"));
        String selections = request.getParameter("selections");
        String centralTree = request.getParameter("centraltree");
        String mapping = new String("");
        String choice = null;
        String xbrl_mismatch="false";
        if (verifyUser("vendor", session)) {
            request.setAttribute("map_type", "cvp");
        } else {
            request.setAttribute("map_type", "cpp");
        }

        MainControlDB mainControlDB = new MainControlDB();
        DataAnnotations dataannotations = mainControlDB.getMapping(-1,service_id, selections);
        mapping = dataannotations.getMapping();

        
       if (mapping != null) {
            mapping = new String(mapping.replace("\"", "\\\""));
           System.out.println("==== mapping=" + mapping.substring(0, 500));
        }

        request.setAttribute("mapping", mapping);
        request.setAttribute("selections", selections); 
        request.setAttribute("xbrl", centralTree);
        
        Service service = mainControlDB.getService(service_id);
        
        WSDLParser wsdlParser = new WSDLParser(service.getWsdl(), service.getNamespace());
        choice = selections.split("\\$")[1];
        String inputoutput = selections.split("\\$")[0];
        String xsdTypes = wsdlParser.extractXSD(choice);
               
        String filename = new String("cvp_" + service.getName() + "_" + ((int)(100000*Math.random())) + ".xsd"); 
        String xsdFilename = new String(xml_rep_path + "/xsd/" + filename);
        PrintWriter xsdFile = new PrintWriter(xsdFilename);
        xsdFile.write(xsdTypes);
        xsdFile.close();
 
              
        if(inputoutput.equalsIgnoreCase("input")){
            request.setAttribute("output", "xsd/" + filename);
            request.setAttribute("input", "xsd/XBRL-GL-PR-2010-04-12/plt/case-c-b-m/gl-cor-content-2010-04-12.xsd");
            request.setAttribute("outputType", choice);
            request.setAttribute("inputType", centralTree);
        }else{
            request.setAttribute("input", "xsd/" + filename);
            request.setAttribute("output", "xsd/XBRL-GL-PR-2010-04-12/plt/case-c-b-m/gl-cor-content-2010-04-12.xsd");
            request.setAttribute("outputType", centralTree);
            request.setAttribute("inputType", choice);  
        }
        
        
        if (!centralTree.equalsIgnoreCase(dataannotations.getXbrl())) xbrl_mismatch= dataannotations.getXbrl()+"$"+centralTree; 
        this.forwardToPage("/proceedDataTree.jsp?schema_id=-1&service_id="+ service_id+"&xbrl_mismatch="+xbrl_mismatch, request, response);
    
    }


    protected void manageVendorSchemaReg(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        if (verifyUser("vendor", session)) {
            String softwareID = request.getParameter("software_id");

            this.forwardToPage("/VendorManager?op=schema_reg&software_id=" + softwareID, request, response);
        } else {
            this.forwardToPage("/error/generic_error.jsp?errormsg=op_not_supported_for_you", request, response);
        }
    }

    protected void manageVendorServiceReg(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        if (verifyUser("vendor", session)) {
            String softwareID = request.getParameter("software_id");

            this.forwardToPage("/VendorManager?op=service_reg&software_id=" + softwareID, request, response);
        } else {
            this.forwardToPage("/error/generic_error.jsp?errormsg=op_not_supported_for_you", request, response);
        }
    }

    protected void managePostMappings(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {
        Integer cvpID = -1;
        String json = request.getParameter("json");

        String xml = request.getParameter("xml");
        String mapType = request.getParameter("map_type");
        String xbrlType=request.getParameter("xbrl");
        int schema_id =-1;
        int service_id =-1;
        boolean isFullyMatched = false;
        
        if (!request.getParameter("schema_id").equalsIgnoreCase("-1")) schema_id = Integer.parseInt(request.getParameter("schema_id"));
        if (!request.getParameter("service_id").equalsIgnoreCase("-1")) service_id = Integer.parseInt(request.getParameter("service_id"));
        
        String name = (String) request.getSession().getAttribute("name");

        String selections = request.getParameter("selections");

        MainControlDB mainControlDB = new MainControlDB();

        if (mapType.equals("cvp")) {
            cvpID = new Integer(mainControlDB.insertCVP(schema_id, service_id, name));
        }
        if (mapType.equals("cpp")) {
            cvpID = new Integer(mainControlDB.getCVP(schema_id,service_id));
            System.out.println("MapType is cpp");
            Integer cppID = new Integer(mainControlDB.insertCPP(schema_id,service_id, name));
        }
        
        mainControlDB.insert_dataannotations(cvpID, xml, schema_id, service_id, name, json, selections,xbrlType);
        isFullyMatched = (service_id!=-1)?mainControlDB.isFullyMatched(0, service_id, cvpID):false;
        this.forwardToPage("/annotationResult.jsp?schema_id=" + schema_id + "&service_id="+service_id+"&dataannotation=true", request, response);

        
          
     

    }

    protected void manageBridgingSchemas(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        String software_id = request.getParameter("software_id");

        if (verifyUser("organization", session)) {
            this.forwardToPage("/organization/showAvailableSources.jsp?software_id=" + software_id, request, response);

        }
    }
    
    protected void manageBridgingServices(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        String software_id = request.getParameter("software_id");

        if (verifyUser("organization", session)) {
        
         MainControlDB mainControlDB = new MainControlDB();
            
        LinkedList<String> taxomonies = (LinkedList<String>) mainControlDB.getTaxonomies();
        System.out.println("taxomonies: " + taxomonies + "lenght: " + taxomonies.size());
        if (taxomonies.size() > 0) {

            JSONObject json_taxonomies = new JSONObject();
            Iterator tax_iterator = taxomonies.iterator();
            while (tax_iterator.hasNext()) {
                String tax = (String) tax_iterator.next();
                json_taxonomies.put(tax,tax);
            }
            session.setAttribute("taxonomies", json_taxonomies);
        }
        
         LinkedList<SoftwareComponent> softwareComponents = (LinkedList<SoftwareComponent>) mainControlDB.getSoftwareComponents();
         if (softwareComponents.size() > 0) {

            JSONObject json_softwareComp = new JSONObject();
            Iterator softcomp_iterator = softwareComponents.iterator();
            while (softcomp_iterator.hasNext()) {
                SoftwareComponent sc = (SoftwareComponent) softcomp_iterator.next();
                json_softwareComp.put(sc.getSoftware_id(),sc.getName());
            }
            session.setAttribute("softwarecomponents", json_softwareComp);
        }
         
         
         this.forwardToPage("/organization/showAvailableServices.jsp?software_id=" + software_id, request, response);

        }
    }

    protected void doBridging(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        String cpa_id = request.getParameter("cpa_id");

        if (verifyUser("organization", session)) {
            this.forwardToPage("/organization/doBridging.jsp?cpa_id=" + cpa_id, request, response);
        }

    }
    
   

    protected void showMyBridges(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        if (verifyUser("organization", session)) {
            this.forwardToPage("/OrganizationManager?op=showMyBridges", request, response);
        }

    }

    protected void showAvailableSources_title(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        MainControlDB mainControlDB = new MainControlDB();

        String software_name = mainControlDB.getSoftwareName(Integer.parseInt(request.getParameter("software_id")));

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write("<h2>Available Source Schemas for the software component: " + software_name + "<h2>");
        out.flush();
    }
    
    protected void showcurrentsoftcomp(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        MainControlDB mainControlDB = new MainControlDB();

        String software_name = mainControlDB.getSoftwareName(Integer.parseInt(request.getParameter("software_id")));

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write(software_name);
        out.flush();
    }


    protected void getMenu(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        int level = Integer.parseInt(request.getParameter("level"));
        String userType = (String) session.getAttribute("userType");
        String menu_level = "";
        String sign_out = "";

        switch (level) {
            case 0:
                menu_level = userType + "/";
                sign_out = "";
                break;
            case 1:
                menu_level = "./" + userType + "/";
                sign_out = "./";
                break;
            case 2:
                menu_level = "";
                sign_out = "../";
                break;
            case 3:
                menu_level = "./";
                sign_out = "/empower/";
                break;
            default:
                menu_level = "";
                sign_out = "";
                break;
        };

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("<rows>");

        if (verifyUser("vendor", session)) {
            out.write("<row id='1'><cell>Register new software^" + menu_level + "softwareReg.jsp^_self</cell></row>"
                    + "<row id='2'><cell>Show software components^" + menu_level + "showSoftwareComponent.jsp^_self</cell></row>"
                    + "<row id='3'><cell>Logout^" + sign_out + "DIController?op=signout^_self</cell></row>");
        } else {
            out.write("<row id='1'><cell>Show software components^" + menu_level + "showSoftwareComponent.jsp?bridging=false^_self</cell></row>"
                    + "<row id='2'><cell>Create My Bridges^" + menu_level + "showSoftwareComponent.jsp?bridging=true^_self</cell></row>"
                    + "<row id='3'><cell>Show My Bridges^" + menu_level + "showMyBridges.jsp^_self</cell></row>"
                    + "<row id='4'><cell>Logout^" + sign_out + "DIController?op=signout^_self</cell></row>");
        }


        out.write("</rows>");
        out.flush();

        return;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
