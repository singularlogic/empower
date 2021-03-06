/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataaccesslayer;

/**
 *
 * @author eleni
 */
public class SoftwareComponent {
    
    private String name;
    private String version;
    private int num_xsds;
    private int num_services;


    public int getSoftware_id() {
        return software_id;
    }

    public void setSoftware_id(int software_id) {
        this.software_id = software_id;
    }
    private int software_id;
    
    public SoftwareComponent(String name, String version, int num_xsds, int num_services, int software_id) {
        this.name = name;
        this.version = version;
        this.num_xsds = num_xsds;
        this.num_services = num_services;
        this.software_id = software_id;
    }

    public SoftwareComponent(String name, String version, int software_id)
    {
        this.name = name;
        this.version = version;
        this.software_id = software_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSoftwareID() {
        return software_id;
    }

    public void setSoftwareID(int software_id) {
        this.software_id = software_id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getNum_xsds() {
        return num_xsds;
    }

    public void setNum_xsds(int num_xsds) {
        this.num_xsds = num_xsds;
    }
    
    public int getNum_services() {
        return num_services;
    }

    public void setNum_services(int num_services) {
        this.num_services = num_services;
    }
}
