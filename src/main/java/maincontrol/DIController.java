/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maincontrol;

import com.google.gson.Gson;
import dataaccesslayer.*;
import java.io.*;
import java.util.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.wsdl.WSDLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import orgports.OrgDBConnector;
import orgports.OrganizationManager;
import vendorports.VendorDBConnector;
import xml.Parser;
import xml.WSDLParser;
import xml.XSDParser;
import org.dom4j.DocumentException;

/**
 *
 * @author eleni
 *
 */
public class DIController extends HttpServlet {

       private static String xml_rep_path;


    public DIController() throws IOException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();
        InputStream in =classLoader.getResourceAsStream("myproperties.properties");
        properties.load(in);

        this.xml_rep_path= properties.getProperty("repo.path").toString();

    }


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
                } else if (operation.equals("show_bridging_services")) {
                    this.manageBridgingServices(request, response, session);
                } else if (operation.equals("doBridging")) {
                    this.doBridging(request, response, session);
                } else if (operation.equals("showMyBridges")) {
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
                } else if (operation.equals("showcurrentwebservice")) {
                    this.showcurrentwebservice(request, response, session);
                } else if (operation.equals("get_menu")) {
                    this.getMenu(request, response, session);
                } else if (operation.equals("show_service")) {
                    this.manageShowService(request, response, session);
                } else if (operation.equals("show_services")) {
                    this.showServices(request, response, session);
                } else if (operation.equals("service_reg")) {
                    this.manageVendorServiceReg(request, response, session);
                } else if (operation.equals("schema_info")) {
                    this.getSchemaInfo(request, response, session);
                } else if (operation.equals("uploadMultiFiles")) {
                    this.uploadMultiFiles(request, response, session);
                }else if (operation.equals("getPreviousFuncAnnotation")) {
                    this.getPreviousFuncAnnotation(request, response, session);
                }else if (operation.equals("redirectToHomePage")) {
                    this.redirectToHomePage(request, response, session);
                }else if (operation.equals("manageinstallations")) {
                    this.manageinstallations(request, response, session);
                }else if (operation.equals("createCPA")) {
                    this.createCPA(request, response, session);
                } else if (operation.equals("showCPAs")) {
                    this.showCPAs(request, response, session);
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
        int user_id = mainControlDB.insertUser(name, password, userType);

        if (user_id != -1) {

            session = request.getSession(true);
            session.setAttribute("userType", userType);
            session.setAttribute("name", name);
            String redirectionURL = new String("/" + userType + "/" + userType + "menu.jsp");
            this.forwardToPage(redirectionURL, request, response);

        } else {
            this.forwardToPage("/error/signin_error.jsp?errormsg=Already exists a user with the same name.Please try to Sing Up with a different name.", request, response);
        }


    }
    
      protected void redirectToHomePage(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
     
            session = request.getSession(true);
            String userType = session.getAttribute("userType").toString();
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
            this.forwardToPage("/OrganizationManager?op=show_components&bridging=" + bridging, request, response);
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
        LinkedList<Service> services = (LinkedList<Service>) mainControlDB.getServices(software_id, false, "IS NULL");
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
                this.forwardToPage("/showSchemas.jsp?software_id=" + software_id, request, response);

            }
        }

        if (verifyUser("organization", session)) {
            if (xsd_num.equals("0")) {
                this.forwardToPage("/info.jsp?message_code=1", request, response);
            } else {
                this.forwardToPage("/showSchemas.jsp?software_id=" + software_id, request, response);
            }
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
            //String delete_option = (verifyUser("vendor", session)) ? "<cell> Delete Schema^./VendorManager?op=delete_schema&amp;schema_id=" + schema.getSchema_id() + "^_self</cell>" : "";
            String delete_option = (verifyUser("vendor", session)) ? "<cell> Delete Schema^javascript:deleteschema("+schema.getSchema_id()+")^_self</cell>" : "";
            
            out.write("<row id=\"" + schema.getSchema_id() + "\"><cell>" + schema.getName() + "</cell>"
                    + "<cell> Annotate Operations^./presentOperationTree.jsp?schema_id=" + schema.getSchema_id() + "^_self</cell>"
                    + "<cell> Annotate Data^./presentDataTree.jsp?schema_id=" + schema.getSchema_id() + "^_self</cell>"
                    + delete_option
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
        LinkedList<Service> services = (LinkedList<Service>) mainControlDB.getServices(software_id, false, "IS NOT NULL");
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
            if (service_num.equals("0")) {
                // if software component with any service
                this.forwardToPage("/vendor/serviceReg.jsp?software_id=" + software_id + "&jsp=false", request, response);
            } else {
                this.forwardToPage("/showServices.jsp?software_id=" + software_id+"&message=", request, response);
            }
        }

        if (verifyUser("organization", session)) {
            if (service_num.equals("0")) {
                this.forwardToPage("/info.jsp?message_code=2", request, response);
            } else {

                //-------------Prepare the select option so as to create new CPP's------------
                JSONObject json_cppsOfOrganization = new JSONObject();
                OrgDBConnector orgDBConnector = new OrgDBConnector();
                LinkedList<Service> cppsofOrg = orgDBConnector.getCPPs((String) session.getAttribute("name"),Integer.parseInt(software_id));

                System.out.println("To size mou einai"+cppsofOrg.size());

                Iterator cppsofOrg_iterator = cppsofOrg.iterator();
                while (cppsofOrg_iterator.hasNext()) {
                    Service cpp_service = (Service) cppsofOrg_iterator.next();
                    json_cppsOfOrganization.put(cpp_service.getCpp_id(), cpp_service.getName() + "  V." + cpp_service.getVersion() + "   CPP Name: " +cpp_service.getCpp_name() + "  ID: " +cpp_service.getCpp_id());
                }
                session.setAttribute("CPPsPerSoftCompPerOrg", json_cppsOfOrganization);

                this.forwardToPage("/showServices.jsp?software_id=" + software_id+"&message=", request, response);
            }
        }

    }

    protected void showServices(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {
        Iterator servIterator;
        String img_link = "";


        MainControlDB mainControlDB = new MainControlDB();



        if (verifyUser("vendor", session)) {

            servIterator =  mainControlDB.getServices((String) request.getParameter("software_id"), false, "IS NOT NULL").iterator();
        } else{
            String organization_name = (String) session.getAttribute("name");
            int organization_id = mainControlDB.getuserid(organization_name);
            // get all the CPPs the currend organization user paricipates
            servIterator =  mainControlDB.getExposedServices((String) request.getParameter("software_id"), "IS NOT NULL",organization_id).iterator();

        }

        /*
        servIterator = (verifyUser("vendor", session)) ? mainControlDB.getServices((String) request.getParameter("software_id"), false, "IS NOT NULL").iterator()
               : mainControlDB.getServices((String) request.getParameter("software_id"), true, "IS NOT NULL").iterator();
        */

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("<rows>");


        while (servIterator.hasNext()) {
            Service service = (Service) servIterator.next();

            img_link = (service.isExposed()) ? "js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/green.gif" : "js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/red.gif";

            String delete_wservice_option = (verifyUser("vendor", session)) ? "<cell>Delete^javascript:deleteservice("+service.getService_id()+")^_self</cell>" : "";

           // String delete_cpp_option = (verifyUser("vendor", session)) ? "<cell type=\"img\">" + img_link + "</cell>" : "<cell>Delete^./OrganizationManager?op=cpp_delete&amp;software_id="+request.getParameter("software_id")+"&amp;cpp_id=" + service.getCpp_id()+ "^_self</cell><cell>"+service.getFromTo()+"</cell>";
            String delete_cpp_option = (verifyUser("vendor", session)) ? "<cell type=\"img\">" + img_link + "</cell>" : "<cell>Delete^javascript:deletecpp("+request.getParameter("software_id")+","+service.getCpp_id()+")^_self</cell><cell>"+service.getFromTo()+"</cell>";

            String cppNameID =  (verifyUser("vendor", session)) ? "": "<cell>"+service.getCpp_name()+" ID:"+service.getCpp_id()+"</cell>";


            System.out.println("img_link" + img_link);
            out.write("<row id=\"" + service.getService_id() +"$"+service.getCpp_id()+ "\">"
                    + "<cell>" + service.getName()+" -- V."+service.getVersion() + "</cell>"
                    + cppNameID
                    + "<cell>Edit^./presentOperationTree.jsp?service_id=" + service.getService_id()+ "^_self</cell>"
                    + "<cell>Edit^./presentDataTree.jsp?service_id=" + service.getService_id()+"&amp;cpp_id="+service.getCpp_id()+"^_self</cell>"
                    + delete_cpp_option
                    + delete_wservice_option
                    + "</row>");
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

    /*
     * Output the operations at schema level to XML
     */
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
        String userType = (verifyUser("organization", session)) ? "organization" : "vendor";
        MainControlDB mainControlDB = new MainControlDB();


        if (!request.getParameter("schema_id").equalsIgnoreCase("null")) {
            schema_id = Integer.parseInt((String) request.getParameter("schema_id"));
            operation_id = Integer.parseInt((String) request.getParameter("selections"));
            this.annotateOperations_schema(schema_id, operation_id, funcSelections, name, userType);
            this.forwardToPage("/annotationResult.jsp?schema_id=" + schema_id + "&dataannotation=false", request, response);
        }
        if (!request.getParameter("service_id").equalsIgnoreCase("null")) {
            service_id = Integer.parseInt((String) request.getParameter("service_id"));
            operation_name = (String) request.getParameter("selections");
            this.annotateOperations_service(service_id, operation_name, funcSelections, name, userType);
            this.forwardToPage("/annotationResult.jsp?schema_id=-1&service_id=" + service_id + "&dataannotation=false", request, response);
        }

    }

    protected boolean annotateOperations_schema(int schema_id, int operation_id, String funcSelections, String name, String userType) {

        MainControlDB mainControlDB = new MainControlDB();

        try {

            if (userType.equalsIgnoreCase("organization")) {
                mainControlDB.insertCPP(schema_id, -1, name);
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

    protected boolean annotateOperations_service(int service_id, String operation_name, String funcSelections, String name, String userType) {

        boolean isFullyMatched = false;
        Integer cvpID = -1;
        MainControlDB mainControlDB = new MainControlDB();

        if (userType.equalsIgnoreCase("organization")) {
            mainControlDB.insertCPP(-1, service_id, name);
            mainControlDB.functionalAnnotationByService(service_id, operation_name, funcSelections, -1);
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

        System.out.println(schema_id + "schema location: " + schema.getLocation() + schema.getInputoutput() + "--" + schema.getOperation_id() + "--" + schema.getOp_taxonomy_id() + "--" + schema.getCvp_id() + "--" + schema.getService_id() + "--" + schema.getSchema_id() + "--" + schema.getSelections());

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
            throws IOException, ServletException, WSDLException {
        int serviceID = Integer.parseInt(request.getParameter("service_id"));

        MainControlDB mainControlDB = new MainControlDB();
        Service service = mainControlDB.getService(serviceID);
        response.setContentType("text/xml; charset=UTF-8");

        WSDLParser wsdlParser = new WSDLParser(service.getWsdl(),
                service.getNamespace());
        wsdlParser.loadService(service.getName());
        wsdlParser.outputToXML(response.getWriter());

        System.out.println("outputXML: " + response.getWriter());
    }

    /*
     * Present parts of XBRL Taxonomy
     */
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
            throws IOException, ServletException, ParserConfigurationException, SAXException, XPathExpressionException, WSDLException, TransformerConfigurationException, TransformerException,DocumentException  {

        if (!request.getParameter("schema_id").equalsIgnoreCase("null")) {
            annotate_data_schema(request, response, session);
        }
        if (!request.getParameter("service_id").equalsIgnoreCase("null")) {
            annotate_data_service(request, response, session);
        }

    }

    protected void annotate_data_schema(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, ParserConfigurationException, SAXException, XPathExpressionException, WSDLException {

        int schema_id = Integer.parseInt((String) request.getParameter("schema_id"));
        String schema_data = ((String) request.getParameter("selections")).split("--")[0];
        String inputoutput = ((String) request.getParameter("selections")).split("--")[1];
        String centralTree = request.getParameter("centraltree");
        String mapping = null;
        String xbrl_mismatch = "false";
        String map_type="";

        MainControlDB mainControlDB = new MainControlDB();
        Schema schema = mainControlDB.getSchema(schema_id);
        String filename = ((String) schema.getLocation()).split("/xsd/")[1];

        request.setAttribute("mapping", mapping);
        if (verifyUser("vendor", session)) {
            request.setAttribute("map_type", "cvp");
            map_type="cvp";
        } else {
            request.setAttribute("map_type", "cpp");
            map_type="cpp";
        }

        DataAnnotations dataannotations = mainControlDB.getMapping(schema_id, -1, inputoutput + "$" + schema_data,map_type);
        mapping = dataannotations.getMapping();

        if (mapping != null) {
            mapping = new String(mapping.replace("\"", "\\\""));
            //System.out.println("==== mapping=" + mapping.substring(0, 500));
        }






        request.setAttribute("selections", inputoutput + "$" + schema_data);
        request.setAttribute("xbrl", centralTree);


        //we pass to the annotator the schema id as service id until only for the first deliverable of the empower project, so as not to double change the annotator without reason
        if (inputoutput.equals("output")) {

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
        }
        if (!centralTree.equalsIgnoreCase(dataannotations.getXbrl()) && dataannotations.getXbrl() != null) {
            xbrl_mismatch = dataannotations.getXbrl() + "$" + centralTree;
        }
        this.forwardToPage("/proceedDataTree.jsp?schema_id=" + schema_id + "&service_id=-1&xbrl_mismatch=" + xbrl_mismatch + "&data=" + new JSONObject(), request, response);

    }

    protected void annotate_data_service(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, ParserConfigurationException, SAXException, XPathExpressionException, WSDLException, TransformerConfigurationException, TransformerException, DocumentException {

        int service_id = Integer.parseInt((String) request.getParameter("service_id"));
        String selections = request.getParameter("selections");
        String centralTree = request.getParameter("centraltree");
        String cpp_id=  request.getParameter("cpp_id");
        String mapping = new String("");
        String choice =  new String("");
        String xbrl_mismatch = "false";
        String map_type= "";

        if (verifyUser("vendor", session)) {
            request.setAttribute("map_type", "cvp");
            map_type="cvp";
        } else {

            request.setAttribute("map_type", "cpp$"+cpp_id);
            map_type="cpp$"+cpp_id;
        }

        MainControlDB mainControlDB = new MainControlDB();
        DataAnnotations dataannotations = mainControlDB.getMapping(-1, service_id, selections, map_type);
        mapping = dataannotations.getMapping();
        System.out.println("This is the mapping i get"+mapping);


        if (mapping != null) {
            mapping = new String(mapping.replace("\"", "\\\""));
            //System.out.println("==== mapping=" + mapping.substring(0, 500));
        }

        request.setAttribute("mapping", mapping);
        request.setAttribute("selections", selections);
        request.setAttribute("xbrl", centralTree);

        Service service = mainControlDB.getService(service_id);

        WSDLParser wsdlParser = new WSDLParser(service.getWsdl(), service.getNamespace());
        choice = selections.split("\\$")[1];
        //choice[1] = selections.split("\\$")[2];
        String inputoutput = selections.split("\\$")[0];
        String xsdTypes = wsdlParser.extractXSD(choice);


        String filename = new String(service.getService_id() + service.getName() + choice + ".xsd");
        String xsdFilename = new String(xml_rep_path + "/xsd/" + filename);
        
        File f = new File(xsdFilename);
        if(!f.exists()) {
        PrintWriter xsdFile = new PrintWriter(xsdFilename);
        xsdFile.write(xsdTypes);
        xsdFile.close();
        }
        

        if (inputoutput.equalsIgnoreCase("input")) {
            request.setAttribute("output", "xsd/" + filename);
            request.setAttribute("input", "xsd/XBRL-GL-PR-2010-04-12/plt/case-c-b-m/gl-cor-content-2010-04-12.xsd");
            request.setAttribute("outputType", choice);
            request.setAttribute("inputType", centralTree);
        } else {
            request.setAttribute("input", "xsd/" + filename);
            request.setAttribute("output", "xsd/XBRL-GL-PR-2010-04-12/plt/case-c-b-m/gl-cor-content-2010-04-12.xsd");
            request.setAttribute("outputType", centralTree);
            request.setAttribute("inputType", choice);
        }

        if (!centralTree.equalsIgnoreCase(dataannotations.getXbrl()) && dataannotations.getXbrl() != null) {
            xbrl_mismatch = dataannotations.getXbrl() + "$" + centralTree;
        }

        // Prepare JSON so as to import the xsd of the web service to mediator Portal
        Parser parser = new Parser();
        xsdTypes = parser.removeTypes(xsdTypes);
        JSONObject o = new JSONObject();
        o.put("modelId", service_id + "_" + choice);
        o.put("description", service_id + "_" + choice);
        o.put("format", "XSD");
        o.put("content", xsdTypes);

        this.forwardToPage("/proceedDataTree.jsp?schema_id=-1&service_id=" + service_id + "&xbrl_mismatch=" + xbrl_mismatch + "&data=" + o.toString(), request, response);
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

    /*
     * Treat response from Annotator tool. Insert dataannotations / cvp or cpp
     */
    protected void managePostMappings(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {
        Integer cvpID = -1;
        Integer cppID = -1;
        String json = request.getParameter("json");

        String xml = request.getParameter("xml");
        xml = xml.replaceAll("'", "''");
        String mapType = request.getParameter("map_type");
        String xbrlType = request.getParameter("xbrl");
        int schema_id = -1;
        int service_id = -1;
        boolean isFullyMatched = false;

        if (!request.getParameter("schema_id").equalsIgnoreCase("-1")) {
            schema_id = Integer.parseInt(request.getParameter("schema_id"));
        }
        if (!request.getParameter("service_id").equalsIgnoreCase("-1")) {
            service_id = Integer.parseInt(request.getParameter("service_id"));
        }

        String name = (String) request.getSession().getAttribute("name");

        String selections = request.getParameter("selections");

        MainControlDB mainControlDB = new MainControlDB();

        if (mapType.equals("cvp")) {
            cvpID = new Integer(mainControlDB.insertCVP(schema_id, service_id, name));
            mainControlDB.insert_cvp_dataannotations(cvpID, xml, schema_id, service_id, name, json, selections, xbrlType);
        }
        if (mapType.contains("cpp")) {
            cvpID = new Integer(mainControlDB.getCVP(schema_id, service_id));
            //cppID = new Integer(mainControlDB.getCPP(schema_id, service_id,cvpID,selections));
            cppID = Integer.parseInt(mapType.split("\\$")[1]);
            System.out.println("MapType is cpp");
            //Integer cppID = new Integer(mainControlDB.insertCPP(schema_id, service_id, name));
            mainControlDB.insert_cpp_dataannotations(cvpID, xml, schema_id, service_id, name, json, selections, xbrlType, cppID);
        }


        isFullyMatched = (service_id != -1) ? mainControlDB.isFullyMatched(0, service_id, cvpID) : false;
        this.forwardToPage("/annotationResult.jsp?schema_id=" + schema_id + "&service_id=" + service_id + "&dataannotation=true", request, response);

    }

    /*
     * Prepare the jsp page that facilitates bridging of schemas
     */
    protected void manageBridgingSchemas(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        String software_id = request.getParameter("software_id");

        if (verifyUser("organization", session)) {
            this.forwardToPage("/organization/showAvailableSources.jsp?software_id=" + software_id, request, response);

        }
    }

    /*
     * Prepare the jsp page that facilitates bridging of services
     */
    protected void manageBridgingServices(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        String software_id = request.getParameter("software_id");

        if (verifyUser("organization", session)) {

            this.addTaxonomiesToSession(session);
            this.addSoftwareComponentsToSession(session);
            this.forwardToPage("/organization/showAvailableServices.jsp?software_id=" + software_id, request, response);
        }

    }


    private void addTaxonomiesToSession(HttpSession session){

        MainControlDB mainControlDB = new MainControlDB();

        LinkedList<String> taxomonies = (LinkedList<String>) mainControlDB.getTaxonomies();
        System.out.println("taxomonies: " + taxomonies + "lenght: " + taxomonies.size());
        if (taxomonies.size() > 0) {

            JSONObject json_taxonomies = new JSONObject();
            Iterator tax_iterator = taxomonies.iterator();
            while (tax_iterator.hasNext()) {
                String tax = (String) tax_iterator.next();
                json_taxonomies.put(tax, tax);
            }
            session.setAttribute("taxonomies", json_taxonomies);
        }

    }

    protected void createCPA(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {

        this.addSoftwareComponentsToSession(session);
        this.addTaxonomiesToSession(session);
        this.forwardToPage("/organization/createCPA.jsp?bridging=true", request, response);


    }

    protected void showCPAs(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {

        this.addCPAsToSession(session);
        this.forwardToPage("/organization/showMyBridges.jsp", request, response);


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
        int software_id = Integer.parseInt(request.getParameter("software_id"));
        String title = "";

        if (software_id!=-1) {
            title = "<h2>Available Source Schemas for the software component: " + mainControlDB.getSoftwareName(software_id) + "<h2>";
        } else   {
            title= "<h2>All Available Source Schemas</h2>";
        }
        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write(title);
        out.flush();
    }
    
    protected void getPreviousFuncAnnotation(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        

        MainControlDB mainControlDB = new MainControlDB();
        
       String taxonomy = ( (request.getParameter("service_id") != null) )? mainControlDB.getOperationTaxonomy(-1,Integer.parseInt(request.getParameter("service_id")),request.getParameter("operation")):mainControlDB.getOperationTaxonomy(Integer.parseInt(request.getParameter("schema_id")),-1,request.getParameter("operation_id"));
       
        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write(taxonomy);
        out.flush();
    }
    

    /*
     * show current sotware component name and version. usefull for the
     * showAvailableSources/Services page
     */
    protected void showcurrentsoftcomp(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        MainControlDB mainControlDB = new MainControlDB();

        String software_name = mainControlDB.getSoftwareName(Integer.parseInt(request.getParameter("software_id")));

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write(software_name);
        out.flush();
    }

    /*
    * show current web service name and version. usefull for the presentOperationTree page
    */
    protected void showcurrentwebservice(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        MainControlDB mainControlDB = new MainControlDB();

        Service service = mainControlDB.getService(Integer.parseInt(request.getParameter("service_id")));

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write(service.getName()+" --V."+service.getVersion());
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
            out.write("<row id='1'><cell>Home^"+ sign_out +"DIController?op=redirectToHomePage^_self</cell></row>"
                    +"<row id='2'><cell>Register software components^" + menu_level + "softwareReg.jsp^_self</cell></row>"
                    + "<row id='3'><cell>Show software components^" + menu_level + "showSoftwareComponent.jsp^_self</cell></row>"
                    + "<row id='4'><cell>Logout^" + sign_out + "DIController?op=signout^_self</cell></row>");
        } else if (verifyUser("organization", session)) {
            out.write("<row id='1'><cell>Home^"+ sign_out +"DIController?op=redirectToHomePage^_self</cell></row>"
                    //+ "<row id='6'><cell>Manage Installations^"+ menu_level +"manageinstallations.jsp?software_id=-1^_self</cell></row>"
                    + "<row id='6'><cell>Manage Installations^"+ sign_out + "DIController?op=manageinstallations^_self</cell></row>"
                    + "<row id='2'><cell>Define CPP's^" + menu_level + "showSoftwareComponent.jsp?bridging=false^_self</cell></row>"
                    //+ "<row id='3'><cell>Define CPA (Bridge)^" + menu_level + "showSoftwareComponent.jsp?bridging=true^_self</cell></row>"
                    + "<row id='3'><cell>Define CPA (Bridge) -- Schemas^" + sign_out + "DIController?op=show_bridging_schemas&amp;software_id=-1^_self</cell></row>"
                    + "<row id='4'><cell>Define CPA (Bridge) -- WS  ^" + sign_out + "DIController?op=createCPA^_self</cell></row>"
                   // + "<row id='5'><cell>Show My CPA's (Bridges)^" + menu_level + "showMyBridges.jsp^_self</cell></row>"
                    + "<row id='5'><cell>Show My CPA's (Bridges)^" + sign_out + "DIController?op=showCPAs^_self</cell></row>"
                    + "<row id='7'><cell>Logout^" + sign_out + "DIController?op=signout^_self</cell></row>");
        } else if (verifyUser("admin", session)) {
            out.write("<row id='1'><cell>Home^"+ sign_out +"DIController?op=redirectToHomePage^_self</cell></row>"
                    +"<row id='3'><cell>Upload new directory to server^" + menu_level + "uploadDirectory.jsp^_self</cell></row>"
                    + "<row id='4'><cell>Logout^" + sign_out + "DIController?op=signout^_self</cell></row>");
        }


        out.write("</rows>");
        out.flush();

        return;
    }

    /*
     * Prepare JSON so as to import the xsd of the schema to mediator Portal
     */
    protected void getSchemaInfo(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        int schema_id = Integer.parseInt(request.getParameter("schema_id"));
        MainControlDB mainControlDB = new MainControlDB();
        Schema schema = mainControlDB.getSchema(schema_id);

        String thisLine;
        String datafile = "";
        FileInputStream fin = new FileInputStream(schema.getLocation());

        BufferedReader myInput = new BufferedReader(new InputStreamReader(fin));
        while ((thisLine = myInput.readLine()) != null) {
            datafile = datafile + thisLine;
        }

        JSONObject schema_info = new JSONObject();

        schema_info.put("modelId", schema_id + "_" + schema.getName());
        schema_info.put("description", schema_id + "_" + schema.getName());
        schema_info.put("format", "XSD");
        schema_info.put("content", datafile);

        // Write response data as JSON.
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(schema_info));


    }

    /*
     * Upload directory with files . Only the admin can do this
     */
    protected void uploadMultiFiles(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, FileUploadException, Exception {
        
        
        // Check that we have a file upload request
        String directoryname = null;
        String message = "";
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();
          
        // Set factory constraints
        factory.setSizeThreshold(50000);
        factory.setRepository(new File(""));
        ServletFileUpload upload = new ServletFileUpload(factory);

        upload.setSizeMax(3000000);
        if (verifyUser("admin", session)) {
            List items = upload.parseRequest(request);

            Iterator iter = items.iterator();

            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (item.isFormField()) {
                    if (item.getFieldName().equals("directoryname")) {
                        directoryname = item.getString();

                        File theDir = new File(this.xml_rep_path + "/adminDirectory/" + directoryname);

                        // if the directory does not exist, create it
                        if (!theDir.exists()) {

                            boolean result = theDir.mkdir();
                            if (result) {
                                System.out.println("DIR created");
                            }
                        }
                    }
                } else {
                    String fieldname = item.getFieldName();

                    String filename = FilenameUtils.getName(item.getName());

                    InputStream filecontent = item.getInputStream();

                    File uploadedFile = new File(this.xml_rep_path + "/adminDirectory/" + directoryname + "/" + filename + ".xsd");
                    item.write(uploadedFile);
                }
            }
            message = "The selected files have been succesfully uploaded to "+directoryname+" Directory.";
        } else {
            message = "You are not authorized to access this page!";
        }
        this.forwardToPage("/vendor/succImportSchema.jsp?message=" + message, request, response);
    }


    protected  void manageinstallations(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, ServletException {
        this.addSoftwareComponentsToSession(session);
        this.forwardToPage("/organization/manageinstallations.jsp?software_id=-1&message=", request, response);
    }

    private void addSoftwareComponentsToSession(HttpSession session){

        MainControlDB mainControlDB= new MainControlDB();


        LinkedList<SoftwareComponent> softwareComponents = (LinkedList<SoftwareComponent>) mainControlDB.getSoftwareComponents();
        if (softwareComponents.size() > 0) {

            JSONObject json_softwareComp = new JSONObject();
            Iterator softcomp_iterator = softwareComponents.iterator();
            while (softcomp_iterator.hasNext()) {
                SoftwareComponent sc = (SoftwareComponent) softcomp_iterator.next();
                json_softwareComp.put(sc.getSoftware_id(), sc.getName() + " V" + sc.getVersion());
            }
            session.setAttribute("softwarecomponents", json_softwareComp);
        }

    }

    private void addCPAsToSession(HttpSession session){

        OrgDBConnector orgDBConnector= new OrgDBConnector();
        LinkedList<CPA> cpas = (LinkedList<CPA>) orgDBConnector.getCPAs((String) session.getAttribute("name"));
        if (cpas.size() > 0) {

            JSONObject json_cpas = new JSONObject();
            Iterator cpa_iterator = cpas.iterator();
            while (cpa_iterator.hasNext()) {
                CPA c = (CPA) cpa_iterator.next();
                json_cpas.put(c.getCpa_id(),"CPA with id:"+ c.getCpa_id());
               /*
                json_cpas.put(c.getCpa_id(),
                        "<input type=\"checkbox\" name=\""+c.getCpa_id()+"\" value=\""+c.getCpa_id()+"$"+c.getCpp_id_first()+"\"> "+c.getCpp_id_first_name() +" ID: "+c.getCpp_id_first()+"<br>" +
                        "<input type=\"checkbox\" name=\""+c.getCpa_id()+"\" value=\""+c.getCpa_id()+"$"+c.getCpp_id_second()+"\"> "+c.getCpp_id_second_name() +" ID: "+c.getCpp_id_second()+"<br>");
                        */
            }
            session.setAttribute("CPAsOfOrg", json_cpas);
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
