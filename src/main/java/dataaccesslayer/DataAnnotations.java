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
    private String xslt_annotations;
    private String selections;
    private int schema_id;
    
    
     public DataAnnotations(int dataAnnotations_id, String mapping,String xbrl) {
        this.dataAnnotations_id = dataAnnotations_id;
        this.mapping = mapping;
        this.xbrl = xbrl;
    }

    public DataAnnotations(int dataAnnotations_id,int schema_id,String xslt_annotations,String mapping, String selections, String xbrl) {
        this.dataAnnotations_id= dataAnnotations_id;
        this.xslt_annotations = xslt_annotations;
        this.mapping = mapping;
        this.selections = selections;
        this.xbrl = xbrl;
        this.schema_id = schema_id;
    }
    public DataAnnotations(int dataAnnotations_id,String xslt_annotations,String mapping, String selections, String xbrl) {
        this.dataAnnotations_id= dataAnnotations_id;
        this.xslt_annotations = xslt_annotations;
        this.mapping = mapping;
        this.selections = selections;
        this.xbrl = xbrl;
    }

    public DataAnnotations() {
        
    }

    public int getSchema_id() {
        return schema_id;
    }

    public void setSchema_id(int schema_id) {
        this.schema_id = schema_id;
    }

    public String getXslt_annotations() {
        return xslt_annotations;
    }

    public void setXslt_annotations(String xslt_annotations) {
        this.xslt_annotations = xslt_annotations;
    }

    public String getSelections() {
        return selections;
    }

    public void setSelections(String selections) {
        this.selections = selections;
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
