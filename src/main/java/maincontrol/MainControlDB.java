/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maincontrol;

import dataaccesslayer.Operation;
import dataaccesslayer.Schema;
import dataaccesslayer.Service;
import dataaccesslayer.dbConnector;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.LinkedList;

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
    
    
    /*
     * Get all services that belong to a software component. if cvp is true the
     * functions only returns those that have been annotated.
     * We put that boolean condition so as to hide services that have never been annotated from organization
     */
    public Collection getServices(String software_id, boolean cvp) {
        ResultSet rs;
        LinkedList<Service> compList = new LinkedList<Service>();

        try {

            this.dbHandler.dbOpen();
            rs = (cvp) ? this.dbHandler.dbQuery("select ws.service_id as service_id,ws.name as service_name , da.dataAnnotations_id as dataAnnotations "
                    + "from web_service ws LEFT JOIN operation o on ws.service_id=o.service_id LEFT JOIN operation_schema os  on o.operation_id = os.operation_id "
                    + "LEFT JOIN schema_xsd  s  on os.schema_id = s.schema_id RIGHT JOIN dataannotations  da  on  s.schema_id = da.schema_id "
                    + "where ws.software_id=" + software_id+" and ws.wsdl IS NOT NULL")
                    : this.dbHandler.dbQuery("select ws.service_id as service_id, ws.name as service_name from web_service ws where ws.software_id=" + software_id+" and ws.wsdl IS NOT NULL");

            
            dbConnector dbHand = new dbConnector();

            dbHand.dbOpen();


            if (rs != null) {
                while (rs.next()) {
                    compList.add(new Service(rs.getInt("service_id"), rs.getString("service_name")));
                }
            }

            rs.close();

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return compList;
    }
    /*
     * Get all schemas that belong to a software component. if cvp is true the
     * functions only returns those that have been annotated.
     * We put that boolean condition so as to hide schemas that have never been annotated from organization
     */

    public Collection getSchemas(String software_id, boolean cvp) {
        ResultSet rs;
        LinkedList<Schema> XSDList = new LinkedList<Schema>();

        try {

            this.dbHandler.dbOpen();

            rs = (cvp) ? this.dbHandler.dbQuery("select s.schema_id as schema_id,s.name as schema_name "
                    + "from operation o  LEFT JOIN web_service ws on o.service_id=ws.service_id  LEFT JOIN operation_schema os  on o.operation_id = os.operation_id "
                    + "LEFT JOIN schema_xsd  s  on os.schema_id = s.schema_id RIGHT JOIN cvp  cvp  on  cvp.service_id = ws.service_id "
                    + "where ws.software_id="+software_id+" and ws.wsdl IS NULL")
                    : this.dbHandler.dbQuery("select s.schema_id as schema_id,s.name as schema_name from operation o LEFT JOIN web_service ws on o.service_id=ws.service_id LEFT JOIN operation_schema os  on o.operation_id = os.operation_id LEFT JOIN schema_xsd  s  on os.schema_id = s.schema_id where ws.wsdl IS NULL and ws.software_id=" + software_id);

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

    public void functionalAnnotationBySchema(int operation_id,String funcSelections,int cvp_id) {

        try {
            this.dbHandler.dbOpen();

            if (cvp_id==-1)  this.dbHandler.dbUpdate("update operation set taxonomy_id='" + funcSelections + "' where operation_id=" + operation_id);
            else  this.dbHandler.dbUpdate("update operation set taxonomy_id='" + funcSelections + "',cvp_id="+cvp_id+" where operation_id=" + operation_id);
            
            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
            System.out.println("Insert Taxonomy " + t);

        }
    }
    
     public void functionalAnnotationByService(int service_id,String operation_name,String funcSelections,int cvp_id) {
         ResultSet rs;
         
         System.out.println(service_id+operation_name+funcSelections+cvp_id);
        try {
            this.dbHandler.dbOpen();
            
            //check if the operation exists for this service_id
            rs= this.dbHandler.dbQuery("select o.operation_id as operation_id from operation o where o.service_id="+service_id+" and o.name='"+operation_name+"'");
            
            if (rs.next()){
                if (cvp_id==-1)  this.dbHandler.dbUpdate("update operation set taxonomy_id='" + funcSelections + "' where operation_id=" + rs.getInt("operation_id"));
                else  this.dbHandler.dbUpdate("update operation set taxonomy_id='" + funcSelections + "',cvp_id="+cvp_id+" where operation_id=" + rs.getInt("operation_id"));
            
            }else{
                if (cvp_id==-1)  this.dbHandler.dbUpdate("insert into operation(name,service_id,taxonomy_id) values('" + operation_name + "',"+service_id+",'"+funcSelections+"')");
                else  this.dbHandler.dbUpdate("insert into operation(cvp_id,name,service_id,taxonomy_id) values(" + cvp_id + ",'" + operation_name + "',"+service_id+",'"+funcSelections+"')");
            }

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

    public String getMapping(int schema_id,int service_id, String selections) {
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

    public int insertCVP(int schema_id, int service_id, String vendorName) {
        ResultSet rs,rs1;
        int vendor_id, cvp_id=-1;
        int num = 0;

        try {

            // get vendor id
            vendor_id = getuserid(vendorName);
            System.out.println(" vendor_id:" + vendor_id);

            //check if exists an other cvp. (if the web service has a cvp asigned)
            this.dbHandler.dbOpen();
            
            rs = (service_id==-1)?this.dbHandler.dbQuery("select cvp.cvp_id as cvp_id from operation_schema os, operation o, cvp cvp where  o.operation_id=os.operation_id and cvp.service_id = o.service_id and os.schema_id = "+ schema_id)
                    : this.dbHandler.dbQuery("select cvp.cvp_id as cvp_id from  cvp cvp where  cvp.service_id="+service_id);
                     
            if (rs.next()) {
                cvp_id = rs.getInt("cvp_id");
                this.dbHandler.dbUpdate("update cvp set vendor_id='" + vendor_id + "' where cvp_id=" + cvp_id + ";");
                System.out.println("update cvp");

            } else {
                //get service_id that participates the schema
                rs1 =this.dbHandler.dbQuery("select o.service_id as service_id from operation_schema os, operation o where os.schema_id = "+schema_id+" and o.operation_id=os.operation_id");
                // insert cvp
                if (rs1.next()) {
                    cvp_id = this.dbHandler.dbUpdate("insert into cvp(service_id, vendor_id) values(" + rs1.getInt("service_id") + ",'" + vendor_id + "');");
                    System.out.println(" create cvp:" + cvp_id);
                }
                rs1.close();
                if (service_id!=-1) cvp_id = this.dbHandler.dbUpdate("insert into cvp(service_id, vendor_id) values(" + service_id + ",'" + vendor_id + "');");
                    
            }
            rs.close();

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return cvp_id;
    }
    
    
    public int insert_dataannotations(int cvp_id ,String annotations, int schema_id, String vendorName, String json, String selections) {
        ResultSet rs;
        int cvp = 0, dataannotations_id, vendor_id;
        int num = 0;

        try {
           this.dbHandler.dbOpen();

            //rs = this.dbHandler.dbQuery("SELECT cvp.cvp_id, da.dataAnnotations_id from dataannotations  da, cvp  cvp  WHERE schema_id=" + schema_id + " and selections='" + selections + "' and da.dataAnnotations_id = cvp.dataAnnotations_id");
              rs = this.dbHandler.dbQuery("select da.dataAnnotations_id as dataAnnotations_id from dataannotations  da where da.cvp_id ="+cvp_id+" and da.schema_id="+schema_id);  
            if (rs.next()) {
                dataannotations_id = rs.getInt("dataAnnotations_id");
                System.out.println(" dataannotations_id exists:" + dataannotations_id);
                //update dataannotations info
                this.dbHandler.dbUpdate("update dataannotations set mapping='" + json + "', xslt_annotations ='" + annotations + "', selections = '" + selections + "'  where dataannotations_id=" + dataannotations_id + ";");
                System.out.println("update dataannotations");
            } else {
                 //create dataannotations info
                dataannotations_id = this.dbHandler.dbUpdate("insert into dataannotations (schema_id, xslt_annotations,mapping,selections,cvp_id) values (" + schema_id + ",'" + annotations + "','" + json + "','" + selections + "',"+cvp_id+");");
                System.out.println(" create dataannotations:" + dataannotations_id);
            }

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return cvp;
    }
    public int getCVP(int schema_id) {
        ResultSet rs,rs1;
        int cvp = 0, vendor_id, cvp_id = -1;
        int num = 0;

        try {
            //check if exists an other cvp. (if the web service has a cvp asigned)
            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("select cvp.cvp_id as cvp_id from operation_schema os, operation o, cvp cvp where  o.operation_id=os.operation_id and cvp.service_id = o.service_id and os.schema_id ="+ schema_id);  
            if (rs.next()) cvp_id =  rs.getInt("cvp_id");
            else throw new EmptyStackException();

             this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }
            return cvp_id;
 
    }

    public int insertCPP(int schema_id, int service_id, String orgName) {
        ResultSet rs, rs1;
        int cvp_id = -1, organization_id, cpp_id = -1, vendor_id = -1, dataannotations_id = -1;
        try {

            // get organization id
            organization_id = getuserid(orgName);

            this.dbHandler.dbOpen();
            //check if exists a cvp
            //rs1 = this.dbHandler.dbQuery("SELECT cvp.cvp_id as cvp_id , da.dataAnnotations_id as dataAnnotations_id, cvp.vendor_id  as vendor_id from dataannotations  da, cvp  cvp  WHERE schema_id=" + schema_id + " and selections='" + selections + "' and da.dataAnnotations_id = cvp.dataAnnotations_id");
            rs1 = (service_id==-1)?this.dbHandler.dbQuery("select cvp.cvp_id as cvp_id, cvp.vendor_id  as vendor_id from operation_schema os, operation o, cvp cvp where  o.operation_id=os.operation_id and cvp.service_id = o.service_id and os.schema_id = "+ schema_id)
                    :this.dbHandler.dbQuery("select cvp.cvp_id as cvp_id, cvp.vendor_id  as vendor_id from cvp cvp where cvp.service_id ="+ service_id);
                 
           
            if (rs1.next()) {
                cvp_id = rs1.getInt("cvp_id");
                vendor_id = rs1.getInt("vendor_id");
                rs1.close();
                
                rs = this.dbHandler.dbQuery("SELECT cpp.cpp_id, cpp.organization_id from cpp cpp  WHERE cpp.cvp_id=" + cvp_id);
                if (rs.next()) {
                     // update cpp
                    cpp_id = rs.getInt("cpp_id");
                    this.dbHandler.dbUpdate("update cpp set organization_id='" + organization_id + "' where cpp_id=" + cpp_id + ";");
                } else {
                    // insert cpp
                    cpp_id = this.dbHandler.dbUpdate("insert into cpp(cvp_id, vendor_id, organization_id) values(" + cvp_id + "," + vendor_id + "," + organization_id + ");");
                }
                 rs.close();
                 
                 this.dbHandler.dbClose();
                 
            } else {
                throw new EmptyStackException();
            }            
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return cpp_id;
    }

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
    
    public String getSoftwareName(int software_id) {
        ResultSet rs;
        String name = "";
        try {

            System.out.println("name: " + name);
            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("select name from softwarecomponent where software_id='" + software_id + "'");

            if (rs != null) {
                rs.next();
                name = rs.getString("name");
            }

            rs.close();

            this.dbHandler.dbClose();
        } catch (Throwable t) {
        }
      
        return name;
    }
    
     public Service getService(int service_id)
    {
        Service service = null;
        ResultSet rs;
        
	try{
            
            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("select * from web_service where service_id=" + service_id);

            if(rs != null)
            {
                rs.next();
                service = new Service(service_id,rs.getString("name"), rs.getString("wsdl"), rs.getString("namespace"));
            }
            
            rs.close();
            
            this.dbHandler.dbClose();
	}
	catch(Throwable t)
	{
		t.printStackTrace(); 
	}

        return service;        
    }
     
    
}
