/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vendorports;

import dataaccesslayer.*;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import xml.WSDLParser;

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

    public int updateSoftware(String softwareID, String softwareName, String version) {
        int num = 0;
        try {
            this.dbHandler.dbOpen();
            num = this.dbHandler.dbUpdate("update softwarecomponent set name=\"" + softwareName
                    + "\", version=\"" + version + "\" where software_id=" + softwareID + ";");
            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return num;
    }

    public void deleteSoftware(String softwareID) {
        try {
            this.dbHandler.dbOpen();
            //this.dbHandler.dbUpdate("delete from dataannotations where cvp_id in (select cvp_id from cvp where service_id in (select service_id from services where softwarecomponent_software_id=" + softwareID + "));");            
            //this.dbHandler.dbUpdate("delete from funcannotations where cvp_cvp_id in (select cvp_id from cvp where service_id in (select service_id from services where softwarecomponent_software_id=" + softwareID + "));");                        
            this.dbHandler.dbUpdate("delete from dataannotations where xsd_id in (select xsd_id from xsd where software_id=" + softwareID + ");");
            this.dbHandler.dbUpdate("delete from cvp where xsd_id in (select xsd_id from xsd where software_id=" + softwareID + ");");
            this.dbHandler.dbUpdate("delete from xsd where software_id=" + softwareID + ";");
            this.dbHandler.dbUpdate("delete from softwarecomponent where software_id=" + softwareID + ";");
            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /*
     * Get the software components that belong to a vendor with the schemas and services of each component
     */
    
    public Collection getSoftwareComponents(String vendor) {
        ResultSet rs, rsServ , rsSchemas;
        LinkedList<SoftwareComponent> compList = new LinkedList<SoftwareComponent>();

        try {

            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("select a.name as name, a.version as version, a.software_id as software_id from softwarecomponent a,"
                    + " vendor c where c.name='" + vendor + "' and a.vendor_id=c.vendor_id;");

            dbConnector dbHand = new dbConnector();

            dbHand.dbOpen();

            if (rs != null) {
                while (rs.next()) {

                    rsSchemas = dbHand.dbQuery("select count(s.schema_id) as schemas_num from operation o LEFT JOIN web_service ws on o.service_id=ws.service_id LEFT JOIN operation_schema os  on o.operation_id = os.operation_id LEFT JOIN schema_xsd  s  on os.schema_id = s.schema_id where ws.software_id=" + rs.getString("software_id"));
                    int schemas_num = (rsSchemas.next()) ? rsSchemas.getInt("schemas_num"): 0;
                    rsSchemas.close();
                    
                    rsServ = dbHand.dbQuery("select count(ws.service_id) as services_num from  web_service ws where ws.software_id="+rs.getString("software_id"));
                    int services_num = (rsServ.next()) ? rsServ.getInt("services_num"): 0;
                    rsServ.close();
                    
                    compList.add(new SoftwareComponent(rs.getString("name"),
                            rs.getString("version"),
                            schemas_num,
                            services_num,
                            rs.getInt("software_id"))); 
                }
            }

            rs.close();

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return compList;
    }

    public int insertSchemaInfo(int software_id, String XSDName, String XSDFilename, String xmlRepPath, String new_web_service_name,String web_service, String operation_name, String inputoutput) {
        int schema_id = -1, service_id = -1, operation_id = -1, operation_schema_id = -1;
        String version = new String("");
        String filename = null;

        try {
            this.dbHandler.dbOpen();

            
            if (new_web_service_name.equalsIgnoreCase("")){
            System.out.println("Hola");    
            service_id = Integer.parseInt(web_service);
            }else service_id = this.dbHandler.dbUpdate("INSERT INTO web_service(name,software_id) VALUES ('" + new_web_service_name + "'," + software_id + ")");
            

            operation_id = this.dbHandler.dbUpdate("INSERT INTO operation(name,service_id,taxonomy_id) VALUES ('" + operation_name + "'," + service_id + ",1)");
            System.out.println("operation_id" + operation_id);

            schema_id = this.dbHandler.dbUpdate("INSERT INTO schema_xsd(location,name) VALUES ('" + XSDFilename + "','" + XSDName + "')");
            System.out.println("schema_id" + schema_id);

            operation_schema_id = this.dbHandler.dbUpdate("INSERT INTO operation_schema(operation_id, schema_id, inputoutput) VALUES (" + operation_id + "," + schema_id + ",'" + inputoutput + "')");
            System.out.println("operation_schema_id" + operation_schema_id);

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println("Insert Schema " + t);

        }

        return schema_id;
    }
    
    public int insertServiceInfo(int software_id, String serviceName, String wsdlFilename, String xmlRepPath, String namespace)
    {
        ResultSet rs;
        int service_id = -1;
        int old_service_id = 0;
        String version = new String("");
        String filename = null;
        Iterator servicePorts;
        int numberOperations, numberMessages;
        
	try
        {
            // for mysql compatibility
            wsdlFilename = wsdlFilename.replace("\\", "\\\\");

            this.dbHandler.dbOpen();

            WSDLParser wsdlParser = new WSDLParser(wsdlFilename, namespace);
            wsdlParser.loadService(serviceName);
            numberOperations = wsdlParser.getOperationsNumber();
             System.out.println("numberOperations: "+numberOperations);
            numberMessages   = wsdlParser.getMessageNumber(); 
             System.out.println("numberMessages: "+numberMessages);

           service_id = this.dbHandler.dbUpdate("insert into web_service(name,software_id,namespace,wsdl,operation_number, messages_number) values('"
                                    + serviceName + "',"+software_id+",'" + namespace +"','" +wsdlFilename + "'," + numberOperations + "," + numberMessages + ");");
            
            servicePorts = wsdlParser.returnServicePortsString();
            
            while(servicePorts.hasNext())
                  this.dbHandler.dbUpdate("insert into installedbinding(service_id,service_port_name) values("
                          + service_id + ",'" + servicePorts.next().toString() + "');");
                System.out.println("servicePorts: "+servicePorts.next().toString());

            this.dbHandler.dbClose();
	}
	catch(Throwable t)
	{
            //t.printStackTrace(); 
            System.out.println(t);
	}
                
        return service_id;
    }

    public Collection getSchemas(String software_id) {
        ResultSet rs;
        LinkedList<Schema> XSDList = new LinkedList<Schema>();

        try {

            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("select s.schema_id as schema_id,s.name as schema_name from operation o LEFT JOIN web_service ws on o.service_id=ws.service_id LEFT JOIN operation_schema os  on o.operation_id = os.operation_id LEFT JOIN schema_xsd  s  on os.schema_id = s.schema_id where ws.software_id=" + software_id);

            // rs = this.dbHandler.dbQuery("select a.name, a.xsd_id from xsd a, softwarecomponent b where " 
            //                             + "b.software_id='" + software_id +"' and b.software_id=a.software_id;");

            if (rs != null) {
                while (rs.next()) {
                    XSDList.add(new Schema(rs.getInt("schema_id"), rs.getString("schema_name")));
                }
            }

            rs.close();

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return XSDList;
    }

    public LinkedList<Operation> getOperationsBySchema(int schema_id) {
        ResultSet rs;
        LinkedList<Operation> OperationList = new LinkedList<Operation>();

        try {

            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("select ws.service_id as service_id,ws.name as web_service_name ,o.operation_id as operation_id ,o.name as operation_name, s.schema_id as schema_id from schema_xsd s LEFT JOIN operation_schema os  on s.schema_id = os.schema_id LEFT JOIN operation o  on o.operation_id = os.operation_id LEFT JOIN web_service ws  on ws.service_id = o.service_id where s.schema_id=" + schema_id);


            if (rs != null) {
                while (rs.next()) {
                    OperationList.add(new Operation(rs.getInt("operation_id"), rs.getString("operation_name"), rs.getInt("service_id"), rs.getString("web_service_name"), rs.getInt("schema_id")));
                }
            }

            rs.close();

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return OperationList;
    }

    public void insertTaxonomyToOperation(int operation_id, String funcSelections) {

        try {
            this.dbHandler.dbOpen();

            this.dbHandler.dbUpdate("update operation set taxonomy_id='" + funcSelections + "' where operation_id=" + operation_id);
            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println("Insert Taxonomy " + t);

        }
    }

    public Schema getSchema(int schema_id) {
        ResultSet rs;
        Schema schema = new Schema();

        try {
            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("select ws.service_id as service_id,ws.name as web_service_name ,o.operation_id as operation_id ,o.name as operation_name, s.schema_id as schema_id , s.location as schema_location, s.name as schema_name, os.inputoutput as inputoutput from schema_xsd s LEFT JOIN operation_schema os  on s.schema_id = os.schema_id LEFT JOIN operation o  on o.operation_id = os.operation_id LEFT JOIN web_service ws  on ws.service_id = o.service_id where s.schema_id=" + schema_id);


            if (rs != null) {

                while (rs.next()) {
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
        } catch (Throwable t) {
        }

        return schema;
    }
    
    /*put in MainControlDB
    public int insertCVP(String annotations, int schema_id, String vendorName, String json, String selections) {
        ResultSet rs;
        int cvp = 0, dataannotations_id, vendor_id, cvp_id;
        int num = 0;

        try {
            
            // get vendor id
            vendor_id = getuserid(vendorName);
            System.out.println(" vendor_id:" + vendor_id);
            
            //check if exists an other cvp
            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("SELECT cvp_id, da.dataAnnotations_id from dataannotations  da, cvp  cvp  WHERE schema_id=" + schema_id +" and selections='"+selections+"' and da.dataAnnotations_id = cvp.dataAnnotations_id");

            if (rs.next()) {
                cvp_id = rs.getInt("cvp_id");
                System.out.println(" CVP exists:" + cvp);

                //update dataannotations info and vendor name info of cvp

                this.dbHandler.dbUpdate("update dataannotations set mapping='" + json + "', xslt_annotations ='" + annotations + "', selections = '"+selections+"'  where dataannotations_id=" + rs.getInt("dataannotations_id") + ";");
                System.out.println("update dataannotations");
                this.dbHandler.dbUpdate("update cvp set vendor_id='" + vendor_id + "' where cvp_id=" + cvp_id + ";");
                System.out.println("update cvp");

            } else {
                dataannotations_id = this.dbHandler.dbUpdate("insert into dataannotations (schema_id, xslt_annotations,mapping,selections) values (" + schema_id + ",'" + annotations + "','" + json + "','"+selections+"');");

                System.out.println(" create dataannotations:" + dataannotations_id);

                // insert cvp
                cvp_id = this.dbHandler.dbUpdate("insert into cvp(dataAnnotations_id, vendor_id) values(" + dataannotations_id + ",'" + vendor_id + "');");

                System.out.println(" create cvp:" + cvp_id);
            }

             this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return cvp;
    }
    */
    
   /*put in MainControlDB
    public CVP getCVP(int cvpID) {
        CVP cvp = null;
        ResultSet rs;

        try {

            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("select * from cvp where cvp_id=" + cvpID);

            if (rs != null) {
                rs.next();
               // cvp = new CVP(Integer.parseInt(rs.getString("service_id")), rs.getString("func_annotations"), rs.getString("data_annotations"));
            }

            rs.close();

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return cvp;
    }*/

    public int getuserid(String name) {
        ResultSet rs;
        int user_id = -1;
        try {

             System.out.println("name: "+ name);
            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("select * from users where name='" + name + "'");

            if (rs != null) {
                rs.next();
                user_id = rs.getInt("user_id");
            }

            rs.close();

            this.dbHandler.dbClose();
        } catch (Throwable t) {
        }
        System.out.println("user_id: "+ user_id);
        return user_id;
    }
    
       public String getMapping(int schema_id , String selections)
    {
        ResultSet rs;
        String mappings = null;

	try
        {
            this.dbHandler.dbOpen();        
            System.out.println("selections: "+ selections);
            rs = this.dbHandler.dbQuery("select mapping from dataannotations where schema_id=" + schema_id +" and selections='"+ selections+"'");
            
            if(rs.next())
            {
                mappings = rs.getString("mapping");
            }
            
            this.dbHandler.dbClose();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
        
        return mappings;
    }
    
    //  To use in futur...
    public boolean isFullyMatched(int cpp, int serviceID, int cvpID)
    { 
        ResultSet rs, tmpSet, funcSet;
        int numberMessages, numberOperations, funcMessages, dataMessages;
        String serviceName = null;
        String operationName = null;
        
	try{
            
            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("select * from services where service_id = " + serviceID + ";");

            if(rs != null)
            {
                    rs.next();
                    serviceID = rs.getInt("service_id");
                    numberMessages = rs.getInt("messages_number");
                    numberOperations = rs.getInt("operations_number");
                    funcMessages = 0;
                    dataMessages = 0;
                    serviceName = new String(rs.getString("name"));

                    System.out.println(numberMessages +" "+ numberOperations);
                    dbConnector dbHandler2 = new dbConnector();                    
/*                    dbHandler2.dbOpen();
                    tmpSet = dbHandler2.dbQuery("select count(operation_name) as cnt from dataannotations where cvp_cvp_id=" + cvpID +";");
                    if(tmpSet.next())
                    {
                        System.out.println(tmpSet.getInt("cnt"));
                        
                        dataMessages = tmpSet.getInt("cnt");
                    }
                    tmpSet.close();
                    dbHandler2.dbClose();                    
*/
                    dbHandler2.dbOpen();
                    tmpSet = dbHandler2.dbQuery("select count(operation_name) as cnt from dataannotations where cvp_cvp_id=" + cvpID +";");
                    if(tmpSet.next())
                    {
                        System.out.println(tmpSet.getInt("cnt"));
                        dataMessages = dataMessages + tmpSet.getInt("cnt");
                    }
                    tmpSet.close();
                    dbHandler2.dbClose();                                        
                    
                    dbHandler2.dbOpen();
                    funcSet = dbHandler2.dbQuery("select count(cvp_cvp_id) as cnt from funcannotations where cvp_cvp_id=" + cvpID + ";");
                    if(funcSet.next())
                    {
                        funcMessages  = funcSet.getInt("cnt");
                    }
                    System.out.println(funcMessages);
                    if(dataMessages==numberMessages && funcMessages==numberOperations)
                        return true;
                    else
                        return false;
                    
//                    dbHandler2.dbClose();
            }
            
            rs.close();
            
            this.dbHandler.dbClose();
	}
	catch(Throwable t)
	{
		t.printStackTrace(); 
	}
        
        return false;
    } 
}
