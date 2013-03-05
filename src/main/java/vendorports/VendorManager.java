/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vendorports;

import dataaccesslayer.Operation;
import dataaccesslayer.SoftwareComponent;
import dataaccesslayer.Schema;
import dataaccesslayer.Service;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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
import maincontrol.MainControlDB;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.xml.sax.SAXException;
import xml.WSDLParser;
import xml.XSDParser;

/**
 *
 * @author eleni
 */
public class VendorManager extends HttpServlet {

    private PrintWriter out;
    private static String xml_rep_path;

    public VendorManager() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();
        InputStream in =classLoader.getResourceAsStream("myproperties.properties");
        properties.load(in);

        this.xml_rep_path= properties.getProperty("repo.path").toString();
        if (xml_rep_path.equalsIgnoreCase("/var/www/empower/empowerdata/")){
        //if (xml_rep_path.equalsIgnoreCase("/home/eleni/Documents/ubi/empower/empower-deliverable-september/empower/")){
            String user_home=System.getProperty("user.home");
            this.xml_rep_path = user_home+"/empower/empowerdata";
        }
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
        out = response.getWriter();
        HttpSession session;
        String operation;

        session = request.getSession(true);

        operation = request.getParameter("op");

        try {
            if (operation != null) {
                if (operation.equals("software_reg")) 
                    this.registerSoftware(request, response, session);
                 else if (operation.equals("show_components")) 
                    this.showComponents(request, response, session);
                 else if (operation.equals("schema_reg")) 
                    this.registerSchema(request, response, session);
                 else if (operation.equals("load_software_reg")) 
                    this.loadSoftwareReg(request, response, session);
                 else if (operation.equals("update_software")) 
                    this.updateSoftware(request, response, session);
                 else if (operation.equals("delete_software")) 
                    this.deleteSoftware(request, response, session);
                 else if (operation.equals("service_reg")) 
                    this.registerService(request, response, session); 
                 else if (operation.equals("delete_schema")) 
                    this.deleteSchema(request, response, session); 
                 else if (operation.equals("delete_wservice")) 
                    this.deleteWebService(request, response, session); 
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

    protected void registerSoftware(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session)
            throws IOException, ServletException {
        int software_id;
        String message="";
        String softwareName = request.getParameter("software_name");
        String version = request.getParameter("software_version");

        VendorDBConnector vendorDBConnector = new VendorDBConnector();
        software_id = vendorDBConnector.insertSoftwareInfo((String) session.getAttribute("name"), softwareName, version);
        message = "New Software has been registered.";
        this.forwardToPage("/vendor/succ.jsp?message="+message, request, response);
    }

    protected void updateSoftware(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {
        String softwareID = (String) request.getParameter("software_id");
        String softwareName = (String) request.getParameter("software_name");
        String version = (String) request.getParameter("software_version");

        VendorDBConnector vendorDBConnector = new VendorDBConnector();
        vendorDBConnector.updateSoftware(softwareID, softwareName, version);
        String message = "Software Component has been updated successfully.";
        this.forwardToPage("/vendor/succ.jsp?message="+message, request, response);

        return;
    }

    protected void deleteSoftware(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {
        String softwareID = (String) request.getParameter("software_id");

        VendorDBConnector vendorDBConnector = new VendorDBConnector();
        String message = vendorDBConnector.deleteSoftware(softwareID);
        
        System.out.println("message"+message);

        this.forwardToPage("/vendor/succ.jsp?message="+message, request, response);

        return;
    }
    
     protected void deleteSchema(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {
        String schema_id = (String) request.getParameter("schema_id");

        VendorDBConnector vendorDBConnector = new VendorDBConnector();
        JSONObject schemaDeleteInfo = vendorDBConnector.deleteSchema(schema_id);
        
        String message = schemaDeleteInfo.getString("message");
        schemaDeleteInfo.remove("message");

        this.forwardToPage("/vendor/succDeleteSchema.jsp?message="+message+"&operationsToDelete="+schemaDeleteInfo, request, response);
    }
     
     protected void deleteWebService(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, WSDLException {
        int service_id = Integer.parseInt(request.getParameter("service_id"));

        MainControlDB mainControlDB = new MainControlDB();
        Service service = mainControlDB.getService(service_id);
        
        WSDLParser wsdlParser = new WSDLParser(xml_rep_path+service.getWsdl(), service.getNamespace());

         wsdlParser.loadService(service.getName());
        LinkedList<String> operations =  wsdlParser.returnOperationNames("");
        JSONObject operationsToDelete = new  JSONObject();
        JSONObject xsdSchemasToDelete = new  JSONObject();
        
       Iterator ops;
        
         ops=  operations.iterator();
        
        while(ops.hasNext()) {
         Object element = ops.next();
         operationsToDelete.put(service_id+"_"+element,service_id+"_"+element);
         xsdSchemasToDelete.put(service_id+service.getName()+element+".xsd",xml_rep_path+"/xsd/"+service_id+service.getName()+element+".xsd");
         System.out.println("operationsToDelete: "+service_id+"_"+element);
      }

         VendorDBConnector vendorDBConnector = new VendorDBConnector();
        String message = vendorDBConnector.deleteWebService(service_id,service.getWsdl(),xsdSchemasToDelete);

        System.out.println("message"+message);

        this.forwardToPage("/vendor/succDeleteSchema.jsp?message="+message+"&operationsToDelete="+operationsToDelete, request, response);


        return;
    }
     
     

    /*
     * show software components
     */
    protected void showComponents(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session)
            throws IOException, ServletException {
        Iterator compIterator;
        int rowID = 1;

        VendorDBConnector vendorDBConnector = new VendorDBConnector();
        compIterator = vendorDBConnector.getSoftwareComponents((String) session.getAttribute("name")).iterator();

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("<rows>");

        while (compIterator.hasNext()) {
            SoftwareComponent comp = (SoftwareComponent) compIterator.next();
            out.write("<row id=\"" + (rowID++) + "_" + comp.getSoftwareID()
                    + "_" + comp.getName() + "\">"
                    + "<cell style='font-weight:bold;color: #055A78;'>" + comp.getName()
                    + "</cell><cell>" + comp.getVersion() + "</cell>"
                    //+ "<cell>Delete^../VendorManager?op=delete_software&amp;software_id="+ comp.getSoftwareID()+ "^_self</cell>"
                    + "<cell>Delete Software^javascript:deletesoftcomp("+comp.getSoftwareID()+")^_self</cell>"
                    //+ "<cell>Update^../VendorManager?op=load_software_reg&amp;software_id=" + comp.getSoftwareID()
                    //+ "&amp;software_name=" + comp.getName() + "&amp;software_version=" + comp.getVersion()
                    //+ "^_self</cell>"
                    + "<cell type=\"img\">../js/dhtmlxSuite/dhtmlxTree/codebase/imgs/xsd.png^Schemas^../DIController?op=show_schema&amp;xsd=" + rowID + "_" + comp.getNum_xsds()
                    + "_" + comp.getSoftwareID() + "^_self</cell>" 
                     + "<cell type=\"img\">../js/dhtmlxSuite/dhtmlxTree/codebase/imgs/wsdl.png^Services^../DIController?op=show_service&amp;service=" + rowID + "_" + comp.getNum_services() + 
                      "_" + comp.getSoftwareID() + "^_self</cell>" 
                    + "</row>");
        }

        out.write("</rows>");
        out.flush();
    }

    protected void loadSoftwareReg(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException {
        String softwareID = (String) request.getParameter("software_id");
        String softwareName = (String) request.getParameter("software_name");
        String version = (String) request.getParameter("software_version");

        this.forwardToPage("/vendor/softwareReg.jsp?operation=VendorManager%3Fop=update_software&software_name="
                + softwareName + "&software_version=" + version + "&software_id=" + softwareID, request, response);

        return;
    }

    protected void registerSchema(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, FileUploadException, Exception {
        // Check that we have a file upload request
        String web_service_name = null;
        String new_web_service_name = null;
        String operation_name = null;
        String schemaFilename = null;
        String relative_schemaFilename = null;
        String schemaName = null;
        String inputoutput = null;
        int software_id = 0;
        String namespace = null;
        int schema_id = 0;

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Set factory constraints
        factory.setSizeThreshold(30000);
        factory.setRepository(new File(""));
        ServletFileUpload upload = new ServletFileUpload(factory);

        upload.setSizeMax(30000);

        // Parse the request
        List items = upload.parseRequest(request);

        Iterator iter = items.iterator();

        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();

            if (item.isFormField()) {
                if (item.getFieldName().equals("software_id")) {
                    software_id = Integer.parseInt(item.getString());
                } else if (item.getFieldName().equals("schema_name")) {
                    schemaName = item.getString();
                } else if (item.getFieldName().equals("web_service_name")) {
                    web_service_name = item.getString();
                }else if (item.getFieldName().equals("new_web_service_name")) {
                    new_web_service_name = item.getString();
                }else if (item.getFieldName().equals("operation_name")) {
                    operation_name = item.getString();
                } else if (item.getFieldName().equals("inputoutput")) {
                    inputoutput = item.getString();
                }

            } else {
                relative_schemaFilename =  new String("/xsd/" + software_id + "_" + schemaName + "_" + ((int) (100000 * Math.random())) + ".xsd");
                schemaFilename = this.xml_rep_path+relative_schemaFilename;
                System.out.println("schemaFilename: " + schemaFilename);
                File uploadedFile = new File(schemaFilename);
                item.write(uploadedFile);
            }
        }

        VendorDBConnector vendorDBConnector = new VendorDBConnector();
  
        schema_id = vendorDBConnector.insertSchemaInfo(software_id, schemaName, relative_schemaFilename, xml_rep_path, new_web_service_name , web_service_name, operation_name, inputoutput);
        
        String message = "";

        if (schema_id==-1) message= "The schema has NOT been registered successfully.";
        else message= "The schema has been registered successfully. You can find the registered xsd at "+this.xml_rep_path+"/xsd for the user.name: " +System.getProperty("user.name");


        this.forwardToPage("/vendor/succImportSchema.jsp?message="+message+"&schema_id="+schema_id, request, response);
    }
    
     protected void registerService(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, FileUploadException, Exception {
        // Check that we have a file upload request

        String service_name = null; 
        String serviceFilename = null;
        String relative_serviceFilename=null;
        String service_namespace = null;
        String service_version = null;
        int sourceOfWSDL =  0;    // 0: no-source , 1: wsdl from file , 2: wsdl from url
        int software_id = 0;
        int service_id = 0;
        String message="";

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Set factory constraints
        factory.setSizeThreshold(30000);
        factory.setRepository(new File(""));
        ServletFileUpload upload = new ServletFileUpload(factory);

        upload.setSizeMax(30000);

        // Parse the request
        List items = upload.parseRequest(request);

        Iterator iter = items.iterator();

        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();

            if (item.isFormField()) {

             if (item.getFieldName().equals("software_id")) {
                    software_id = Integer.parseInt(item.getString());
                } else if (item.getFieldName().equals("service_name")) {
                    service_name = item.getString();
                } else if (item.getFieldName().equals("service_namespace")) {
                    service_namespace = item.getString();
                } else if (item.getFieldName().equals("service_version")) {
                 service_version = item.getString();
                }else if (item.getFieldName().equals("from") && item.getString().equalsIgnoreCase("FromUrlPath")) {
                 sourceOfWSDL =2;
                }else if (item.getFieldName().equals("from") && item.getString().equalsIgnoreCase("service_wsdl_fromFilePath")) {
                sourceOfWSDL =1;
                }else if (item.getFieldName().equals("FromUrlPath") && sourceOfWSDL==2) {

                 relative_serviceFilename =  new String("/wsdl/" + software_id + "_" + service_name + "_" + ((int) (100000 * Math.random())) + ".wsdl");
                 serviceFilename = this.xml_rep_path + relative_serviceFilename;

                 File uploadedFile = new File(serviceFilename);
                 FileWriter fileWriter = new FileWriter(uploadedFile);
                 URL wsdlurl = new URL(item.getString());
                 BufferedReader in = new BufferedReader(new InputStreamReader(wsdlurl.openStream()));

                 String inputLine;
                 while ((inputLine = in.readLine()) != null) {
                     fileWriter.write(inputLine+System.getProperty( "line.separator" ));
                 }
                 in.close();
                 fileWriter.close();

                }
            } else if(!item.isFormField() && sourceOfWSDL==1 ) {

                relative_serviceFilename =  new String( "/wsdl/" + software_id + "_" + service_name + "_" + ((int) (100000 * Math.random())) + ".wsdl");
                serviceFilename =    this.xml_rep_path +  relative_serviceFilename;
                File uploadedFile = new File(serviceFilename);
                item.write(uploadedFile);

            }
        }


        VendorDBConnector vendorDBConnector = new VendorDBConnector();

        service_id = vendorDBConnector.insertServiceInfo(software_id, service_name ,service_version,relative_serviceFilename, xml_rep_path,service_namespace);

        if (service_id==-1)  message= "Service has NOT been succesfully registered. Please check that the service name and the namespace are the same with the ones in the .wsdl file.";
        else message= "Service has been succesfully registered. You can find the registered wsdl at "+this.xml_rep_path+"/wsdl for the user.name: " +System.getProperty("user.name");

        this.forwardToPage("/vendor/succ.jsp?message="+message, request, response);

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
