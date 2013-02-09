/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vendorports;

import dataaccesslayer.*;
import java.io.File;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
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

            rs = this.dbHandler.dbQuery("select * from vendor where name='" + vendor + "'");

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

    public String deleteSoftware(String software_id) {
        ResultSet rs;
        String message = "";
        try {
            //check if the software has web services
            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("SELECT sc.software_id as software_id FROM softwarecomponent sc, web_service ws WHERE  sc.software_id=ws.software_id and  sc.software_id=" + software_id);
            if (rs.next()) {
                message = "The software Component you want to delete has assigned some web services.Please delete first the web services!";

            } else {
                this.dbHandler.dbUpdate("delete from softwarecomponent where software_id=" + software_id);
                message = "Software component is deleted";
            }

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return message;
    }

    public JSONObject deleteSchema(String schema_id) {
        ResultSet rs;
        String message = "";
        int operation_id = -1;
        int service_id = -1;
        String schema_name="";
        String schema_location = "";
        JSONObject o = new JSONObject();

        try {
           
            //get the schema operation and web service
            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("select os.operation_id as operation_id, o.service_id as service_id, s.name as schema_name, s.location as schema_location   "
                    + "from operation_schema os, schema_xsd s, operation o where os.operation_id=o.operation_id "
                    + "and os.schema_id=s.schema_id and s.schema_id=" + schema_id);
            if (rs.next()) {
                operation_id = rs.getInt("operation_id");
                service_id = rs.getInt("service_id");
                schema_name = rs.getString("schema_name");
                schema_location = rs.getString("schema_location");
                o.put("modelID", schema_id+"_"+schema_name);
            }
            this.dbHandler.dbUpdate("delete from dataannotations where schema_id=" + schema_id);
            this.dbHandler.dbUpdate("delete from schema_xsd where schema_id=" + schema_id);
            this.dbHandler.dbUpdate("delete from operation_schema where schema_id=" + schema_id);
            //this.dbHandler.dbUpdate("delete from operation where operation_id=" + operation_id);
            rs.close();
            message = "Schema component is deleted. ";
            this.dbHandler.dbClose();

            message = message + this.deleteSchemaService(service_id,operation_id,schema_id);
            o.put("message", message);
            
             File fileTodelete = new File(schema_location);
             boolean success = fileTodelete.delete();
            
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return o;
    }

    public String deleteSchemaService(int service_id,int operation_id, String schema_id) {
        ResultSet rs,rs1;
        String message = "";
        int cpp_id=-1;
        boolean disable_bridge= false;
        int op_num=-1;
        try {
            //delete web service of schema only if has no other operation assigned to it
            this.dbHandler.dbOpen();
            
            rs = this.dbHandler.dbQuery("select count(*) as op_num "
                    + "from operation o where o.service_id=" + service_id);
            
            if (rs.next()){
                op_num = rs.getInt("op_num");
            }
            rs.close();
            //System.out.println("op_num"+op_num+"    "+"select count(*) as op_num from operation o where o.service_id=" + service_id);
            if (op_num==1){
                
                
                //int cvp_id = this.dbHandler.dbUpdate("delete from cvp where service_id=" + service_id);
                //cpp_id = this.dbHandler.dbUpdate("delete from cpp where cvp_id=" + cvp_id);
                rs1 = this.dbHandler.dbQuery("select cpp.cpp_id as cpp_id  from cpp cpp, cvp cvp where cpp.cvp_id=cvp.cvp_id and cvp.service_id=" + service_id);
                if (rs1.next()){
                cpp_id = rs1.getInt("cpp_id");
                } 
                rs1.close();
                 
                this.dbHandler.dbUpdate("delete cpp.* from cpp,cvp where cpp.cvp_id=cvp.cvp_id and  service_id="+service_id);
                System.out.println("cpp deleted id: "+cpp_id);
                this.dbHandler.dbUpdate("delete from cvp where service_id=" + service_id);
                this.dbHandler.dbUpdate("delete from operation where operation_id=" + operation_id);
                this.dbHandler.dbUpdate("delete from web_service where service_id=" + service_id);
                disable_bridge=true;
                message = " The web service has no schemas any more and is deleted too. ";
            
            
            }else{
                this.dbHandler.dbUpdate("delete from operation where operation_id=" + operation_id);
            }

            this.dbHandler.dbClose();
            
            if (disable_bridge)   message = message + this.desactivateBridge(cpp_id);
            
            this.desactivateBridgeSchema(service_id,schema_id);
            
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return message;
    }

    public String deleteWebService(int service_id,String service_location,JSONObject xsdSchemasToDelete) {
        ResultSet rs;
        String message = "";
        int cvp_id = -1;
        int cpp_id = -1;
        try {
            //check if the web service has a cvp created
            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("select cvp.cvp_id as cvp_id , cpp.cpp_id as cpp_id from cvp cvp "
                    + "LEFT JOIN cpp cpp on cvp.cvp_id=cpp.cvp_id "
                    + "where   cvp.service_id=" + service_id);

            if (rs.next()) {

                //delete  --operation -- dataannotations -- cpp -- cvp
                cvp_id = rs.getInt("cvp_id");
                cpp_id = rs.getInt("cpp_id");
                System.out.println("info cvp_id: " + cvp_id + "cpp_id" + cpp_id);
                this.dbHandler.dbUpdate("delete from operation where service_id=" + service_id);
                this.dbHandler.dbUpdate("delete from dataannotations where cvp_id=" + cvp_id);
                this.dbHandler.dbUpdate("delete from cpp where cvp_id=" + cvp_id);
                this.dbHandler.dbUpdate("delete from cvp where cvp_id=" + cvp_id);
                
        // Delete schemas extracted from Types of wsdl       
        Iterator keys = xsdSchemasToDelete.keys();

        while( keys.hasNext() ){
            String key = (String)keys.next();
            File fileTodelete = new File(xsdSchemasToDelete.get(key).toString());
            boolean success = fileTodelete.delete(); 
        }

            }
            //delete in any case -- installedbinding and web service

            this.dbHandler.dbUpdate("delete from installedbinding where service_id=" + service_id);
            this.dbHandler.dbUpdate("delete from web_service where service_id=" + service_id);
            
            //Delete .wsdl file
            File fileTodelete = new File(service_location);
            boolean success = fileTodelete.delete(); 
            

            //desactivate possible bridge if any
            rs.close();
            this.dbHandler.dbClose();

            message = this.desactivateBridge(cpp_id);

            message = " The web service has been deleted. " + message;

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return message;
    }

    public String desactivateBridge(int cpp_id) {
        ResultSet rs;
        String message = "";

        try {
            //check if there is any cpa with cpp first or second the input cpp_id
            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("SELECT cpa.cpa_id as cpa_id FROM cpa cpa WHERE cpp_id_first=" + cpp_id + " || cpp_id_second=" + cpp_id);

            System.out.println("SELECT cpa.cpa_id as cpa_id FROM cpa cpa WHERE cpp_id_first=" + cpp_id + " || cpp_id_second=" + cpp_id);
            if (rs != null) {
                while (rs.next()) {

                    int cpa_id = rs.getInt("cpa_id");
                    this.dbHandler.dbUpdate("update cpa set disabled=true where cpa_id=" + cpa_id);
                    message = " And the Bridges that participates have been disabled. ";
                }
            }
            rs.close();
            this.dbHandler.dbClose();

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return message;
    }
    
    public String desactivateBridgeSchema(int service_id, String schema_id) {
        ResultSet rs;
        String message = "";

        try {
            //check if there is any cpa that includes the deleted schema_id
            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("SELECT cpa.cpa_id,cpa.cpa_info FROM cpa cpa , cvp cvp , cpp cpp WHERE (cpa.cpp_id_first=cpp.cpp_id or cpa.cpp_id_second=cpp.cpp_id) and cpp.cvp_id=cvp.cvp_id and cvp.service_id="+service_id);

            if (rs != null) {
                while (rs.next()) {

                    String cpa_info = rs.getString("cpa_info");
                    int cpa_id = rs.getInt("cpa_id");
                    
                    JSONObject o = new JSONObject();
                    o = (JSONObject) JSONSerializer.toJSON(cpa_info);
                    JSONObject o_first = (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_first"));
                    JSONObject o_second = (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_second"));

                    if (o_first.getString("schema_id").equalsIgnoreCase(schema_id) || o_second.getString("schema_id").equalsIgnoreCase(schema_id)){
                       this.dbHandler.dbUpdate("update cpa set disabled=true where cpa_id=" + cpa_id);
                        }
                }
            }
            rs.close();
            this.dbHandler.dbClose();

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return message;
    }
    

    /*
     * Get the software components that belong to a vendor with the schemas and
     * services of each component
     */
    public Collection getSoftwareComponents(String vendor) {
        ResultSet rs, rsServ, rsSchemas;
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
                    int schemas_num = (rsSchemas.next()) ? rsSchemas.getInt("schemas_num") : 0;
                    rsSchemas.close();

                    rsServ = dbHand.dbQuery("select count(ws.service_id) as services_num from  web_service ws where ws.software_id=" + rs.getString("software_id")+ "  AND ws.wsdl IS NOT NULL");
                    int services_num = (rsServ.next()) ? rsServ.getInt("services_num") : 0;
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

    public int insertSchemaInfo(int software_id, String XSDName, String XSDFilename, String xmlRepPath, String new_web_service_name, String web_service, String operation_name, String inputoutput) {
        int schema_id = -1, service_id = -1, operation_id = -1, operation_schema_id = -1;
        String version = new String("");
        String filename = null;

        try {
            this.dbHandler.dbOpen();


            if (new_web_service_name.equalsIgnoreCase("")) {
                service_id = Integer.parseInt(web_service);
            } else {
                service_id = this.dbHandler.dbUpdate("INSERT INTO web_service(name,software_id) VALUES ('" + new_web_service_name + "'," + software_id + ")");
            }


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

    public int insertServiceInfo(int software_id, String serviceName, String service_version , String wsdlFilename, String xmlRepPath, String namespace) {
        ResultSet rs;
        int service_id = -1;
        Iterator servicePorts;
        int numberOperations, numberMessages;

        try {
            // for mysql compatibility
            wsdlFilename = wsdlFilename.replace("\\", "\\\\");

            this.dbHandler.dbOpen();

            WSDLParser wsdlParser = new WSDLParser(wsdlFilename, namespace);
            wsdlParser.loadService(serviceName);
            numberOperations = wsdlParser.getOperationsNumber();
            System.out.println("numberOperations: " + numberOperations);
            numberMessages = wsdlParser.getMessageNumber();
            System.out.println("numberMessages: " + numberMessages);

            service_id = this.dbHandler.dbUpdate("insert into web_service(name,version,software_id,namespace,wsdl,operation_number, messages_number) values('"
                    + serviceName + "','" +service_version + "'," +software_id + ",'" + namespace + "','" + wsdlFilename + "'," + numberOperations + "," + numberMessages + ");");

            servicePorts = wsdlParser.returnServicePortsString();

            String soap_location= wsdlParser.getSoapAdressURL(serviceName);

            while (servicePorts.hasNext()) {
                this.dbHandler.dbUpdate("insert into installedbinding(service_id,url_binding,service_port_name) values("
                        + service_id + ",'" + soap_location+"','"+ servicePorts.next().toString() + "');");
            }
            System.out.println("servicePorts: " + servicePorts.next().toString());

            this.dbHandler.dbClose();
        } catch (Throwable t) {
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

    /*
     * put in MainControlDB public int insertCVP(String annotations, int
     * schema_id, String vendorName, String json, String selections) { ResultSet
     * rs; int cvp = 0, dataannotations_id, vendor_id, cvp_id; int num = 0;
     *
     * try {
     *
     * // get vendor id vendor_id = getuserid(vendorName); System.out.println("
     * vendor_id:" + vendor_id);
     *
     * //check if exists an other cvp this.dbHandler.dbOpen();
     *
     * rs = this.dbHandler.dbQuery("SELECT cvp_id, da.dataAnnotations_id from
     * dataannotations da, cvp cvp WHERE schema_id=" + schema_id +" and
     * selections='"+selections+"' and da.dataAnnotations_id =
     * cvp.dataAnnotations_id");
     *
     * if (rs.next()) { cvp_id = rs.getInt("cvp_id"); System.out.println(" CVP
     * exists:" + cvp);
     *
     * //update dataannotations info and vendor name info of cvp
     *
     * this.dbHandler.dbUpdate("update dataannotations set mapping='" + json +
     * "', xslt_annotations ='" + annotations + "', selections =
     * '"+selections+"' where dataannotations_id=" +
     * rs.getInt("dataannotations_id") + ";"); System.out.println("update
     * dataannotations"); this.dbHandler.dbUpdate("update cvp set vendor_id='" +
     * vendor_id + "' where cvp_id=" + cvp_id + ";"); System.out.println("update
     * cvp");
     *
     * } else { dataannotations_id = this.dbHandler.dbUpdate("insert into
     * dataannotations (schema_id, xslt_annotations,mapping,selections) values
     * (" + schema_id + ",'" + annotations + "','" + json +
     * "','"+selections+"');");
     *
     * System.out.println(" create dataannotations:" + dataannotations_id);
     *
     * // insert cvp cvp_id = this.dbHandler.dbUpdate("insert into
     * cvp(dataAnnotations_id, vendor_id) values(" + dataannotations_id + ",'" +
     * vendor_id + "');");
     *
     * System.out.println(" create cvp:" + cvp_id); }
     *
     * this.dbHandler.dbClose(); } catch (Throwable t) { t.printStackTrace(); }
     *
     * return cvp; }
     */
    /*
     * put in MainControlDB public CVP getCVP(int cvpID) { CVP cvp = null;
     * ResultSet rs;
     *
     * try {
     *
     * this.dbHandler.dbOpen(); rs = this.dbHandler.dbQuery("select * from cvp
     * where cvp_id=" + cvpID);
     *
     * if (rs != null) { rs.next(); // cvp = new
     * CVP(Integer.parseInt(rs.getString("service_id")),
     * rs.getString("func_annotations"), rs.getString("data_annotations")); }
     *
     * rs.close();
     *
     * this.dbHandler.dbClose(); } catch (Throwable t) { t.printStackTrace(); }
     *
     * return cvp;
    }
     */
    public int getuserid(String name) {
        ResultSet rs;
        int user_id = -1;
        try {

            System.out.println("name: " + name);
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
        System.out.println("user_id: " + user_id);
        return user_id;
    }

    public String getMapping(int schema_id, String selections) {
        ResultSet rs;
        String mappings = null;

        try {
            this.dbHandler.dbOpen();
            System.out.println("selections: " + selections);
            rs = this.dbHandler.dbQuery("select mapping from dataannotations where schema_id=" + schema_id + " and selections='" + selections + "'");

            if (rs.next()) {
                mappings = rs.getString("mapping");
            }

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return mappings;
    }

    //  To use in futur...
    public boolean isFullyMatched(int cpp, int serviceID, int cvpID) {
        ResultSet rs, tmpSet, funcSet;
        int numberMessages, numberOperations, funcMessages, dataMessages;
        String serviceName = null;
        String operationName = null;

        try {

            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("select * from services where service_id = " + serviceID + ";");

            if (rs != null) {
                rs.next();
                serviceID = rs.getInt("service_id");
                numberMessages = rs.getInt("messages_number");
                numberOperations = rs.getInt("operations_number");
                funcMessages = 0;
                dataMessages = 0;
                serviceName = new String(rs.getString("name"));

                System.out.println(numberMessages + " " + numberOperations);
                dbConnector dbHandler2 = new dbConnector();
                /*
                 * dbHandler2.dbOpen(); tmpSet = dbHandler2.dbQuery("select
                 * count(operation_name) as cnt from dataannotations where
                 * cvp_cvp_id=" + cvpID +";"); if(tmpSet.next()) {
                 * System.out.println(tmpSet.getInt("cnt"));
                 *
                 * dataMessages = tmpSet.getInt("cnt"); } tmpSet.close();
                 * dbHandler2.dbClose();
                 */
                dbHandler2.dbOpen();
                tmpSet = dbHandler2.dbQuery("select count(operation_name) as cnt from dataannotations where cvp_cvp_id=" + cvpID + ";");
                if (tmpSet.next()) {
                    System.out.println(tmpSet.getInt("cnt"));
                    dataMessages = dataMessages + tmpSet.getInt("cnt");
                }
                tmpSet.close();
                dbHandler2.dbClose();

                dbHandler2.dbOpen();
                funcSet = dbHandler2.dbQuery("select count(cvp_cvp_id) as cnt from funcannotations where cvp_cvp_id=" + cvpID + ";");
                if (funcSet.next()) {
                    funcMessages = funcSet.getInt("cnt");
                }
                System.out.println(funcMessages);
                if (dataMessages == numberMessages && funcMessages == numberOperations) {
                    return true;
                } else {
                    return false;
                }

//                    dbHandler2.dbClose();
            }

            rs.close();

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return false;
    }
}
