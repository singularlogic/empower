/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orgports;

/**
 *
 * @author eleni
 */
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import javax.xml.rpc.ServiceException;
import javax.xml.soap.*;
import javax.xml.transform.stream.StreamSource;
import net.sf.json.JSONObject;
import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.soap.MessageFactoryImpl;
import org.w3c.dom.NodeList;

public class SOAPEnvelopeInvoker {

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
    public SOAPEnvelopeInvoker(String SoapAdressURL, String source_operation_name, String complexType_input, String complexType_output,JSONObject inputargs, String namespace) {
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
            System.out.println(resp.toString());


            SOAPBody sb = resp.getBody();

            
            
            String outputXML ="<"+complexType_output+">";
                    

            Iterator output = sb.getChildElements();
            while (output.hasNext()) {

                Node rt = (Node) output.next();

                NodeList children = rt.getChildNodes();

                for (int i = 0; i < children.getLength(); i++) {
                     Node childNode = (Node) children.item(i);
                     
                     System.out.println("childNode Name:" +  childNode.getNodeName());
                     System.out.println("childNode Value:" +  childNode.getValue());
                     
                     outputXML = outputXML +"<"+childNode.getNodeName()+">"+childNode.getValue()+"</"+childNode.getNodeName()+">";

                }


                System.out.println("children lenght:" + children.getLength());

            }
            
            outputXML= outputXML +"</"+complexType_output+">";
            
            System.out.println("outputXML  "+outputXML);
            
            
            
            SOAPEnvelopeInvokerResponse.put("outputXML", outputXML);
            SOAPEnvelopeInvokerResponse.put("Soap:Envelope", resp.toString());
            
            
           


        } catch (AxisFault ex) {
            System.out.println(ex.getMessage());
        } catch (ServiceException ex) {
            System.out.println(ex.getMessage());
        } catch (SOAPException ex) {
            System.out.println(ex.getMessage());
        }
       
        return SOAPEnvelopeInvokerResponse;
        
    }
}