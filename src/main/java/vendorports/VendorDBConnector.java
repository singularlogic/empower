/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vendorports;

import dataaccesslayer.Operation;
import dataaccesslayer.SoftwareComponent;
import dataaccesslayer.Schema;
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
                   
                    rsServ = dbHand.dbQuery("select count(s.schema_id) as rows from operation o LEFT JOIN web_service ws on o.service_id=ws.service_id LEFT JOIN operation_schema os  on o.operation_id = os.operation_id LEFT JOIN schema_xsd  s  on os.schema_id = s.schema_id where ws.software_id="+rs.getString("software_id"));
                                       
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

    public int insertSchemaInfo(int software_id, String XSDName, String XSDFilename, String xmlRepPath,String web_service_name,String operation_name,String inputoutput ) {
        int schema_id= -1, service_id = -1, operation_id= -1 ,operation_schema_id= -1;
        String version = new String("");
        String filename = null;

        try {
            this.dbHandler.dbOpen();

            service_id = this.dbHandler.dbUpdate("INSERT INTO web_service(name,software_id) VALUES ('"+web_service_name+"',"+software_id+")");
             System.out.println("service_id" +service_id);

            operation_id = this.dbHandler.dbUpdate("INSERT INTO operation(name,service_id,taxonomy_id) VALUES ('"+operation_name+"',"+service_id+",1)");
             System.out.println("operation_id" +operation_id);
           
             schema_id = this.dbHandler.dbUpdate("INSERT INTO schema_xsd(location,name) VALUES ('"+XSDFilename+"','"+XSDName+"')");
             System.out.println("schema_id" +schema_id);
            
            operation_schema_id= this.dbHandler.dbUpdate("INSERT INTO operation_schema(operation_id, schema_id, inputoutput) VALUES ("+operation_id+","+schema_id+",'"+inputoutput+"')");
             System.out.println("operation_schema_id" +operation_schema_id);

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println("Insert Schema " + t);

        }

        return schema_id;
    }
    
    
      public Collection getSchemas(String software_id)
    {
        ResultSet rs;
        LinkedList<Schema> XSDList = new LinkedList<Schema>();
        
	try{
            
            this.dbHandler.dbOpen();
            
            rs= this.dbHandler.dbQuery("select s.schema_id as schema_id,s.name as schema_name from operation o LEFT JOIN web_service ws on o.service_id=ws.service_id LEFT JOIN operation_schema os  on o.operation_id = os.operation_id LEFT JOIN schema_xsd  s  on os.schema_id = s.schema_id where ws.software_id="+software_id);
           
            // rs = this.dbHandler.dbQuery("select a.name, a.xsd_id from xsd a, softwarecomponent b where " 
           //                             + "b.software_id='" + software_id +"' and b.software_id=a.software_id;");

            if(rs != null)
            {
                while(rs.next())
                    XSDList.add(new Schema(rs.getInt("schema_id"), rs.getString("schema_name") ));
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
      
      
       public LinkedList<Operation> getOperationsBySchema(int schema_id)
    {
        ResultSet rs;
        LinkedList<Operation> OperationList = new LinkedList<Operation>();
        
	try{
            
            this.dbHandler.dbOpen();
            
            rs= this.dbHandler.dbQuery("select ws.service_id as service_id,ws.name as web_service_name ,o.operation_id as operation_id ,o.name as operation_name, s.schema_id as schema_id from schema_xsd s LEFT JOIN operation_schema os  on s.schema_id = os.schema_id LEFT JOIN operation o  on o.operation_id = os.operation_id LEFT JOIN web_service ws  on ws.service_id = o.service_id where s.schema_id="+schema_id);
           
            
            if(rs != null)
            {
                while(rs.next())
                    OperationList.add(new Operation(rs.getInt("operation_id"), rs.getString("operation_name"),rs.getInt("service_id"),rs.getString("web_service_name"),rs.getInt("schema_id") ));
            }
            
            rs.close();
            
            this.dbHandler.dbClose();
	}
	catch(Throwable t)
	{
		t.printStackTrace(); 
	}

        return OperationList;
    }
       
       
       
       
       public void insertTaxonomyToOperation (int operation_id, String funcSelections){
       
       try {
            this.dbHandler.dbOpen();

            this.dbHandler.dbUpdate("update operation set taxonomy_id='" + funcSelections + "' where operation_id=" + operation_id);           
            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println("Insert Taxonomy " + t);

        }
       }
       
       
       public Schema getSchema(int schema_id)
       {
           ResultSet rs;
           Schema schema = new Schema();
           
           try{
            this.dbHandler.dbOpen();
            
            rs= this.dbHandler.dbQuery("select ws.service_id as service_id,ws.name as web_service_name ,o.operation_id as operation_id ,o.name as operation_name, s.schema_id as schema_id , s.location as schema_location, s.name as schema_name, os.inputoutput as inputoutput from schema_xsd s LEFT JOIN operation_schema os  on s.schema_id = os.schema_id LEFT JOIN operation o  on o.operation_id = os.operation_id LEFT JOIN web_service ws  on ws.service_id = o.service_id where s.schema_id="+schema_id);
           
            
            if(rs != null)
            {
                
                while(rs.next())
                {
                    schema.setSchema_id(rs.getInt("schema_id"));
                    schema.setName(rs.getString("schema_name"));
                    schema.setLocation(rs.getString("schema_location"));
                    schema.setService(rs.getString("web_service_name"));
                    schema.setOperation(rs.getString("operation_name"));
                    schema.setInputoutput(rs.getString("inputoutput"));
                }
            }
            
            rs.close();
            
            this.dbHandler.dbClose();
           }catch(Throwable t){
           }
           
           return schema;
       }
}
