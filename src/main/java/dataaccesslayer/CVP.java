/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataaccesslayer;

/**
 *
 * @author eleni
 */
public class CVP {
    
    private int cvpID;
    private String dataAnnotations;
    private int vendor_id;
    //private int serviceID;
    //private String funcAnnotations;
    //private String operation;
    
     public CVP(int cvpID, String dataAnnotations,int vendor_id) {
        this.cvpID = cvpID;
        this.dataAnnotations = dataAnnotations;
        this.vendor_id = vendor_id;
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

    
    

    
}
