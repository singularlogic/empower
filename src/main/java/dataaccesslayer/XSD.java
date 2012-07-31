/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataaccesslayer;

/**
 *
 * @author eleni
 */
public class XSD {

    public XSD(int xsd_id, String name) {
        this.xsd_id = xsd_id;
        this.name = name;
    }
    
    int xsd_id;
    String name;
    String location;
    int software_id;
 
    

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSoftware_id() {
        return software_id;
    }

    public void setSoftware_id(int software_id) {
        this.software_id = software_id;
    }

    public int getXsd_id() {
        return xsd_id;
    }

    public void setXsd_id(int xsd_id) {
        this.xsd_id = xsd_id;
    }
   
}
