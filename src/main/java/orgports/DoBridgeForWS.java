package orgports;

import dataaccesslayer.CPA;
import dataaccesslayer.Service;
import maincontrol.MainControlDB;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.dom4j.DocumentException;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import xml.Parser;
import xml.WSDLParser;

import javax.jws.WebService;
import javax.wsdl.WSDLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: eleni
 * Date: 3/19/13
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class DoBridgeForWS {

    private static String xml_rep_path;



    public JSONObject jsonToReturn = null;

    public DoBridgeForWS() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();
        InputStream in =classLoader.getResourceAsStream("myproperties.properties");
        properties.load(in);

        this.xml_rep_path= properties.getProperty("repo.path").toString();
        if (xml_rep_path.equalsIgnoreCase("/var/www/empower/empowerdata/")){
            //if (xml_rep_path.equalsIgnoreCase("/home/eleni/Documents/ubi/empower/empower-deliverable-september/empower/")){
            String user_home=System.getProperty("user.home");
            this.xml_rep_path = user_home+"/empower/empowerdata";
        }
    }



    public JSONObject doBridgingService(int cpa_id, String xml_data) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, TransformerException, WSDLException, DocumentException {

        try{
            System.out.println("333");
            JSONObject infoBridgingProcess = new JSONObject();
        MainControlDB mainControlDB = new MainControlDB();


        //--------------------GET INFO OF THE WEB SERVICES {service_id ,operation_name , service_namespace , SoapAdressURL , complexType_input , complexType_output}-----------------

        OrgDBConnector orgDBConnector = new OrgDBConnector();
        CPA cpa = orgDBConnector.getCPA(cpa_id);
        String cpa_info = cpa.getCpa_info();

        JSONObject o = (JSONObject) JSONSerializer.toJSON(cpa_info);
        JSONObject info_first = (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_first"));
        JSONObject first_webServiceInfo = this.getWebServiceInfo(info_first);


        JSONObject info_second = (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_second"));
        JSONObject second_webServiceInfo = this.getWebServiceInfo(info_second);


        LinkedList complexType = new LinkedList();
        complexType.add(first_webServiceInfo.getString("complexType_input"));
        complexType.add(first_webServiceInfo.getString("complexType_output"));
        complexType.add(second_webServiceInfo.getString("complexType_input"));
        complexType.add(second_webServiceInfo.getString("complexType_output"));


        //-------------------- PREPARE INVOCATION OF FIRST WEB SERVICE----------------------------------------------------------------

        String complexType_output_name_first = (first_webServiceInfo.getString("complexType_output").equalsIgnoreCase(""))?"":first_webServiceInfo.getString("complexType_output").split("\\$")[1];


        // Get Dao entity of web service operation if exists
        Service FirstWservice = mainControlDB.getService(first_webServiceInfo.getInt("service_id"));
        WSDLParser firstWSDLParser = new WSDLParser(xml_rep_path+FirstWservice.getWsdl(), FirstWservice.getNamespace());
        String firstDaoEntity = firstWSDLParser.getDaoEntity(first_webServiceInfo.getString("operation_name"));
        String firstInputXML = prepareSoapBody(xml_data,first_webServiceInfo.getString("operation_name"),firstDaoEntity);

        //-------------------DO INVOCATION OF FIRST WEB SERVICE-------------------------------

        System.out.println("SoapAdressURL"+first_webServiceInfo.getString("SoapAdressURL"));
        System.out.println("operation_name"+first_webServiceInfo.getString("operation_name") );
        System.out.println("complexType_input"+ first_webServiceInfo.getString("complexType_input").split("\\$")[1] );
            System.out.println("complexType_output_name_first"+ complexType_output_name_first );

            System.out.println("service_namespace"+ first_webServiceInfo.getString("service_namespace") );
            System.out.println("firstInputXML"+ firstInputXML );


        SOAPEnvelopeInvoker soapEnvelopeInvoker = new SOAPEnvelopeInvoker(first_webServiceInfo.getString("SoapAdressURL"), first_webServiceInfo.getString("operation_name"), first_webServiceInfo.getString("complexType_input").split("\\$")[1], complexType_output_name_first, first_webServiceInfo.getString("service_namespace"),firstInputXML);
        JSONObject SOAPEnvelopeInvokerResponse = soapEnvelopeInvoker.callWebService();

        //-------------------MANIPULATE RESPONSE OF FIRST WEB SERVICE-------------------------------

        if (SOAPEnvelopeInvokerResponse.containsKey("ErrorMessage")) {
            //Case that the call to the web service erases an ErrorMessage (ex.404 Not Found) redirect to Error page/

            System.out.println(SOAPEnvelopeInvokerResponse.getString("ErrorMessage"));

        } else if(SOAPEnvelopeInvokerResponse.get("outputXML").toString().equalsIgnoreCase("")){
            // Case that the first web service has no Response

            infoBridgingProcess = fillΙnfoBridgingProcess(SOAPEnvelopeInvokerResponse.get("Soap:EnvelopeRequest").toString(),"<info>The first Web Service has no response</info>","","","","", "<info>The second Web Service has not been invoked</info>", "<info>The second Web Service has not been invoked</info>", "<info>The second Web Service has not been invoked</info>");

             jsonToReturn =  updateJSONOfShowBridgingResults(xml_data,"<info><fact>The second Web Service has not been invoked</fact><reason>Because the First Web Service gives no response</reason></info>", infoBridgingProcess);

            //this.forwardToPage("/organization/showBridging.jsp?type=service", request, response);
        }
        else {
            // Case that the first web service has Response

            CPA cpainfo = orgDBConnector.getinfocpa(cpa_id);
            int cpp_a = cpainfo.getCpp_id_first();
            int cpp_b = cpainfo.getCpp_id_second();


            //-------------------- TRANSFORM (upcasting & Downcasting) the output of the  source web service to an input of the target Web service--------------------

            JSONObject transform_response = this.transform(cpp_a, cpp_b, SOAPEnvelopeInvokerResponse.getString("outputXML"), cpainfo.getCpa_info(), complexType);
            String target_xml = transform_response.getString("xml");


            //-------------------- PREPARE INVOCATION OF SECOND WEB SERVICE----------------------------------------------------------------

            String complexType_output_name_second = (second_webServiceInfo.getString("complexType_output").equalsIgnoreCase(""))?"":second_webServiceInfo.getString("complexType_output").split("\\$")[1];


            //Get Dao entity of web service operation if exists
            Service SecondWservice = mainControlDB.getService(second_webServiceInfo.getInt("service_id"));
            WSDLParser secondWSDLParser = new WSDLParser(xml_rep_path+SecondWservice.getWsdl(), SecondWservice.getNamespace());
            String daoEntity = secondWSDLParser.getDaoEntity(second_webServiceInfo.getString("operation_name"));
            String secondInputXML = prepareSoapBody(target_xml,second_webServiceInfo.getString("operation_name"),daoEntity);

            //-------------------DO INVOCATION OF SECOND WEB SERVICE-------------------------------

            SOAPEnvelopeInvoker targetsoapEnvelopeInvoker = new SOAPEnvelopeInvoker(second_webServiceInfo.getString("SoapAdressURL"), second_webServiceInfo.getString("operation_name"), second_webServiceInfo.getString("complexType_input").split("\\$")[1], complexType_output_name_second, second_webServiceInfo.getString("service_namespace"),secondInputXML);
            JSONObject targetsoapEnvelopeInvokerResponse = targetsoapEnvelopeInvoker.callWebService();

            //-------------------MANIPULATE RESPONSE OF SECOND WEB SERVICE-------------------------------

            if (targetsoapEnvelopeInvokerResponse.containsKey("ErrorMessage")) {

               System.out.println( targetsoapEnvelopeInvokerResponse.getString("ErrorMessage"));
               // this.forwardToPage("/error/generic_error.jsp?errormsg=" + targetsoapEnvelopeInvokerResponse.getString("ErrorMessage"), request, response);

            }else if(targetsoapEnvelopeInvokerResponse.get("outputXML").toString().equalsIgnoreCase("")){

                infoBridgingProcess = fillΙnfoBridgingProcess(SOAPEnvelopeInvokerResponse.get("Soap:EnvelopeRequest").toString(),SOAPEnvelopeInvokerResponse.get("Soap:EnvelopeResponse").toString(),SOAPEnvelopeInvokerResponse.get("outputXML").toString(),target_xml,transform_response.get("xsltRulesFirst").toString(),transform_response.get("xsltRulesSecond").toString(), targetsoapEnvelopeInvokerResponse.get("Soap:EnvelopeRequest").toString(),targetsoapEnvelopeInvokerResponse.get("Soap:EnvelopeResponse").toString(), "<return>This Operation has no Response</return>" );

                 jsonToReturn = updateJSONOfShowBridgingResults(xml_data, "<return>This Operation has no Response</return>", infoBridgingProcess);

               // this.forwardToPage("/organization/showBridging.jsp?type=service", request, response);

            } else {

                infoBridgingProcess = fillΙnfoBridgingProcess(SOAPEnvelopeInvokerResponse.get("Soap:EnvelopeRequest").toString(),SOAPEnvelopeInvokerResponse.get("Soap:EnvelopeResponse").toString(), SOAPEnvelopeInvokerResponse.get("outputXML").toString(), target_xml,transform_response.get("xsltRulesFirst").toString(), transform_response.get("xsltRulesSecond").toString(), targetsoapEnvelopeInvokerResponse.get("Soap:EnvelopeRequest").toString(), targetsoapEnvelopeInvokerResponse.get("Soap:EnvelopeResponse").toString(), targetsoapEnvelopeInvokerResponse.get("outputXML").toString());

                 jsonToReturn =  updateJSONOfShowBridgingResults(xml_data, targetsoapEnvelopeInvokerResponse.getString("outputXML"), infoBridgingProcess);

                //this.forwardToPage("/organization/showBridging.jsp?type=service", request, response);
            }
        }


        }catch (Exception e){
            System.out.println("Generic Exception: " + e.toString());
        }

        return    jsonToReturn;

    }


    private JSONObject getWebServiceInfo(JSONObject info) throws WSDLException, FileNotFoundException, IOException, ParserConfigurationException, SAXException, XPathExpressionException, DocumentException, TransformerException {
        MainControlDB mainControlDB = new MainControlDB();
        int service_id = Integer.parseInt(info.get("service_id").toString());

        String operation_name = (String) info.get("operation");
        System.out.println("operation_name: " + operation_name);

        Service service = mainControlDB.getService(service_id);
        WSDLParser wsdlParser = new WSDLParser(xml_rep_path+service.getWsdl(), service.getNamespace());
        String service_name = service.getName();
        wsdlParser.loadService(service.getName());
        String service_namespace = service.getNamespace();

        //String SoapAdressURL = wsdlParser.getSoapAdressURL(service.getName());
        String SoapAdressURL = (String) info.get("urlbinding");


        LinkedList<String> complexType = wsdlParser.getXSDAttibutes(operation_name);

        String complexType_input = complexType.get(0);
        String complexType_output = (complexType.size()>1)?complexType.get(1):"";
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
        return webServiceInfo;
    }





    public String prepareSoapBody(String xml,String operation, String complexType) throws TransformerException, ParserConfigurationException, IOException, SAXException {

        System.out.println("prepareSoapBody xml " + xml);
        System.out.println("operation xml " + operation);
        System.out.println("complexType xml " + complexType);
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

        return body;
    }



    private JSONObject fillΙnfoBridgingProcess(String FirstSoapRequest, String FirstSoapResponse,String FirstOutputXML,String XMLAfterDownCasting, String UpcastingXSLT, String DowncastingXSLT, String SecondSoapRequest, String  SecondSoapResponse, String SecondoutputXML  ){
        JSONObject infoBridgingProcess = new JSONObject();


        infoBridgingProcess.put("FirstSoapRequest", FirstSoapRequest);
        infoBridgingProcess.put("FirstSoapResponse",FirstSoapResponse);
        infoBridgingProcess.put("Output XML from First Web Service for XBRL UPCasting",FirstOutputXML);
        infoBridgingProcess.put("XML after XBRL DownCasting - Input to Second Web Service", XMLAfterDownCasting);
        infoBridgingProcess.put("Upcasting xslt",UpcastingXSLT);
        infoBridgingProcess.put("Downcasting xslt",DowncastingXSLT);

        infoBridgingProcess.put("SecondSoapRequest",SecondSoapRequest);
        infoBridgingProcess.put("SecondSoapResponse",SecondSoapResponse);
        infoBridgingProcess.put("SecondoutputXML", SecondoutputXML);

        return  infoBridgingProcess;
    }



    /*
   * Update the session attributes of the showBridging page so as to inform the end user about the process of the bridging
   * */
    private JSONObject updateJSONOfShowBridgingResults(String source_xml, String target_xml, JSONObject infoBridgingProcess){

        JSONObject jsonToReturn = new JSONObject();

        jsonToReturn.put("source_xml",source_xml);
        jsonToReturn.put("target_xml", target_xml);
        jsonToReturn.put("infoBridgingProcess", infoBridgingProcess);

        return jsonToReturn;

    }




    public JSONObject transform(int cpp_a, int cpp_b, String xmlData, String cpa_info, LinkedList<String> service_selections) {

        JSONObject trasform_response = new JSONObject();
        OrgDBConnector orgDBConnector = new OrgDBConnector();
        String finalxmloutput = "";
        String finalxmlAllUpcastedXML="";


        System.out.println("elaaaaaaaa kai thelo na teleiono..."+cpp_a+"  "+cpp_b);

        String xsltRulesFirst = orgDBConnector.retrieveXLST(cpp_a, "input", cpa_info, service_selections);//input
        String xsltRulesSecond = orgDBConnector.retrieveXLST(cpp_b, "output", cpa_info, service_selections);//output

        trasform_response.put("xsltRulesFirst", xsltRulesFirst);
        trasform_response.put("xsltRulesSecond", xsltRulesSecond);

        System.out.println("CHECK input " + xsltRulesFirst);
        System.out.println("CHECK output " + xsltRulesSecond);

        StringWriter stw = new StringWriter();
        StringWriter stwRes = new StringWriter();
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer;
        Parser parser = new Parser();

        try {
            if (service_selections.isEmpty()) {

                //check if the xml apart from root has repetitive first level elements

                LinkedList<String> xmlnodes = parser.parseXML(xmlData);

                String firstxmlnode = xmlnodes.get(0);

                transformer = tFactory.newTransformer(new StreamSource(new StringReader(xsltRulesFirst)));
                transformer.transform(new StreamSource(new StringReader(firstxmlnode)), new StreamResult(stw));

                System.out.println("UpcastingXML: " + stw.toString());

                tFactory = TransformerFactory.newInstance();
                transformer = tFactory.newTransformer(new StreamSource(new StringReader(xsltRulesSecond)));
                transformer.transform(new StreamSource(new StringReader(stw.toString())), new StreamResult(stwRes));

                List Namespaces = parser.getNamespaces(stwRes.toString());
                List XbrlNamespaces = parser.getNamespaces(stw.toString());

                String outputAllXML = "<root>";
                String outputAllUpcastedXML = "<root>";
                Iterator i = xmlnodes.iterator();
                while (i.hasNext()) {

                    String xmlnode = (String) i.next();
                    StringWriter stw1 = new StringWriter();
                    StringWriter stwRes1 = new StringWriter();

                    transformer = tFactory.newTransformer(new StreamSource(new StringReader(xsltRulesFirst)));
                    transformer.transform(new StreamSource(new StringReader(xmlnode)), new StreamResult(stw1));
                    System.out.println("UpcastingXML: " + stw1.toString());
                    outputAllUpcastedXML = outputAllUpcastedXML + parser.removeNamespaces(stw1.toString());


                    tFactory = TransformerFactory.newInstance();
                    transformer = tFactory.newTransformer(new StreamSource(new StringReader(xsltRulesSecond)));
                    transformer.transform(new StreamSource(new StringReader(stw1.toString())), new StreamResult(stwRes1));

                    outputAllXML = outputAllXML + parser.removeNamespaces(stwRes1.toString());

                }
                outputAllXML = outputAllXML + "</root>";
                outputAllUpcastedXML = outputAllUpcastedXML + "</root>";
                System.out.println("All output XML+ " + outputAllXML);


                finalxmloutput = parser.addNamespaces(outputAllXML, Namespaces);
                finalxmlAllUpcastedXML = parser.addNamespaces(outputAllUpcastedXML, XbrlNamespaces);


            } else {
                transformer = tFactory.newTransformer(new StreamSource(new StringReader(xsltRulesFirst)));
                transformer.transform(new StreamSource(new StringReader(xmlData)), new StreamResult(stw));

                System.out.println("UpcastingXML: " + stw.toString());
                finalxmlAllUpcastedXML= stw.toString();

                tFactory = TransformerFactory.newInstance();
                transformer = tFactory.newTransformer(new StreamSource(new StringReader(xsltRulesSecond)));
                transformer.transform(new StreamSource(new StringReader(stw.toString())), new StreamResult(stwRes));

                System.out.println("Hola" + stwRes.toString() + "Adios" + stw.toString());

                finalxmloutput = stwRes.toString();


            }


        } catch (Throwable t) {
            //t.printStackTrace();
            System.out.println("Hola" + t);
        }

        trasform_response.put("xml", finalxmloutput);
        trasform_response.put("UPCasted XML To XBRL Taxonomy", finalxmlAllUpcastedXML);
        System.out.println("trasform_response: "+trasform_response.getString("UPCasted XML To XBRL Taxonomy"));

        return trasform_response;
    }



}


