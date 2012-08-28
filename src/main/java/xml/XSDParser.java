/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import dataaccesslayer.Schema;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

/**
 *
 * @author eleni
 */
public class XSDParser  {
    public Schema schema = new Schema();
    
    public XSDParser(Schema schema) {
        
        this.schema = schema;
    }
   
    
    public String convertSchemaToXML() throws ParserConfigurationException, SAXException, IOException{
        
         File x = new File(schema.getLocation());
         SAXParserFactory f = SAXParserFactory.newInstance();
         //System.out.println(f.toString());
         SAXParser p = f.newSAXParser();
         //System.out.println(p.toString());
         myParser handler = new myParser();
         p.parse(x, handler);
            
        return handler.getXML();
    }
    
    public class myParser extends DefaultHandler
    {
    boolean complexTypefl = false;
    boolean schemafl = false;
    public ArrayList<String> xmlToReturn = new ArrayList<String>();
    String xmlToString;
    //String schema;
    
    public String getXML(){
        Iterator<String> xml_it = xmlToReturn.iterator();
        while (xml_it.hasNext()){
        xmlToString += (xml_it.next());
        
        }
        System.out.println("xmlToString of getXML() : "+xmlToString);

        return xmlToString;
    }
        
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {


        if (qName.equalsIgnoreCase("xs:schema")) {
            schemafl= true;
            xmlToReturn.add("<tree id='0'>"
                    + "<item text='"+schema.getService()+"' id='"+schema.getService()+"' nocheckbox='true'>"
                    + "<item text='"+schema.getOperation()+"' id='"+schema.getOperation()+"' nocheckbox='true'>"
                    + "<item text='"+schema.getName()+"' id='"+schema.getName()+"' nocheckbox='true'>");
        }

        if (qName.equalsIgnoreCase("xs:complexType")) {
            complexTypefl = true;

            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    xmlToReturn.add("<item text='" + attributes.getValue(i)+"("+schema.getInputoutput()+")" + "' id='" + attributes.getValue(i) +"--"+schema.getInputoutput()+"--"+ schema.getOperation_id()+"--"+schema.getOp_taxonomy_id()+"--"+schema.getCvp_id()+"--"+schema.getService_id()+"' >");
                }
            }
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equals("xs:complexType")) {
            xmlToReturn.add("</item>");
        }
        
        if (qName.equals("xs:schema")) {
            xmlToReturn.add("</item></item></item></tree>");
           //System.out.println(xmlToReturn);

        }
        
    }

    public void characters(char ch[], int start, int length) throws SAXException {

        if (complexTypefl) {
            xmlToString = new String(ch, start, length);
            complexTypefl = false;
        }
        if (schemafl) {
            //schema = new String(ch, start, length);
            schemafl = false;
        }
    }
    
    }
    
    

    public static void main(String[] args) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {

       
            String pathname = "/home/eleni/Desktop/semantix and stuff folder/filesystem/semantix_rep/xsd_tmp/source_changed_empower.xsd";
            String xmltostring;
            //XSDParser p = new XSDParser();
            //xmltostring = p.convertSchemaToXML(pathname);            
           // System.out.println("Aqui llega?"+xmltostring);



    }

 
}
