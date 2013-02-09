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
public class CPA {
    int cpa_id;
    int cpp_id_first;
    String cpp_id_first_name;
    int cpp_id_second;
    String cpp_id_second_name;
    String cpa_info;
    boolean disabled;

    

    public CPA(int cpa_id, int cpp_id_first, int cpp_id_second, String cpa_info,boolean disabled) {

        OrgDBConnector orgDBConnector = new OrgDBConnector();

        this.cpa_id= cpa_id;
        this.cpp_id_first = cpp_id_first;
        this.cpp_id_first_name = orgDBConnector.getCPPName(cpp_id_first);
        this.cpp_id_second = cpp_id_second;
        this.cpp_id_second_name = orgDBConnector.getCPPName(cpp_id_second);
        this.cpa_info = cpa_info;
        this.disabled = disabled;
        
    }

    public CPA() {
        
    }

    public String getCpp_id_first_name() {
        return cpp_id_first_name;
    }

    public void setCpp_id_first_name(String cpp_id_first_name) {
        this.cpp_id_first_name = cpp_id_first_name;
    }

    public String getCpp_id_second_name() {
        return cpp_id_second_name;
    }

    public void setCpp_id_second_name(String cpp_id_second_name) {
        this.cpp_id_second_name = cpp_id_second_name;
    }
    
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public int getCpa_id() {
        return cpa_id;
    }

    public void setCpa_id(int cpa_id) {
        this.cpa_id = cpa_id;
    }

    public String getCpa_info() {
        return cpa_info;
    }

    public void setCpa_info(String cpa_info) {
        this.cpa_info = cpa_info;
    }

    public int getCpp_id_first() {
        return cpp_id_first;
    }

    public void setCpp_id_first(int cpp_id_first) {
        this.cpp_id_first = cpp_id_first;
    }

    public int getCpp_id_second() {
        return cpp_id_second;
    }

    public void setCpp_id_second(int cpp_id_second) {
        this.cpp_id_second = cpp_id_second;
    }
    
}
