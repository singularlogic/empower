/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataaccesslayer;

import orgports.OrgDBConnector;

/**
 *
 * @author eleni
 */
public class CVP {
    
    private int cvpID;
    private String dataAnnotations;
    private int vendor_id;
    private String service;
    private String operation;
    private String schema;
    private String schema_complexType;
    
     public CVP(int cvpID, String dataAnnotations,int vendor_id) {
        this.cvpID = cvpID;
        this.dataAnnotations = dataAnnotations;
        this.vendor_id = vendor_id;
    }
     
    public CVP(int cvpID, int service_id, int operation_id, int schema_id, String schema_complexType ) {
        OrgDBConnector orgDBConnector = new OrgDBConnector();
        this.cvpID = cvpID;
        this.service = orgDBConnector.getServiceName(service_id);
        this.operation = orgDBConnector.getOperationName(operation_id);
        this.schema = orgDBConnector.getSchemaName(schema_id);
        this.schema_complexType = schema_complexType;
        
       
    }

    public int getCvpID() {
        return cvpID;
    }

    public void setCvpID(int cvpID) {
        this.cvpID = cvpID;
    }

    public String getDataAnnotations() {
        return dataAnnotations;
    }

    public void setDataAnnotations(String dataAnnotations) {
        this.dataAnnotations = dataAnnotations;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }
    
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getSchema_complexType() {
        return schema_complexType;
    }

    public void setSchema_complexType(String schema_complexType) {
        this.schema_complexType = schema_complexType;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getService() {
        return service;
    }

    public void setService_id(String service) {
        this.service = service;
    }

    
    

    
}
