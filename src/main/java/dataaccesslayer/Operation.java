/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataaccesslayer;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author eleni
 */
public class Operation {
    
    int service_id = -1, operation_id = -1, schema_id =-1;
    String web_service_name= null;
    String operation_name = null;
    String service_version= null;
    String taxonomy=null;

    public Operation(int operation_id ,String operation_name,int service_id ,String web_service_name , int schema_id) {
    
        this.operation_id = operation_id;
        this.operation_name = operation_name;
        this.service_id = service_id;
        this.web_service_name = web_service_name;
        this.schema_id = schema_id;
    }


    public Operation(String operation_name,int operation_id,String web_service_name, String service_version, String taxonomy) {

        this.web_service_name = web_service_name;
        this.service_version  = service_version;
        this.operation_name = operation_name;
        this.operation_id = operation_id;
        this.taxonomy = taxonomy;
    }


    public String getService_version() {
        return service_version;
    }

    public void setService_version(String service_version) {
        this.service_version = service_version;
    }

    public String getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
    }
    public int getOperation_id() {
        return operation_id;
    }

    public void setOperation_id(int operation_id) {
        this.operation_id = operation_id;
    }

    public String getOperation_name() {
        return operation_name;
    }

    public void setOperation_name(String operation_name) {
        this.operation_name = operation_name;
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

    public String getWeb_service_name() {
        return web_service_name;
    }

    public void setWeb_service_name(String web_service_name) {
        this.web_service_name = web_service_name;
    }
   
    
}
