/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vendorports;

import dataaccesslayer.SoftwareComponent;
import dataaccesslayer.XSD;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.text.Document;
import javax.xml.parsers.SAXParser;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.NodeList;

/**
 *
 * @author eleni
 */
public class VendorManager extends HttpServlet {

    private PrintWriter out;
    private boolean isInputMessageType = false;
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
                 else if (operation.equals("xsd_reg")) 
                    this.registerXSD(request, response, session);
                 else if (operation.equals("show_xsds")) 
                    this.showXSDs(request, response, session);
                 else if(operation.equals("load_software_reg"))
                    this.loadSoftwareReg(request, response, session); 
                else if(operation.equals("update_software"))
                     this.updateSoftware(request, response, session);
                else if(operation.equals("delete_software"))
                     this.deleteSoftware(request, response, session);
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
        String softwareName = request.getParameter("software_name");
        String version = request.getParameter("software_version");

        VendorDBConnector vendorDBConnector = new VendorDBConnector();
        software_id = vendorDBConnector.insertSoftwareInfo((String) session.getAttribute("name"), softwareName, version);
        this.forwardToPage("/vendor/succ.jsp", request, response);
    }
    
    protected void updateSoftware(HttpServletRequest request,HttpServletResponse response,HttpSession session)
    throws IOException, ServletException
    {
        String softwareID = (String)request.getParameter("software_id");
        String softwareName = (String)request.getParameter("software_name");
        String version = (String)request.getParameter("software_version");
        
        VendorDBConnector vendorDBConnector = new VendorDBConnector();
        vendorDBConnector.updateSoftware(softwareID, softwareName, version);
        
        this.forwardToPage("/vendor/succ.jsp", request, response);
        
        return;
    } 
    
        protected void deleteSoftware(HttpServletRequest request,HttpServletResponse response,HttpSession session)
    throws IOException, ServletException
    {
        String softwareID = (String)request.getParameter("software_id");

        VendorDBConnector vendorDBConnector = new VendorDBConnector();
        vendorDBConnector.deleteSoftware(softwareID);
        
        this.forwardToPage("/vendor/succ.jsp", request, response);
        
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
                    + "_" + comp.getName() + "\"><cell>" + comp.getName()
                    + "</cell><cell>" + comp.getVersion() + "</cell>"
                    + "<cell>Delete^../VendorManager?op=delete_software&amp;software_id="
                    + comp.getSoftwareID()
                    + "^_self</cell><cell>Update^../VendorManager?op=load_software_reg&amp;software_id=" + comp.getSoftwareID()
                    + "&amp;software_name=" + comp.getName() + "&amp;software_version=" + comp.getVersion()
                    + "^_self</cell>"
                    + "<cell>XSDs^../DIController?op=show_xsd&amp;xsd=" + rowID + "_" + comp.getNum_xsds()
                    + "_" + comp.getSoftwareID() + "^_self</cell>" + "</row>");
        }

        out.write("</rows>");
        out.flush();
    }
    
    
     protected void loadSoftwareReg(HttpServletRequest request,HttpServletResponse response,HttpSession session)
    throws IOException, ServletException
    {
        String softwareID = (String)request.getParameter("software_id");
        String softwareName = (String)request.getParameter("software_name");
        String version = (String)request.getParameter("software_version");
        
        this.forwardToPage("/vendor/softwareReg.jsp?operation=VendorManager%3Fop=update_software&software_name=" +
                           softwareName + "&software_version=" + version + "&software_id=" + softwareID, request, response);
        
        return;
    } 

    protected void registerXSD(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException, ServletException, FileUploadException, Exception {
        // Check that we have a file upload request

        String XSDFilename = null;
        String XSDName = null;
        int software_id = 0;
        String namespace = null;
        int xsd_id = 0;

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

        Iterator iter = items.iterator();

        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();

            if (item.isFormField()) {
                if (item.getFieldName().equals("software_id")) {
                    software_id = Integer.parseInt(item.getString());
                } else if (item.getFieldName().equals("xsd_name")) {
                    XSDName = new String(item.getString());
                }
            } else {
                XSDFilename = new String(this.xml_rep_path + "/xsd/" + software_id + "_" + XSDName + "_" + ((int) (100000 * Math.random())) + ".xsd");
                File uploadedFile = new File(XSDFilename);
                item.write(uploadedFile);
            }
        }

        VendorDBConnector vendorDBConnector = new VendorDBConnector();
        xsd_id = vendorDBConnector.insertXSDInfo(software_id, XSDName, XSDFilename, xml_rep_path);



        this.forwardToPage("/vendor/succ.jsp", request, response);
    }

    protected void showXSDs(HttpServletRequest request,HttpServletResponse response,HttpSession session)
            throws IOException, ServletException {
        Iterator XSDIterator;
        int rowID = 1;

        VendorDBConnector vendorDBConnector = new VendorDBConnector();
        XSDIterator = vendorDBConnector.getXSDs((String) request.getParameter("software_id")).iterator();

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("<rows>");

        while (XSDIterator.hasNext()) {
            XSD xsd = (XSD) XSDIterator.next();
            out.write("<row id=\"" + xsd.getXsd_id() + "\"><cell>" + xsd.getName() + "</cell>"
                    +"<cell> Annotate Data of XSD^./vendor/presentXSDTree.jsp?xsd_id=" + xsd.getXsd_id()+"^_self</cell>"
                    + "<cell>Annotate Data^./vendor/presentServiceTree.jsp?service_id=" + xsd.getXsd_id()
                    + "^_self</cell>"
                    + "<cell>Annotate Functions^./vendor/presentServiceFunctionTree.jsp?service_id=" + xsd.getXsd_id()
                    + "^_self</cell>"
                    + "</row>");
        }

        out.write("</rows>");
        out.flush();

        return;
    }
    
      protected void presentServiceTree(HttpServletRequest request,HttpServletResponse response, HttpSession session)
    throws IOException, ServletException
    {
        /*
        int serviceID = Integer.parseInt(request.getParameter("service_id"));
        
        VendorDBConnector vendorDBConnector = new VendorDBConnector();
        Service service = vendorDBConnector.getService(serviceID);
        response.setContentType("text/xml; charset=UTF-8");
        
        WSDLParser wsdlParser = new WSDLParser(service.getWsdl(),
                                               service.getNamespace());
        wsdlParser.loadService(service.getName());
        wsdlParser.outputToXML(response.getWriter());
        
       SAXParser saxparser=  new SAXParser();
        */


        //this.forwardToPage("/vendormenu.jsp", request, response);
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
