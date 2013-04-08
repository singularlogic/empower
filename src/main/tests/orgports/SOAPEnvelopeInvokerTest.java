package orgports;

import net.sf.json.JSONObject;
import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.soap.MessageFactoryImpl;
import org.dom4j.DocumentException;
import org.dom4j.Namespace;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import xml.Parser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.soap.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: eleni
 * Date: 11/27/12
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class SOAPEnvelopeInvokerTest {



    String SoapAdressURL = "";
    String source_operation_name = "";
    String complexType_input = "";
    String complexType_output = "";
    JSONObject inputargs;
    String soap_inputargs = "";
    String SOAP_REQUEST = "";
    String HOST_ADDRESS = "";
    String SOAPACTION_URI = "";
    /*
     * Example of a SOAP_REQUEST
     *
     * "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
     * xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"
     * xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"> " +
     * "<soap:Body> " + "<invoiceMessageInput>" + "<code>lalala</code>" +
     * "<payment>eleni</payment>" + "</invoiceMessageInput>" + "</soap:Body>" +
     * "</soap:Envelope>" ;
     *
     *
     * Example of a HOST_ADDRESS
     * http://127.0.0.1:8080/wscrm-1.0-SNAPSHOT/ResellerImplService?getInvoice
     *
     * Example of SOAPACTION_URI
     *
     * http://ws.eleni.com/getInvoice
     */

    /*
    public SOAPEnvelopeInvokerTest1(String SoapAdressURL, String source_operation_name, String complexType_input, String complexType_output, JSONObject inputargs, String namespace) {
        this.SoapAdressURL = SoapAdressURL;
        this.source_operation_name = source_operation_name;
        this.complexType_input = complexType_input;
        this.complexType_output = complexType_output;
        this.inputargs = inputargs;


        for (Object o : inputargs.keySet()) {
            String key = (String) o;
            String value = inputargs.getString(key);
            System.out.println("key " + key + " value: " + value);
            soap_inputargs = soap_inputargs.concat("<" + key + ">" + value + "</" + key + ">");
        }

        System.out.println("namespace" + namespace);

        SOAP_REQUEST = "<?xml version=\"1.0\"?><soap:Envelope "
                + "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + "   xmlns:m=\""+namespace+"\">"
                + "<soap:Body> <m:" + complexType_input + ">  "
                + soap_inputargs
                + "</m:" + complexType_input + "></soap:Body></soap:Envelope>";

        System.out.println("SOAP_REQUEST" + SOAP_REQUEST);

        HOST_ADDRESS = SoapAdressURL + "?" + source_operation_name;

        System.out.println("HOST_ADDRESS" + HOST_ADDRESS);

        SOAPACTION_URI = namespace + source_operation_name;

        System.out.println("SOAPACTION_URI" + SOAPACTION_URI);

    }

    public JSONObject callWebService() {
        JSONObject SOAPEnvelopeInvokerResponse = new JSONObject();
        try {
            SOAPEnvelopeInvokerResponse.put("Soap:EnvelopeRequest", SOAP_REQUEST);
            byte[] reqBytes = SOAP_REQUEST.getBytes();
            ByteArrayInputStream bis = new ByteArrayInputStream(reqBytes);
            StreamSource ss = new StreamSource(bis);


            //Create a SOAP Message Object
            MessageFactoryImpl messageFactory = new MessageFactoryImpl();
            SOAPMessage msg = messageFactory.createMessage();
            SOAPPart soapPart = msg.getSOAPPart();

            //Set the soapPart Content with the stream source
            soapPart.setContent(ss);

            //Create a WebService Call
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(HOST_ADDRESS);
            call.setProperty(Call.SOAPACTION_USE_PROPERTY, new Boolean(true));
            call.setProperty(Call.SOAPACTION_URI_PROPERTY, SOAPACTION_URI);

            //Invoke the WebService.
            SOAPEnvelope resp = call.invoke(((org.apache.axis.SOAPPart) soapPart).getAsSOAPEnvelope());


            //Reading result
            System.out.println("EnvelopeResponse: "+resp.toString());
            SOAPEnvelopeInvokerResponse.put("Soap:EnvelopeResponse", resp.toString());

            SOAPBody sb = resp.getBody();


            String outputXML ="<"+complexType_output+">";

            System.out.println("outputXML start:  "+outputXML);


            Iterator output = sb.getChildElements();
            while (output.hasNext()) {


                Node rt = (Node) output.next();

                NodeList children = rt.getChildNodes();

                for (int i = 0; i < children.getLength(); i++) {
                    Node childNode = (Node) children.item(i);

                    System.out.println("childNode Name:" +  childNode.getNodeName());
                    System.out.println("childNode Value:" +  childNode.getValue());

                    NodeList grandchildren = childNode.getChildNodes();
                    if (childNode.getNodeName().equalsIgnoreCase("return") && grandchildren.getLength()>1){
                        for (int k = 0; k < grandchildren.getLength(); k++) {
                            Node grandchildNode = (Node) grandchildren.item(k);
                            System.out.println("childNode Name:" +  grandchildNode.getNodeName());
                            System.out.println("childNode Value:" +  grandchildNode.getValue());
                            outputXML = outputXML +"<"+grandchildNode.getNodeName()+">"+grandchildNode.getValue()+"</"+grandchildNode.getNodeName()+">";
                        }
                    }else{
                        outputXML = outputXML +"<"+childNode.getNodeName()+">"+childNode.getValue()+"</"+childNode.getNodeName()+">";

                    }
                }
            }

            outputXML= outputXML +"</"+complexType_output+">";

            System.out.println("outputXML  "+outputXML);



            SOAPEnvelopeInvokerResponse.put("outputXML", outputXML);

        } catch (AxisFault ex) {
            System.out.println(ex.getMessage());
            SOAPEnvelopeInvokerResponse.put("ErrorMessage", "SOAP Request to "+HOST_ADDRESS+"  "+ ex.getMessage());
        } catch (ServiceException ex) {
            System.out.println(ex.getMessage());
            SOAPEnvelopeInvokerResponse.put("ErrorMessage","SOAP Request to "+HOST_ADDRESS+"  "+ ex.getMessage());
        } catch (SOAPException ex) {
            System.out.println(ex.getMessage());
            SOAPEnvelopeInvokerResponse.put("ErrorMessage","SOAP Request to "+HOST_ADDRESS+"  "+ ex.getMessage());
        }

        return SOAPEnvelopeInvokerResponse;

    }
     */
    /*
    @Test
    public static void main(String[] args)  {
        /*
        * 3.Call the web services (SOAP request response and the respective
        * xmls)
        */
        /*
        SOAPEnvelopeInvoker soapEnvelopeInvoker = new SOAPEnvelopeInvoker(first_webServiceInfo.getString("SoapAdressURL"),
                first_webServiceInfo.getString("operation_name"),
                first_webServiceInfo.getString("complexType_input").split("\\$")[1],
                first_webServiceInfo.getString("complexType_output").split("\\$")[1],
                inputargs,
                first_webServiceInfo.getString("service_namespace"));
        *-/
        JSONObject inputargs = new JSONObject();
        inputargs.put("arg0","lala");
        inputargs.put("arg1","lolo");
        inputargs.put("arg2","lili");

        SOAPEnvelopeInvokerTest soapEnvelopeInvoker = new SOAPEnvelopeInvokerTest("http://localhost:8080/wsSILTest_warexploded/TestService",
                "AddTesttable", "AddTesttable", "AddTesttableResponse", inputargs,"http://wsSIL.eleni.com/");
        JSONObject SOAPEnvelopeInvokerResponse = soapEnvelopeInvoker.callWebService();

    if (SOAPEnvelopeInvokerResponse.containsKey("ErrorMessage")) {
       // this.forwardToPage("/error/generic_error.jsp?errormsg=" + SOAPEnvelopeInvokerResponse.getString("ErrorMessage"), request, response);
        fail();
    } else {

        /*
        * 5. Recolect all the important info of the requests in the
        * infoBridgingProcess JSONObject
        *-/
        JSONObject     infoBridgingProcess = new JSONObject();
        infoBridgingProcess.put("FirstSoapRequest", SOAPEnvelopeInvokerResponse.get("Soap:EnvelopeRequest"));
        infoBridgingProcess.put("FirstSoapResponse", SOAPEnvelopeInvokerResponse.get("Soap:EnvelopeResponse"));
        infoBridgingProcess.put("Output XML from First Web Service for XBRL UPCasting", SOAPEnvelopeInvokerResponse.get("outputXML"));
    }


    }
      */
    @Test
    public void  SOAPEnvelopeInvokerTest() throws TransformerException, IOException, ParserConfigurationException, SAXException {

        JSONObject inputargs= new JSONObject();
        //inputargs.put("id","2");
        /*
        inputargs.put("clientCategoryId","");
        inputargs.put("clientStateId","1");
        inputargs.put("countryId","1");
        inputargs.put("clientName","elaaaaaante1111");
         */

       // Parser pa = new Parser();

       //JSONObject XMLInputArgs =  pa.getInputArgs("/home/eleni/Documents/ubi/empower/empower-deliverable-september/empower/xsd/169ClientServiceImplsaveClient.xsd","saveClient");
        //JSONObject XMLInputArgs =  pa.getInputArgs("/home/eleni/Documents/ubi/empower/empower-deliverable-september/empower/xsd/169ClientServiceImplfindClient.xsd","findClient")  ;


        SOAPEnvelopeInvoker soapEnvelopeInvoker = new SOAPEnvelopeInvoker("http://localhost:8080/projectx/services/ClientServiceImpl",
               "findClient","findClient","findClientResponse","http://service.sample.eu/","<m:findClient><id>96</id></m:findClient>");

        //SOAPEnvelopeInvoker soapEnvelopeInvoker = new SOAPEnvelopeInvoker("http://127.0.0.1:8081/wscrm-1.0-SNAPSHOT/ResellerImplService?getCustomerInfo",
          //      "findClient","findClient","findClientResponse", "http://service.sample.eu/","");



        //SOAPEnvelopeInvoker soapEnvelopeInvoker = new SOAPEnvelopeInvoker("http://localhost:8080/projectx/services/ClientServiceImpl",
          //           "saveClient","saveClient","Response", inputargs,XMLInputArgs, "http://service.sample.eu/");

        JSONObject SOAPEnvelopeInvokerResponse = soapEnvelopeInvoker.callWebService();







    }
         /*
         * 1.   http://localhost:8080/projectx/services/ClientServiceImpl
         * 2.   findClient
         * 3.   findClient
         * 4.   findClientResponse
         * 5.   {"id":"2"}
         * 6.   http://service.sample.eu/
         * */


    @Test
    public void  SOAPEnvelopeInvokerTest1() throws ParserConfigurationException, IOException, SAXException, DocumentException, TransformerException {

    String xml = " <saveErporder>\n" +
            "         <erporder>\n" +
            "            <code>1DI4397429</code>\n" +
            "            <seriesCode>SDI382729</seriesCode>\n" +
            "            <description>dell inspiron</description>\n" +
            "            <warehouseCode>liosia dell</warehouseCode>\n" +
            "            <branchCode>dell</branchCode>\n" +
            "            <number>588</number>\n" +
            "            <paymentMethodCode>cash</paymentMethodCode>\n" +
            "            <paymentMethodName>cash</paymentMethodName>\n" +
            "         </erporder>\n" +
            "      </saveErporder>\n";

        xml =  xml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>","");

        System.out.println("prepareSoapBody xml " + xml);

        String operation = "saveErporder";
        String complexType = "erporder";


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




        System.out.println(body);


    }



}
