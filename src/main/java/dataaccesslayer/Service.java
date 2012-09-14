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
    String wsdl;
    String namespace;

    public Service(int service_id, String name) {
        this.service_id = service_id;
        this.name = name;
    }
    
    public Service(int service_id, String name, String wsdl , String namespace) {
        this.service_id = service_id;
        this.name = name;
        this.wsdl = wsdl;
        this.namespace = namespace;
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
