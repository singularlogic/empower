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
public class CPP {
    
    private int cppID;
    private String dataAnnotations;
    private int vendor_id;
    private String service;
    private int service_id;
    private String operation;
    private int operation_id;
    private String schema;
    private int schema_id;
    private String schema_complexType;
    
     public CPP(int cppID, String dataAnnotations,int vendor_id) {
        this.cppID = cppID;
        this.dataAnnotations = dataAnnotations;
        this.vendor_id = vendor_id;
    }
     
    public CPP(int cppID, int service_id, int operation_id, int schema_id, String schema_complexType ) {
        OrgDBConnector orgDBConnector = new OrgDBConnector();
        this.cppID = cppID;
        this.service = orgDBConnector.getServiceName(service_id);
        this.service_id = service_id;
        this.operation = orgDBConnector.getOperationName(operation_id);
        this.operation_id = operation_id;
        this.schema = orgDBConnector.getSchemaName(schema_id);
        this.schema_id = schema_id;
        this.schema_complexType = schema_complexType;
        
       
    }

    public int getCppID() {
        return cppID;
    }

    public void setCppID(int cppID) {
        this.cppID = cppID;
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
    
    public int getOperation_id() {
        return operation_id;
    }

    public void setOperation_id(int operation_id) {
        this.operation_id = operation_id;
    }

    public int getSchema_id() {
        return schema_id;
    }

    public void setSchema_id(int schema_id) {
        this.schema_id = schema_id;
    }

    public int getService_id() {
        return service_id;
    }

    public void setService_id(int service_id) {
        this.service_id = service_id;
    }

    
    

    
}
