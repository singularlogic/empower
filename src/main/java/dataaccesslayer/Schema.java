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
    int operation_id;
    String operation;
    String op_taxonomy_id;
    int service_id;
    String service;
    String inputoutput;
    int cvp_id;
    String selections;

     
    public Schema(int service_id , String ws_name, int operation_id,String operation_name,String op_taxonomy_id,String inputoutput,int schema_id,String schema_location, String name, int cvp_id, String selections) {
        this.service_id = service_id;
        this.service = ws_name;
        this.operation_id = operation_id;
        this.operation = operation_name;
        this.op_taxonomy_id = op_taxonomy_id;
        this.inputoutput = inputoutput;
        this.schema_id = schema_id;
        this.location = schema_location;
        this.name = name;
        this.cvp_id = cvp_id;
        this.selections = selections;
    }
       
    public Schema(int schema_id, String name) {
        this.schema_id = schema_id;
        this.name = name;
    }
    

    public Schema() {
    }
    
    public Schema(int schema_id, String name,String location) {
        this.schema_id = schema_id;
        this.name = name;
    }
    
    public int getCvp_id() {
        return cvp_id;
    }

    public void setCvp_id(int cvp_id) {
        this.cvp_id = cvp_id;
    }
    
     public String getOp_taxonomy_id() {
        return op_taxonomy_id;
    }

    public void setOp_taxonomy_id(String op_taxonomy_id) {
        this.op_taxonomy_id = op_taxonomy_id;
    }
    
    public int getService_id() {
        return service_id;
    }

    public void setService_id(int service_id) {
        this.service_id = service_id;
    }
    
     public int getOperation_id() {
        return operation_id;
    }

    public void setOperation_id(int operation_id) {
        this.operation_id = operation_id;
    }

    public String getInputoutput() {
        return inputoutput;
    }

    public void setInputoutput(String inputoutput) {
        this.inputoutput = inputoutput;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
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
    
     public String getSelections() {
        return selections;
    }

    public void setSelections(String selections) {
        this.selections = selections;
    }
   
}
