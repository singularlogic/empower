/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataaccesslayer;

/**
 *
 * @author eleni
 */
public class CPA {
    int cpa_id;
    String url;
    String port;
    int cpp_id_first;
    int cpp_id_second;
    String cpa_info;

    public CPA(int cpa_id, int cpp_id_first, int cpp_id_second, String cpa_info,String url, String port) {
        this.cpa_id= cpa_id;
        this.url = url;
        this.port = port;
        this.cpp_id_first = cpp_id_first;
        this.cpp_id_second = cpp_id_second;
        this.cpa_info = cpa_info;
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

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    
}
