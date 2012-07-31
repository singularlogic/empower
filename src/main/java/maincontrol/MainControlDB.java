/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maincontrol;

import dataaccesslayer.dbConnector;
import java.sql.ResultSet;

/**
 *
 * @author eleni
 */
public class MainControlDB {

    private dbConnector dbHandler;

    public MainControlDB() {
        this.dbHandler = new dbConnector();
    }

    public String isLegalUser(String name, String password) {
        ResultSet rs;
        boolean isVendor = false;

        try {
            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("select * from users where name='"
                    + name + "' and password='" + password + "';");


            if (rs != null) {
                rs.next();
                if (rs.getString("name").equals(name)) {
                    return rs.getString("usertype");
                }
            }

            rs.close();
            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    /*
     * Insert User in users table and in vendor or organization table depending
     * on the usetype
     */
    public void insertUser(String name, String password, String usertype) {
        try {

            int user_id;
            this.dbHandler.dbOpen();
            user_id = this.dbHandler.dbUpdate("insert into users(name,password,usertype) values('" + name + "','" + password + "','" + usertype + "');");

            if (usertype.equals("vendor")) {
                this.dbHandler.dbUpdate("insert into vendor(vendor_id,name) values('" + user_id + "','" + name + "');");
            } else if (usertype.equals("organization")) {
                this.dbHandler.dbUpdate("insert into organization(organization_id,name,description) values('" + user_id + "','" + name + "','');");
            }

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
