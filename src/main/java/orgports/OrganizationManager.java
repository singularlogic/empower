/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orgports;

import com.google.gson.Gson;
import dataaccesslayer.*;
import flexjson.JSONException;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.wsdl.WSDLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;
import maincontrol.MainControlDB;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import java.net.URL;
import org.dom4j.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import xml.Parser;
import xml.WSDLParser;
import xml.XSDParser;

/**
 *
 * @author eleni
 */
public class OrganizationManager extends HttpServlet {

    private static String xml_rep_path;

    public OrganizationManager() throws IOException {
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

        HttpSession session;
        String operation;

        session = request.getSession(true);
        operation = request.getParameter("op");


        try {
            if (operation != null) {
                if (operation.equals("show_components")) {
                    this.showComponents(request, response, session);
                } else if (operation.equals("show_bridging_schemas")) {
                    this.manageBridgingSchemas(request, response, session);
                } else if (operation.equals("show_bridging_services")) {
                    this.manageBridgingServices(request, response, session);
                } else if (operation.equals("showPossibleTargets")) {
                    this.showPossibleTargets(request, response, session);
                } else if (operation.equals("showexposedServicesByTaxonomy")) {
                    this.showexposedServicesByTaxonomy(request, response, session);
                } else if (operation.equals("createBridging")) {
                    this.createBridging(request, response, session);
                } else if (operation.equals("createBridgingServices")) {
                    this.createBridgingServices(request, response, session);
                } else if (operation.equals("doBridging")) {
                    this.doBridging(request, response, session);
                } else if (operation.equals("showMyBridges")) {
                    this.showMyBridges(request, response, session);
                } else if (operation.equals("doBridgingServicePrepare")) {
                    this.doBridgingServicePrepare(request, response, session);
                } else if (operation.equals("doBridgingService")) {
                    this.doBridgingService(request, response, session);
                } else if (operation.equals("deleteBridging")) {
                    this.deleteBridging(request, response, session);
                } else if (operation.equals("getModelsForComparation")) {
                    this.getModelsForComparation(request, response, session);
                } else if (operation.equals("getModelsForComparationSchema")) {
                    this.getModelsForComparationSchema(request, response, session);
                } else if (operation.equals("getModelsForComparationSchemaXBRL")) {
                    this.getModelsForComparationSchemaXBRL(request, response, session);
                } else if (operation.equals("cpp_reg")) {
                    this.CPPRegistration(request, response, session);
                } else if (operation.equals("cpp_delete")) {
                    this.CPPDelete(request, response, session);
                } else if (operation.equals("ws_installations")) {
                    this.getWebServicesInstallations(request, response, session);
                } else if (operation.equals("addUrlBinding")) {
                    this.addUrlBinding(request, response, session);
                } else if (operation.equals("deleteUrlBinding")) {
                    this.deleteUrlBinding(request, response, session);
                } else if (operation.equals("getCPPsInstallations")) {
                    this.getCPPsInstallations(request, response, session);
                } else if (operation.equals("showCppsForBridging")) {
                    this.showCppsForBridging(request, response, session);
                }


            }
        } catch (Throwable t) {
            t.printStackTrace();
            this.forwardToPage("/error/generic_error.jsp?errormsg=GenericError ", request, response);
        } finally {
            out.close();
        }
    }

    protected void showComponents(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {
        int rowID = 1;
        String bridging = request.getParameter("bridging");
        System.out.println("bridging " + bridging);
        OrgDBConnector orgDBConnector = new OrgDBConnector();
        Iterator compIterator = (orgDBConnector.getSoftwareComponents()).iterator();

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("<rows>");

        while (compIterator.hasNext()) {
            SoftwareComponent comp = (SoftwareComponent) compIterator.next();
            out.write("<row id=\"" + comp.getSoftwareID() + "\">"
                    + "<cell>" + comp.getSoftwareID() + "</cell>"
                    //+ "<cell>" + comp.getName() + "^../DIController?op=show_bridging_schemas&amp;software_id=" + comp.getSoftwareID() + "^_self</cell>"
                    + "<cell  style='font-weight:bold;color: #055A78;'>" + comp.getName() + "</cell>"
                    + "<cell>" + comp.getVersion() + "</cell>");

            if (bridging.equalsIgnoreCase("true")) {
                out.write("<cell type=\"img\">../js/dhtmlxSuite/dhtmlxTree/codebase/imgs/xsd.png^Schema Bridging^../DIController?op=show_bridging_schemas&amp;software_id=" + comp.getSoftwareID() + "^_self</cell>");
                out.write("<cell type=\"img\">../js/dhtmlxSuite/dhtmlxTree/codebase/imgs/wsdl.png^Service Bridging^../DIController?op=show_bridging_services&amp;software_id=" + comp.getSoftwareID() + "^_self</cell>");


            } else {
                out.write("<cell type=\"img\">../js/dhtmlxSuite/dhtmlxTree/codebase/imgs/xsd.png^Schemas^../DIController?op=show_schema&amp;xsd=1_"+comp.getNum_xsds()+"_" + comp.getSoftwareID() + "^_self</cell>"
                        + "<cell type=\"img\">../js/dhtmlxSuite/dhtmlxTree/codebase/imgs/wsdl.png^Services^../DIController?op=show_service&amp;service=1_"+comp.getNum_services()+"_" + comp.getSoftwareID() + "^_self</cell>");
            }
            out.write(" </row>");
        }

        out.write("</rows>");
        out.flush();

    }

    protected void CPPRegistration(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, ServletException {

        int cpp_id = Integer.parseInt(request.getParameter("CPPid"));
        String organization_name = (String) session.getAttribute("name");
        String cpp_name= (String)  request.getParameter("cpp_name");
        String software_id = (String) request.getParameter("software_id");

        OrgDBConnector orgDBConnector = new OrgDBConnector();

        orgDBConnector.insertNewCPP(cpp_id,organization_name,cpp_name,-1);
        String message= "A new CPP has been created!!!" ;
        System.out.println("message"+message);


         //---------------------------------------------

        LinkedList<Service> cppsofOrg = orgDBConnector.getCPPs((String) session.getAttribute("name"),Integer.parseInt(software_id));

        JSONObject json_cppsOfOrganization = new JSONObject();

        Iterator cppsofOrg_iterator = cppsofOrg.iterator();
        while (cppsofOrg_iterator.hasNext()) {
            Service cpp_service = (Service) cppsofOrg_iterator.next();
            json_cppsOfOrganization.put(cpp_service.getCpp_id(), cpp_service.getName() + "  V." + cpp_service.getVersion() + "   CPP Name: " +cpp_service.getCpp_name() + "  ID: " +cpp_service.getCpp_id());
        }
        session.removeAttribute("CPPsPerSoftCompPerOrg");
        session.setAttribute("CPPsPerSoftCompPerOrg", json_cppsOfOrganization);



        //---------------------------------------------


        this.forwardToPage("/showServices.jsp?software_id=" + software_id+"&message="+message, request, response);

    }

    protected void CPPDelete(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, ServletException {

        int cpp_id = Integer.parseInt(request.getParameter("cpp_id"));
        String software_id = (String) request.getParameter("software_id");


        OrgDBConnector orgDBConnector = new OrgDBConnector();
        orgDBConnector.deleteCPP(cpp_id);
        String message= "The CPP with ID:"+cpp_id+" has been succesfully deleted!!!" ;
        System.out.println("message"+message);

        //---------------------------------------------

        LinkedList<Service> cppsofOrg = orgDBConnector.getCPPs((String) session.getAttribute("name"),Integer.parseInt(software_id));

        JSONObject json_cppsOfOrganization = new JSONObject();

        Iterator cppsofOrg_iterator = cppsofOrg.iterator();
        while (cppsofOrg_iterator.hasNext()) {
            Service cpp_service = (Service) cppsofOrg_iterator.next();
            json_cppsOfOrganization.put(cpp_service.getCpp_id(), cpp_service.getName() + "  V." + cpp_service.getVersion() + "   CPP Name: " +cpp_service.getCpp_name() + "  ID: " +cpp_service.getCpp_id());
        }
        session.removeAttribute("CPPsPerSoftCompPerOrg");
        session.setAttribute("CPPsPerSoftCompPerOrg", json_cppsOfOrganization);



        //---------------------------------------------

        //Desactivate Bridge
        orgDBConnector.desactivateBridge(cpp_id);

        this.forwardToPage("/showServices.jsp?software_id=" + software_id+"&message="+message, request, response);

    }


    protected void addUrlBinding(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, ServletException {


        String service_id = request.getParameter("add_url").split("\\$")[0].split("WS")[1];
        String url_binding= (String)  request.getParameter("url");


        OrgDBConnector orgDBConnector = new OrgDBConnector();
        orgDBConnector.insertNewUrlBinding(service_id, url_binding) ;
        String message= "A new Url Binding has been added at the service "+ orgDBConnector.getServiceName(Integer.parseInt(service_id))+ "!!!";
        System.out.println("message"+message);
        this.forwardToPage("/organization/manageinstallations.jsp?software_id=-1&message="+message, request, response);

    }




    protected void deleteUrlBinding(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException, ServletException {
        String url_binding_id = request.getParameter("delete_url").split("IB")[1];

        OrgDBConnector orgDBConnector = new OrgDBConnector();
        orgDBConnector.deleteUrlBindingID(url_binding_id);
        String message= "The selected Url Binding has been succesfully deleted!!!" ;
        System.out.println("message"+message);
        this.forwardToPage("/organization/manageinstallations.jsp?software_id=-1&message="+message, request, response);

    }


    protected void manageBridgingSchemas(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, ParserConfigurationException, SAXException {
        String software_id = (String) request.getParameter("software_id");
        String xml_string = "";
        int service_id = -1;

        OrgDBConnector orgDBConnector = new OrgDBConnector();

        LinkedList<Schema> schemas = (LinkedList<Schema>) orgDBConnector.getSchemas(software_id);

        Iterator<Schema> schemas_it = schemas.iterator();
        while (schemas_it.hasNext()) {

            Schema schema = schemas_it.next();

            XSDParser p = new XSDParser(schema);
            xml_string += p.convertSchemaToXML();
        }

        xml_string = xml_string.replace("<tree id='0'>", "").replace("</tree>", "");
        xml_string = "<tree id='0'>" + xml_string + "</tree>";

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write(xml_string);

    }

    protected void showPossibleTargets(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, ParserConfigurationException, SAXException {

        String selections = request.getParameter("selections");
        String inputoutput = selections.split("--")[1];
        String taxonomy_id = selections.split("--")[3];
        String xbrl_taxonomy = selections.split("--")[8];
        String xml_string = "";

        OrgDBConnector orgDBConnector = new OrgDBConnector();

        LinkedList<Schema> schemas = (LinkedList<Schema>) orgDBConnector.getTargetSchemas(inputoutput, taxonomy_id, xbrl_taxonomy);

        Iterator<Schema> schemas_it = schemas.iterator();
        while (schemas_it.hasNext()) {

            Schema schema = schemas_it.next();
            //System.out.println("Schema: " + schema.getService() + " " + schema.getOperation_id() + " " + schema.getOperation() + " " + schema.getInputoutput() + " " + schema.getSchema_id() + " " + schema.getName() + " " + schema.getSchema_id());
            XSDParser p = new XSDParser(schema);
            xml_string += p.convertSchemaToXML();
        }



        xml_string = xml_string.replace("<tree id='0'>", "").replace("</tree>", "");
        xml_string = "<tree id='0'>" + xml_string + "</tree>";


        Map<String, Object> data = new HashMap<String, Object>();
        data.put("selections", request.getParameter("selections"));
        data.put("tree", xml_string);

        // Write response data as JSON.
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(data));


    }

    protected void showexposedServicesByTaxonomy(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, ParserConfigurationException, SAXException, WSDLException {

        String form = request.getParameter("form");
        OrgDBConnector orgDBConnector = new OrgDBConnector();
        String tax = request.getParameter("tax");
        String software_id = request.getParameter("software_id");
        LinkedList<Service> services = (LinkedList<Service>) orgDBConnector.getServicesByTaxonomy(tax, software_id);
        System.out.println("services: " + services + "lenght: " + services.size());


        if (form.equalsIgnoreCase("json")) {

            String xml = "";
            String wsdlParserString = "";
            String listWS= "<option value=\"-1\">Choose WS</option>";

            if (services.size() > 0) {
                Iterator serv_iterator = services.iterator();
                while (serv_iterator.hasNext()) {
                    Service service = (Service) serv_iterator.next();
                    System.out.println("My service id:" + service.getService_id() + " service_name: " + service.getName() + " wsdl: " + service.getWsdl() + " " + service.getNamespace());
                    WSDLParser wsdlParser = new WSDLParser(service.getWsdl(), service.getNamespace());
                    wsdlParser.loadService(service.getName());
                    wsdlParserString = wsdlParserString.concat(wsdlParser.outputFunctionsToXMLFromRoot(xml, service.getName(), service.getService_id()));

                    //Prepare dropdownlist with services

                    listWS += "<option value="+service.getService_id()+">"+service.getName()+" V."+ service.getVersion()+"</option>";
                }
            }

            xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<tree id=\"0\">" + wsdlParserString + "</tree>";

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("tree", xml);
            data.put("listWS", listWS);
            //data.put("listOfWebServices",json_listWS);
            // Write response data as JSON.
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new Gson().toJson(data));


        } else if (form.equalsIgnoreCase("out")) {

            response.setContentType("text/xml; charset=UTF-8");

            if (services.size() > 0) {
                PrintWriter out = response.getWriter();
                out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                out.write("<tree id=\"0\">");
                Iterator serv_iterator = services.iterator();
                while (serv_iterator.hasNext()) {
                    Service service = (Service) serv_iterator.next();
                    System.out.println("My service id:" + service.getService_id() + " service_name: " + service.getName() + " wsdl: " + service.getWsdl() + " " + service.getNamespace());
                    WSDLParser wsdlParser = new WSDLParser(service.getWsdl(), service.getNamespace());
                    wsdlParser.loadService(service.getName());
                    wsdlParser.outputFunctionsToXMLFromRoot(out, service.getName(), service.getService_id());
                }
                out.write("</tree>");
                out.close();
            }
        }

    }



    protected void showCppsForBridging(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, ParserConfigurationException, SAXException, WSDLException {

        String form = request.getParameter("form");
        MainControlDB mainControlDB = new MainControlDB();
        int service_id =Integer.parseInt(request.getParameter("service_id"));
        Service service = mainControlDB.getService(service_id);

        if (form.equalsIgnoreCase("json")) {

            String xml = "";
            String wsdlParserString = "";

             WSDLParser wsdlParser = new WSDLParser(service.getWsdl(), service.getNamespace());
             wsdlParser.loadService(service.getName());
             wsdlParserString = wsdlParserString.concat(wsdlParser.outputFunctionsToXMLFromRoot(xml, service.getName(), service.getService_id()));


            xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<tree id=\"0\">" + wsdlParserString + "</tree>";

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("tree", xml);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new Gson().toJson(data));


        }

    }

    protected void createBridging(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {

        OrgDBConnector orgDBConnector = new OrgDBConnector();

        int cpa_id = -1;
        String selections_source = request.getParameter("selections_source");
        //String installations_source =
        String cvp_source = selections_source.split("--")[4];
        int cpp_source = orgDBConnector.getCPP(Integer.parseInt(cvp_source), (String) session.getAttribute("name"));
        System.out.println("cpp_source: " + cpp_source);

        String selections_target = request.getParameter("selections_target");
        String cvp_target = selections_target.split("--")[4];
        int cpp_target = orgDBConnector.getCPP(Integer.parseInt(cvp_target), (String) session.getAttribute("name"));
        System.out.println("cpp_target: " + cpp_target);

        String organization_name = (String) session.getAttribute("name");

        Map<String, CPP> CPPList = new HashMap<String, CPP>();

        String service_id = selections_source.split("--")[5];
        String operation_id = selections_source.split("--")[2];
        String schema_id = selections_source.split("--")[6];
        String complex_type = selections_source.split("--")[0];
        CPP cppinfo_first = new CPP(cpp_source, Integer.parseInt(service_id), Integer.parseInt(operation_id), Integer.parseInt(schema_id), complex_type);

        service_id = selections_target.split("--")[5];
        operation_id = selections_target.split("--")[2];
        schema_id = selections_target.split("--")[6];
        complex_type = selections_target.split("--")[0];
        CPP cppinfo_second = new CPP(cpp_target, Integer.parseInt(service_id), Integer.parseInt(operation_id), Integer.parseInt(schema_id), complex_type);


        CPPList.put("cppinfo_first", cppinfo_first);
        CPPList.put("cppinfo_second", cppinfo_second);


        Map<String, Integer> data = orgDBConnector.insertBridging(cpp_source, cpp_target, organization_name, new Gson().toJson(CPPList));


        if (data.containsKey("new_cpa_id")) {
            session.setAttribute("message", "New bridging was created!");
            System.out.println("New bridging was created");
            cpa_id = data.get("new_cpa_id");
        } else {
            session.setAttribute("message", "Bridging already existed!");
            System.out.println("Bridging already existed!");
            cpa_id = data.get("existing");
        };

        this.forwardToPage("/organization/succ.jsp?level=schema&cpa_id=" + cpa_id, request, response);
    }

    protected void createBridgingServices(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {

        OrgDBConnector orgDBConnector = new OrgDBConnector();
        MainControlDB mainControlDB = new MainControlDB();


        int cpa_id = -1;
        String organization_name = (String) session.getAttribute("name");
        String selections_source = request.getParameter("selections_source");
        int service_id_source = Integer.parseInt(selections_source.split("\\$")[0]);
        String operation_name_source = (String) selections_source.split("\\$")[1];

        //int cpp_source = orgDBConnector.getCPP(service_id_source);
        int cpp_source = Integer.parseInt(request.getParameter("CPPs_source"));
        int installations_source = Integer.parseInt(request.getParameter("installations_source"));

        //create the cpp entity in case the org user type has not changed the annotations
        //if (cpp_source == -1) {
        //    cpp_source = mainControlDB.insertCPP(-1, service_id_source, organization_name);
        //}

        System.out.println("cpp_source: " + cpp_source);


        String selections_target = request.getParameter("selections_target");
        int service_id_target = Integer.parseInt(selections_target.split("\\$")[0]);
        String operation_name_target = (String) selections_target.split("\\$")[1];


        //int cpp_target = orgDBConnector.getCPP(service_id_target);
        int cpp_target = Integer.parseInt(request.getParameter("CPPs_target"));
        int installations_target = Integer.parseInt(request.getParameter("installations_target"));



        //if (cpp_target == -1) {
        //    cpp_target = mainControlDB.insertCPP(-1, service_id_target, organization_name);
        //}


        System.out.println("cpp_target: " + cpp_target);



        Map<String, CPP> CPPList = new HashMap<String, CPP>();
        CPP cppinfo_first = new CPP(cpp_source, service_id_source, operation_name_source,installations_source);
        CPP cppinfo_second = new CPP(cpp_target, service_id_target, operation_name_target,installations_target);


        CPPList.put("cppinfo_first", cppinfo_first);
        CPPList.put("cppinfo_second", cppinfo_second);


        Map<String, Integer> data = orgDBConnector.insertBridging(cpp_source, cpp_target, organization_name, new Gson().toJson(CPPList));


        if (data.containsKey("new_cpa_id")) {
            session.setAttribute("message", "New bridging was created!");
            System.out.println("New bridging was created");
            cpa_id = data.get("new_cpa_id");
        } else {
            session.setAttribute("message", "Bridging already existed!");
            System.out.println("Bridging already existed!");
            cpa_id = data.get("existing");
        };

        this.forwardToPage("/organization/succ.jsp?level=service&cpa_id=" + cpa_id, request, response);
    }

    protected void doBridging(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, FileUploadException {

        int cpa_id = Integer.parseInt(request.getParameter("cpa_id"));
        String data = "";

        OrgDBConnector orgDBConnector = new OrgDBConnector();
        CPA cpainfo = orgDBConnector.getinfocpa(cpa_id);

        int cpp_a = cpainfo.getCpp_id_first();
        int cpp_b = cpainfo.getCpp_id_second();

        // Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        // Parse the request
        List items = upload.parseRequest(request);

        // Process the uploaded items
        Iterator iter = items.iterator();
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();

            if (!item.isFormField()) {
                //System.out.println("data: " + item.getString());
                data = item.getString();
            }
        }

        JSONObject transform_response = this.transform(cpp_a, cpp_b, data, cpainfo.getCpa_info(), new LinkedList<String>());
        String target_xml = transform_response.getString("xml");

        transform_response.remove("xml");

        session.setAttribute("source_xml", data.toString());
        session.setAttribute("target_xml", target_xml);
        session.removeAttribute("infoBridgingProcess");
        session.setAttribute("infoBridgingProcess", transform_response);
        this.forwardToPage("/organization/showBridging.jsp?type=schema", request, response);
    }

    public JSONObject transform(int cpp_a, int cpp_b, String xmlData, String cpa_info, LinkedList<String> service_selections) {

        JSONObject trasform_response = new JSONObject();
        OrgDBConnector orgDBConnector = new OrgDBConnector();
        String finalxmloutput = "";
        String finalxmlAllUpcastedXML="";

        String xsltRulesFirst = orgDBConnector.retrieveXLST(cpp_a, "input", cpa_info, service_selections);//input
        String xsltRulesSecond = orgDBConnector.retrieveXLST(cpp_b, "output", cpa_info, service_selections);//output

        trasform_response.put("xsltRulesFirst", xsltRulesFirst);
        trasform_response.put("xsltRulesSecond", xsltRulesSecond);

        System.out.println("CHECK " + xsltRulesFirst);
        System.out.println("CHECK " + xsltRulesSecond);

        StringWriter stw = new StringWriter();
        StringWriter stwRes = new StringWriter();
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer;
        Parser parser = new Parser();

        try {
            if (service_selections.isEmpty()) {

                //check if the xml apart from root has repetitive first level elements
                
                LinkedList<String> xmlnodes = parser.parseXML(xmlData);

                String firstxmlnode = xmlnodes.get(0);

                transformer = tFactory.newTransformer(new StreamSource(new StringReader(xsltRulesFirst)));
                transformer.transform(new StreamSource(new StringReader(firstxmlnode)), new StreamResult(stw));

                System.out.println("UpcastingXML: " + stw.toString());

                tFactory = TransformerFactory.newInstance();
                transformer = tFactory.newTransformer(new StreamSource(new StringReader(xsltRulesSecond)));
                transformer.transform(new StreamSource(new StringReader(stw.toString())), new StreamResult(stwRes));

                List Namespaces = parser.getNamespaces(stwRes.toString());
                List XbrlNamespaces = parser.getNamespaces(stw.toString());

                String outputAllXML = "<root>";
                String outputAllUpcastedXML = "<root>";
                Iterator i = xmlnodes.iterator();
                while (i.hasNext()) {

                    String xmlnode = (String) i.next();
                    StringWriter stw1 = new StringWriter();
                    StringWriter stwRes1 = new StringWriter();

                    transformer = tFactory.newTransformer(new StreamSource(new StringReader(xsltRulesFirst)));
                    transformer.transform(new StreamSource(new StringReader(xmlnode)), new StreamResult(stw1));
                    System.out.println("UpcastingXML: " + stw1.toString());
                    outputAllUpcastedXML = outputAllUpcastedXML + parser.removeNamespaces(stw1.toString());
                    

                    tFactory = TransformerFactory.newInstance();
                    transformer = tFactory.newTransformer(new StreamSource(new StringReader(xsltRulesSecond)));
                    transformer.transform(new StreamSource(new StringReader(stw1.toString())), new StreamResult(stwRes1));

                    outputAllXML = outputAllXML + parser.removeNamespaces(stwRes1.toString());

                }
                outputAllXML = outputAllXML + "</root>";
                outputAllUpcastedXML = outputAllUpcastedXML + "</root>";
                System.out.println("All output XML+ " + outputAllXML);


                finalxmloutput = parser.addNamespaces(outputAllXML, Namespaces);
                finalxmlAllUpcastedXML = parser.addNamespaces(outputAllUpcastedXML, XbrlNamespaces);
               
                
            } else {
                transformer = tFactory.newTransformer(new StreamSource(new StringReader(xsltRulesFirst)));
                transformer.transform(new StreamSource(new StringReader(xmlData)), new StreamResult(stw));

                System.out.println("UpcastingXML: " + stw.toString());
                finalxmlAllUpcastedXML= stw.toString();

                tFactory = TransformerFactory.newInstance();
                transformer = tFactory.newTransformer(new StreamSource(new StringReader(xsltRulesSecond)));
                transformer.transform(new StreamSource(new StringReader(stw.toString())), new StreamResult(stwRes));

                System.out.println("Hola" + stwRes.toString() + "Adios" + stw.toString());

                finalxmloutput = stwRes.toString();
               

            }


        } catch (Throwable t) {
            //t.printStackTrace();
            System.out.println("Hola" + t);
        }

        trasform_response.put("xml", finalxmloutput);
        trasform_response.put("UPCasted XML To XBRL Taxonomy", finalxmlAllUpcastedXML);
        System.out.println("trasform_response: "+trasform_response.getString("UPCasted XML To XBRL Taxonomy"));
        
        return trasform_response;
    }

    protected void manageBridgingServices(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, ParserConfigurationException, SAXException, WSDLException {

        MainControlDB mainControlDB = new MainControlDB();

        response.setContentType("text/xml; charset=UTF-8");
        String software_id = (String) request.getParameter("software_id");

        LinkedList<Service> services = (LinkedList<Service>) mainControlDB.getServices(software_id, true, "IS NOT NULL");

        System.out.println("services: " + services + "lenght: " + services.size());
        PrintWriter out = response.getWriter();
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.write("<tree id=\"0\">");
        if (services.size() > 0) {

            Iterator serv_iterator = services.iterator();
            while (serv_iterator.hasNext()) {
                Service service = (Service) serv_iterator.next();
                if (service.isExposed()) {
                    System.out.println("My service " + service.getService_id() + " " + service.getWsdl() + " " + service.getNamespace());
                    WSDLParser wsdlParser = new WSDLParser(service.getWsdl(), service.getNamespace());
                    wsdlParser.loadService(service.getName());
                    wsdlParser.outputFunctionsToXMLFromRoot(out, service.getName(), service.getService_id());
                }
            }


        }
        out.write("</tree>");
        out.close();
    }

    private void forwardToPage(String url, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        RequestDispatcher dis;

        dis = getServletContext().getRequestDispatcher(url);
        dis.forward(req, resp);
        return;
    }

    protected void showMyBridges(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, FileUploadException {

        String name = (String) request.getSession().getAttribute("name");
        String additional_schema_info = "";
        String dobridging_url = "";

        OrgDBConnector orgDBConnector = new OrgDBConnector();
        Iterator cpaIterator = (orgDBConnector.getCPAs(name)).iterator();

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("<rows>");

        if (!cpaIterator.hasNext()) {
            out.write("<row><cell>There are no </cell><cell>Bridges </cell><cell> created!!!</cell></row>");
        }

        while (cpaIterator.hasNext()) {
            CPA cpa = (CPA) cpaIterator.next();
            JSONObject o = (JSONObject) JSONSerializer.toJSON(cpa.getCpa_info());
            JSONObject o_first = (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_first"));
            JSONObject o_second = (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_second"));

            System.out.println("o_first.get(schema)::" + o_first.get("schema"));

            String active_bridge_schema = (cpa.isDisabled()) ? "<cell>Disabled</cell>" : "<cell type=\"img\">./js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/usebridge.png^Use Bridge^./DIController?op=doBridging&amp;cpa_id=" + cpa.getCpa_id() + "^_self</cell>";


            additional_schema_info = (o_first.get("schema") == null) ? "" : "<row id=\"" + cpa.getCpa_id() + "3\">"
                    + "<cell>Schema:</cell>"
                    + "<cell>" + o_first.get("schema")
                    + "</cell>"
                    + "<cell>" + o_second.get("schema")
                    + "</cell>"
                    + "<cell> </cell>"
                    + "<cell> </cell>"
                    + "</row>"
                    + "<row id=\"" + cpa.getCpa_id() + "31\">"
                    + "<cell>Element:</cell>"
                    + "<cell>" + o_first.get("schema_complexType")
                    + "</cell>"
                    + " <cell>" + o_second.get("schema_complexType")
                    + "</cell>"
                    + active_bridge_schema
                    //+ "<cell type=\"img\">../js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/usebridge.png^Use bridge^../DIController?op=doBridging&amp;cpa_id=" + cpa.getCpa_id() + "^_self</cell>"
                    //+ "<cell type=\"img\">../js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/deletebridge.png^Delete Bridge^../OrganizationManager?op=deleteBridging&amp;cpa_id=" + cpa.getCpa_id() + "^_self</cell>"
                    + "<cell type=\"img\">./js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/deletebridge.png^Delete Bridge^javascript:deletebridge("+cpa.getCpa_id()+")^_self</cell>"
                    + "</row>";


            String active_bridge = (cpa.isDisabled()) ? "<cell>Disabled</cell>"
                    : "<cell type=\"img\">./js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/usebridge.png^Use Bridge^../OrganizationManager?op=doBridgingServicePrepare&amp;cpa_id=" + cpa.getCpa_id() + "^_self</cell>";

            /*
            dobridging_url = (o_first.get("schema") == null) ? active_bridge + "<cell type=\"img\">../js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/deletebridge.png^Delete Bridge^../OrganizationManager?op=deleteBridging&amp;cpa_id=" + cpa.getCpa_id() + "^_self</cell>"
                    : "<cell></cell>"
                    + "<cell></cell>";
            */
            
              dobridging_url = (o_first.get("schema") == null) ? active_bridge + "<cell type=\"img\">./js/dhtmlxSuite/dhtmlxGrid/codebase/imgs/deletebridge.png^Delete Bridge^javascript:deletebridge("+cpa.getCpa_id()+")^_self</cell>"
                    : "<cell></cell>"
                    + "<cell></cell>";

            out.write("<row id=\"" + cpa.getCpa_id() + "ws1\">"
                    + "<cell> Web Service:</cell>"
                    + "<cell>" + o_first.get("service")
                    + "</cell>"
                    + "<cell>" + o_second.get("service")
                    + "</cell>"
                    + "<cell> </cell>"
                    + "<cell> </cell>"
                    + "</row>"
                    + "<row id=\"" + cpa.getCpa_id() + "2\">"
                    + "<cell>Operation:</cell>"
                    + "<cell>" + o_first.get("operation")
                    + "</cell>"
                    + "<cell>" + o_second.get("operation")
                    + "</cell>"
                    + dobridging_url
                    + "</row>"
                    + additional_schema_info
                    + "<row id=\"" + cpa.getCpa_id() + "6\">"
                    + "<cell></cell>"
                    + "<cell></cell>"
                    + "<cell></cell>"
                    + "<cell></cell>"
                    + "<cell></cell>"
                    + "</row>");

        }

        out.write("</rows>");
        out.flush();
    }

    protected void doBridgingServicePrepare(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException, WSDLException, FileNotFoundException, ParserConfigurationException, SAXException, XPathExpressionException, DocumentException, TransformerException {

        int cpa_id = Integer.parseInt(request.getParameter("cpa_id"));
        /*
        OrgDBConnector orgDBConnector = new OrgDBConnector();
        MainControlDB mainControlDB = new MainControlDB();

        CPA cpa = orgDBConnector.getCPA(cpa_id);

        String cpa_info = cpa.getCpa_info();

        JSONObject o = (JSONObject) JSONSerializer.toJSON(cpa_info);
        JSONObject info = (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_first"));

        JSONObject webServiceInfo = this.getWebServiceInfo(info);

        JSONObject json_service_input_arg0 = new JSONObject();


        Parser pa = new Parser();
        JSONObject fieldsToPrepare = pa.getInputArgs(xml_rep_path + "/xsd/" + webServiceInfo.getString("service_id")+webServiceInfo.getString("service_name")+webServiceInfo.getString("complexType_input").split("\\$")[1]+".xsd",webServiceInfo.getString("operation_name"));

        List<String> TypesToExposeinForm = (List<String>) fieldsToPrepare.get("TypesToExposeinForm");

        Iterator field_iterator = TypesToExposeinForm.iterator();
        while (field_iterator.hasNext()) {
            String field = (String) field_iterator.next();
            json_service_input_arg0.put(field, field);
            System.out.println("field: " + field);
        }


        System.out.println("fieldsToPrepare.size(): "+fieldsToPrepare.size());

        if (TypesToExposeinForm.size()<1)
        {

            XSDParser p = new XSDParser(new Schema(xml_rep_path + "/xsd/" + webServiceInfo.getString("service_id")+webServiceInfo.getString("service_name")+webServiceInfo.getString("complexType_input").split("\\$")[1]+".xsd"));
            //Get all XML elements of XSD

            ArrayList<String> xml_string = p.getXMLElements();

                System.out.println("i ami heri y mi size es: "+xml_string.size());

            Iterator alternative_field_iterator = xml_string.iterator();
            while (alternative_field_iterator.hasNext()) {
                String field = (String) alternative_field_iterator.next();
                json_service_input_arg0.put(field, field);
                System.out.println("field: " + field);
            }

        }

          */

        //System.out.println("source_operation_name:" + webServiceInfo.getString("operation_name"));
        //session.setAttribute("serviceInputArgs", json_service_input_arg0);
        session.setAttribute("serviceInputArgs", "");
        this.forwardToPage("/organization/doBridgingService.jsp?cpa_id=" + cpa_id, request, response);
    }

    protected void doBridgingService(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException, WSDLException, FileNotFoundException, ParserConfigurationException, SAXException, XPathExpressionException, FileUploadException, DocumentException, TransformerException {

        int cpa_id = Integer.parseInt(request.getParameter("cpa_id"));
        JSONObject infoBridgingProcess = new JSONObject();
        MainControlDB mainControlDB = new MainControlDB();


        //---------GET XML DATA OF THE UPLOADED FILE----------------------//
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(30000);
        factory.setRepository(new File("/home/eleni/Desktop/"));
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(30000);

        List items = upload.parseRequest(request);
        String xml_data = "";
        Iterator iter = items.iterator();

        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();
            if (!item.isFormField() && !item.getString().equalsIgnoreCase("")) {
                xml_data = item.getString();
            }
        }

        //--------------------GET INFO OF THE WEB SERVICES {service_id ,operation_name , service_namespace , SoapAdressURL , complexType_input , complexType_output}-----------------

        OrgDBConnector orgDBConnector = new OrgDBConnector();
        CPA cpa = orgDBConnector.getCPA(cpa_id);
        String cpa_info = cpa.getCpa_info();

        JSONObject o = (JSONObject) JSONSerializer.toJSON(cpa_info);
        JSONObject info_first = (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_first"));
        JSONObject first_webServiceInfo = this.getWebServiceInfo(info_first);


        JSONObject info_second = (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_second"));
        JSONObject second_webServiceInfo = this.getWebServiceInfo(info_second);


        LinkedList complexType = new LinkedList();
        complexType.add(first_webServiceInfo.getString("complexType_input"));
        complexType.add(first_webServiceInfo.getString("complexType_output"));
        complexType.add(second_webServiceInfo.getString("complexType_input"));
        complexType.add(second_webServiceInfo.getString("complexType_output"));


        //-------------------- PREPARE INVOCATION OF FIRST WEB SERVICE----------------------------------------------------------------

        String complexType_output_name_first = (first_webServiceInfo.getString("complexType_output").equalsIgnoreCase(""))?"":first_webServiceInfo.getString("complexType_output").split("\\$")[1];


        // Get Dao entity of web service operation if exists
        Service FirstWservice = mainControlDB.getService(first_webServiceInfo.getInt("service_id"));
        WSDLParser firstWSDLParser = new WSDLParser(FirstWservice.getWsdl(), FirstWservice.getNamespace());
        String firstDaoEntity = firstWSDLParser.getDaoEntity(first_webServiceInfo.getString("operation_name"));
        String firstInputXML = prepareSoapBody(xml_data,first_webServiceInfo.getString("operation_name"),firstDaoEntity);

        //-------------------DO INVOCATION OF FIRST WEB SERVICE-------------------------------

       SOAPEnvelopeInvoker soapEnvelopeInvoker = new SOAPEnvelopeInvoker(first_webServiceInfo.getString("SoapAdressURL"), first_webServiceInfo.getString("operation_name"), first_webServiceInfo.getString("complexType_input").split("\\$")[1], complexType_output_name_first, first_webServiceInfo.getString("service_namespace"),firstInputXML);
       JSONObject SOAPEnvelopeInvokerResponse = soapEnvelopeInvoker.callWebService();

        //-------------------MANIPULATE RESPONSE OF FIRST WEB SERVICE-------------------------------

        if (SOAPEnvelopeInvokerResponse.containsKey("ErrorMessage")) {
            //Case that the call to the web service erases an ErrorMessage (ex.404 Not Found) redirect to Error page/

            this.forwardToPage("/error/generic_error.jsp?errormsg=" + SOAPEnvelopeInvokerResponse.getString("ErrorMessage"), request, response);

        } else if(SOAPEnvelopeInvokerResponse.get("outputXML").toString().equalsIgnoreCase("")){
            // Case that the first web service has no Response

           infoBridgingProcess = fillΙnfoBridgingProcess(SOAPEnvelopeInvokerResponse.get("Soap:EnvelopeRequest").toString(),"<info>The first Web Service has no response</info>","","","","", "<info>The second Web Service has not been invoked</info>", "<info>The second Web Service has not been invoked</info>", "<info>The second Web Service has not been invoked</info>");

            updateSessionOfShowBridgingResults(session,xml_data,"<info><fact>The second Web Service has not been invoked</fact><reason>Because the First Web Service gives no response</reason></info>", infoBridgingProcess);

            this.forwardToPage("/organization/showBridging.jsp?type=service", request, response);
        }
        else {
            // Case that the first web service has Response

            CPA cpainfo = orgDBConnector.getinfocpa(cpa_id);
            int cpp_a = cpainfo.getCpp_id_first();
            int cpp_b = cpainfo.getCpp_id_second();


           //-------------------- TRANSFORM (upcasting & Downcasting) the output of the  source web service to an input of the target Web service--------------------

            JSONObject transform_response = this.transform(cpp_a, cpp_b, SOAPEnvelopeInvokerResponse.getString("outputXML"), cpainfo.getCpa_info(), complexType);
            String target_xml = transform_response.getString("xml");


            //-------------------- PREPARE INVOCATION OF SECOND WEB SERVICE----------------------------------------------------------------

            String complexType_output_name_second = (second_webServiceInfo.getString("complexType_output").equalsIgnoreCase(""))?"":second_webServiceInfo.getString("complexType_output").split("\\$")[1];


            //Get Dao entity of web service operation if exists
            Service SecondWservice = mainControlDB.getService(second_webServiceInfo.getInt("service_id"));
            WSDLParser secondWSDLParser = new WSDLParser(SecondWservice.getWsdl(), SecondWservice.getNamespace());
            String daoEntity = secondWSDLParser.getDaoEntity(second_webServiceInfo.getString("operation_name"));
            String secondInputXML = prepareSoapBody(target_xml,second_webServiceInfo.getString("operation_name"),daoEntity);

            //-------------------DO INVOCATION OF SECOND WEB SERVICE-------------------------------

            SOAPEnvelopeInvoker targetsoapEnvelopeInvoker = new SOAPEnvelopeInvoker(second_webServiceInfo.getString("SoapAdressURL"), second_webServiceInfo.getString("operation_name"), second_webServiceInfo.getString("complexType_input").split("\\$")[1], complexType_output_name_second, second_webServiceInfo.getString("service_namespace"),secondInputXML);
            JSONObject targetsoapEnvelopeInvokerResponse = targetsoapEnvelopeInvoker.callWebService();

            //-------------------MANIPULATE RESPONSE OF SECOND WEB SERVICE-------------------------------

            if (targetsoapEnvelopeInvokerResponse.containsKey("ErrorMessage")) {

                this.forwardToPage("/error/generic_error.jsp?errormsg=" + targetsoapEnvelopeInvokerResponse.getString("ErrorMessage"), request, response);

            }else if(targetsoapEnvelopeInvokerResponse.get("outputXML").toString().equalsIgnoreCase("")){

                infoBridgingProcess = fillΙnfoBridgingProcess(SOAPEnvelopeInvokerResponse.get("Soap:EnvelopeRequest").toString(),SOAPEnvelopeInvokerResponse.get("Soap:EnvelopeResponse").toString(),SOAPEnvelopeInvokerResponse.get("outputXML").toString(),target_xml,transform_response.get("xsltRulesFirst").toString(),transform_response.get("xsltRulesSecond").toString(), targetsoapEnvelopeInvokerResponse.get("Soap:EnvelopeRequest").toString(),targetsoapEnvelopeInvokerResponse.get("Soap:EnvelopeResponse").toString(), "<return>This Operation has no Response</return>" );

                updateSessionOfShowBridgingResults(session,xml_data,"<return>This Operation has no Response</return>",infoBridgingProcess);

                this.forwardToPage("/organization/showBridging.jsp?type=service", request, response);

            } else {

                infoBridgingProcess = fillΙnfoBridgingProcess(SOAPEnvelopeInvokerResponse.get("Soap:EnvelopeRequest").toString(),SOAPEnvelopeInvokerResponse.get("Soap:EnvelopeResponse").toString(), SOAPEnvelopeInvokerResponse.get("outputXML").toString(), target_xml,transform_response.get("xsltRulesFirst").toString(), transform_response.get("xsltRulesSecond").toString(), targetsoapEnvelopeInvokerResponse.get("Soap:EnvelopeRequest").toString(), targetsoapEnvelopeInvokerResponse.get("Soap:EnvelopeResponse").toString(), targetsoapEnvelopeInvokerResponse.get("outputXML").toString());

                updateSessionOfShowBridgingResults(session,xml_data,targetsoapEnvelopeInvokerResponse.getString("outputXML"),infoBridgingProcess);

                this.forwardToPage("/organization/showBridging.jsp?type=service", request, response);
            }
        }

    }

    private JSONObject getWebServiceInfo(JSONObject info) throws WSDLException, FileNotFoundException, IOException, ParserConfigurationException, SAXException, XPathExpressionException, DocumentException, TransformerException {
        MainControlDB mainControlDB = new MainControlDB();
        int service_id = Integer.parseInt(info.get("service_id").toString());

        String operation_name = (String) info.get("operation");
        System.out.println("operation_name: " + operation_name);

        Service service = mainControlDB.getService(service_id);
        WSDLParser wsdlParser = new WSDLParser(service.getWsdl(), service.getNamespace());
        String service_name = service.getName();
        wsdlParser.loadService(service.getName());
        String service_namespace = service.getNamespace();

        //String SoapAdressURL = wsdlParser.getSoapAdressURL(service.getName());
        String SoapAdressURL = (String) info.get("urlbinding");


        LinkedList<String> complexType = wsdlParser.getXSDAttibutes(operation_name);
       
        String complexType_input = complexType.get(0);
        String complexType_output = (complexType.size()>1)?complexType.get(1):"";
        String xsdTypes = wsdlParser.extractXSD(complexType_input.split("\\$")[1]);
        System.out.println("SoapAdressURL: " + SoapAdressURL);

        JSONObject webServiceInfo = new JSONObject();
        webServiceInfo.put("service_id", service_id);
        webServiceInfo.put("operation_name", operation_name);
        webServiceInfo.put("service_name", service_name);
        webServiceInfo.put("service_namespace", service_namespace);
        webServiceInfo.put("SoapAdressURL", SoapAdressURL);
        webServiceInfo.put("complexType", complexType);
        webServiceInfo.put("complexType_input", complexType_input);
        webServiceInfo.put("complexType_output", complexType_output);
        webServiceInfo.put("xsdTypes", xsdTypes);
        return webServiceInfo;
    }

    /*Fill the JSONObject  infoBridgingProcess.
    * In this JSONObject is stored all the info that is printed at the  showBridging page so as to inform the end user about the process of the bridging
    */
    private JSONObject fillΙnfoBridgingProcess(String FirstSoapRequest, String FirstSoapResponse,String FirstOutputXML,String XMLAfterDownCasting, String UpcastingXSLT, String DowncastingXSLT, String SecondSoapRequest, String  SecondSoapResponse, String SecondoutputXML  ){
        JSONObject infoBridgingProcess = new JSONObject();


        infoBridgingProcess.put("FirstSoapRequest", FirstSoapRequest);
        infoBridgingProcess.put("FirstSoapResponse",FirstSoapResponse);
        infoBridgingProcess.put("Output XML from First Web Service for XBRL UPCasting",FirstOutputXML);
        infoBridgingProcess.put("XML after XBRL DownCasting - Input to Second Web Service", XMLAfterDownCasting);
        infoBridgingProcess.put("Upcasting xslt",UpcastingXSLT);
        infoBridgingProcess.put("Downcasting xslt",DowncastingXSLT);

        infoBridgingProcess.put("SecondSoapRequest",SecondSoapRequest);
        infoBridgingProcess.put("SecondSoapResponse",SecondSoapResponse);
        infoBridgingProcess.put("SecondoutputXML", SecondoutputXML);

        return  infoBridgingProcess;
    }

    /*
    * Update the session attributes of the showBridging page so as to inform the end user about the process of the bridging
    * */
    private void updateSessionOfShowBridgingResults(HttpSession session,String source_xml, String target_xml, JSONObject infoBridgingProcess){

        session.setAttribute("source_xml",source_xml);
        session.setAttribute("target_xml", target_xml);
        session.removeAttribute("infoBridgingProcess");
        session.setAttribute("infoBridgingProcess", infoBridgingProcess);

    }

     


    protected void deleteBridging(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {

        int cpa_id = Integer.parseInt(request.getParameter("cpa_id"));
        OrgDBConnector orgDBConnector = new OrgDBConnector();
        orgDBConnector.deleteBridge(cpa_id);

        this.forwardToPage("/organization/succ.jsp?level=delete&cpa_id=" + cpa_id, request, response);
    }

    // service info in the form service_id$operation_name
    protected void getModelsForComparation(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, WSDLException {
               
        MainControlDB mainControlDB = new MainControlDB();

        String source_service_info = request.getParameter("source_service");

        int source_service_id = Integer.parseInt(source_service_info.split("\\$")[0]);
        String source_operation = source_service_info.split("\\$")[1];
        
         Service source_service = mainControlDB.getService(source_service_id);
         WSDLParser source_wsdlParser = new WSDLParser(source_service.getWsdl(), source_service.getNamespace());
         source_wsdlParser.loadService(source_service.getName());
        
         System.out.println("source_operation:" + source_operation); 
        LinkedList<String> source_operations = source_wsdlParser.returnOperationNames(source_operation);
        
  
       String target_service_info = request.getParameter("target_service");

        int target_service_id = Integer.parseInt(target_service_info.split("\\$")[0]);
        String target_operation = target_service_info.split("\\$")[1];
        
         Service target_service = mainControlDB.getService(target_service_id);
         WSDLParser target_wsdlParser = new WSDLParser(target_service.getWsdl(), target_service.getNamespace());
         target_wsdlParser.loadService(target_service.getName());
         
         System.out.println("target_operation:" + target_operation); 
         LinkedList<String> target_operations = target_wsdlParser.returnOperationNames(target_operation);
         
         
         System.out.println("input op:" + source_operations.get(1));
         
         JSONObject schemasToCompare = new JSONObject();
         schemasToCompare.put("source_schema", source_service_id + "_" + source_operations.get(1));
         schemasToCompare.put("target_schema", target_service_id + "_" + target_operations.get(0));
       
         
         response.setContentType("application/json");
         response.setCharacterEncoding("UTF-8");
         response.getWriter().write(new Gson().toJson(schemasToCompare));
    }
    
     /*
      *  service info in the form: ex. attributes--Inputoutput--schemaOperation_id--taxonomy--schemaCvp_id--schemaService_id--Schema_id--Selections--Xbrl
      * Absence--input--86--Demand_Supply_Planning--152--76--39--input$Absence--entryDetailComplexType
      */
    protected void getModelsForComparationSchema(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, WSDLException {
               
        MainControlDB mainControlDB = new MainControlDB();

        String source_service_info = request.getParameter("source_service");
        int source_schema_id = Integer.parseInt(source_service_info.split("--")[6]);      
        Schema source_schema = mainControlDB.getSchema(source_schema_id);
        String source_schema_name = source_schema.getName();
        
        String target_service_info = request.getParameter("target_service");
        int target_schema_id = Integer.parseInt(target_service_info.split("--")[6]);      
        Schema target_schema = mainControlDB.getSchema(target_schema_id);
        String target_schema_name = target_schema.getName();
        
            
         JSONObject schemasToCompare = new JSONObject();
         schemasToCompare.put("source_schema", source_schema_id + "_" + source_schema_name);
         schemasToCompare.put("target_schema", target_schema_id + "_" + target_schema_name);
       
         
         response.setContentType("application/json");
         response.setCharacterEncoding("UTF-8");
         response.getWriter().write(new Gson().toJson(schemasToCompare));
    }
    
     /*
      *  service info in the form: ex. attributes--Inputoutput--schemaOperation_id--taxonomy--schemaCvp_id--schemaService_id--Schema_id--Selections--Xbrl
      * Absence--input--86--Demand_Supply_Planning--152--76--39--input$Absence--entryDetailComplexType
      */
    protected void getModelsForComparationSchemaXBRL(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, WSDLException {
               
        MainControlDB mainControlDB = new MainControlDB();
        JSONObject schemasToCompare = new JSONObject();

        String source_schema_id = request.getParameter("schema_id");
        
        if (!source_schema_id.equalsIgnoreCase("null")){
             System.out.println("source_schema_id "+source_schema_id);
        Schema source_schema = mainControlDB.getSchema(Integer.parseInt(source_schema_id));
        String source_schema_name = source_schema.getName();
        System.out.println("source_schema_name "+source_schema_name);
            
         
         schemasToCompare.put("source_schema", source_schema_id + "_" + source_schema_name);
            
            }
       

         response.setContentType("application/json");
         response.setCharacterEncoding("UTF-8");
         response.getWriter().write(new Gson().toJson(schemasToCompare));
    }



    public String prepareSoapBody(String xml,String operation, String complexType) throws TransformerException, ParserConfigurationException, IOException, SAXException {

        System.out.println("prepareSoapBody xml " + xml);
        System.out.println("operation xml " + operation);
        System.out.println("complexType xml " + complexType);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(xml)));

        String   bodychild_name =    doc.getFirstChild().getNodeName();
        String   bodygrandchild_name =   doc.getFirstChild().getFirstChild().getNodeName();

        NamedNodeMap attributes =  doc.getFirstChild().getAttributes();

        List attrtoremove = new ArrayList<String>();

        for (int i = 0; i < attributes.getLength(); i++) {
            attrtoremove.add(attributes.item(i).toString().split("=")[0]);
        }


        Iterator attr = attrtoremove.iterator();
        while(attr.hasNext()) {
            String attName = attr.next().toString();
            ((org.w3c.dom.Element) doc.getFirstChild()).removeAttribute(attName);
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StreamResult result;
        result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);


        String body =  result.getWriter().toString();
        if (bodychild_name.equalsIgnoreCase(operation))
        {
            body =  body.replace("<"+bodychild_name+">","<m:"+bodychild_name+">");
            body =  body.replace("</"+bodychild_name+">","</m:"+bodychild_name+">");
        }
        if (bodygrandchild_name.equalsIgnoreCase(complexType))
        {
            body =  body.replace("<"+bodygrandchild_name+">","<m:"+bodygrandchild_name+">");
            body =  body.replace("</"+bodygrandchild_name+">","</m:"+bodygrandchild_name+">");
        }

          body =  body.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>","");

        System.out.println("final target body: "+body);

              return body;
    }


    protected void getWebServicesInstallations(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, ParserConfigurationException, SAXException, WSDLException {

        OrgDBConnector orgDBConnector = new OrgDBConnector();

        response.setContentType("text/xml; charset=UTF-8");
        String software_id = (String) request.getParameter("software_id");
        String form = request.getParameter("form");
        JSONObject softcomp = new JSONObject();
        JSONObject wservices = new JSONObject();

        LinkedList<Service> services = (LinkedList<Service>) orgDBConnector.getWebServicesInstallations(software_id, (String) session.getAttribute("name"));
        String xml= "";

        for(Service serv:services){
            if(!softcomp.containsKey(serv.getSoftware_id())){
                softcomp.put(serv.getSoftware_id(),serv.getSoftware_name()+" V."+serv.getSoftware_version());
            }
        }

        for(Service serv:services){
            if(!wservices.containsKey(serv.getService_id()))
            {
                wservices.put(serv.getService_id()+"$"+serv.getSoftware_id(),serv.getName()+" V."+serv.getVersion());
            }
        }

        if (form.equalsIgnoreCase("json")){

          xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?><tree id=\"0\">";

            Iterator sc = softcomp.keys();
            while(sc.hasNext()){
                String key = (String)sc.next();
                String value = softcomp.getString(key);
                xml += "<item text='"+value+"' id='SC"+key+"' nocheckbox=\"true\">";

                Iterator ws = wservices.keys();
                while(ws.hasNext()){

                    String wskey = (String)ws.next();
                    int webservice_id= Integer.parseInt(wskey.split("\\$")[0]);
                    String webservice_software_id= wskey.split("\\$")[1].toString();

                    if (webservice_software_id.equalsIgnoreCase(key.toString())){
                        String wsvalue = wservices.getString(wskey);
                        xml +="<item text='"+wsvalue+"' id='WS"+wskey+"'>";

                        for(Service service:services){
                            if(webservice_id==service.getService_id())
                            {
                                xml +="<item text='"+service.getUrl_binding()+"' id='IB"+service.getInstalledbinding_id()+"'></item>";
                            }
                        }
                        xml +="</item>";
                    }

                }
                xml +="</item>";
            }
            xml +="</tree>";


            Map<String, Object> data = new HashMap<String, Object>();
            data.put("tree", xml);

            System.out.println("Auto einai to tree"+data.get("tree"));
            // Write response data as JSON.
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new Gson().toJson(data));


        } else if (form.equalsIgnoreCase("out")) {

            System.out.println("i am in out");

            PrintWriter out = response.getWriter();
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.write("<tree id=\"0\">");

            xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?><tree id=\"0\">";

            Iterator sc = softcomp.keys();
            while(sc.hasNext()){
                String key = (String)sc.next();
                String value = softcomp.getString(key);
                out.write("<item text='"+value+"' id='SC"+key+"' nocheckbox=\"true\" >");
                Iterator ws = wservices.keys();
                while(ws.hasNext()){

                    String wskey = (String)ws.next();
                    int webservice_id= Integer.parseInt(wskey.split("\\$")[0]);
                    String webservice_software_id= wskey.split("\\$")[1].toString();

                    if (webservice_software_id.equalsIgnoreCase(key.toString())){
                        String wsvalue = wservices.getString(wskey);
                        out.write("<item text='"+wsvalue+"' id='WS"+wskey+"'>");
                        for(Service service:services){
                            if(webservice_id==service.getService_id())
                            {
                                out.write("<item text='"+service.getUrl_binding()+"' id='IB"+service.getInstalledbinding_id()+"'></item>");
                            }
                        }
                        out.write("</item>");
                    }

                }
                out.write("</item>");
            }
            out.write("</tree>");
            out.close();
        }
    }

    protected void getCPPsInstallations(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, ParserConfigurationException, SAXException, WSDLException {

        String form = request.getParameter("form");
        OrgDBConnector orgDBConnector = new OrgDBConnector();
        String service_id = request.getParameter("service_id");
        String organization_name = (String) session.getAttribute("name");
        LinkedList<Service> services_installations = (LinkedList<Service>) orgDBConnector.getWebServicesInstallationsByWS(service_id);
        LinkedList<Service> services_cpps = (LinkedList<Service>) orgDBConnector.getCPPByWS(service_id,organization_name);

        if (form.equalsIgnoreCase("json")) {

            String xml = "";
            String wsdlParserString = "";
            String list_installations= "";
            String list_CPP= "<option value=\"-1\">Choose CPP</option>";

            if (services_installations.size() > 0) {
                Iterator serv_iterator = services_installations.iterator();
                while (serv_iterator.hasNext()) {
                    Service service = (Service) serv_iterator.next();
                    //Prepare dropdownlist with installations
                    list_installations += "<option value="+service.getInstalledbinding_id()+">"+service.getUrl_binding()+"</option>";
                }
            }

            if (services_cpps.size() > 0) {
                Iterator serv_iterator = services_cpps.iterator();
                while (serv_iterator.hasNext()) {
                    Service service = (Service) serv_iterator.next();
                    //Prepare dropdownlist with cpps
                    list_CPP += "<option value="+service.getCpp_id()+">"+service.getCpp_name()+"</option>";
                }
            }


            Map<String, Object> data = new HashMap<String, Object>();
            data.put("list_installations", list_installations);
            data.put("list_CPP", list_CPP);
            // Write response data as JSON.
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new Gson().toJson(data));


        }
    }

    /*
    protected void getWebServicesInstallations(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, ParserConfigurationException, SAXException, WSDLException {

        OrgDBConnector orgDBConnector = new OrgDBConnector();

        response.setContentType("text/xml; charset=UTF-8");
        String software_id = (String) request.getParameter("software_id");


        JSONObject softcomp = new JSONObject();
        JSONObject wservices = new JSONObject();

        LinkedList<Service> services = (LinkedList<Service>) orgDBConnector.getWebServicesInstallations(software_id, (String) session.getAttribute("name"));


        for(Service serv:services){
            if(!softcomp.containsKey(serv.getSoftware_id())){
                softcomp.put(serv.getSoftware_id(),serv.getSoftware_name()+" V."+serv.getSoftware_version());
            }

        }

        for(Service serv:services){
            if(!wservices.containsKey(serv.getService_id()))
            {
                wservices.put(serv.getService_id()+"$"+serv.getSoftware_id(),serv.getName()+" V."+serv.getVersion());
            }
        }

        PrintWriter out = response.getWriter();
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.write("<tree id=\"0\">");


        Iterator sc = softcomp.keys();
        while(sc.hasNext()){
            String key = (String)sc.next();
            String value = softcomp.getString(key);
            out.write("<item text='"+value+"' id='"+key+"' nocheckbox=\"true\" >");

            Iterator ws = wservices.keys();
            while(ws.hasNext()){

                String wskey = (String)ws.next();
                int webservice_id= Integer.parseInt(wskey.split("\\$")[0]);
                String webservice_software_id= wskey.split("\\$")[1].toString();

                if (webservice_software_id.equalsIgnoreCase(key.toString())){
                    String wsvalue = wservices.getString(wskey);
                    out.write("<item text='"+wsvalue+"' id='"+wskey+"'>");

                    for(Service service:services){
                        if(webservice_id==service.getService_id())
                        {
                            out.write("<item text='"+service.getCpp_name()+" ID."+service.getCpp_id()+"' id='"+service.getCpp_id()+"' nocheckbox=\"true\" ></item>");

                        }
                    }

                    out.write("</item>");

                }

            }

            out.write("</item>");

        }

        out.write("</tree>");
        out.close();
    } */
    
   

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
