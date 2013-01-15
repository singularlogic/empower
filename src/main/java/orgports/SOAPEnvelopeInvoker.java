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
import javax.xml.rpc.ServiceException;
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
    public SOAPEnvelopeInvoker(String SoapAdressURL, String source_operation_name, String complexType_input, String complexType_output, JSONObject inputargs,JSONObject XMLParserInputArgs, String namespace) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        this.SoapAdressURL = SoapAdressURL;
        this.source_operation_name = source_operation_name;
        this.complexType_input = complexType_input;
        this.complexType_output = complexType_output;
        this.inputargs = inputargs;

        
        //Construct input fields of WS.
        // If inputArgs contain nested fields we getit from  TypesToExposeinForm
        // else we iterate the inputArgs taken from HTTP request

        List<String>  TypesToExposeinForm = (List<String>) XMLParserInputArgs.get("TypesToExposeinForm");
        if((TypesToExposeinForm.size()>1))
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(XMLParserInputArgs.get("Typesinxml").toString())));

            for (Object o : inputargs.keySet()) {
                String key = (String) o;
                String value = inputargs.getString(key);
               if(doc.getElementsByTagName(key).getLength()>0){
                   for (int j = 0; j < doc.getElementsByTagName(key).getLength(); j++) {
                   if (!doc.getElementsByTagName(key).item(j).hasChildNodes())
                   {
                       Element el = (Element) doc.getElementsByTagName(key).item(j);
                       if(el.hasAttribute("fk") && !value.equalsIgnoreCase(""))
                       {
                           doc.getElementsByTagName(key).item(j).setTextContent("<"+el.getAttribute("fk")+">"+value+"</"+el.getAttribute("fk")+">");
                       }
                       else   doc.getElementsByTagName(key).item(j).setTextContent(value);
                   }

                   }
               }
            }
                org.w3c.dom.Node operationComplexType = doc.getFirstChild().getChildNodes().item(0);

            List nodesWithNoValue = new ArrayList<String>();
            //remove nodes without value
            for (int k = 0; k <  operationComplexType.getChildNodes().getLength(); k++) {
                 if (operationComplexType.getChildNodes().item(k).getTextContent().equalsIgnoreCase(""))
                     nodesWithNoValue.add(operationComplexType.getChildNodes().item(k).getNodeName());
            }

            Node nodetodelete;
            for(Object elem :nodesWithNoValue){


                operationComplexType.removeChild(doc.getElementsByTagName(elem.toString()).item(0));
            }


            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StreamResult result;
            result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);


            String ArgsToImport =  result.getWriter().toString();
            ArgsToImport =   ArgsToImport.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?><rootElement>","");
            ArgsToImport =   ArgsToImport.replace("</rootElement>","");
            ArgsToImport =   ArgsToImport.replace("&lt;","<");
            ArgsToImport =   ArgsToImport.replace("&gt;",">");



            soap_inputargs = ArgsToImport;


        }
        else{
        for (Object o : inputargs.keySet()) {
            String key = (String) o;
            String value = inputargs.getString(key);
            System.out.println("key " + key + " value: " + value);
            soap_inputargs = soap_inputargs.concat("<" + key + ">" + value + "</" + key + ">");
        }
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
                System.out.println("ena ena dio ");
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

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document sbstringdoc = builder.parse(new InputSource(new StringReader(sbstring)));

                org.w3c.dom.Node returnode = sbstringdoc.getFirstChild().getFirstChild().getFirstChild();

                outputXML ="<"+complexType_output+"><return>";

                //for(Node child = (Node) returnode.getFirstChild();child!=null;){

                for (int l = 0; l < returnode.getChildNodes().getLength(); l++)  {

                    Element child = (Element) returnode.getChildNodes().item(l);


                    if (child.getChildNodes().getLength()==1)   {


                        outputXML += "<"+child.getNodeName()+">"+child.getFirstChild().getTextContent()+"</"+child.getNodeName()+">";

                    }


                }


                outputXML= outputXML +"</return></"+complexType_output+">";


            }
            else{
            outputXML="";
            try {
            // remove all nodes that have more than 2 depth so as to get the return values and their foreign keys
            SOAPBody sb = resp.getBody();
            //Reading result
            System.out.println("EnvelopeResponse: "+resp.toString());
            SOAPEnvelopeInvokerResponse.put("Soap:EnvelopeResponse", resp.toString());

            outputXML ="<"+complexType_output+">";
            
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
                         //put here the foreignkeys
                  
                         }
                }
            }
            
            outputXML= outputXML +"</"+complexType_output+">";
            
            System.out.println("outputXML  "+outputXML);
        }catch (Exception e){
               // The Soap Response is null

                outputXML="";

            }
            
        }
            System.out.println("outputXML:"+ outputXML.toString());
            SOAPEnvelopeInvokerResponse.put("outputXML", outputXML.toString());

        } catch (AxisFault ex) {
            System.out.println(ex.getMessage());
            SOAPEnvelopeInvokerResponse.put("ErrorMessage", "SOAP Request to "+HOST_ADDRESS+"  "+ ex.getMessage());
        } catch (ServiceException ex) {
            System.out.println(ex.getMessage());
              SOAPEnvelopeInvokerResponse.put("ErrorMessage","SOAP Request to "+HOST_ADDRESS+"  "+ ex.getMessage());
        } catch (SOAPException ex) {
            System.out.println(ex.getMessage());
            SOAPEnvelopeInvokerResponse.put("ErrorMessage","SOAP Request to "+HOST_ADDRESS+"  "+ ex.getMessage());
        }catch (Exception ex) {
            System.out.println(ex.getMessage());
            outputXML="";
        }
       
        return SOAPEnvelopeInvokerResponse;
        
    }
}