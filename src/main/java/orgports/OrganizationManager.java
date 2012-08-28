/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orgports;

import com.google.gson.Gson;
import dataaccesslayer.Operation;
import dataaccesslayer.Schema;
import dataaccesslayer.SoftwareComponent;
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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.xml.sax.SAXException;
import xml.XSDParser;

/**
 *
 * @author eleni
 */
public class OrganizationManager extends HttpServlet {

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
                } else if (operation.equals("showPossibleTargets")) {
                    this.showPossibleTargets(request, response, session);
                } else if (operation.equals("createBridging")) {
                    this.createBridging(request, response, session);
                } else if (operation.equals("doBridging")) {
                    this.doBridging(request, response, session);
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
        //String vendorName = request.getParameter("software_name");
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
                    + "<cell>" + comp.getVersion() + "</cell>"
                    + "<cell>Schemas^../DIController?op=show_bridging_schemas&amp;software_id=" + comp.getSoftwareID() + "^_self</cell></row>");
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
        String xml_string = "";

        OrgDBConnector orgDBConnector = new OrgDBConnector();

        LinkedList<Schema> schemas = (LinkedList<Schema>) orgDBConnector.getTargetSchemas(inputoutput, taxonomy_id);

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

    protected void createBridging(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {

        int cpa_id = -1;
        String selections_source = request.getParameter("selections_source");
        String cvp_source = selections_source.split("--")[4];

        System.out.println("cvp_source: " + cvp_source);

        String selections_target = request.getParameter("selections_target");
        String cvp_target = selections_target.split("--")[4];

        System.out.println("cvp_target: " + cvp_target);

        OrgDBConnector orgDBConnector = new OrgDBConnector();

        String organization_name = (String) session.getAttribute("name");

        System.out.println("organization_name: " + organization_name);

        Map<String, Integer> data = orgDBConnector.insertBridging(Integer.parseInt(cvp_source), Integer.parseInt(cvp_target), organization_name);


        if (data.containsKey("new_cpa_id")) {
            session.setAttribute("message", "New bridging was created!");
            System.out.println("New bridging was created");
            cpa_id = data.get("new_cpa_id");
        } else {
            session.setAttribute("message", "Bridging already existed!");
            System.out.println("Bridging already existed!");
            cpa_id = data.get("existing");
        };

        this.forwardToPage("/organization/succ.jsp?cpa_id=" + cpa_id, request, response);

    }

    protected void doBridging(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, FileUploadException {
        
        int  cpa_id =Integer.parseInt(request.getParameter("cpa_id"));
        String data= "";
        
        
        OrgDBConnector orgDBConnector = new OrgDBConnector();
        String cpainfo = orgDBConnector.getinfocpa(cpa_id);
        
        int cvp_a = Integer.parseInt(cpainfo.split("--")[0]);
        int cvp_b = Integer.parseInt(cpainfo.split("--")[1]);
        
        System.out.println("cpainfo: "+ cpainfo);
        
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
               System.out.println("data: "+item.getString());
              data = item.getString();
            }
        }
        
        
        //return(this.transform(90,93,data));
        String target_xml = this.transform(cvp_a,cvp_b,data).toString();
        
         
        session.setAttribute("source_xml", data.toString());
        session.setAttribute("target_xml", target_xml);
       
      
        this.forwardToPage("/organization/showBridging.jsp", request, response);

    }

    private String transform(int cvp_a, int cvp_b, String xmlData) {

        OrgDBConnector orgDBConnector = new OrgDBConnector();
        
        String xsltRulesFirst = orgDBConnector.retrieveXLST(cvp_a);//input
        String xsltRulesSecond = orgDBConnector.retrieveXLST(cvp_b);//output
       

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

    private void forwardToPage(String url, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        RequestDispatcher dis;

        dis = getServletContext().getRequestDispatcher(url);
        dis.forward(req, resp);
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
