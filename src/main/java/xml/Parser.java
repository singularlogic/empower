/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import net.sf.json.JSONObject;
import org.apache.xerces.dom.DeferredElementImpl;
import org.cyberneko.html.HTMLElements;
import org.dom4j.DocumentException;
import org.dom4j.Namespace;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import static java.util.Arrays.asList;

/**
 *
 * @author eleni
 */
public class Parser {


    List<String> dataTypes = asList("string","int","datetime","decimal","dateTime","float");



    public Parser() {
    }
    
     /*
     * remove types attribute from extracted schema so as to accept it the Mediation Portal
     */ 
    public String removeTypes(String xsdtypes) throws TransformerConfigurationException, TransformerException {
         StreamResult result = null;
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            //parse using builder to get DOM representation of the XML file
            Document dom = db.parse(new InputSource(new StringReader(xsdtypes)));

            // Get a list of all elements in the document
            NodeList list = dom.getElementsByTagName("*");
            for (int i = 0; i < list.getLength(); i++) {
                // Get element
                Element element = (Element) list.item(i);
                element.removeAttribute("type");
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(dom);
            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return result.getWriter().toString();

    }
    
     public List getNamespaces(String xml) throws DocumentException {

        SAXReader reader = new SAXReader();
        InputStream xml_stream = new ByteArrayInputStream(xml.getBytes());
        org.dom4j.Document document = reader.read(xml_stream);

        org.dom4j.Element root = document.getRootElement();
        return root.declaredNamespaces();
    }
    
    public String removeNamespaces(String xml) throws DocumentException {

        SAXReader reader = new SAXReader();
        InputStream xml_stream = new ByteArrayInputStream(xml.getBytes());
        org.dom4j.Document document = reader.read(xml_stream);

        org.dom4j.Element root = document.getRootElement();
        List namespaces = root.declaredNamespaces();

        Iterator i = namespaces.iterator();
        while (i.hasNext()) {
            Namespace namespace = (Namespace) i.next();
            System.out.println("namespace Removed: " + namespace.toString());
            root.remove(namespace);
        }
        return root.asXML();

    }

    public String addNamespaces(String xml, List namespaces) throws DocumentException {

        SAXReader reader = new SAXReader();
        InputStream xml_stream = new ByteArrayInputStream(xml.getBytes());
        org.dom4j.Document document = reader.read(xml_stream);

        org.dom4j.Element root = document.getRootElement();
        Iterator i = namespaces.iterator();
        while (i.hasNext()) {
            Namespace namespace = (Namespace) i.next();
            root.add(namespace);
        }
        return root.asXML();

    }
    
     public LinkedList<String> parseXML(String xml) throws DocumentException {

        SAXReader reader = new SAXReader();
        InputStream xml_stream = new ByteArrayInputStream(xml.getBytes());
        org.dom4j.Document document = reader.read(xml_stream);

        org.dom4j.Element root = document.getRootElement();
        String element_name = "";
        LinkedList<String> elementsXML = new LinkedList<String>();
        LinkedList<String> xmlnodes = new LinkedList<String>();

        for (Iterator i = root.elementIterator(); i.hasNext();) {
            org.dom4j.Element element = (org.dom4j.Element) i.next();
            element_name = element.getName();
         
            if (!elementsXML.contains(element_name)) elementsXML.add(element_name);
             System.out.println("ti tha tiposeis tora?: " + element_name);
             
        }
        
        Iterator iteratorelementsXML = elementsXML.iterator();
         while (iteratorelementsXML.hasNext()) {
         List elementstoprint = root.elements((String)iteratorelementsXML.next());
        
        Iterator il = elementstoprint.iterator();
        while (il.hasNext()) {
            org.dom4j.Element element = (org.dom4j.Element) il.next();
            //System.out.println("ElementsasXML: " + element.getName() + "   " + element.asXML());
            xmlnodes.add(element.asXML());
        }
         
         }

        return xmlnodes;


    }
     
      /*
     * parse the input xml add its elements to the inputargs JSONObject
     */
    public JSONObject parseXML(String xml, JSONObject inputargs) throws DocumentException {

        SAXReader reader = new SAXReader();
        InputStream xml_stream = new ByteArrayInputStream(xml.getBytes());
        org.dom4j.Document document = reader.read(xml_stream);

        org.dom4j.Element root = document.getRootElement();
        inputargs.clear();

        // iterate through child elements of root
        for (Iterator i = root.elementIterator(); i.hasNext();) {
            org.dom4j.Element element = (org.dom4j.Element) i.next();
            inputargs.put(element.getName(), element.getText());
            if(element.elementIterator().hasNext()){

                for (Iterator k = element.elementIterator(); k.hasNext();) {
                    org.dom4j.Element elementk = (org.dom4j.Element) k.next();
                    inputargs.put(elementk.getName(), elementk.getText());}

            }
        }

        return inputargs;
    }


    /*
     Given an xsd an a nodetype, it returns the type="" attributes for the given nodetype
      */
    public JSONObject getTypes(String xsdtypes) throws TransformerConfigurationException, TransformerException {
        StreamResult result = null;
        JSONObject typeToreturn = new JSONObject();
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            //parse using builder to get DOM representation of the XML file
            Document dom = db.parse(new InputSource(new StringReader(xsdtypes)));

            // Get a list of all elements in the document
            NodeList list = dom.getElementsByTagName("xs:element");

            for (int i = 0; i < list.getLength(); i++) {
                // Get element
                Element element = (Element) list.item(i);
                System.out.println("This is the element: "+element.toString());
                System.out.println("And this is the type: " + element.getAttribute("type"));
                typeToreturn.put("namespace",element.getAttribute("type").split(":")[0]);
                typeToreturn.put("entity",element.getAttribute("type").split(":")[1]);
            }

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return typeToreturn;

    }



    /*
    Given an xsd structure it removes the unbounded elements of the structure (one to Many Keys). If the structure is a FK it also
    removes the second foreign keys elements.
    */
    public String alterFKs(String nodeStructure, boolean secondForeingKeys) throws TransformerException {

        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        StreamResult result = null;
        Node parentNode = null;
        Document dom = null;
        Parser parser = new Parser();
        String ComplexTypeToReturn="";

        try {
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            //parse using builder to get DOM representation of the XML file
            dom = db.parse(new InputSource(new StringReader(nodeStructure)));


            // Get a list of all elements in the document
            NodeList list = dom.getElementsByTagName("xs:element");
            ArrayList<Element> elLis = new ArrayList<Element>();

            for (int i = 0; i < list.getLength(); i++) {
                // Get element
                Element element = (Element) list.item(i);

                 //select secondForeing keys so as to remove
                if( secondForeingKeys &&  !parser.isbaseDataType(element.getAttribute("type"))  && !element.hasAttribute("maxOccurs")){
                   elLis.add(element);
                }
                parentNode = element.getParentNode();
                //select one to Many Keys  so as to remove
                if(  !parser.isbaseDataType(element.getAttribute("type"))  && element.hasAttribute("maxOccurs") && element.getAttribute("maxOccurs").equalsIgnoreCase("unbounded")){
                    elLis.add(element);
                }
            }

            for (Element el : elLis) {
                parentNode.removeChild(el);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(dom);
            transformer.transform(source, result);


        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        ComplexTypeToReturn = result.getWriter().toString();
        ComplexTypeToReturn= ComplexTypeToReturn.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>","");

        return ComplexTypeToReturn;
    }

    /*
   Given an xsd structure it replaces the foreign keys as xs:int and  removes one to Many Keys
    */
    public String alterFKsOLD(String nodeStructure) throws TransformerConfigurationException, TransformerException {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        StreamResult result = null;
        Node parentNode = null;
        Document dom = null;
        Parser parser = new Parser();

        try {
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            //parse using builder to get DOM representation of the XML file
             dom = db.parse(new InputSource(new StringReader(nodeStructure)));


            // Get a list of all elements in the document
            NodeList list = dom.getElementsByTagName("xs:element");
            ArrayList<Element> elLis = new ArrayList<Element>();

            for (int i = 0; i < list.getLength(); i++) {
                // Get element
                Element element = (Element) list.item(i);
                System.out.println("This is the element: "+element.toString());
                System.out.println("And this is the type: " + element.getAttribute("type"));

                // replace the foreign keys as xs:int
                //if(!element.getAttribute("type").split(":")[0].equalsIgnoreCase("xs") && element.getAttribute("name").contains("Id")){
                if(  !parser.isbaseDataType(element.getAttribute("type"))  && !element.hasAttribute("maxOccurs")){
                    element.setAttribute("type", "xs:int");
                }
                 parentNode = element.getParentNode();
                //select one to Many Keys  so as to remove
                //if(!element.getAttribute("type").split(":")[0].equalsIgnoreCase("xs")){
                if(  !parser.isbaseDataType(element.getAttribute("type"))  && element.hasAttribute("maxOccurs") && element.getAttribute("maxOccurs").equalsIgnoreCase("unbounded")){
                    elLis.add(element);
                }
            }

            for (Element el : elLis) {
                parentNode.removeChild(el);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(dom);
            transformer.transform(source, result);


        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return result.getWriter().toString();
    }



    /*
   Given an xsd getInputArgs so as to prepare web service invocation form
    */
    public JSONObject getInputArgs(String xsdLocation,String operation_name) throws TransformerConfigurationException, TransformerException, IOException, ParserConfigurationException {

        JSONObject typeToreturn = new JSONObject();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document xmldocument = builder.newDocument();
        Element root = (Element) xmldocument.createElement("rootElement");
        xmldocument.appendChild(root);
        Element el;
        List<String> TypesToExposeinForm = new ArrayList<String>();

        String xsdtypes = getFileAsString(xsdLocation);

        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            //parse using builder to get DOM representation of the XML file
            Document dom = db.parse(new InputSource(new StringReader(xsdtypes)));
            // Get a list of all elements in the document
            NodeList list = dom.getElementsByTagName("xs:complexType");

            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);


                if (element.hasAttribute("name") && element.getAttribute("name").equalsIgnoreCase(operation_name) )
                {
                    NodeList childrenOfOperationNode =  element.getElementsByTagName("xs:element");

                    for (int j = 0; j < childrenOfOperationNode.getLength(); j++) {
                        Element nestedElement = (Element) childrenOfOperationNode.item(j);

                        if (!isbaseDataType(nestedElement.getAttribute("type")))
                        {
                            Node childOfNodeToAdd = getComplexTypes(dom, nestedElement.getAttribute("name"),TypesToExposeinForm);
                            Node nodeToImport = xmldocument.importNode(childOfNodeToAdd,true);
                            xmldocument.getDocumentElement().appendChild(nodeToImport);
                        }else{
                            el = (Element) xmldocument.createElement(nestedElement.getAttribute("name"));
                            root.appendChild(el);
                            TypesToExposeinForm.add(nestedElement.getAttribute("name"));
                        }
                    }
                }
            }

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StreamResult result;
        result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(xmldocument);
        transformer.transform(source, result);

        typeToreturn.put("Typesinxml",result.getWriter().toString());
        typeToreturn.put("TypesToExposeinForm",TypesToExposeinForm);

        return typeToreturn;

    }

        public Node getComplexTypes(Document dom, String complexTypeName, List<String> TypesToExposeinForm) throws ParserConfigurationException {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document xmldocument = builder.newDocument();
            Element root = (Element) xmldocument.createElement("m:"+complexTypeName);
            xmldocument.appendChild(root);

            Node x;
            Element el;

            try {
             // Get a list of all elements in the document
            NodeList list = dom.getElementsByTagName("xs:complexType");

            for (int i = 0; i < list.getLength(); i++) {
                // Get element
                Element element = (Element) list.item(i);


                if (element.hasAttribute("name") && element.getAttribute("name").equalsIgnoreCase(complexTypeName) )

                {
                    NodeList childrenOfOperationNode =  element.getElementsByTagName("xs:element");

                    for (int j = 0; j < childrenOfOperationNode.getLength(); j++) {
                        // Get element
                        Element nestedElement = (Element) childrenOfOperationNode.item(j);

                        if (!isbaseDataType(nestedElement.getAttribute("type")))
                        {
                            Node childOfNodeToAdd = getComplexTypes(dom,nestedElement.getAttribute("type"),TypesToExposeinForm);
                            xmldocument.appendChild(childOfNodeToAdd);

                        }  else{
                            el = (Element) xmldocument.createElement(nestedElement.getAttribute("name"));
                            Element appendedChild = (Element) root.appendChild(el);
                            TypesToExposeinForm.add(nestedElement.getAttribute("name"));
                            if (nestedElement.hasAttribute("fk"))  appendedChild.setAttribute("fk",nestedElement.getAttribute("fk"));
                        }
                    }


                    }
            }


            }catch (Exception e){

                     System.out.println(e.getMessage());
            }

            return root;
        }


        public boolean isbaseDataType(String dataType){

            String type=null;

            try{
               type = dataType.split(":")[1];
            }
            catch (Exception e){
               type= dataType;
            }

            if (dataTypes.contains(type)) return true;
            else return false;


        }


         //Get file as string so as to parse it
        public String getFileAsString(String xsdLocation) throws IOException {
            String xsdtypes="";

            try{
            // Open the file
            FileInputStream fstream = new FileInputStream(xsdLocation);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine=null;

            //Read File Line By Line
            while ((strLine = br.readLine()) != null)   {
                // Print the content on the console
                System.out.println (strLine);
                xsdtypes +=  strLine;
            }
            //Close the input stream
            in.close();

            System.out.println("xsdtypes: "+xsdtypes);

        } catch (IOException io) {
              System.out.println(io.toString());
            }
            return xsdtypes;
        }
}
