/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orgports;

import dataaccesslayer.Schema;
import dataaccesslayer.SoftwareComponent;
import dataaccesslayer.dbConnector;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author eleni
 */
public class OrgDBConnector {
    private dbConnector dbHandler;
    
    public OrgDBConnector()
    {
	this.dbHandler = new dbConnector();
    }
    
    public Collection getSoftwareComponents()
    {
        ResultSet rs;
        LinkedList<SoftwareComponent> compList = new LinkedList<SoftwareComponent>();
        
	try{
            
            this.dbHandler.dbOpen();
            
            rs = this.dbHandler.dbQuery("select * from softwarecomponent;");

            if(rs != null)
            {
                while(rs.next())
                    compList.add(new SoftwareComponent(rs.getString("name"),rs.getString("version"),rs.getInt("software_id")));
            }
            rs.close();
            
            this.dbHandler.dbClose();
	}
	catch(Throwable t)
	{
		t.printStackTrace(); 
	}

        return compList;
    }
    
     public Collection getSchemas(String software_id) {
        ResultSet rs;
        LinkedList<Schema> XSDList = new LinkedList<Schema>();

        try {

            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("select ws.software_id as software_id,ws.service_id as service_id,ws.name as ws_name,o.operation_id as operation_id,o.name as operation_name,o.taxonomy_id as op_taxonomy_id,os.inputoutput as inputoutput, s.schema_id as schema_id, s.location as schema_location, s.name as schema_name  from operation o LEFT JOIN web_service ws on o.service_id=ws.service_id LEFT JOIN operation_schema os  on o.operation_id = os.operation_id "
                    + "LEFT JOIN schema_xsd  s  on os.schema_id = s.schema_id where ws.software_id="+software_id + " and os.inputoutput=\"input\" order by ws.service_id");

            if (rs != null) {
                while (rs.next()) {
                    XSDList.add(new Schema(rs.getInt("service_id"), rs.getString("ws_name"),rs.getInt("operation_id"),rs.getString("operation_name"),rs.getString("op_taxonomy_id"),rs.getString("inputoutput"),rs.getInt("schema_id"),rs.getString("schema_location"), rs.getString("schema_name")));
                }
            }

            rs.close();

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return XSDList;
    }
     
        public Collection getTargetSchemas(String inputoutput, String taxonomy_id) {
        ResultSet rs;
        LinkedList<Schema> XSDList = new LinkedList<Schema>();

        try {

            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("select ws.software_id as software_id,ws.service_id as service_id,ws.name as ws_name,o.operation_id as operation_id,o.name as operation_name,o.taxonomy_id as op_taxonomy_id,os.inputoutput as inputoutput, s.schema_id as schema_id, s.location as schema_location, s.name as schema_name "
                    + " from operation o LEFT JOIN web_service ws on o.service_id=ws.service_id LEFT JOIN operation_schema os  on o.operation_id = os.operation_id  LEFT JOIN schema_xsd  s  on os.schema_id = s.schema_id "
                    + "where  o.taxonomy_id = '"+taxonomy_id+"' and os.inputoutput='output' order by ws.service_id");
            if (rs != null) {
                while (rs.next()) {
                    XSDList.add(new Schema(rs.getInt("service_id"), rs.getString("ws_name"),rs.getInt("operation_id"),rs.getString("operation_name"),rs.getString("op_taxonomy_id"),rs.getString("inputoutput"),rs.getInt("schema_id"),rs.getString("schema_location"), rs.getString("schema_name")));
                }
            }

            rs.close();

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return XSDList;
    }
 
    
}
