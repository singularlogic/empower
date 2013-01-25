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

    
    public Service(int service_id, String name,boolean exposed) {
        this.service_id = service_id;
        this.name = name;
        this.exposed = exposed;
    }
    
    public Service(int service_id, String name, String version, String wsdl , String namespace,boolean exposed) {
        this.service_id = service_id;
        this.name = name;
        this.version = version;
        this.wsdl = wsdl;
        this.namespace = namespace;
        this.exposed = exposed;
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
