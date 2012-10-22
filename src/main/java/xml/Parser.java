/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
import org.dom4j.DocumentException;
import org.dom4j.Namespace;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author eleni
 */
public class Parser {

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

        for (Iterator i = root.elementIterator(); i.hasNext();) {
            org.dom4j.Element element = (org.dom4j.Element) i.next();
            element_name = element.getName();
        }

        List elementstoprint = root.elements(element_name);

        LinkedList<String> xmlnodes = new LinkedList<String>();
        Iterator il = elementstoprint.iterator();
        while (il.hasNext()) {
            org.dom4j.Element element = (org.dom4j.Element) il.next();
            System.out.println("ElementsasXML: " + element.getName() + "   " + element.asXML());
            xmlnodes.add(element.asXML());
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
        }

        return inputargs;
    }

    
}
