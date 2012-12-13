package orgports;

import net.sf.json.JSONObject;
import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.soap.MessageFactoryImpl;
import org.junit.Test;
import org.w3c.dom.NodeList;

import javax.xml.rpc.ServiceException;
import javax.xml.soap.*;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.util.Iterator;

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
    public SOAPEnvelopeInvokerTest(String SoapAdressURL, String source_operation_name, String complexType_input, String complexType_output, JSONObject inputargs, String namespace) {
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
        */
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
        */
        JSONObject     infoBridgingProcess = new JSONObject();
        infoBridgingProcess.put("FirstSoapRequest", SOAPEnvelopeInvokerResponse.get("Soap:EnvelopeRequest"));
        infoBridgingProcess.put("FirstSoapResponse", SOAPEnvelopeInvokerResponse.get("Soap:EnvelopeResponse"));
        infoBridgingProcess.put("Output XML from First Web Service for XBRL UPCasting", SOAPEnvelopeInvokerResponse.get("outputXML"));
    }


    }
}
