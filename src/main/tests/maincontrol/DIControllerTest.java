package maincontrol;

import dataaccesslayer.DataAnnotations;
import dataaccesslayer.Service;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.xml.sax.SAXException;
import xml.Parser;
import xml.WSDLParser;

import javax.enterprise.inject.Model;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: eleni
 * Date: 11/23/12
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class DIControllerTest {

    private static String xml_rep_path = "/home/eleni/Documents/ubi/empower/empower-deliverable-september/empowerTest/";


    @Test
    public void annotate_data_service()
            throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, WSDLException, TransformerConfigurationException, TransformerException {

        int service_id = 158;
        String selections = "input$addTesttableResponse$addTesttable$TestBinding";
        //int service_id = 79;
        //String selections = "input$getClientCholesterol$getClientCholesterol$ERPImplServiceSoapBinding";
        String centralTree = "entryDetailComplexType";
        String mapping = new String("");
        String choice = "";
        String xbrl_mismatch = "false";


        MainControlDB mainControlDB = new MainControlDB();
        //DataAnnotations dataannotations = mainControlDB.getMapping(-1, service_id, selections);
        //mapping = dataannotations.getMapping();


        //if (mapping != null) {
          //  mapping = new String(mapping.replace("\"", "\\\""));
            //System.out.println("==== mapping=" + mapping.substring(0, 500));
        //}

        //request.setAttribute("mapping", mapping);
        //request.setAttribute("selections", selections);
        //request.setAttribute("xbrl", centralTree);

        Service service = mainControlDB.getService(service_id);

        WSDLParser wsdlParser = new WSDLParser(service.getWsdl(), service.getNamespace());
        choice = selections.split("\\$")[1];
       // choice[1] = selections.split("\\$")[2];
        String inputoutput = selections.split("\\$")[0];
        String xsdTypes = wsdlParser.extractXSD(choice);
        //String xsdTypes =    response_xsdTypes.getString("xsdTypes");

        String filename = new String(service.getService_id() + service.getName() + choice + ".xsd");
        String xsdFilename = new String(xml_rep_path + "/xsd/" + filename);

        File f = new File(xsdFilename);
        if(!f.exists()) {
            PrintWriter xsdFile = new PrintWriter(xsdFilename);
            xsdFile.write(xsdTypes);
            xsdFile.close();
        }


        /*

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
        */
    }

    @Test
    public void presentServiceTree()
            throws IOException, WSDLException {
        //int serviceID = 79;
        int serviceID = 145;

        MainControlDB mainControlDB = new MainControlDB();
        Service service = mainControlDB.getService(serviceID);
       // response.setContentType("text/xml; charset=UTF-8");
        PrintWriter pw = new PrintWriter(System.out, true);

        WSDLParser wsdlParser = new WSDLParser(service.getWsdl(),service.getNamespace());
        wsdlParser.loadService(service.getName());
        wsdlParser.outputToXML(pw);

        System.out.println("outputXML: " + pw.toString());
    }

    @Test
    public void testProperties() throws Exception {
      /* Properties props = System.getProperties();
         Enumeration e = props.propertyNames();
         while (e.hasMoreElements()) {
          String key = (String) e.nextElement();
          //System.out.println(key + " -- " + props.getProperty(key));
      }   */

        String   userdir = System.getProperty("user.dir");

        System.out.println("userdir  -- " + userdir);

        Properties properties = new Properties();
        try {

            FileInputStream fi =  new FileInputStream(userdir+"/target/classes/application");

            properties.load(fi);

            String url =    properties.getProperty("database.jdbc.connectionURL").toString();
            assertEquals("jdbc:mysql://127.0.0.1:3306/", url);

            String driver = properties.getProperty("database.jdbc.driverClass").toString();
            assertEquals("com.mysql.jdbc.Driver", driver);

            String dbName = properties.getProperty("database.jdbc.name").toString();
            assertEquals("empower_db", dbName);

            String userName = properties.getProperty("database.jdbc.username").toString();
            assertEquals("root", userName);

            String password = properties.getProperty("database.jdbc.password").toString();
            assertEquals("!uflow!", password);



        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }



    }

    @Test
    public void testGetServletInfo() throws Exception {

    }
}
