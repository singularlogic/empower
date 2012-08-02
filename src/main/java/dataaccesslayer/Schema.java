/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataaccesslayer;

/**
 *
 * @author eleni
 */
public class Schema {

    int schema_id;
    String name;
    String location;
    int software_id;
    
    
    
    public Schema(int schema_id, String name) {
        this.schema_id = schema_id;
        this.name = name;
    }
        

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

    public int getSchema_id() {
        return schema_id;
    }

    public void setSchema_id(int xsd_id) {
        this.schema_id = schema_id;
    }
   
}
