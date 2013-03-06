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
    private Schema schema;
    //public Schema schema = new Schema();
    private int numOfComplexTypes = 0;
    
    public XSDParser(Schema schema) {
        
       this.schema = schema;
    }
   
    
    public String convertSchemaToXML() throws ParserConfigurationException, SAXException, IOException{
        
         File x = new File(schema.getLocation());
         SAXParserFactory f = SAXParserFactory.newInstance();
         SAXParser p = f.newSAXParser();
         myParser handler = new myParser();
         p.parse(x, handler);
            
        return handler.getXML();
    }
    
    public ArrayList<String> getXMLElements() throws ParserConfigurationException, SAXException, IOException{
        
         File x = new File(schema.getLocation());
         SAXParserFactory f = SAXParserFactory.newInstance();
         SAXParser p = f.newSAXParser();
         myParser handler = new myParser();
         p.parse(x, handler);

        return handler.getElements();
    }
    
    public class myParser extends DefaultHandler
    {
    boolean complexTypefl = false;
    boolean schemafl = false;
    boolean elementfl = false;
    public ArrayList<String> xmlToReturn = new ArrayList<String>();
    public ArrayList<String> elementsList = new ArrayList<String>();
    String xmlToString;
    
    public String getXML(){
        Iterator<String> xml_it = xmlToReturn.iterator();
        while (xml_it.hasNext()){
        xmlToString += (xml_it.next());
        }
        //System.out.println("xmlToString of getXML() : "+xmlToString);

        return xmlToString;
    }
    public ArrayList<String> getElements(){
        
        return elementsList;
    }
        
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {


        if (qName.equalsIgnoreCase("xs:schema") || qName.equalsIgnoreCase("xsd:schema") ) {
            schemafl= true;
            xmlToReturn.add("<tree id='0'>"
                   // + "<item text='"+schema.getService()+"' id='"+schema.getService()+"' nocheckbox='true'>"
                    //+ "<item text='"+schema.getOperation()+"' id='"+schema.getOperation()+"' nocheckbox='true'>"
                    //+ "<item text='"+schema.getName()+"' id='"+schema.getName()+"' nocheckbox='true'>");
                    + "<item text='"+schema.getService()+"' id='Service"+schema.getService_id()+"' nocheckbox='true'>"
                    + "<item text='"+schema.getOperation()+"' id='Operation"+schema.getOperation()+"' nocheckbox='true'>"
                    + "<item text='"+schema.getName()+"' id='Schema"+schema.getName()+"' nocheckbox='true'>");
        }


        if (qName.equalsIgnoreCase("xs:complexType") || qName.equalsIgnoreCase("xsd:complexType")) {
            complexTypefl = true;
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    numOfComplexTypes =   attributes.getLength();
                    //xmlToReturn.add("<item text='" + attributes.getValue(i)+"("+schema.getInputoutput()+")"  + "' id='" + attributes.getValue(i) +"--"+schema.getInputoutput()+"--"+ schema.getOperation_id()+"--"+schema.getOp_taxonomy_id()+"--"+schema.getCvp_id()+"--"+schema.getService_id()+"--"+schema.getSchema_id()+"--"+schema.getSelections()+"--"+schema.getXbrl()+"--"+schema.getCpp_id()+"' >");
                    xmlToReturn.add("<item text='" + schema.getCpp_name()+"("+schema.getInputoutput()+")"+attributes.getValue(i)  + "' id='" + attributes.getValue(i) +"--"+schema.getInputoutput()+"--"+ schema.getOperation_id()+"--"+schema.getOp_taxonomy_id()+"--"+schema.getCvp_id()+"--"+schema.getService_id()+"--"+schema.getSchema_id()+"--"+schema.getSelections()+"--"+schema.getXbrl()+"--"+schema.getCpp_id()+"' >");


                    System.out.println("' id='" + attributes.getValue(i) +"--"+schema.getInputoutput()+"--"+ schema.getOperation_id()+"--"+schema.getOp_taxonomy_id()+"--"+schema.getCvp_id()+"--"+schema.getService_id()+"--"+schema.getSchema_id()+"--"+schema.getSelections()+"--"+schema.getCpp_id());
                }
            }
        }
        
        if (qName.equalsIgnoreCase("xs:element") || qName.equalsIgnoreCase("xsd:element")) {
            elementfl = true;

            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    if(attributes.getQName(i).equalsIgnoreCase("name")){
                    elementsList.add(attributes.getValue(i));
                    System.out.println("element from XSDParser:" +attributes.getValue(i));
                    }
                }
            }
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {


        if (qName.equals("xs:complexType") || qName.equals("xsd:complexType")) {
            System.out.println("numOfComplexTypes"+numOfComplexTypes);
            if (numOfComplexTypes >0) {
            xmlToReturn.add("</item>");
                numOfComplexTypes -=1;
            }

        }
        if (qName.equals("xs:schema") || qName.equals("xsd:schema")) {
            xmlToReturn.add("</item></item></item></tree>");
        }
        
    }

    public void characters(char ch[], int start, int length) throws SAXException {

        if (complexTypefl) {
            xmlToString = new String(ch, start, length);
            complexTypefl = false;
        }
        if (schemafl) {
            schemafl = false;
        }
        if (elementfl) {
            schemafl = false;
        }
    }
    
    }

}
