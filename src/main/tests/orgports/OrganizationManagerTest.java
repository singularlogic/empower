package orgports;

import dataaccesslayer.Service;
import maincontrol.MainControlDB;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.xml.sax.SAXException;
import xml.WSDLParser;

import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: eleni
 * Date: 11/23/12
 * Time: 10:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class OrganizationManagerTest {
    @Test
    public void getWebServiceInfo() throws WSDLException, FileNotFoundException, IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        MainControlDB mainControlDB = new MainControlDB();
        int service_id = 146;

        String operation_name = "addTesttable";
        System.out.println("operation_name: " + operation_name);

        Service service = mainControlDB.getService(service_id);
        WSDLParser wsdlParser = new WSDLParser(service.getWsdl(), service.getNamespace());
        String service_name = service.getName();
        wsdlParser.loadService(service.getName());
        String service_namespace = service.getNamespace();

        String SoapAdressURL = wsdlParser.getSoapAdressURL(service.getName());

        LinkedList<String> complexType = wsdlParser.getXSDAttibutes(operation_name);

        String complexType_input = complexType.get(0);
        String complexType_output = complexType.get(1);
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
        //return webServiceInfo;
    }

}
