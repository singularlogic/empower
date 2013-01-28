/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataaccesslayer;

/**
 *
 * @author eleni
 */
public class Service {
    int service_id;
    String  name;
    String  version;
    String wsdl;
    String namespace;
    boolean exposed;
    int cvp_id;
    String cpp_name;
    int cpp_id;



    int vendor_id;

    
    public Service(int service_id, String name,boolean exposed) {
        this.service_id = service_id;
        this.name = name;
        this.exposed = exposed;
    }



    public Service(int service_id, String name , String version, String wsdl , String namespace,boolean exposed) {
        this.service_id = service_id;
        this.name = name;
        this.version = version;
        this.wsdl = wsdl;
        this.namespace = namespace;
        this.exposed = exposed;
    }

    public Service(int service_id, String name, String version, String wsdl , String namespace,boolean exposed,int cvp_id, int vendor_id) {
        this.service_id = service_id;
        this.name = name;
        this.version = version;
        this.wsdl = wsdl;
        this.namespace = namespace;
        this.exposed = exposed;
        this.cvp_id= cvp_id;
        this.vendor_id=vendor_id;
    }


    public Service(int service_id, String name, String cpp_name, int cpp_id, String version, String wsdl , String namespace,boolean exposed) {
        this.service_id = service_id;
        this.name = name;
        this.cpp_name = cpp_name;
        this.cpp_id =  cpp_id;
        this.version = version;
        this.wsdl = wsdl;
        this.namespace = namespace;
        this.exposed = exposed;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public String getCpp_name() {
        return cpp_name;
    }

    public void setCpp_name(String cpp_name) {
        this.cpp_name = cpp_name;
    }

    public int getCvp_id() {
        return cvp_id;
    }

    public void setCvp_id(int cvp_id) {
        this.cvp_id = cvp_id;
    }
    public int getCpp_id() {
        return cpp_id;
    }

    public void setCpp_id(int cpp_id) {
        this.cpp_id = cpp_id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
    public boolean isExposed() {
        return exposed;
    }

    public void setExposed(boolean exposed) {
        this.exposed = exposed;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getService_id() {
        return service_id;
    }

    public void setService_id(int service_id) {
        this.service_id = service_id;
    }
    
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getWsdl() {
        return wsdl;
    }

    public void setWsdl(String wsdl) {
        this.wsdl = wsdl;
    }
}
