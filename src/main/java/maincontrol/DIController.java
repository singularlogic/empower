/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maincontrol;

import dataaccesslayer.Service;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sf.json.JSONObject;
import vendorports.VendorDBConnector;

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
        try {
            operation = request.getParameter("op");

            System.out.println("operation" + operation);

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
                } 
                //else
                //  this.forwardToPage("/error/generic_error.jsp?errormsg=Cannot find op", request, response);
            }
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
            System.out.println("giati mpaino mesa?");

            JSONObject json_services = new JSONObject();
            Iterator serv_iterator = services.iterator();
            while (serv_iterator.hasNext()) {
                Service serv = (Service) serv_iterator.next();
                json_services.put(serv.getService_id(), serv.getName());
            }
            session.setAttribute("services", json_services);
        } else {
            System.out.println("eimai ekso");
            session.setAttribute("services", "");
        }

        if (verifyUser("vendor", session)) {
            // if software component without an xsd
            if (xsd_num.equals("0")) {
                this.forwardToPage("/vendor/SchemaReg.jsp?software_id=" + software_id + "&jsp=false", request, response);
            } else {
                this.forwardToPage("/vendor/showSchemas.jsp?software_id=" + software_id, request, response);
            }
        }

        if (verifyUser("organization", session)) {
            //this.forwardToPage("/organization/registerBinding.jsp?service_id=" + serviceID, request, response);
        }
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

        VendorDBConnector vendorDBConnector = new VendorDBConnector();
        //System.out.println("XML=" + xml + "\n\njson=" + json);

        if (mapType.equals("cvp")) {

            Integer cvpID = new Integer(vendorDBConnector.insertCVP(xml, schema_id, name, json, selections));
            this.forwardToPage("/vendor/annotationResult.jsp?schema_id=" + schema_id + "&dataannotation='true'", request, response);
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
