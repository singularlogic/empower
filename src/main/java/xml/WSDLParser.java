/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.ibm.wsdl.BindingOperationImpl;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.wsdl.*;
import javax.wsdl.Types;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.factory.*;
import javax.wsdl.xml.*;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.xerces.util.DOMInputSource;
import org.w3c.dom.Document;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WSDLParser
{
    private WSDLReader reader;
    private Definition definitionEntity;
    private String targetNamespace;
    private String WSDL_URL;
    private Service service;
    private Binding binding;
    private int messageNumber;
    private int operationsNumber;
    
    public WSDLParser(String WSDL_URL, String targetNamespace)
    throws WSDLException
    {
        this.targetNamespace = new String(targetNamespace);
        this.WSDL_URL = new String(WSDL_URL);
        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        definitionEntity = reader.readWSDL(this.WSDL_URL);
        this.messageNumber = 0;
        this.operationsNumber = 0;
    }
    
    public void loadService(String serviceName)
    {
        this.service = definitionEntity.getService(
                new QName(this.targetNamespace, serviceName));    
        Map ports = service.getPorts();
        Iterator iter = ports.keySet().iterator();

        while(iter.hasNext())
        {
            Binding b = null;
            Port port = service.getPort((String)iter.next());
            b = port.getBinding();        
            PortType portType = b.getPortType();
            List portOperations = portType.getOperations();          
            this.operationsNumber += portOperations.size();
            this.messageNumber += operationsNumber * 2;
        }
    }

    public Service getService()
    {
        return this.service;
    }
    
    public Iterator returnPort()
    {
        PortType portType = binding.getPortType();
        List operations = portType.getOperations();

        return operations.iterator();   
    }
    
    public Iterator returnTypes()
    {
        Types types = definitionEntity.getTypes();
        List list = types.getExtensibilityElements();
 
        return list.iterator();
    }
    
    public Iterator returnBindingsOperations(String portName)
    {
        Port port = service.getPort(portName);
        binding = port.getBinding();
        List bindOperations = binding.getBindingOperations();
        
        return bindOperations.iterator();        
    }

    public Binding returnBinding(String portName)
    {
        Port port = service.getPort(portName);
        return port.getBinding(); 
    }
    
    public void outputPortMessages(BindingOperationImpl boperation, String operationName, String bindingName, PrintWriter out)
    throws IOException
    {
          PortType portType = binding.getPortType();
          List portOperations = portType.getOperations();
          Iterator opIterator = portOperations.iterator();
          Iterator keyIterator;
          int ind = 0;
          String messageQname = null;
/*          
          while (opIterator.hasNext())
          {
                System.out.println(ind++);
 * 
 */
                Operation operation = boperation.getOperation();

                if (!operation.isUndefined())
                {   
                    keyIterator = operation.getInput().getMessage().getParts().keySet().iterator();

                    //strip namespace info
                    messageQname = operation.getInput().getMessage().getQName().toString().replaceAll("\\{.*\\}", "");

//                    out.write("<item text=\"" +  messageQname
//                              + "\" id=\"" + messageQname + "\" nocheckbox=\"true\">");
                    
                    System.out.print(" -" + messageQname + " ");
                    while(keyIterator.hasNext())
                    {
                        String messagePartName = (String) keyIterator.next();  
                        Part messagePart = operation.getInput().getMessage().getPart(messagePartName);
                        
                        System.out.println(messagePartName);  
                        
                        String messageType="";
                        try
                        {
                         messageType = messagePart.getTypeName().toString().replaceAll("\\{.*\\}", "");
                        }
                        catch(Throwable t)
                         {
                           messageType = messageQname;
                         }
                        
                        
                        //String messageType = messagePart.getTypeName().toString().replaceAll("\\{.*\\}", "");
                        System.out.println(" ----------- " + messageType);
                        
                        out.write("<item text=\"" +
                              messagePart.getName() + "\" id=\"input$" + messageType + "$" + operationName + "$" + bindingName + "\"/>");                                 
//                              messagePart.getName() + "\" id=\"input_" + messagePart.getName() + "_" + operationName + "_" + bindingName + "\"/>"); 
                    }
                    
//                    out.write("</item>");

                    keyIterator = operation.getOutput().getMessage().getParts().keySet().iterator();
                    messageQname = operation.getOutput().getMessage().getQName().toString().replaceAll("\\{.*\\}", "");

                    out.write("<item text=\"" +  messageQname
                              + "\" id=\"" + messageQname + "\" nocheckbox=\"true\">");

                    System.out.print(" -2" + messageQname + " ");
                    
                    while(keyIterator.hasNext())
                    {
                        String messagePartName = (String) keyIterator.next();  
                        Part messagePart = operation.getOutput().getMessage().getPart(messagePartName);
                        System.out.println(messagePartName);                        

                        
                        // add this so as to recognize the wsdl produced by netbeans creating webservice
                        String messageType="";
                        try
                        {
                         messageType = messagePart.getTypeName().toString().replaceAll("\\{.*\\}", "");
                        }
                        catch(Throwable t)
                         {
                          messageType = messageQname;
                         }
                        
                        //String messageType = messagePart.getTypeName().toString().replaceAll("\\{.*\\}", "");
                        System.out.println(" ----------- " + messageType);
                    
                        out.write("<item text=\"" +
                              messagePart.getName() + "\" id=\"output$" + messageType + "$" + operationName + "$" + bindingName + "\"/>");
                                
//                              messagePart.getName() + "\" id=\"output_" + messagePart.getName() + "_" + operationName + "_" + bindingName + "\"/>");                                
                        
                    }
                    
                    out.write("</item>");
                }
//          }
          
          out.write("</item>");
    }
    
    public Map returnServicePorts()
    {
        return service.getPorts();
    }

    public Map returnServicePortTypes()
    {
        return this.definitionEntity.getPortTypes();
    }    
    
    public Iterator returnServicePortsString()
    {
                
        Map servicePorts = returnServicePorts();
        return servicePorts.keySet().iterator();
    }
            
    public String extractXSD()
    throws FileNotFoundException, IOException
    {
        String xsdTypes = new String("");
        String line;
        BufferedReader wsdlFile = new BufferedReader(new FileReader(WSDL_URL));
        
        while(((line=wsdlFile.readLine())!=null) && (!line.trim().equals("<wsdl:types>"))) {}
        while(((line=wsdlFile.readLine())!=null) && (!line.trim().equals("</wsdl:types>")))
            xsdTypes = xsdTypes + "\n" + line;
        
        return xsdTypes;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public int getOperationsNumber() {
        return operationsNumber;
    }
    
    public String convertNodeToXMLString(Node n)
    {
        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            StringWriter stw = new StringWriter();
            Transformer serializer = TransformerFactory.newInstance().newTransformer();
            serializer.transform(new DOMSource(n), new StreamResult(stw));
            return stw.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String extractXSD(String element)
    throws FileNotFoundException, IOException, ParserConfigurationException,
    SAXException, XPathExpressionException
    {
        String xsdTypes = new String("");
        String line;
        BufferedReader wsdlFile = new BufferedReader(new FileReader(WSDL_URL));

        xsdTypes += "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    +  " elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">";
        
        while(((line=wsdlFile.readLine())!=null) && (!line.trim().equals("<xs:complexType name=\"" + element + "\">"))) {}
        xsdTypes += line;
        while(((line=wsdlFile.readLine())!=null) && (!line.trim().equals("</xs:complexType>")))
            xsdTypes = xsdTypes + "\n" + line;

        xsdTypes += "</xs:complexType></xs:schema>";
                
        return xsdTypes;
    }
    
    /*

    public String extractXSD(LinkedList<String> elementsList)
    throws FileNotFoundException, IOException, ParserConfigurationException,
    SAXException, XPathExpressionException
    {
        String namesExpression = null;
        String elementName = null;
        String xsdContents = new String("");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(this.WSDL_URL));
        System.out.println(doc.toString());
        doc.getDocumentElement().normalize();
        XPathFactory xFactory = XPathFactory.newInstance();

        XPath xpath = xFactory.newXPath();
        
        //setup basic namespace URLs
        xpath.setNamespaceContext(new NamespaceContext() {       
                public String getNamespaceURI(String prefix) {
                    System.out.println(prefix);
                    
                    if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
                        System.out.println("default");
                        return "http://schemas.xmlsoap.org/wsdl/";
                    }
                    else if(prefix.equals("xs")){
                        return("http://www.w3.org/2001/XMLSchema.xsd");
                    }
                    else                    
                        return null;
                    
                }

                public String getPrefix(String namespaceURI) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                public Iterator getPrefixes(String namespaceURI) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
        });
        
        xsdContents += "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\""
                    +  " elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">";

        if(elementsList == null)
            namesExpression = new String("");
        else
        {
            namesExpression = new String("[@name='" + elementsList.get(0) + "'");
            for(int i=1;i<elementsList.size();i++)
                namesExpression += " or @name='" + elementsList.get(i) + "'";

            namesExpression += "]";
        }
        
        System.out.println("//xs:schema/xs:complexType" + namesExpression);
        XPathExpression expr = xpath.compile("//xs:schema/xs:complexType" + namesExpression);
        Object resultElements = expr.evaluate(doc, XPathConstants.NODESET);
        System.out.println("---- " + xsdContents);

        
        if(resultElements instanceof NodeList)
        {
            NodeList nodes = (NodeList) resultElements;
            // iterate through first level elements
            for (int i = 0; i < nodes.getLength(); i++)
            {
                elementName = nodes.item(i).getAttributes().item(0).toString().replace("name=\"", "");
                xsdContents += new String("\n<" + nodes.item(i).getNodeName()
                                                + " name=\"" + elementName +  ">");

                expr = xpath.compile("//*[local-name()='complexType' and @name=\""
                                    + elementName + "]/child::*");

                Object nestedElements = expr.evaluate(doc, XPathConstants.NODE);
                Node node = (Node) nestedElements;
                xsdContents += convertNodeToXMLString(node);
                xsdContents += "\n</xs:complexType>";
            }
        }

        System.out.println("---- " + xsdContents);
        
        xsdContents += "\n</xs:schema>";
        
        //remove unecessary attibutes and elements
        //so that annotator can process the xsd file
        xsdContents = xsdContents.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
        xsdContents = xsdContents.replaceAll("<xs:complexType\\s.*>", "<xs:complexType>");
        return xsdContents;
    }    
    
    public String extractXSD(LinkedList<String> elementsList)
    throws FileNotFoundException, IOException, ParserConfigurationException,
    SAXException, XPathExpressionException
    {
        String namesExpression = null;
        String elementName = null;
        String xsdContents = new String("");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(this.WSDL_URL));
        System.out.println(doc.toString());
        doc.getDocumentElement().normalize();
        XPathFactory xFactory = XPathFactory.newInstance();

        XPath xpath = xFactory.newXPath();
        
        //setup basic namespace URLs
        xpath.setNamespaceContext(new NamespaceContext() {       
                public String getNamespaceURI(String prefix) {
                    System.out.println(prefix);
                    
                    if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
                        System.out.println("default");
                        return "http://schemas.xmlsoap.org/wsdl/";
                    }
                    else if(prefix.equals("xsd")){
                        return("http://www.w3.org/2001/XMLSchema.xsd");
                    }
                    else                    
                        return null;
                    
                }

                public String getPrefix(String namespaceURI) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                public Iterator getPrefixes(String namespaceURI) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
        });
        
        xsdContents += "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                    +  " elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">";

        if(elementsList == null)
            namesExpression = new String("");
        else
        {
            namesExpression = new String("[@name='" + elementsList.get(0) + "'");
            for(int i=1;i<elementsList.size();i++)
                namesExpression += " or @name='" + elementsList.get(i) + "'";

            namesExpression += "]";
        }
        
        System.out.println("//xsd:schema/xsd:element" + namesExpression);
        XPathExpression expr = xpath.compile("//xsd:schema/xsd:element" + namesExpression);
        Object resultElements = expr.evaluate(doc, XPathConstants.NODESET);
        System.out.println("---- " + xsdContents);

        
        if(resultElements instanceof NodeList)
        {
            NodeList nodes = (NodeList) resultElements;
            // iterate through first level elements
            for (int i = 0; i < nodes.getLength(); i++)
            {
                elementName = nodes.item(i).getAttributes().item(0).toString().replace("name=\"", "");
                xsdContents += new String("\n<" + nodes.item(i).getNodeName()
                                                + " name=\"" + elementName +  ">");

                expr = xpath.compile("//*[local-name()='element' and @name=\""
                                    + elementName + "]/child::*");

                Object nestedElements = expr.evaluate(doc, XPathConstants.NODE);
                Node node = (Node) nestedElements;
                xsdContents += convertNodeToXMLString(node);
                xsdContents += "\n</xsd:element>";
            }
        }

        System.out.println("---- " + xsdContents);
        
        xsdContents += "\n</xsd:schema>";
        
        //remove unecessary attibutes and elements
        //so that annotator can process the xsd file
        xsdContents = xsdContents.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
        xsdContents = xsdContents.replaceAll("<xsd:complexType\\s.*>", "<xsd:complexType>");
        return xsdContents;
    }
  */     
    public void outputToXML(PrintWriter out)
    {
        int idNum = 1;
        int internalIdNum;
        Map servicePorts;
        Binding portBinding;
        Iterator portsIterator, operationIterator;
        
        try
        {
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.write("<tree id=\"0\">");
                                            
            servicePorts = returnServicePorts();
            portsIterator = servicePorts.keySet().iterator();

            // drill down the wsdl hierarchy
            // starting from definition
            while(portsIterator.hasNext())
            {
                internalIdNum = 1;
                String portName = portsIterator.next().toString();

                out.write("<item text=\"" + portName +
                          "\" id=\"" + portName + "\" nocheckbox=\"true\">");
                

                portBinding = returnBinding(portName);

                int internalIdNum2 = 1;
                    
                String binding = (String) portBinding.getQName().getLocalPart();

                out.write("<item text=\"" + binding +
                          "\" id=\"" + binding +"\" nocheckbox=\"true\">");


                operationIterator = this.returnBindingsOperations(portName);
                while(operationIterator.hasNext())
                {
                        internalIdNum2 = 1;
                        BindingOperationImpl operationBinding = 
                                   (BindingOperationImpl)operationIterator.next();
                        
                        String operationName = operationBinding.getName();
                        System.out.println(" IN " + operationName);
                        out.write("<item text=\"" + operationName +
                                  "\" id=\"" + operationName + "\" nocheckbox=\"true\">");
                        
                        outputPortMessages(operationBinding, operationName, binding, out);
                }
                
                internalIdNum++;
                out.write("</item>");

                idNum++;
                out.write("</item>");
            }
            
            out.write("</tree>");
            out.close();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
    }    

    public void outputFunctionsToXML(PrintWriter out)
    {
        int idNum = 1;
        int internalIdNum;
        Map servicePorts;
        Binding portBinding;
        Iterator portsIterator, operationIterator;
        
        try
        {
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.write("<tree id=\"0\">");
                                            
            servicePorts = returnServicePorts();
            portsIterator = servicePorts.keySet().iterator();

            // drill down the wsdl hierarchy
            // starting from definition
            while(portsIterator.hasNext())
            {
                internalIdNum = 1;
                String portName = portsIterator.next().toString();

                out.write("<item text=\"" + portName +
                          "\" id=\"" + portName + "\" nocheckbox=\"true\">");
                

                portBinding = returnBinding(portName);

                int internalIdNum2 = 1;
                    
                String binding = (String) portBinding.getQName().getLocalPart();

                out.write("<item text=\"" + binding +
                          "\" id=\"" + binding +"\" nocheckbox=\"true\">");


                operationIterator = this.returnBindingsOperations(portName);
                while(operationIterator.hasNext())
                {
                        internalIdNum2 = 1;
                        BindingOperationImpl operationBinding = 
                                   (BindingOperationImpl)operationIterator.next();
                        
                        String operationName = operationBinding.getName();

                        out.write("<item text=\"" + operationName +
                                  "\" id=\"" + operationName + "\"/>");
                        
                        //outputPortMessages(operationName, binding, out);
                }
                internalIdNum++;
                out.write("</item>");

                idNum++;
                out.write("</item>");
            }
            
            out.write("</tree>");
            out.close();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
    }    
    
    public void outputFunctionsToXMLFromRoot(PrintWriter out,String service_name, int service_id)
    {
        int idNum = 1;
        int internalIdNum;
        Map servicePorts;
        Binding portBinding;
        Iterator portsIterator, operationIterator;
        
        try
        {
             out.write("<item text=\"" +service_name  +"\" id=\"" + service_id + "\" nocheckbox=\"true\">");
            //String elaaa = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<tree id=\"0\">";
                                            
            servicePorts = returnServicePorts();
            portsIterator = servicePorts.keySet().iterator();

            // drill down the wsdl hierarchy
            // starting from definition
            while(portsIterator.hasNext())
            {
                internalIdNum = 1;
                String portName = portsIterator.next().toString();

                out.write("<item text=\"" + portName +
                          "\" id=\"" +service_id+"$"+ portName + "\" nocheckbox=\"true\">");
                
                //elaaa = elaaa + "<item text=\"" + portName +
                //          "\" id=\"" + portName + "\" nocheckbox=\"true\">";
                
                portBinding = returnBinding(portName);

                int internalIdNum2 = 1;
                    
                String binding = (String) portBinding.getQName().getLocalPart();

                out.write("<item text=\"" + binding +
                          "\" id=\""+service_id+"$" + binding +"\" nocheckbox=\"true\">");

                  //elaaa = elaaa + "<item text=\"" + binding +
                  //         "\" id=\"" + binding +"\" nocheckbox=\"true\">";

                operationIterator = this.returnBindingsOperations(portName);
                while(operationIterator.hasNext())
                {
                        internalIdNum2 = 1;
                        BindingOperationImpl operationBinding = 
                                   (BindingOperationImpl)operationIterator.next();
                        
                        String operationName = operationBinding.getName();

                        out.write("<item text=\"" + operationName +
                                  "\" id=\"" +service_id+"$"+operationName + "\"/>");
                        
                          //elaaa = elaaa + "<item text=\"" + operationName +
                           //        "\" id=\"" + operationName + "\"/>";
                        
                        //outputPortMessages(operationName, binding, out);
                }
                internalIdNum++;
                out.write("</item>");
                 //elaaa = elaaa + "</item>";

                idNum++;
                out.write("</item>");
                // elaaa = elaaa + "</item>";
            }
            out.write("</item>");
            //out.write("</tree>");
             //elaaa = elaaa + "</tree>";
            //out.close();
            
            //System.out.println("OUUUUUUTTTT: "+elaaa);
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
    } 
    
    
    public String outputFunctionsToXMLFromRoot(String xml,String service_name, int service_id)
    {
        int idNum = 1;
        int internalIdNum;
        Map servicePorts;
        Binding portBinding;
        Iterator portsIterator, operationIterator;
        String xml_string="";
        
        try
        {
            xml_string= xml.concat("<item text=\"" +service_name  +"\" id=\"" + service_id + "\" nocheckbox=\"true\">");
              
            System.out.println("xml_string: "+xml_string);
            servicePorts = returnServicePorts();
            portsIterator = servicePorts.keySet().iterator();

            // drill down the wsdl hierarchy
            // starting from definition
            while(portsIterator.hasNext())
            {
                internalIdNum = 1;
                String portName = portsIterator.next().toString();

                xml_string = xml_string.concat("<item text=\"" + portName +
                          "\" id=\"" +service_id+"$"+ portName + "\" nocheckbox=\"true\">");
               
                portBinding = returnBinding(portName);

                int internalIdNum2 = 1;
                    
                String binding = (String) portBinding.getQName().getLocalPart();

                xml_string = xml_string.concat("<item text=\"" + binding +
                          "\" id=\""+service_id+"$" + binding +"\" nocheckbox=\"true\">");

                operationIterator = this.returnBindingsOperations(portName);
                while(operationIterator.hasNext())
                {
                        internalIdNum2 = 1;
                        BindingOperationImpl operationBinding = 
                                   (BindingOperationImpl)operationIterator.next();
                        
                        String operationName = operationBinding.getName();

                   xml_string = xml_string.concat("<item text=\"" + operationName +
                                  "\" id=\"" +service_id+"$"+operationName + "\"/>");
                        
                }
                internalIdNum++;
               xml_string = xml_string.concat("</item>");
                
                idNum++;
               xml_string = xml_string.concat("</item>");
               
            }
           xml_string = xml_string.concat("</item>");
           System.out.println("xml_string1: "+xml_string);
           
           return xml_string;
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
        return xml_string;
    } 

    public String getFirstPortBinding()
    {
        Map servicePorts;
        Binding portBinding;
        Iterator portsIterator, operationIterator;

        servicePorts = returnServicePortTypes();
        portsIterator = servicePorts.keySet().iterator();
        
        return portsIterator.next().toString().replaceAll("\\{.*\\}", "");
    }
    
    public void outputPortsToXML(PrintWriter out)
    {
        int idNum = 1;
        int internalIdNum;
        Map servicePorts;
        Binding portBinding;
        Iterator portsIterator, operationIterator;
        
        try
        {
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.write("<tree id=\"0\">");
                                            
            servicePorts = returnServicePortTypes();
            portsIterator = servicePorts.keySet().iterator();

            // drill down the wsdl hierarchy
            // starting from definition
            while(portsIterator.hasNext())
            {
                internalIdNum = 1;
                String portName = portsIterator.next().toString().replaceAll("\\{.*\\}", "");

                out.write("<item text=\"" + portName +
                          "\" id=\"" + portName + "\"/>");
                

            }
            
            out.write("</tree>");
            out.close();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
    }    
    
    
    public static void main(String[] args) throws FileNotFoundException
    {
        LinkedList<String> elementsList = new LinkedList<String>();
        
        elementsList.add("myObject");
        //elementsList.add("product2");

        try {
            WSDLParser wsdlParser = new WSDLParser("C:\\Users\\user\\Desktop\\ResellerImpl.wsdl", "http://ws.panos.com/");
            wsdlParser.loadService("ResellerImplService");
            //System.out.println(wsdlParser.getMessageNumber() + " " + wsdlParser.getOperationsNumber());
            //wsdlParser.outputToXML(new PrintWriter(System.out));
            //wsdlParser.outputToXML(new PrintWriter(System.out));
            //System.out.println(wsdlParser.getOperationsNumber());            
            //WSDLParser wsdlParser = new WSDLParser("D:\\xml_rep\\wsdl\\Prosorino8_JustAService.wsdl", "urn:xmltoday-delayed-quotes");
            //wsdlParser.loadService("JustAServce");
            System.out.println(wsdlParser.extractXSD("myObject"));
            //wsdlParser.outputToXML(new PrintWriter("C:\\Users\\user\\Desktop\\empower\\test.xml"));
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
    }
    
    
    public LinkedList<String> getXSDAttibutes(String sourceOperationName)
    {
        int idNum = 1;
        int internalIdNum;
        Map servicePorts;
        Binding portBinding;
        Iterator portsIterator, operationIterator;
        LinkedList<String> xsd_to_string= new LinkedList<String>();
        
        try
        {
                                                       
            servicePorts = returnServicePorts();
            portsIterator = servicePorts.keySet().iterator();

            // drill down the wsdl hierarchy
            // starting from definition
            while(portsIterator.hasNext())
            {
                internalIdNum = 1;
                String portName = portsIterator.next().toString();
                
                System.out.println("---Trying to get the input fields--------------");

                System.out.println("portName: "+ portName);
                

                portBinding = returnBinding(portName);

                int internalIdNum2 = 1;
                    
                String binding = (String) portBinding.getQName().getLocalPart();
                
                System.out.println("binding: " + binding);
               


                operationIterator = this.returnBindingsOperations(portName);
                while(operationIterator.hasNext())
                {
                        internalIdNum2 = 1;
                        BindingOperationImpl operationBinding = 
                                   (BindingOperationImpl)operationIterator.next();
                        
                        String operationName = operationBinding.getName();
                        
                        if (operationName.equalsIgnoreCase(sourceOperationName)){
                        
                        System.out.println("operationName" + operationName);
                        
                        xsd_to_string = outputPortMessages_Eleni(operationBinding, operationName, binding);
                        }
                }
                
                internalIdNum++;
               

                idNum++;
               
            }
            
            
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
        return xsd_to_string;
    }
    
    
    public LinkedList<String> outputPortMessages_Eleni(BindingOperationImpl boperation, String operationName, String bindingName)
    throws IOException
    {
          PortType portType = binding.getPortType();
          List portOperations = portType.getOperations();
          Iterator opIterator = portOperations.iterator();
          Iterator keyIterator;
          int ind = 0;
          String messageQname = null;
          LinkedList<String> element = new LinkedList<String>();



                Operation operation = boperation.getOperation();

                if (!operation.isUndefined())
                {   
                    keyIterator = operation.getInput().getMessage().getParts().keySet().iterator();

                    //strip namespace info
                    messageQname = operation.getInput().getMessage().getQName().toString().replaceAll("\\{.*\\}", "");

                    
                    //System.out.print(" -" + messageQname + " ");
                    while(keyIterator.hasNext())
                    {
                        String messagePartName = (String) keyIterator.next();  
                        Part messagePart = operation.getInput().getMessage().getPart(messagePartName);

                        // add this so as to recognize the wsdl produced by netbeans creating webservice
                        String messageType="";
                        try
                        {
                         messageType = messagePart.getTypeName().toString().replaceAll("\\{.*\\}", "");
                        }
                        catch(Throwable t)
                         {
                          messageType = messageQname;
                         }
                        
                        //String messageType = messagePart.getTypeName().toString().replaceAll("\\{.*\\}", "");

                        System.out.println("INFO THAT ELENI HAS TO GET: input$" + messageType + "$" + operationName + "$" + bindingName);                                 
                        element.add(0, "input$" + messageType + "$" + operationName + "$" + bindingName);
                        
                    }
                    
                    
                    keyIterator = operation.getOutput().getMessage().getParts().keySet().iterator();
                    messageQname = operation.getOutput().getMessage().getQName().toString().replaceAll("\\{.*\\}", "");
                   
                    while(keyIterator.hasNext())
                    {
                        String messagePartName = (String) keyIterator.next();  
                        Part messagePart = operation.getOutput().getMessage().getPart(messagePartName);
                                             
                        // add this so as to recognize the wsdl produced by netbeans creating webservice
                        String messageType="";
                        try
                        {
                         messageType = messagePart.getTypeName().toString().replaceAll("\\{.*\\}", "");
                        }
                        catch(Throwable t)
                         {
                          messageType = messageQname;
                         }
                        //String messageType = messagePart.getTypeName().toString().replaceAll("\\{.*\\}", "");
                        element.add(1, "output$" + messageType + "$" + operationName + "$" + bindingName);
                        
                    }
                }
                
                return element;
    }
    
    
    public String getSoapAdressURL(String serviceName)
    {
       String actualUrl = null;
         this.service = definitionEntity.getService(
                new QName(this.targetNamespace, serviceName));    
       
            Collection<Port> ports = (service.getPorts().values());
            for (Port port : ports) {
                List extensions = port.getExtensibilityElements();
                for (Object extension : extensions) {
                    if (extension instanceof SOAP12Address) {
                        actualUrl = ((SOAP12Address)extension).getLocationURI();
                    } else if (extension instanceof SOAPAddress) {
                        actualUrl = ((SOAPAddress)extension).getLocationURI();
                    }
                }
            }
            return actualUrl;
        
    }
}
