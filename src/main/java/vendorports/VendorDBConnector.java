/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vendorports;

import dataaccesslayer.SoftwareComponent;
import dataaccesslayer.XSD;
import dataaccesslayer.dbConnector;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author eleni
 */
public class VendorDBConnector {

    private dbConnector dbHandler;

    public VendorDBConnector() {
        this.dbHandler = new dbConnector();
    }

    public int insertSoftwareInfo(String vendor, String software_name, String version) {
        ResultSet rs;
        int vendor_id = 0, software_id = 0;

        try {
            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("select * from vendor where name='" + vendor + "';");

            rs.next();
            vendor_id = rs.getInt("vendor_id");
            rs.close();

            software_id = this.dbHandler.dbUpdate("insert into softwarecomponent(name,version,vendor_id) values('"
                    + software_name + "','" + version + "'," + vendor_id + ");");

            rs.close();
            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return software_id;
    }
    
     public int updateSoftware(String softwareID, String softwareName, String version)
    {
        int num = 0;
	try
        {
            this.dbHandler.dbOpen();
            num = this.dbHandler.dbUpdate("update softwarecomponent set name=\"" + softwareName
                                    + "\", version=\"" + version + "\" where software_id=" + softwareID + ";");
            this.dbHandler.dbClose();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }        
        
        return num;
    }
     
     
         public void deleteSoftware(String softwareID)
    {
	try
        {
            this.dbHandler.dbOpen();
            //this.dbHandler.dbUpdate("delete from dataannotations where cvp_id in (select cvp_id from cvp where service_id in (select service_id from services where softwarecomponent_software_id=" + softwareID + "));");            
            //this.dbHandler.dbUpdate("delete from funcannotations where cvp_cvp_id in (select cvp_id from cvp where service_id in (select service_id from services where softwarecomponent_software_id=" + softwareID + "));");                        
            this.dbHandler.dbUpdate("delete from dataannotations where xsd_id in (select xsd_id from xsd where software_id=" + softwareID + ");");            
            this.dbHandler.dbUpdate("delete from cvp where xsd_id in (select xsd_id from xsd where software_id=" + softwareID + ");");
            this.dbHandler.dbUpdate("delete from xsd where software_id=" + softwareID + ";");
            this.dbHandler.dbUpdate("delete from softwarecomponent where software_id=" + softwareID + ";");
            this.dbHandler.dbClose();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }        
    }

    public Collection getSoftwareComponents(String vendor) {
        ResultSet rs, rsServ;
        LinkedList<SoftwareComponent> compList = new LinkedList<SoftwareComponent>();

        try {

            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("select a.name as name, a.version as version, a.software_id as software_id from softwarecomponent a,"
                    + " vendor c where c.name='" + vendor + "' and a.vendor_id=c.vendor_id;");

            dbConnector dbHand = new dbConnector();

            dbHand.dbOpen();



            if (rs != null) {
                while (rs.next()) {
                    rsServ = dbHand.dbQuery("select count(xsd_id) as rows from softwarecomponent a,"
                            + " xsd b, vendor c where c.name='" + vendor + "' and a.vendor_id=c.vendor_id and a.software_id=b.software_id;");
                    rsServ.next();
                    compList.add(new SoftwareComponent(rs.getString("name"),
                            rs.getString("version"),
                            rsServ.getInt("rows"),
                            rs.getInt("software_id")));
                    System.out.println();
                    rsServ.close();
                }
            }

            rs.close();

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return compList;
    }

    public int insertXSDInfo(int software_id, String XSDName, String XSDFilename, String xmlRepPath) {
        int xsd_id= -1;
        String version = new String("");
        String filename = null;

        try {
            this.dbHandler.dbOpen();

            xsd_id = this.dbHandler.dbUpdate("insert into xsd(location,name,software_id) values('"
                    + XSDFilename + "','" + XSDName + "'," + software_id + ");");

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println("Insert XSD " + t);

        }

        return xsd_id;
    }
    
    
      public Collection getXSDs(String software_id)
    {
        ResultSet rs;
        LinkedList<XSD> XSDList = new LinkedList<XSD>();
        
	try{
            
            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("select a.name, a.xsd_id from xsd a, softwarecomponent b where " 
                                        + "b.software_id='" + software_id +"' and b.software_id=a.software_id;");

            if(rs != null)
            {
                while(rs.next())
                    XSDList.add(new XSD(rs.getInt("xsd_id"), rs.getString("name") ));
            }
            
            rs.close();
            
            this.dbHandler.dbClose();
	}
	catch(Throwable t)
	{
		t.printStackTrace(); 
	}

        return XSDList;
    }
}
