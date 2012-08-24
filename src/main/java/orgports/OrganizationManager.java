/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orgports;

import com.google.gson.Gson;
import dataaccesslayer.Operation;
import dataaccesslayer.Schema;
import dataaccesslayer.SoftwareComponent;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.json.JSONObject;
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
               if (operation.equals("show_components")) 
                    this.showComponents(request, response, session);
                else  if (operation.equals("show_bridging_schemas")) 
                    this.manageBridgingSchemas(request, response, session);
               else  if (operation.equals("showPossibleTargets")) 
                    this.showPossibleTargets(request, response, session);
                       
                       

            }
        }catch(Throwable t)
        {
            t.printStackTrace();
            this.forwardToPage("/error/generic_error.jsp?errormsg=GenericError ", request, response);            
        }
        finally {            
            out.close();
        }
    }
    
    
     protected void showComponents(HttpServletRequest request,HttpServletResponse response,HttpSession session)
    throws IOException, ServletException
    {
        int rowID = 1;
        //String vendorName = request.getParameter("software_name");
        OrgDBConnector orgDBConnector = new OrgDBConnector();
        Iterator compIterator = (orgDBConnector.getSoftwareComponents()).iterator();

        response.setContentType("text/xml; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.write("<rows>");
        
        while(compIterator.hasNext())
        {
            SoftwareComponent comp = (SoftwareComponent) compIterator.next();
            out.write("<row id=\"" + comp.getSoftwareID() + "\">" 
                    + "<cell>" + comp.getSoftwareID() +"</cell>"
                    + "<cell>" + comp.getName() + "^../DIController?op=show_bridging_schemas&amp;software_id=" +  comp.getSoftwareID() + "^_self</cell>"
                    + "<cell>" + comp.getVersion() + "</cell>"
                    + "<cell>Schemas^../DIController?op=show_bridging_schemas&amp;software_id=" +  comp.getSoftwareID() + "^_self</cell></row>");        
        }
        
        out.write("</rows>");
        out.flush();
        
    //    this.forwardToPage("/organizationmenu.jsp", request, response);
    }  
     protected void manageBridgingSchemas(HttpServletRequest request,HttpServletResponse response,HttpSession session)
    throws IOException, ServletException, ParserConfigurationException, SAXException
    {
        String software_id = (String) request.getParameter("software_id");
        String xml_string ="";
        int service_id = -1;
        
        OrgDBConnector orgDBConnector = new OrgDBConnector();
        
        LinkedList<Schema> schemas = (LinkedList<Schema>) orgDBConnector.getSchemas(software_id);
        
         Iterator<Schema> schemas_it = schemas.iterator();
            while (schemas_it.hasNext()) {

                Schema schema = schemas_it.next();
               
                System.out.println("Schema: "+ schema.getService() + " " + schema.getOperation_id() + " " + schema.getOperation() + " " + schema.getInputoutput()+ " " +schema.getSchema_id() + " " + schema.getName() );
                
                XSDParser p = new XSDParser(schema);
                xml_string += p.convertSchemaToXML();
            }
            
           
           
           xml_string = xml_string.replace("<tree id='0'>", "").replace("</tree>", "");
           xml_string = "<tree id='0'>" + xml_string + "</tree>";
                      
           response.setContentType("text/xml; charset=UTF-8");
           PrintWriter out = response.getWriter();
           out.write(xml_string);    
       
        
    }
     
     protected void showPossibleTargets(HttpServletRequest request,HttpServletResponse response,HttpSession session)
    throws IOException, ServletException, ParserConfigurationException, SAXException{

    String selections = request.getParameter("selections");
    String inputoutput = selections.split("--")[1];
    String taxonomy_id = selections.split("--")[3];
    String xml_string = "";
    
     OrgDBConnector orgDBConnector = new OrgDBConnector();
        
     LinkedList<Schema> schemas = (LinkedList<Schema>) orgDBConnector.getTargetSchemas(inputoutput,taxonomy_id);
     
      Iterator<Schema> schemas_it = schemas.iterator();
            while (schemas_it.hasNext()) {

                Schema schema = schemas_it.next();
               
                System.out.println("Schema: "+ schema.getService() + " " + schema.getOperation_id() + " " + schema.getOperation() + " " + schema.getInputoutput()+ " " +schema.getSchema_id() + " " + schema.getName() );
                
                XSDParser p = new XSDParser(schema);
                xml_string += p.convertSchemaToXML();
            }
            
           
           
           xml_string = xml_string.replace("<tree id='0'>", "").replace("</tree>", "");
           xml_string = "<tree id='0'>" + xml_string + "</tree>";
                      
             
    
    //List<Product> products = productDAO.list(); // Product is just a Javabean with properties `id`, `name` and `description`.
    
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("selections", request.getParameter("selections"));
    data.put("tree1","<tree id='0'><item text='DI-Regnskap ws1' id='DI-Regnskap ws1' nocheckbox='true'><item text='DI-Regnskap ws1.op1' id='DI-Regnskap ws1.op1' nocheckbox='true'><item text='source_DI-Regnskap ws1.op1' id='source_DI-Regnskap ws1.op1' nocheckbox='true'><item text='Absence(input)' id='Absence--input' ></item><item text='AbsenceType(input)' id='AbsenceType--input' ></item></item></item></item></tree>");
    data.put("tree", xml_string);
    
    // Write response data as JSON.
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(new Gson().toJson(data));


    }
    
     private void forwardToPage(String url, HttpServletRequest req, HttpServletResponse resp)
    throws IOException, ServletException
    {
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
