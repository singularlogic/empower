/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataaccesslayer;

/**
 *
 * @author eleni
 */
public class DataAnnotations {
    
    private int dataAnnotations_id;
    private String mapping;
    private String xbrl;
    
    
     public DataAnnotations(int dataAnnotations_id, String mapping,String xbrl) {
        this.dataAnnotations_id = dataAnnotations_id;
        this.mapping = mapping;
        this.xbrl = xbrl;
    }

    public DataAnnotations() {
        
    }

    

    public int getDataAnnotations_id() {
        return dataAnnotations_id;
    }

    public void setDataAnnotations_id(int dataAnnotations_id) {
        this.dataAnnotations_id = dataAnnotations_id;
    }

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public String getXbrl() {
        return xbrl;
    }

    public void setXbrl(String xbrl) {
        this.xbrl = xbrl;
    }
    
    
}
