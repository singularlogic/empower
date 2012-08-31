/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maincontrol;

import dataaccesslayer.Operation;
import dataaccesslayer.Schema;
import dataaccesslayer.Service;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import net.sf.json.JSONObject;
import org.xml.sax.SAXException;
import xml.XSDParser;


/**
 *
 * @author eleni
 */
public class DIController extends HttpServlet {

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
                } else if (operation.equals("doBridging")) {
                    this.doBridging(request, response, session);
                } else if (operation.equals("showMyBridges")) {
                    this.showMyBridges(request, response, session);
                } else if (operation.equals("present_service_operations")) 
                    this.presentServiceOperationsTree(request, response, session);
                else if (operation.equals("annotate_operations")) 
                    this.annotateOperations(request, response, session);
                else if (operation.equals("present_service_schema")) 
                    this.presentServiceSchemaTree(request, response, session);
                else if (operation.equals("present_central_trees")) 
                    this.presentOptionTrees(request, response, session);
                else if (operation.equals("annotate")) 
                    this.annotate(request, response, session);
                
                //else
                //  this.forwardToPage("/error/generic_error.jsp?errormsg=Cannot find op", request, response);
            }  } catch (Throwable t) {
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
            this.forwardToPage("/OrganizationManager?op=show_components", request, response);
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
        LinkedList<Service> services = (LinkedList<Service>) mainControlDB.getServices(software_id);
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
        
        XSDIterator = (verifyUser("vendor", session)) ? mainControlDB.getSchemas((String) request.getParameter("software_id"),false).iterator() : mainControlDB.getSchemas((String) request.getParameter("software_id"),true).iterator();
        
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
    
    protected void presentServiceOperationsTree(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {

        int schema_id = Integer.parseInt(request.getParameter("schema_id"));

        MainControlDB mainControlDB = new MainControlDB();
        LinkedList<Operation> operations = mainControlDB.getOperationsBySchema(schema_id);

        response.setContentType("text/xml; charset=UTF-8");

        this.outputOperationsToXML(response.getWriter(), operations);

        //WSDLParser wsdlParser = new WSDLParser(service.getWsdl(),service.getNamespace());
        //wsdlParser.loadService(service.getName());
        //wsdlParser.outputFunctionsToXML(response.getWriter());
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
       
        int schema_id = Integer.parseInt((String) request.getParameter("schema_id"));
        int operation_id = Integer.parseInt((String) request.getParameter("selections"));
        String funcSelections = request.getParameter("funcselections");
        String name = (String) session.getAttribute("name");

        System.out.println(funcSelections + " " + schema_id + " " + operation_id + " " + name);

        MainControlDB mainControlDB = new MainControlDB();

        mainControlDB.insertTaxonomyToOperation(operation_id, funcSelections);

        //vendorDBConnector.insertCVPFunction(funcSelections, serviceID, selections, name);

        this.forwardToPage("/annotationResult.jsp?schema_id=" + schema_id, request, response);
    }
    
    protected void presentServiceSchemaTree(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, ParserConfigurationException, SAXException {

        int schema_id = Integer.parseInt(request.getParameter("schema_id"));

        MainControlDB mainControlDB = new MainControlDB();
        Schema schema = mainControlDB.getSchema(schema_id);
        System.out.println("schema location: " + schema.getLocation());

        XSDParser p = new XSDParser(schema);
        String xml_string = p.convertSchemaToXML();
        System.out.println("xml_string: " + xml_string);

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write(xml_string);
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
            throws IOException, ServletException, ParserConfigurationException, SAXException, XPathExpressionException {
        int schema_id = Integer.parseInt((String) request.getParameter("schema_id"));
        String schema_data = ((String) request.getParameter("selections")).split("--")[0];
        String inputoutput = ((String) request.getParameter("selections")).split("--")[1];
        String centralTree = request.getParameter("centraltree");
        String mapping = null;

        MainControlDB mainControlDB = new MainControlDB();
        Schema schema = mainControlDB.getSchema(schema_id);
        String filename = ((String) schema.getLocation()).split("/xsd/")[1];

        if (inputoutput.equals("output")) mapping = mainControlDB.getMapping(schema_id, centralTree + "$" + schema_data);
        else  mapping = mainControlDB.getMapping(schema_id, schema_data + "$" + centralTree);

        if (mapping != null) {
            mapping = new String(mapping.replace("\"", "\\\""));
            System.out.println("==== mapping=" + mapping.substring(0, 500));
        }

        request.setAttribute("mapping", mapping);
        if (verifyUser("vendor", session)) request.setAttribute("map_type", "cvp");
            else request.setAttribute("map_type", "cpp");

        //we pass to the annotator the schema id as service id until only for the first deliverable of the empower project, so as not to double change the annotator without reason
        if (inputoutput.equals("output")) {

            //response.sendRedirect(response.encodeRedirectURL("http://127.0.0.1:8080/annotator?input=xsd/XBRL-GL-PR-2010-04-12/plt/case-c-b-m/gl-cor-content-2010-04-12.xsd&output=xsd/" + filename + "&service_id=" + schema_id + "&map_type=cvp&outputType=" + schema_data +"&inputType=" + centralTree + "&mapping="+ mapping)); 
            request.setAttribute("output", "xsd/" + filename);
            request.setAttribute("input", "xsd/XBRL-GL-PR-2010-04-12/plt/case-c-b-m/gl-cor-content-2010-04-12.xsd");
            request.setAttribute("outputType", schema_data);
            request.setAttribute("inputType", centralTree);
            request.setAttribute("selections", centralTree + "$" + schema_data);
        } else {
            //response.sendRedirect(response.encodeRedirectURL("http://127.0.0.1:8080/annotator?input=xsd/" + filename + "&output=xsd/XBRL-GL-PR-2010-04-12/plt/case-c-b-m/gl-cor-content-2010-04-12.xsd&service_id=" + schema_id + "&map_type=cvp&outputType=" + centralTree + "&inputType=" + schema_data + "&mapping="));
            request.setAttribute("input", "xsd/" + filename);
            request.setAttribute("output", "xsd/XBRL-GL-PR-2010-04-12/plt/case-c-b-m/gl-cor-content-2010-04-12.xsd");
            request.setAttribute("outputType", centralTree);
            request.setAttribute("inputType", schema_data);
            request.setAttribute("selections", schema_data + "$" + centralTree);
        }
        this.forwardToPage("/proceedDataTree.jsp?schema_id=" + schema_id, request, response);

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

    protected void managePostMappings(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {
        String json = request.getParameter("json");

        String xml = request.getParameter("xml");
        String mapType = request.getParameter("map_type");
        int schema_id = Integer.parseInt(request.getParameter("service_id"));
        String name = (String) request.getSession().getAttribute("name");

        String selections = request.getParameter("selections");
        //String service = (String)session.getAttribute("service");
        //String userType = (String) request.getSession().getAttribute("userType");

        MainControlDB mainControlDB = new MainControlDB();
        //System.out.println("XML=" + xml + "\n\njson=" + json);

        if (mapType.equals("cvp")) {

            Integer cvpID = new Integer(mainControlDB.insertCVP(xml, schema_id, name, json, selections));
            this.forwardToPage("/annotationResult.jsp?schema_id=" + schema_id + "&dataannotation='true'", request, response);
        }
        if (mapType.equals("cpp")) {
            
            System.out.println("MapTye is cpp");
            Integer cvpID = new Integer(mainControlDB.insertCPP(xml, schema_id, name, json, selections));
            this.forwardToPage("/annotationResult.jsp?schema_id=" + schema_id + "&dataannotation='true'", request, response);
        }
        /*
         * System.out.println("isfullymatched " +
         * vendorDBConnector.isFullyMatched(0, serviceID, cvpID));
         * if(vendorDBConnector.isFullyMatched(0, serviceID, cvpID))
         * this.forwardToPage("/SemanticPublish?op=publish&service_id="+
         * serviceID + "&cvp_id=" + cvpID, request, response); else
         * this.forwardToPage(redirectionURL, request, response);          *
         *
         * else if(mapType.equals("cpp")) { System.out.println(" POST " +
         * selections + " //// " + name); Integer cvpID =
         * (Integer)request.getSession().getAttribute("cvp_id");
         * System.out.println(" POST " + selections + " //// " + name + cvpID);
         * int cpp = vendorDBConnector.insertCPP(xml, serviceID, selections,
         * name, cvpID); System.out.println(" POST //// " + cpp);          *
         * if(vendorDBConnector.isFullyMatched(cpp, serviceID, cvpID))
         * this.forwardToPage("/SemanticPublish?op=publish&service_id="+
         * serviceID + "&cpp_id=" + cpp, request, response); else
         * this.forwardToPage(redirectionURL, request, response); } else
         * if(mapType.equals("functions")) {
         * vendorDBConnector.insertCVPFunction(xml, serviceID, selections,
         * name); this.forwardToPage(redirectionURL, request, response);        
        }
         */

    }

    protected void manageBridgingSchemas(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        String software_id = request.getParameter("software_id");

        if (verifyUser("organization", session)) {
            this.forwardToPage("/organization/showAvailableSources.jsp?software_id=" + software_id, request, response);

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
