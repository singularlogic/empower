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
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.rpc.ServiceException;
import javax.xml.soap.*;
import javax.xml.soap.Node;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.sf.json.JSONObject;
import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.soap.MessageFactoryImpl;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SOAPEnvelopeInvoker {

    String SoapAdressURL = "";
    String source_operation_name = "";
    String complexType_input = "";
    String complexType_output = "";
    //JSONObject inputargs;
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
    public SOAPEnvelopeInvoker(String SoapAdressURL, String source_operation_name, String complexType_input, String complexType_output, String namespace, String xmldata) throws ParserConfigurationException, IOException, SAXException, TransformerException {

        this.SoapAdressURL = SoapAdressURL;
        this.source_operation_name = source_operation_name;
        this.complexType_input = complexType_input;
        this.complexType_output = complexType_output;
        /*

        SOAP_REQUEST = "<?xml version=\"1.0\"?><soap:Envelope "
                + "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\""
                 + "   xmlns:m=\""+namespace+"\">"
                + "<soap:Body> <m:" + complexType_input + ">  "
                + soap_inputargs
                + "</m:" + complexType_input + "></soap:Body></soap:Envelope>";
         */


        SOAP_REQUEST = "<?xml version=\"1.0\"?><soap:Envelope "
                + "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + "   xmlns:m=\""+namespace+"\">"
                + "<soap:Body>"+xmldata+"</soap:Body></soap:Envelope>";


        System.out.println("SOAP_REQUEST" + SOAP_REQUEST);

        HOST_ADDRESS = SoapAdressURL + "?" + source_operation_name;

        System.out.println("HOST_ADDRESS" + HOST_ADDRESS);

        SOAPACTION_URI = namespace + source_operation_name;

        System.out.println("SOAPACTION_URI" + SOAPACTION_URI);

    }

    public JSONObject callWebService() throws ParserConfigurationException, TransformerException, IOException, SAXException {
        JSONObject SOAPEnvelopeInvokerResponse = new JSONObject();
        Boolean removetypesFromSoapResponse = false;
        String outputXML= " ";
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


            try{

                // remove all nodes that have more than 2 depth so as to get the return values and their foreign keys
                SOAPBody sb = resp.getBody();

                org.w3c.dom.Node returnnode = sb.getChildNodes().item(0).getChildNodes().item(0);



                for (int k = 0; k < returnnode.getChildNodes().getLength(); k++)
                {
                    Element element = (Element) returnnode.getChildNodes().item(k);

                    for(Node childNode = (Node) element.getFirstChild();childNode!=null;){
                        Node nextChild = (Node) childNode.getNextSibling();
                        // Do something with childNode,
                        //   including move or delete...

                        if ( childNode.hasChildNodes())   {
                            //org.w3c.dom.Node nodeToremove = element.removeChild(childNode);
                            if (childNode.getChildNodes().getLength()>1)
                            {
                                childNode.getParentNode().removeChild(childNode);
                                removetypesFromSoapResponse = true;
                            }

                        }
                        childNode = nextChild;

                    }
                }

            }catch (Exception e){
                removetypesFromSoapResponse=false;
            }


            outputXML="";

            if (removetypesFromSoapResponse){

                // remove all nodes that have more than 2 depth so as to get the return values and their foreign keys
                SOAPBody sb = resp.getBody();

                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                StreamResult result;
                result = new StreamResult(new StringWriter());
                DOMSource source = new DOMSource(sb);
                transformer.transform(source, result);

                String sbstring = result.getWriter().toString();

                System.out.println("EnvelopeResponse with one to many removed: "+resp.toString());
                SOAPEnvelopeInvokerResponse.put("Soap:EnvelopeResponse", sbstring);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document sbstringdoc = builder.parse(new InputSource(new StringReader(sbstring)));

                org.w3c.dom.Node returnode = sbstringdoc.getFirstChild().getFirstChild().getFirstChild();


                //String olele = sbstringdoc.getFirstChild().getFirstChild().getFirstChild().getTextContent();

                StreamResult  result1 = new StreamResult(new StringWriter());
                DOMSource returnodeDOM = new DOMSource(returnode);
                transformer.transform(returnodeDOM, result1);


                String sk =result1.getWriter().toString();

                sk = sk.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>","");
                sk = sk.replaceAll("<ns[0-9]:return>","");
                sk = sk.replaceAll("</ns[0-9]:return>","");
                String bodyOutputXML = "<"+complexType_output+"><return>"+sk+"</return></"+complexType_output+">";

                outputXML = bodyOutputXML;



            }
            else{
                System.out.println("eimai mesa sto ELSE removetypesFromSoapResponse"+ removetypesFromSoapResponse);
                outputXML="";
                try {
                    // remove all nodes that have more than 2 depth so as to get the return values and their foreign keys
                    SOAPBody sb = resp.getBody();
                    //Reading result
                    System.out.println("EnvelopeResponse: "+resp.toString());
                    SOAPEnvelopeInvokerResponse.put("Soap:EnvelopeResponse", resp.toString());

                    outputXML ="<"+complexType_output+"><return>";

                    System.out.println("outputXML start:  "+outputXML);


                    Iterator output = sb.getChildElements();
                    while (output.hasNext()) {


                        Node rt = (Node) output.next();

                        NodeList children = rt.getChildNodes();


                        if (children.getLength()==1 && children.item(0).getNodeName().endsWith(":return"))
                        {
                            Node rtchild = (Node) children.item(0);
                            children = rtchild.getChildNodes();
                        }

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

                    outputXML= outputXML +"</return></"+complexType_output+">";

                    System.out.println("outputXML  "+outputXML);
                }catch (Exception e){
                    // The Soap Response is null

                    outputXML="";
                    SOAPEnvelopeInvokerResponse.put("Soap:EnvelopeResponse", "");

                }

            }
            System.out.println("outputXML:"+ outputXML.toString());
            SOAPEnvelopeInvokerResponse.put("outputXML", outputXML.toString());

        } catch (AxisFault ex) {
            System.out.println(ex.getMessage());
            SOAPEnvelopeInvokerResponse.put("ErrorMessage", "SOAP Request to "+HOST_ADDRESS+"  "+ ex.getMessage());
        }
        //catch (ServiceException ex) {
        //  System.out.println(ex.getMessage());
        //  SOAPEnvelopeInvokerResponse.put("ErrorMessage","SOAP Request to "+HOST_ADDRESS+"  "+ ex.getMessage());
        //}
        catch (SOAPException ex) {
            System.out.println(ex.getMessage());
            SOAPEnvelopeInvokerResponse.put("ErrorMessage","SOAP Request to "+HOST_ADDRESS+"  "+ ex.getMessage());
        }catch (Exception ex) {
            System.out.println(ex.getMessage());
            outputXML="";
        }

        return SOAPEnvelopeInvokerResponse;

    }
}