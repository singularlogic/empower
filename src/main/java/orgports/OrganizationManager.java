/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orgports;

import com.google.gson.Gson;
import dataaccesslayer.*;
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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
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
import org.dom4j.io.SAXReader;
import java.net.URL;
import org.xml.sax.SAXException;
import xml.WSDLParser;
import xml.XSDParser;

/**
 *
 * @author eleni
 */
public class OrganizationManager extends HttpServlet {

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
                } else if (operation.equals("doBridgingService")) {
                    this.doBridgingService(request, response, session);
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
                    + "<cell>" + comp.getName() + "^../DIController?op=show_bridging_schemas&amp;software_id=" + comp.getSoftwareID() + "^_self</cell>"
                    + "<cell>" + comp.getVersion() + "</cell>");

            if (bridging.equalsIgnoreCase("true")) {
                out.write("<cell type=\"img\">../js/dhtmlxSuite/dhtmlxTree/codebase/imgs/xsd.png^Schema Bridging^../DIController?op=show_bridging_schemas&amp;software_id=" + comp.getSoftwareID() + "^_self</cell>");
                out.write("<cell type=\"img\">../js/dhtmlxSuite/dhtmlxTree/codebase/imgs/wsdl.png^Service Bridging^../DIController?op=show_bridging_services&amp;software_id=" + comp.getSoftwareID() + "^_self</cell>");



            } else {
                out.write("<cell>Schemas^../DIController?op=show_schema&amp;xsd=1_1_" + comp.getSoftwareID() + "^_self</cell>"
                        + "<cell>Services^../DIController?op=show_service&amp;service=1_1_" + comp.getSoftwareID() + "^_self</cell>");
            }
            out.write(" </row>");
        }

        out.write("</rows>");
        out.flush();

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

            System.out.println("Schema: " + schema.getService() + " " + schema.getOperation_id() + " " + schema.getOperation() + " " + schema.getInputoutput() + " " + schema.getSchema_id() + " " + schema.getName() + " " + schema.getCvp_id() + " " + schema.getSchema_id());

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

            System.out.println("Schema: " + schema.getService() + " " + schema.getOperation_id() + " " + schema.getOperation() + " " + schema.getInputoutput() + " " + schema.getSchema_id() + " " + schema.getName() + " " + schema.getSchema_id());

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
        System.out.println("form: " + form);
        OrgDBConnector orgDBConnector = new OrgDBConnector();
        String tax = request.getParameter("tax");
        String software_id = request.getParameter("software_id");
        LinkedList<Service> services = (LinkedList<Service>) orgDBConnector.getServicesByTaxonomy(tax, software_id);
        System.out.println("services: " + services + "lenght: " + services.size());


        if (form.equalsIgnoreCase("json")) {

            //String selections = request.getParameter("selections");
            //String service_id = selections.split("\\$")[0];
            //String operation_name = selections.split("\\$")[1];
            String xml = "";
            String wsdlParserString = "";

            if (services.size() > 0) {
                Iterator serv_iterator = services.iterator();
                while (serv_iterator.hasNext()) {
                    Service service = (Service) serv_iterator.next();
                    System.out.println("My service id:" + service.getService_id() + " service_name: " + service.getName() + " wsdl: " + service.getWsdl() + " " + service.getNamespace());
                    WSDLParser wsdlParser = new WSDLParser(service.getWsdl(), service.getNamespace());
                    wsdlParser.loadService(service.getName());
                    wsdlParserString = wsdlParserString.concat(wsdlParser.outputFunctionsToXMLFromRoot(xml, service.getName(), service.getService_id()));
                }
            }

            xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<tree id=\"0\">" + wsdlParserString + "</tree>";

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("tree", xml);
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

    protected void createBridging(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {

        OrgDBConnector orgDBConnector = new OrgDBConnector();

        int cpa_id = -1;
        String selections_source = request.getParameter("selections_source");
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

        int cpa_id = -1;
        String selections_source = request.getParameter("selections_source");
        int service_id_source = Integer.parseInt(selections_source.split("\\$")[0]);
        String operation_name_source = (String) selections_source.split("\\$")[1];

        int cpp_source = orgDBConnector.getCPP(service_id_source);
        System.out.println("cpp_source: " + cpp_source);


        String selections_target = request.getParameter("selections_target");
        int service_id_target = Integer.parseInt(selections_target.split("\\$")[0]);
        String operation_name_target = (String) selections_target.split("\\$")[1];

        int cpp_target = orgDBConnector.getCPP(service_id_target);
        System.out.println("cpp_target: " + cpp_target);

        String organization_name = (String) session.getAttribute("name");

        Map<String, CPP> CPPList = new HashMap<String, CPP>();


        CPP cppinfo_first = new CPP(cpp_source, service_id_source, operation_name_source);


        CPP cppinfo_second = new CPP(cpp_target, service_id_target, operation_name_target);


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
                System.out.println("data: " + item.getString());
                data = item.getString();
            }
        }

        String target_xml = this.transform(cpp_a, cpp_b, data, cpainfo.getCpa_info(), new LinkedList<String>()).toString();
        session.setAttribute("source_xml", data.toString());
        session.setAttribute("target_xml", target_xml);
        this.forwardToPage("/organization/showBridging.jsp", request, response);

    }

    private String transform(int cpp_a, int cpp_b, String xmlData, String cpa_info, LinkedList<String> service_selections) {


        OrgDBConnector orgDBConnector = new OrgDBConnector();

        String xsltRulesFirst = orgDBConnector.retrieveXLST(cpp_a, "input", cpa_info, service_selections);//input
        String xsltRulesSecond = orgDBConnector.retrieveXLST(cpp_b, "output", cpa_info, service_selections);//output

        System.out.println("CHECK " + xsltRulesFirst);
        System.out.println("CHECK " + xsltRulesSecond);

        StringWriter stw = new StringWriter();
        StringWriter stwRes = new StringWriter();
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer;

        try {
            transformer = tFactory.newTransformer(new StreamSource(new StringReader(xsltRulesFirst)));
            transformer.transform(new StreamSource(new StringReader(xmlData)), new StreamResult(stw));

            System.out.println("UpcastingXML: " + stw.toString());

            tFactory = TransformerFactory.newInstance();
            transformer = tFactory.newTransformer(new StreamSource(new StringReader(xsltRulesSecond)));
            transformer.transform(new StreamSource(new StringReader(stw.toString())), new StreamResult(stwRes));

            System.out.println("Hola" + stwRes.toString() + "Adios" + stw.toString());


        } catch (Throwable t) {
            //t.printStackTrace();
            System.out.println("Hola" + t);
        }
        return stwRes.toString();
    }

    protected void manageBridgingServices(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, ParserConfigurationException, SAXException, WSDLException {

        MainControlDB mainControlDB = new MainControlDB();

        response.setContentType("text/xml; charset=UTF-8");
        String software_id = (String) request.getParameter("software_id");
        System.out.println("My software_id " + software_id);


        LinkedList<Service> services = (LinkedList<Service>) mainControlDB.getServices(software_id, true, "IS NOT NULL");

        System.out.println("services: " + services + "lenght: " + services.size());
        if (services.size() > 0) {
            PrintWriter out = response.getWriter();
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.write("<tree id=\"0\">");
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

            out.write("</tree>");
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
            JSONObject o = new JSONObject();
            o = (JSONObject) JSONSerializer.toJSON(cpa.getCpa_info());
            JSONObject o_first = (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_first"));
            JSONObject o_second = (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_second"));

            System.out.println("o_first.get(schema)::" + o_first.get("schema"));

            additional_schema_info = (o_first.get("schema") == null) ? "" : "<row id=\"" + cpa.getCpa_id() + "3\">"
                    + "<cell>Schema:</cell>"
                    + "<cell>" + o_first.get("schema")
                    + "</cell>"
                    + "<cell>" + o_second.get("schema")
                    + "</cell>"
                    + "<cell> </cell>"
                    + "</row>"
                    + "<row id=\"" + cpa.getCpa_id() + "\">"
                    + "<cell>Element:</cell>"
                    + "<cell>" + o_first.get("schema_complexType")
                    + "</cell>"
                    + " <cell>" + o_second.get("schema_complexType")
                    + "</cell>"
                    + "<cell></cell>"
                    + "</row>";

            dobridging_url = (o_first.get("schema") == null) ? "<cell> Do Brindging^../DIController?op=doBridgingServicePrepare&amp;cpa_id=" + cpa.getCpa_id() + "^_self</cell>"
                    : "<cell> Do Brindging^../DIController?op=doBridging&amp;cpa_id=" + cpa.getCpa_id() + "^_self</cell>";



            out.write("<row id=\"" + cpa.getCpa_id() + "1\">"
                    + "<cell> Web Service:</cell>"
                    + "<cell>" + o_first.get("service")
                    + "</cell>"
                    + "<cell>" + o_second.get("service")
                    + "</cell>"
                    + "<cell> </cell>"
                    + "</row>"
                    + "<row id=\"" + cpa.getCpa_id() + "2\">"
                    + "<cell>Operation:</cell>"
                    + "<cell>" + o_first.get("operation")
                    + "</cell>"
                    + "<cell>" + o_second.get("operation")
                    + "</cell>"
                    + "<cell></cell>"
                    + "</row>"
                    + additional_schema_info
                    + "<row id=\"" + cpa.getCpa_id() + "4\">"
                    + "<cell></cell>"
                    + "<cell></cell>"
                    + "<cell></cell>"
                    //+ "<cell> Do Brindging^../DIController?op=doBridging&amp;cpa_id=" + cpa.getCpa_id() + "^_self</cell>"
                    + dobridging_url
                    + "</row>");

        }

        out.write("</rows>");
        out.flush();
    }

    protected void doBridgingService(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException, WSDLException, FileNotFoundException, ParserConfigurationException, SAXException, XPathExpressionException, FileUploadException, DocumentException {

        String xml_input="";
        int cpa_id = Integer.parseInt(request.getParameter("cpa_id"));

        JSONObject inputargs = (JSONObject) session.getAttribute("serviceInputArgs");

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Set factory constraints
        factory.setSizeThreshold(30000);
        factory.setRepository(new File("/home/eleni/Desktop/"));
        ServletFileUpload upload = new ServletFileUpload(factory);

        upload.setSizeMax(30000);

        // Parse the request
        List items = upload.parseRequest(request);

        // fill the json object with the values of the request

        Iterator iter = items.iterator();

        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();

            if (item.isFormField()) {

                if (inputargs.containsKey(item.getFieldName())) {
                    inputargs.put(item.getFieldName(), item.getString());
                }
            } else {
                /*
                 * System.out.println("Here we put the file ");
                 *
                 * schemaFilename = new String(this.xml_rep_path + "/xsd/" +
                 * software_id + "_" + schemaName + "_" + ((int) (100000 *
                 * Math.random())) + ".xsd");
                 * System.out.println("schemaFilename: " + schemaFilename); File
                 * uploadedFile = new File(schemaFilename);
                 * item.write(uploadedFile);
                *
                 */
            }
        }

        //print the key value pairs 
        for (Object o : inputargs.keySet()) {
            String key = (String) o;
            String value = inputargs.getString(key);
            xml_input += "<"+key+">"+value+"</"+key+">";
            System.out.println("key " + key + " value: " + value);
        }

        OrgDBConnector orgDBConnector = new OrgDBConnector();
        MainControlDB mainControlDB = new MainControlDB();

        CPA cpa = orgDBConnector.getCPA(cpa_id);

        String cpa_info = cpa.getCpa_info();

        JSONObject o = new JSONObject();
        o = (JSONObject) JSONSerializer.toJSON(cpa_info);
        JSONObject info_first = (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_first"));

        int service_id = Integer.parseInt(info_first.get("service_id").toString());
        String source_operation_name = (String) info_first.get("operation");
        System.out.println("source_operation_name: " + source_operation_name);
        Service service = mainControlDB.getService(service_id);
        WSDLParser wsdlParser = new WSDLParser(service.getWsdl(), service.getNamespace());
        wsdlParser.loadService(service.getName());
        String SoapAdressURL = wsdlParser.getSoapAdressURL(service.getName());
        LinkedList<String> complexType = wsdlParser.getXSDAttibutes_Eleni(source_operation_name);
        String complexType_input = complexType.get(0).split("\\$")[1];
        String complexType_output = complexType.get(1).split("\\$")[1];
        String xsdTypes = wsdlParser.extractXSD(complexType_input);
        System.out.println("SoapAdressURL: " + SoapAdressURL);

        
         String xml_input_to_print = "<"+complexType_input+">"+xml_input+"</"+complexType_input+">";
         
         System.out.println("xml_input_to_print"+xml_input_to_print);
             
                     
        JSONObject info_second = (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_second"));
        int service_id_second = Integer.parseInt(info_second.get("service_id").toString());
        String target_operation_name = (String) info_second.get("operation");
        System.out.println("target_operation_name: " + target_operation_name);
        Service service_second = mainControlDB.getService(service_id_second);
        WSDLParser wsdlParser_second = new WSDLParser(service_second.getWsdl(), service_second.getNamespace());
        wsdlParser_second.loadService(service_second.getName());
        String target_SoapAdressURL = wsdlParser_second.getSoapAdressURL(service_second.getName());
        LinkedList<String> complexType_second = wsdlParser_second.getXSDAttibutes_Eleni(target_operation_name);

        complexType.addAll(complexType_second);


        String complexType_second_input = complexType_second.get(0).split("\\$")[1];
        String complexType_second_output = complexType_second.get(1).split("\\$")[1];
        String target_xsdTypes = wsdlParser_second.extractXSD(complexType_second_input);
        System.out.println("target_SoapAdressURL: " + target_SoapAdressURL);


        SOAPEnvelopeInvoker soapEnvelopeInvoker = new SOAPEnvelopeInvoker(SoapAdressURL, source_operation_name, complexType_input, complexType_output, inputargs, service.getNamespace());


        JSONObject SOAPEnvelopeInvokerResponse = soapEnvelopeInvoker.callWebService();



        CPA cpainfo = orgDBConnector.getinfocpa(cpa_id);

        int cpp_a = cpainfo.getCpp_id_first();
        int cpp_b = cpainfo.getCpp_id_second();


        String target_xml = this.transform(cpp_a, cpp_b, SOAPEnvelopeInvokerResponse.getString("outputXML"), cpainfo.getCpa_info(), complexType).toString();



        SAXReader reader = new SAXReader();
        InputStream txml = new ByteArrayInputStream(target_xml.getBytes());
        Document document = reader.read(txml);

        System.out.println("Here i ammmmmmmmmm: " + document.getText());

        Element root = document.getRootElement();
        inputargs.clear();

        // iterate through child elements of root
        for (Iterator i = root.elementIterator(); i.hasNext();) {
            Element element = (Element) i.next();
            inputargs.put(element.getName(), element.getText());

            System.out.println("Elements of Return XML: " + element.getName() + element.getText());
        }


        SOAPEnvelopeInvoker targetsoapEnvelopeInvoker = new SOAPEnvelopeInvoker(target_SoapAdressURL, target_operation_name, complexType_second_input, complexType_second_output, inputargs, service_second.getNamespace());

        JSONObject targetsoapEnvelopeInvokerResponse = targetsoapEnvelopeInvoker.callWebService();
        
        session.setAttribute("source_xml", xml_input_to_print);
        session.setAttribute("target_xml", targetsoapEnvelopeInvokerResponse.getString("outputXML"));
        this.forwardToPage("/organization/showBridging.jsp", request, response);

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
