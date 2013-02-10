package orgports;

import dataaccesslayer.CPA;
import dataaccesslayer.Service;
import maincontrol.MainControlDB;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.dom4j.DocumentException;
import org.junit.Test;
import org.xml.sax.SAXException;
import xml.Parser;
import xml.WSDLParser;

import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: eleni
 * Date: 11/23/12
 * Time: 10:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class OrganizationManagerTest {
    @Test
    public void getWebServiceInfo() throws WSDLException, FileNotFoundException, IOException, ParserConfigurationException, SAXException, XPathExpressionException, DocumentException, TransformerException {
        MainControlDB mainControlDB = new MainControlDB();
        //int service_id = 146;
        int service_id = 169;
        //int service_id = 170;


        String operation_name = "saveClient";

        //String operation_name = "saveShop";

        System.out.println("operation_name: " + operation_name);

        Service service = mainControlDB.getService(service_id);
        WSDLParser wsdlParser = new WSDLParser(service.getWsdl(), service.getNamespace());
        String service_name = service.getName();
        wsdlParser.loadService(service.getName());
        String service_namespace = service.getNamespace();

        String SoapAdressURL = wsdlParser.getSoapAdressURL(service.getName());

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
        //return webServiceInfo;
    }

    @Test
    public void testgetTypes() throws Exception {
        String xsdTypes= "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\"><xs:complexType name=\"saveShop\">\n" +
                "    <xs:sequence>\n" +
                "      <xs:element form=\"qualified\" minOccurs=\"0\" name=\"shop\" type=\"Shop\"/>\n" +
                "    </xs:sequence></xs:complexType><xs:complexType name=\"Shop\">\n" +
                "    <xs:sequence>\n" +
                "      \n" +
                "      \n" +
                "      \n" +
                "      <xs:element minOccurs=\"0\" name=\"countryId\" type=\"xs:int\"/>\n" +
                "      <xs:element minOccurs=\"0\" name=\"shopBlockTypeId\" type=\"xs:int\"/>\n" +
                "      <xs:element minOccurs=\"0\" name=\"name\" type=\"xs:string\"/>\n" +
                "      <xs:element minOccurs=\"0\" name=\"phone\" type=\"xs:string\"/>\n" +
                "      <xs:element minOccurs=\"0\" name=\"fax\" type=\"xs:string\"/>\n" +
                "      <xs:element minOccurs=\"0\" name=\"taxNumber\" type=\"xs:string\"/>\n" +
                "      <xs:element minOccurs=\"0\" name=\"address\" type=\"xs:string\"/>\n" +
                "      <xs:element minOccurs=\"0\" name=\"zip\" type=\"xs:string\"/>\n" +
                "      <xs:element minOccurs=\"0\" name=\"city\" type=\"xs:string\"/>\n" +
                "      <xs:element minOccurs=\"0\" name=\"shopId\" type=\"xs:int\"/>\n" +
                "    </xs:sequence></xs:complexType></xs:schema>";


        Parser pa = new Parser();
        pa.getTypes(xsdTypes);
    }


    @Test
    public void doBridgingService() throws IOException, TransformerException, DocumentException, SAXException, XPathExpressionException, WSDLException, ParserConfigurationException {

        String xml_input = "";
        String xml_data="";
        Parser parser = new Parser();
        int cpa_id = 73;
        //int cpa_id = 65;
        JSONObject inputargs= new JSONObject();
        inputargs.put("id","61");
        //inputargs.put("customeraddress","narkisson");

        JSONObject infoBridgingProcess = new JSONObject();


        /*                                                                                       second_webServiceInfo.getInt("service_id")
         * 2. Get info of the web services {service_id operation_name
         * service_namespace SoapAdressURL complexType_input complexType_output}
         */
        OrgDBConnector orgDBConnector = new OrgDBConnector();
        CPA cpa = orgDBConnector.getCPA(cpa_id);
        String cpa_info = cpa.getCpa_info();

        JSONObject o = (JSONObject) JSONSerializer.toJSON(cpa_info);
        JSONObject info_first = (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_first"));


        JSONObject first_webServiceInfo = this.getWebServiceInfo(info_first);

        // case is not submitted a xml file i create it by the input args
        String xml_input_to_print = (xml_data == "") ? "<" + first_webServiceInfo.getString("complexType_input").split("\\$")[1] + ">" + xml_input + "</" + first_webServiceInfo.getString("complexType_input").split("\\$")[1] + ">" : xml_data;

        System.out.println("xml_input_to_print" + xml_input_to_print);

        JSONObject info_second = (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_second"));
        JSONObject second_webServiceInfo = this.getWebServiceInfo(info_second);


        LinkedList complexType = new LinkedList();
        complexType.add(first_webServiceInfo.getString("complexType_input"));
        complexType.add(first_webServiceInfo.getString("complexType_output"));
        complexType.add(second_webServiceInfo.getString("complexType_input"));
        complexType.add(second_webServiceInfo.getString("complexType_output"));


        /*
         * 3.Call the web services (SOAP request response and the respective
         * xmls)
         */
        String complexType_output_name_first = (first_webServiceInfo.getString("complexType_output").equalsIgnoreCase(""))?"":first_webServiceInfo.getString("complexType_output").split("\\$")[1];



        //Get the inputargs as given by Parser( parser.getInputArgs()) so as to merge them with inputargs given by the HtmlRequest during invocation
        //System.out.println("Does it get the correct parameters???:  "+xml_rep_path + "/xsd/" + first_webServiceInfo.getInt("service_id")+first_webServiceInfo.getString("service_name")+first_webServiceInfo.getString("operation_name")+".xsd");


        Parser pFirst = new Parser();
        JSONObject XMLParserInputArgsFirst =   pFirst.getInputArgs( "/home/eleni/Documents/ubi/empower/empower-deliverable-september/empower/xsd/" + first_webServiceInfo.getInt("service_id")+first_webServiceInfo.getString("service_name")+first_webServiceInfo.getString("operation_name")+".xsd",first_webServiceInfo.getString("operation_name"));


        SOAPEnvelopeInvoker soapEnvelopeInvoker = new SOAPEnvelopeInvoker(first_webServiceInfo.getString("SoapAdressURL"),
                first_webServiceInfo.getString("operation_name"), first_webServiceInfo.getString("complexType_input").split("\\$")[1],
                complexType_output_name_first, first_webServiceInfo.getString("service_namespace"),"");


        JSONObject SOAPEnvelopeInvokerResponse = soapEnvelopeInvoker.callWebService();


        /*                                                                                       second_webServiceInfo.getInt("service_id")
        * 4. In case of that the call to the web service erases
        * adoBridgingServicen exception (ex.404 Not Found) redirect to Error
        * page with the respective message
        */

        if (SOAPEnvelopeInvokerResponse.containsKey("ErrorMessage")) {
           System.out.println("ErrorMessage");
        } else if(SOAPEnvelopeInvokerResponse.get("outputXML").toString().equalsIgnoreCase("")){
            // in case that the first web service has no Response


            infoBridgingProcess.put("Upcasting xslt","");
            infoBridgingProcess.put("Downcasting xslt","");

            infoBridgingProcess.put("SecondSoapRequest","");
            infoBridgingProcess.put("SecondSoapResponse","");
            infoBridgingProcess.put("SecondoutputXML", "");

             /*
            session.setAttribute("source_xml", xml_input_to_print);
            session.setAttribute("target_xml", "");
            session.removeAttribute("infoBridgingProcess");
            session.setAttribute("infoBridgingProcess", infoBridgingProcess);
               */
        }
        else {

            /*
             * 5. Recolect all the important info of the requests in the
             * infoBridgingProcess JSONObject
             */
            infoBridgingProcess.put("FirstSoapRequest", SOAPEnvelopeInvokerResponse.get("Soap:EnvelopeRequest"));
            infoBridgingProcess.put("FirstSoapResponse", SOAPEnvelopeInvokerResponse.get("Soap:EnvelopeResponse"));
            infoBridgingProcess.put("Output XML from First Web Service for XBRL UPCasting", SOAPEnvelopeInvokerResponse.get("outputXML"));

            Boolean redirectPage= false;

            CPA cpainfo = orgDBConnector.getinfocpa(cpa_id);

            int cpp_a = cpainfo.getCpp_id_first();
            int cpp_b = cpainfo.getCpp_id_second();

            /*
             * 5. Do upcasting and Downcasting and transform the output of the
             * source web service to an input of the target Web service
             */

            JSONObject transform_response = this.transform(cpp_a, cpp_b, SOAPEnvelopeInvokerResponse.getString("outputXML"), cpainfo.getCpa_info(), complexType);



            String target_xml = transform_response.getString("xml");

            infoBridgingProcess.put("XML after XBRL DownCasting - Input to Second Web Service", target_xml);

            inputargs = parser.parseXML(target_xml, inputargs);

            Parser pSecond = new Parser();
            JSONObject XMLParserInputArgsSecond =   pSecond.getInputArgs( "/home/eleni/Documents/ubi/empower/empower-deliverable-september/empower/xsd/" + second_webServiceInfo.getInt("service_id")+second_webServiceInfo.getString("service_name")+second_webServiceInfo.getString("operation_name")+".xsd",second_webServiceInfo.getString("operation_name"));




            String complexType_output_name_second = (second_webServiceInfo.getString("complexType_output").equalsIgnoreCase(""))?"":second_webServiceInfo.getString("complexType_output").split("\\$")[1];

            SOAPEnvelopeInvoker targetsoapEnvelopeInvoker = new SOAPEnvelopeInvoker(second_webServiceInfo.getString("SoapAdressURL"),
                    second_webServiceInfo.getString("operation_name"),
                    second_webServiceInfo.getString("complexType_input").split("\\$")[1], complexType_output_name_second,
                     second_webServiceInfo.getString("service_namespace"),"");


            JSONObject targetsoapEnvelopeInvokerResponse = targetsoapEnvelopeInvoker.callWebService();

            if (targetsoapEnvelopeInvokerResponse.containsKey("ErrorMessage")) {
               System.out.println("ErroMessage");
            }else if(targetsoapEnvelopeInvokerResponse.get("outputXML").toString().equalsIgnoreCase("")){

                transform_response.getString("xml");
                infoBridgingProcess.put("Upcasting xslt", transform_response.get("xsltRulesFirst"));
                infoBridgingProcess.put("Downcasting xslt", transform_response.get("xsltRulesSecond"));

                infoBridgingProcess.put("SecondSoapRequest", targetsoapEnvelopeInvokerResponse.get("Soap:EnvelopeRequest"));
                infoBridgingProcess.put("SecondSoapResponse", targetsoapEnvelopeInvokerResponse.get("Soap:EnvelopeResponse"));
                infoBridgingProcess.put("SecondoutputXML", "<return>This Operation has no Response</return>");


               /*
                session.setAttribute("source_xml", xml_input_to_print);
                session.setAttribute("target_xml", "<return>This Operation has no Response</return>");
                session.removeAttribute("infoBridgingProcess");
                session.setAttribute("infoBridgingProcess", infoBridgingProcess);
               */

            } else {

                transform_response.getString("xml");
                infoBridgingProcess.put("Upcasting xslt", transform_response.get("xsltRulesFirst"));
                infoBridgingProcess.put("Downcasting xslt", transform_response.get("xsltRulesSecond"));

                infoBridgingProcess.put("SecondSoapRequest", targetsoapEnvelopeInvokerResponse.get("Soap:EnvelopeRequest"));
                infoBridgingProcess.put("SecondSoapResponse", targetsoapEnvelopeInvokerResponse.get("Soap:EnvelopeResponse"));
                infoBridgingProcess.put("SecondoutputXML", targetsoapEnvelopeInvokerResponse.get("outputXML"));

                /*
                    session.setAttribute("source_xml", xml_input_to_print);
                   session.setAttribute("target_xml", targetsoapEnvelopeInvokerResponse.getString("outputXML"));
                       session.removeAttribute("infoBridgingProcess");
                     session.setAttribute("infoBridgingProcess", infoBridgingProcess);
                      this.forwardToPage("/organization/showBridging.jsp?type=service", request, response);
                */
                            }
                        }

                    }


    private JSONObject getWebServiceInfo(JSONObject info) throws WSDLException, FileNotFoundException, IOException, ParserConfigurationException, SAXException, XPathExpressionException, DocumentException, TransformerException {
        MainControlDB mainControlDB = new MainControlDB();
        int service_id = Integer.parseInt(info.get("service_id").toString());

        String operation_name = (String) info.get("operation");
        System.out.println("operation_name: " + operation_name);

        Service service = mainControlDB.getService(service_id);
        WSDLParser wsdlParser = new WSDLParser(service.getWsdl(), service.getNamespace());
        String service_name = service.getName();
        wsdlParser.loadService(service.getName());
        String service_namespace = service.getNamespace();

        String SoapAdressURL = wsdlParser.getSoapAdressURL(service.getName());

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













    public JSONObject transform(int cpp_a, int cpp_b, String xmlData, String cpa_info, LinkedList<String> service_selections) {

        JSONObject trasform_response = new JSONObject();
        OrgDBConnector orgDBConnector = new OrgDBConnector();
        String finalxmloutput = "";
        String finalxmlAllUpcastedXML="";

        String xsltRulesFirst = orgDBConnector.retrieveXLST(cpp_a, "input", cpa_info, service_selections);//input
        String xsltRulesSecond = orgDBConnector.retrieveXLST(cpp_b, "output", cpa_info, service_selections);//output

        trasform_response.put("xsltRulesFirst", xsltRulesFirst);
        trasform_response.put("xsltRulesSecond", xsltRulesSecond);

        System.out.println("CHECK " + xsltRulesFirst);
        System.out.println("CHECK " + xsltRulesSecond);

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


    @Test
       public void  testgetDaoEntity() throws WSDLException, TransformerException, IOException, SAXException, ParserConfigurationException {
           MainControlDB mainControlDB = new MainControlDB();
           Service SecondWservice = mainControlDB.getService(183);
           WSDLParser wsdlParser = new WSDLParser(SecondWservice.getWsdl(), SecondWservice.getNamespace());

           String daoEntity = wsdlParser.getDaoEntity("findClient");

       }




                }
