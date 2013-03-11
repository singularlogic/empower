/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orgports;

import com.google.gson.Gson;
import dataaccesslayer.*;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

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
        ResultSet rs, rsServ, rsSchemas;
        LinkedList<SoftwareComponent> compList = new LinkedList<SoftwareComponent>();
       try {

            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("select a.name as name, a.version as version, a.software_id as software_id from softwarecomponent a");

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
    
     public Collection getSchemas(String software_id,  String organization_name) {
        ResultSet rs;
        LinkedList<Schema> XSDList = new LinkedList<Schema>();

        int user_id= this.getUserID(organization_name);

       String sofwareCondition = (software_id.equalsIgnoreCase("-1"))?"":" ws.software_id="+software_id+" and ";

         try {

            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery(" select cpp.cpp_id as cpp_id, cpp.name as cpp_name , ws.software_id as software_id,ws.service_id as service_id,ws.name as ws_name,o.operation_id as operation_id,o.name as operation_name,o.taxonomy_id as op_taxonomy_id,os.inputoutput as inputoutput, s.schema_id as schema_id, s.location as schema_location, s.name as schema_name, cvp.cvp_id as cvp_id , da.selections as selections, da.xbrl as xbrl  "
                    + " from operation o "
                    + " LEFT JOIN web_service ws on o.service_id=ws.service_id "
                    + " LEFT JOIN operation_schema os  on o.operation_id = os.operation_id "
                    + " LEFT JOIN schema_xsd  s  on os.schema_id = s.schema_id "
                    + " LEFT JOIN dataannotations da on da.schema_id=s.schema_id "
                    + " LEFT JOIN cvp cvp on cvp.cvp_id = da.cvp_id  "
                    + " LEFT JOIN cpp cpp on cpp.cvp_id = cvp.cvp_id  "
                    + " where "+ sofwareCondition +" os.inputoutput='input'  and cvp.cvp_id IS NOT NULL and da.cpp_id IS NOT NULL  and cpp.organization_id="+user_id+ "  GROUP BY cpp.cpp_id  order by ws.service_id ");



             if (rs != null) {
                while (rs.next()) {
                    XSDList.add(new Schema(rs.getInt("service_id"), rs.getString("ws_name"),rs.getInt("operation_id"),rs.getString("operation_name"),rs.getString("op_taxonomy_id"),rs.getString("inputoutput"),rs.getInt("schema_id"),rs.getString("schema_location"), rs.getString("schema_name"), rs.getInt("cvp_id"),rs.getString("selections"),rs.getString("xbrl"),rs.getString("cpp_name"),rs.getInt("cpp_id")));
                }
            }

            rs.close();

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return XSDList;
    }
     
        public Collection getTargetSchemas(String inputoutput, String taxonomy_id,String xbrl_taxonomy, String organization_name) {
        ResultSet rs;
        LinkedList<Schema> XSDList = new LinkedList<Schema>();
        String xbrl = xbrl_taxonomy;
        int user_id= this.getUserID(organization_name);

        try {

            this.dbHandler.dbOpen();

           
            rs = this.dbHandler.dbQuery("select cpp.cpp_id as cpp_id, cpp.name as cpp_name , ws.software_id as software_id,ws.service_id as service_id,ws.name as ws_name,o.operation_id as operation_id,o.name as operation_name,o.taxonomy_id as op_taxonomy_id,os.inputoutput as inputoutput, s.schema_id as schema_id, s.location as schema_location, s.name as schema_name , cvp.cvp_id as cvp_id, da.selections as selections, da.xbrl as xbrl"
                    + " from operation o LEFT JOIN web_service ws on o.service_id=ws.service_id "
                    + " LEFT JOIN operation_schema os  on o.operation_id =os.operation_id  "
                    + " LEFT JOIN schema_xsd  s  on os.schema_id = s.schema_id "
                    + " LEFT JOIN dataannotations da on da.schema_id = s.schema_id "
                    + " LEFT JOIN cvp cvp on cvp.cvp_id = da.cvp_id "
                    + "LEFT JOIN cpp cpp on cpp.cvp_id = cvp.cvp_id"
                    + " where  o.taxonomy_id = '"+taxonomy_id+"' and os.inputoutput='output' and cvp.cvp_id IS NOT NULL and da.xbrl='"+xbrl+"'  and cpp.organization_id="+user_id+ "  GROUP BY cpp.cpp_id  order by ws.service_id ");





            if (rs != null) {
                while (rs.next()) {
                    XSDList.add(new Schema(rs.getInt("service_id"), rs.getString("ws_name"),rs.getInt("operation_id"),rs.getString("operation_name"),rs.getString("op_taxonomy_id"),rs.getString("inputoutput"),rs.getInt("schema_id"),rs.getString("schema_location"), rs.getString("schema_name"),rs.getInt("cvp_id"),rs.getString("selections"),rs.getString("xbrl"),rs.getString("cpp_name"),rs.getInt("cpp_id")));
                    System.out.println("Schema: "+ rs.getString("ws_name") + " " + rs.getInt("operation_id") + " " + rs.getString("operation_name") + " " + rs.getString("inputoutput")+ " " +rs.getInt("schema_id") + " " + rs.getString("schema_name")+ " " +rs.getInt("cvp_id") );
                
                }
            }

            rs.close();

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return XSDList;
    }
    
    public Collection getServicesByTaxonomy(String taxonomy_id, String softwareID) {
        ResultSet rs;
        LinkedList<Service> ServiceList = new LinkedList<Service>();
        
        try {

            this.dbHandler.dbOpen();
            String tax= (taxonomy_id.equalsIgnoreCase("All")) ? " IS NOT NULL": "='"+taxonomy_id+"'";
            String software_id = (softwareID.equalsIgnoreCase("All")) ? " IS NOT NULL": "="+softwareID;
           
            rs = this.dbHandler.dbQuery("select ws.service_id as service_id, ws.name as service_name, ws.version as service_version, ws.exposed as exposed, ws.wsdl as wsdl, ws.namespace as namespace from web_service ws,operation o where ws.service_id=o.service_id and ws.exposed=1 and o.taxonomy_id"+tax +" and ws.software_id"+software_id+" group by ws.service_id");
            System.out.println("select ws.service_id as service_id, ws.name as service_name, ws.exposed as exposed, ws.wsdl as wsdl, ws.namespace as namespace from web_service ws,operation o where ws.service_id=o.service_id and ws.exposed=1 and o.taxonomy_id"+tax+" and ws.software_id"+software_id+" group by ws.service_id");
            
            
            if (rs != null) {
                while (rs.next()) {
                    ServiceList.add(new Service(rs.getInt("service_id"), rs.getString("service_name"),rs.getString("service_version"),rs.getString("wsdl"),rs.getString("namespace"),rs.getBoolean("exposed")));
                }
            }

            rs.close();

            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return ServiceList;
    }
        
        
   public Map<String,Integer> insertBridging(int cvp_source , int cvp_target, String organization , String json, int installations_source, int installation_target) {
        ResultSet rs,rs1;
        int organization_id = -1, cpa_id = -1, organization_cpa= -1;
        Map<String, Integer> data = new HashMap<String, Integer>();

        try {
            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("select * from organization where name='" + organization + "';");

            rs.next();
            organization_id = rs.getInt("organization_id");
            rs.close();
            
            rs1 =  this.dbHandler.dbQuery("SELECT ib.cpa_id as cpa_id FROM organization_cpa ib, cpa cpa"
                    + " WHERE ib.cpa_id=cpa.cpa_id and organization_id="+organization_id+ " and cpa.cpp_id_first="+cvp_source+" and cpa.cpp_id_second="+cvp_target);
            
        
            if (rs1.next()){
              cpa_id=  rs1.getInt("cpa_id");
              data.put("existing",rs1.getInt("cpa_id"));
              this.dbHandler.dbUpdate("update cpa set cpa_info='"+ json +"',url_binding_first="+installations_source+",url_binding_second="+installation_target+"  where cpa_id="+cpa_id);
            }
            else{
                cpa_id = this.dbHandler.dbUpdate("insert into cpa(cpp_id_first,cpp_id_second,url_binding_first,url_binding_second,cpa_info,disabled) values('"
                    + cvp_source + "','" + cvp_target + "',"+installations_source+","+installation_target+",'"+json+"',false)");
            
                organization_cpa = this.dbHandler.dbUpdate("insert into organization_cpa(organization_id,cpa_id) values("+organization_id+","+cpa_id+")");

                data.put("new_cpa_id", cpa_id);
            }

            this.dbHandler.dbUpdate("update cpp set fromTo='From/CPA:"+cpa_id+"'  where cpp_id=" + cvp_source);
            this.dbHandler.dbUpdate("update cpp set fromTo='TO/CPA:"+cpa_id+"'  where cpp_id=" + cvp_target);

            rs1.close();
            
            this.dbHandler.dbClose();


        } catch (Throwable t) {
            t.printStackTrace();
        }

        return data;
    }
   
   
    public String retrieveXLST(int cpp_id,String inputoutput, String cpa_info, LinkedList<String> service_selections)
    {
        String xsltCode = null;
        ResultSet rs;
        
	try{
            
            if (service_selections.isEmpty()){
            
            JSONObject o = new JSONObject();
            o = (JSONObject) JSONSerializer.toJSON(cpa_info); 
            JSONObject info= (inputoutput=="input")? (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_first")):(JSONObject) JSONSerializer.toJSON(o.get("cppinfo_second")); 
            
            System.out.println("inputoutput:"+inputoutput+" cpp_id: "+cpp_id+" schmema_id: "+info.get("schema_id"));
            
            this.dbHandler.dbOpen();
            //rs = this.dbHandler.dbQuery("select da.xslt_annotations as xslt_annotations from dataannotations da, cvp cvp, cpp cpp  where cpp.cpp_id =" +cpp_id+ " and cpp.cvp_id = cvp.cvp_id and da.cvp_id=cvp.cvp_id ");
            rs = this.dbHandler.dbQuery("select da.xslt_annotations as xslt_annotations "
                    + " from dataannotations da, cvp cvp, cpp cpp ,operation_schema os  "
                    + " where cpp.cvp_id = cvp.cvp_id and da.cvp_id=cvp.cvp_id  and os.schema_id = da.schema_id and da.cpp_id = cpp.cpp_id "
                    + " and os.inputoutput='"+inputoutput+"' and cpp.cpp_id ="+cpp_id+"  and da.schema_id="+info.get("schema_id") +"  and da.selections LIKE '%"+info.get("schema_complexType")+"%'  and os.operation_id="+info.get("operation_id"));



                if(rs.next())
                xsltCode = new String(rs.getString("xslt_annotations"));

            this.dbHandler.dbClose();
            
            }else{
            
             JSONObject o = new JSONObject();
            o = (JSONObject) JSONSerializer.toJSON(cpa_info); 
            JSONObject info= (inputoutput=="input")? (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_first")):(JSONObject) JSONSerializer.toJSON(o.get("cppinfo_second")); 
            
            String selections = (inputoutput=="input")?service_selections.get(1):service_selections.get(2);
            
            System.out.println("inputoutput:"+inputoutput+" cpp_id: "+cpp_id+" service_id: "+info.get("service_id"));
            
            this.dbHandler.dbOpen();
             rs = this.dbHandler.dbQuery("select da.xslt_annotations as xslt_annotations "
                    + " from dataannotations da, cpp cpp    "
                    + " where cpp.cpp_id=da.cpp_id and cpp.cpp_id ="+cpp_id+"  and da.selections='"+selections+"'");

            if(rs.next())
                xsltCode = new String(rs.getString("xslt_annotations"));

            this.dbHandler.dbClose();
            
            
            
            }
	}
	catch(Throwable t)
	{
		t.printStackTrace(); 
	}

	return xsltCode;
    }
    
     public CPA getinfocpa(int cpa_id)
     {
         ResultSet rs;
         CPA cpa = null;
         try{
            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("select cpa.cpp_id_first as cpp_id_first,cpa.cpp_id_second as cpp_id_second, cpa.cpa_info as cpa_info, cpa.disabled as disabled from cpa where cpa.cpa_id=" + cpa_id );
            
            if(rs.next())
               cpa = new CPA(cpa_id,rs.getInt("cpp_id_first"),rs.getInt("cpp_id_second"),rs.getString("cpa_info"),rs.getBoolean("disabled"));

            this.dbHandler.dbClose();
	}
	catch(Throwable t)
	{
		t.printStackTrace(); 
	}
         return cpa;
     
     }
     
     
    public String getServiceName(int service_id)
    {
        ResultSet rs;
        String service_name="";
        try{
            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("SELECT ws.name as name FROM web_service ws WHERE service_id=" + service_id );
            
            if(rs.next())
                service_name = rs.getString("name");

            this.dbHandler.dbClose();
	}
	catch(Throwable t)
	{
		t.printStackTrace(); 
	}
        return service_name;
    }

    public String getUrlBindingAddress(int urlBinding_id)
    {
        ResultSet rs;
        String url_binding="";
        try{
            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("SELECT * FROM `installedbinding` WHERE installedbinding_id=" + urlBinding_id);

            if(rs.next())
                url_binding = rs.getString("url_binding");

            this.dbHandler.dbClose();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
        return url_binding;
    }
    
     public String getOperationName(int operation_id)
    {
        ResultSet rs;
        String operation_name="";
        try{
            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("SELECT name FROM operation WHERE operation_id=" + operation_id );
            
            if(rs.next())
                operation_name = rs.getString("name");

            this.dbHandler.dbClose();
	}
	catch(Throwable t)
	{
		t.printStackTrace(); 
	}
        return operation_name;
    }
     
      public String getSchemaName(int schema_id)
    {
        ResultSet rs;
        String schema_name="";
        try{
            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("SELECT name FROM schema_xsd WHERE schema_id=" + schema_id );
            
            if(rs.next())
                schema_name = rs.getString("name");

            this.dbHandler.dbClose();
	}
	catch(Throwable t)
	{
		t.printStackTrace(); 
	}
        return schema_name;
    }
      
      public int getUserID(String organization_name)
    {
        ResultSet rs;
        int organization_id=-1;
        try{
            this.dbHandler.dbOpen();
            rs = this.dbHandler.dbQuery("SELECT organization_id FROM organization WHERE name='" + organization_name+"'" );
            
            if(rs.next())
                organization_id = rs.getInt("organization_id");

            this.dbHandler.dbClose();
	}
	catch(Throwable t)
	{
		t.printStackTrace(); 
	}
        return organization_id;
    }
      
    public LinkedList<CPA> getCPAs(String organization_name){
    ResultSet rs;
     LinkedList<CPA> cpaList = new LinkedList<CPA>();
     int organization_id = this.getUserID(organization_name);
        
	try{
            
            this.dbHandler.dbOpen();
            
            rs = this.dbHandler.dbQuery("select cpa.cpa_id as cpa_id,cpa.cpp_id_first as cpp_id_first,cpa.cpp_id_second as cpp_id_second,cpa.cpa_info as cpa_info,cpa.disabled as disabled from organization_cpa ib, cpa cpa where ib.cpa_id=cpa.cpa_id and organization_id="+organization_id);

            if(rs != null)
            {
                while(rs.next())
                    cpaList.add(new CPA(rs.getInt("cpa_id"),rs.getInt("cpp_id_first"),rs.getInt("cpp_id_second"),rs.getString("cpa_info"),rs.getBoolean("disabled")));
            }
            rs.close();
            
            this.dbHandler.dbClose();
	}
	catch(Throwable t)
	{
		t.printStackTrace(); 
	}

        return cpaList;
    
    }
    
    
    /*
     * Given the cvp_in we look for the cpp_id so as to create the cpa registry
     * if ther is not no cpp we create it
     */
    public int getCPP(int cvp_id,String organization_name){
        ResultSet rs,rs1;
        int cpp_id = -1;
        try{
            int organization_id = this.getUserID(organization_name);
            
            this.dbHandler.dbOpen();
            
            rs = this.dbHandler.dbQuery("select cpp_id from cpp where cvp_id="+cvp_id + " and organization_id="+organization_id);

            if(rs.next()){
                cpp_id =rs.getInt("cpp_id");
                rs.close();
            }else {
                rs1 = this.dbHandler.dbQuery("select vendor_id from cvp where cvp_id="+cvp_id);
                  if(rs1.next()) {
                      int vendor_id = rs1.getInt("vendor_id");
                      cpp_id = this.dbHandler.dbUpdate("insert into cpp(cvp_id,vendor_id,organization_id) values("
                    + cvp_id + "," + vendor_id + ","+organization_id+")");
                  }
            }

            this.dbHandler.dbClose();
	}
	catch(Throwable t)
	{
		t.printStackTrace(); 
	}
        return cpp_id;
    }


    public String getCPPName(int cpp_id){


        ResultSet rs;
        String cpp_name="";
        try{

            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("select cpp.name as name from cpp cpp where cpp.cpp_id="+cpp_id);

            if(rs != null)
            {
                while(rs.next()){
                cpp_name =rs.getString("name");
                }

            }
            rs.close();
            this.dbHandler.dbClose();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
        return cpp_name;
    }


    // Given an organization and a Soft Component we get all CPPS
    public LinkedList<Service> getCPPs(String organization_name, int software_id,String serviceORschema){
        ResultSet rs;
        LinkedList<Service> cppServList = new LinkedList<Service>();

        System.out.println("serviceORschema: "+serviceORschema);

        try{
            int organization_id = this.getUserID(organization_name);

            this.dbHandler.dbOpen();

            rs =(serviceORschema.equalsIgnoreCase("service"))? this.dbHandler.dbQuery("select ws.service_id as service_id, ws.name as service_name, ws.version as service_version, cpp.name as cpp_name,cpp.cpp_id as cpp_id, ws.exposed as exposed, ws.wsdl as wsdl, ws.namespace as namespace" +
                    " from web_service ws,cvp cvp,cpp cpp where ws.service_id=cvp.service_id and cpp.cvp_id=cvp.cvp_id and ws.exposed=1 and ws.software_id="+software_id+" and cpp.organization_id="+organization_id+"  and ws.wsdl IS NOT NULL"):
                    this.dbHandler.dbQuery("select ws.service_id as service_id, ws.name as service_name, ws.version as service_version, cpp.name as cpp_name,cpp.cpp_id as cpp_id, ws.exposed as exposed, ws.wsdl as wsdl, ws.namespace as namespace" +
                    " from web_service ws,cvp cvp,cpp cpp where ws.service_id=cvp.service_id and cpp.cvp_id=cvp.cvp_id and ws.software_id="+software_id+" and cpp.organization_id="+organization_id+"  and cpp.cpp_id IS NOT NULL and ws.wsdl IS NULL");

            System.out.println("select ws.service_id as service_id, ws.name as service_name, ws.version as service_version, cpp.name as cpp_name,cpp.cpp_id as cpp_id, ws.exposed as exposed, ws.wsdl as wsdl, ws.namespace as namespace" +
                    " from web_service ws,cvp cvp,cpp cpp where ws.service_id=cvp.service_id and cpp.cvp_id=cvp.cvp_id and ws.software_id="+software_id+" and cpp.organization_id="+organization_id+"  and cpp.cpp_id IS NOT NULL");

            if (rs != null) {
                while (rs.next()) {

                    cppServList.add(new Service(rs.getInt("service_id"), rs.getString("service_name"),rs.getString("cpp_name"),rs.getInt("cpp_id"),rs.getString("service_version"), rs.getString("wsdl"), rs.getString("namespace"), rs.getBoolean("exposed")));
                }}

                    this.dbHandler.dbClose();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
        return cppServList;
    }



    /*
     * Given the cvp_in we look for the cpp_id so as to create the cpa registry
     * if ther is not no cpp we create it
     */
    public int getCPP(int service_id){
        ResultSet rs,rs1;
        int cpp_id = -1;
        try{
                     
            this.dbHandler.dbOpen();
            
            rs = this.dbHandler.dbQuery("select cpp.cpp_id as cpp_id from cpp cpp, cvp cvp where cvp.service_id="+service_id+ " and cvp.cvp_id=cpp.cvp_id");

            if(rs.next()){
                cpp_id =rs.getInt("cpp_id");
                rs.close();
            }

            this.dbHandler.dbClose();
	}
	catch(Throwable t)
	{
		t.printStackTrace(); 
	}
        return cpp_id;
    } 
    
    
    /*
     * Given the cvp_in we look for the cpp_id so as to create the cpa registry
     * if ther is not no cpp we create it
     */
    public CPA getCPA(int cpa_id){
        ResultSet rs,rs1;
        int cpp_id = -1;
        CPA cpa = new CPA();
        try{
                     
            this.dbHandler.dbOpen();
            
            rs = this.dbHandler.dbQuery("select * from cpa where cpa_id="+cpa_id);

            if(rs.next()){
                
                cpa.setCpa_id(rs.getInt("cpa_id"));
                cpa.setCpp_id_first(rs.getInt("cpp_id_first"));
                cpa.setCpp_id_second(rs.getInt("cpp_id_second"));
                cpa.setCpa_info(rs.getString("cpa_info"));
                
                rs.close();
            }

            this.dbHandler.dbClose();
	}
	catch(Throwable t)
	{
		t.printStackTrace(); 
	}
        return cpa;
    }
    
    
    public void deleteBridge(int cpa_id){
       int return_id=-1;
        try{
                     
            this.dbHandler.dbOpen();
            
            return_id = this.dbHandler.dbUpdate("delete from cpa where cpa_id="+cpa_id);

            this.dbHandler.dbClose();
	}
	catch(Throwable t)
	{
		t.printStackTrace(); 
	}
    
    
    }



    public void insertNewCPP(int father_cpp_id,String organization_name,String cpp_name,int cpa_id) {
        ResultSet rs;
        int cvp_id = 0;
        int vendor_id = 0;

        LinkedList<DataAnnotations> daList = new LinkedList<DataAnnotations>();
        try {

            int organization_id =  this.getUserID(organization_name);

            this.dbHandler.dbOpen();


            //get all data annotations with specific cvp and insert them adding the cpp_id
            rs= this.dbHandler.dbQuery("SELECT * FROM dataannotations da, cpp cpp WHERE da.cpp_id=cpp.cpp_id and da.cpp_id="+father_cpp_id);

            System.out.println("axxx baxxx SELECT * FROM dataannotations da, cpp cpp WHERE da.cpp_id=cpp.cpp_id and da.cpp_id="+father_cpp_id);
            if (rs != null) {
                while (rs.next()) {
                    daList.add(new DataAnnotations(rs.getInt("dataAnnotations_id"),rs.getInt("da.schema_id"), rs.getString("xslt_annotations"), rs.getString("mapping"), rs.getString("selections"), rs.getString("xbrl")));
                    cvp_id= rs.getInt("cvp_id");
                    vendor_id = rs.getInt("vendor_id");
                }}

            System.out.println("insert into cpp(name,cvp_id,cpa_id,vendor_id,organization_id) values('"+cpp_name+"'," + cvp_id + ","+cpa_id +"," +vendor_id +","+ organization_id +")");



            // create cpp with the given cvp_id and organization_id
            int new_cpp_id=(cpa_id==-1)? this.dbHandler.dbUpdate("insert into cpp(name,cvp_id,vendor_id,organization_id) values('"+cpp_name+"'," + cvp_id + ","+vendor_id +","+ organization_id +");"):
                    this.dbHandler.dbUpdate("insert into cpp(name,cvp_id,cpa_id,vendor_id,organization_id) values('"+cpp_name+"',"+ cvp_id + "," + cpa_id + "," +vendor_id +","+ organization_id +");");

            System.out.println("new_cpp_id"+new_cpp_id);

            String putSchemaIDToDataannotationsTuple="";
            for (DataAnnotations da: daList){

                int newda_id= this.dbHandler.dbUpdate("INSERT INTO dataannotations (xslt_annotations , mapping, selections,xbrl) SELECT xslt_annotations , mapping, selections,xbrl FROM dataannotations  WHERE dataAnnotations_id ="+da.getDataAnnotations_id());

                System.out.println("da.getSchema_id()"+ da.getSchema_id());
                putSchemaIDToDataannotationsTuple = (da.getSchema_id()!=0) ?  " schema_id="+da.getSchema_id()+", " : "";

                if(cpa_id==-1){
                this.dbHandler.dbUpdate("update dataannotations set "+putSchemaIDToDataannotationsTuple+" cvp_id='" + cvp_id + "',cpp_id=" + new_cpp_id + " where dataAnnotations_id=" + newda_id);

                } else{
                this.dbHandler.dbUpdate("update dataannotations set "+putSchemaIDToDataannotationsTuple+" cvp_id='" + cvp_id + "',cpp_id=" + new_cpp_id +", cpa_id=" + cpa_id + " where dataAnnotations_id=" + newda_id);

                }
             }

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public void insertNewUrlBinding(String service_id,String url_binding) {

        try {


            this.dbHandler.dbOpen();
            // create cpp with the given cvp_id and organization_id
            int new_cpp_id=  this.dbHandler.dbUpdate("insert into installedbinding(service_id,url_binding) values("+service_id+",'"+url_binding +"');");

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }


    public void deleteUrlBindingID(String url_binding_id) {
       ResultSet rs;

        try {
            this.dbHandler.dbOpen();
            // create cpp with the given cvp_id and organization_id
            int urlbinding_id_deleted=  this.dbHandler.dbUpdate("DELETE FROM installedbinding WHERE installedbinding_id="+url_binding_id);


            rs= this.dbHandler.dbQuery("SELECT cpa.cpa_id  as cpa_id FROM cpa WHERE url_binding_first="+url_binding_id + " OR  url_binding_second="+ url_binding_id);

            if (rs != null) {
                while (rs.next()) {
                    this.dbHandler.dbUpdate("update cpa set disabled=true where cpa_id=" + rs.getInt("cpa_id"));
                    System.out.print("olele olele update cpa set disabled=true where cpa_id=" + rs.getInt("cpa_id"));
                }}

            this.dbHandler.dbClose();

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }





    public void deleteCPP(int cpp_id) {

        try {

            this.dbHandler.dbOpen();

            this.dbHandler.dbUpdate("DELETE FROM dataannotations WHERE cpp_id="+cpp_id);

            this.dbHandler.dbUpdate("DELETE FROM cpp WHERE cpp_id="+cpp_id);

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }


    public LinkedList<Service> getWebServicesInstallations(String software_id, String organization_name)
    {
        ResultSet rs;
        LinkedList<Service> ServList = new LinkedList<Service>();

        try{
            int organization_id = this.getUserID(organization_name);
            String software_id_restriction=(!software_id.toString().equalsIgnoreCase("-1"))?" and ws.software_id="+software_id:"";
            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("select ws.software_id as software_id, sc.name  as software_name ,sc.version as software_version ,ws.service_id as service_id, ws.name as service_name, ws.version as service_version,ib.installedbinding_id,ib.url_binding " +
                    " from web_service ws, softwarecomponent sc, installedbinding ib where ws.software_id=sc.software_id and  ws.service_id=ib.service_id and  ws.exposed=1  and ws.wsdl IS NOT NULL "+ software_id_restriction +"  order by sc.software_id, ws.service_id ");


            if (rs != null) {
                while (rs.next()) {

                    ServList.add(new Service(rs.getInt("software_id"), rs.getString("software_name"),rs.getString("software_version") ,rs.getInt("service_id"), rs.getString("service_name"),rs.getString("service_version"),rs.getInt("installedbinding_id"), rs.getString("url_binding")));
                }}

            this.dbHandler.dbClose();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
        return ServList;
    }

    public LinkedList<Service> getWebServicesInstallationsByWS(String service_id)
    {
        ResultSet rs;
        LinkedList<Service> ServList = new LinkedList<Service>();

        try{
           this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("select ws.software_id as software_id ,ws.service_id as service_id, ws.name as service_name, ws.version as service_version,ib.installedbinding_id,ib.url_binding from web_service ws,installedbinding ib where  ws.service_id=ib.service_id and  ws.exposed=1  and ws.wsdl IS NOT NULL and ws.service_id="+service_id);

            if (rs != null) {
                while (rs.next()) {

                    ServList.add(new Service(rs.getInt("software_id"), "","" ,rs.getInt("service_id"), rs.getString("service_name"),rs.getString("service_version"),rs.getInt("installedbinding_id"), rs.getString("url_binding")));
                }}

            this.dbHandler.dbClose();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
        return ServList;
    }

    public LinkedList<Service> getCPPByWS(String service_id,String organization_name)
    {
        ResultSet rs;
        LinkedList<Service> ServList = new LinkedList<Service>();

        try{
            int organization_id = this.getUserID(organization_name);
            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("select ws.software_id as software_id,ws.service_id as service_id, ws.name as service_name, ws.version as service_version, cpp.name as cpp_name,cpp.cpp_id as cpp_id, ws.exposed as exposed, ws.wsdl as wsdl, ws.namespace as namespace \n" +
                    "from web_service ws,cvp cvp,cpp cpp where  ws.service_id=cvp.service_id and cpp.cvp_id=cvp.cvp_id and ws.exposed=1 and cpp.organization_id="+organization_id+"  and ws.wsdl IS NOT NULL  and ws.service_id="+service_id);
            if (rs != null) {
                while (rs.next()) {

                    ServList.add(new Service(rs.getInt("software_id"),"","",rs.getInt("service_id"), rs.getString("service_name"),rs.getString("cpp_name"),rs.getInt("cpp_id"),rs.getString("service_version"), rs.getString("wsdl"), rs.getString("namespace"), rs.getBoolean("exposed")));
                }}

            this.dbHandler.dbClose();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
        return ServList;
    }


    public void desactivateBridge(int cpp_id) {
        ResultSet rs;
        try {

            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("select * from cpa where cpa.cpp_id_first="+cpp_id+" OR cpa.cpp_id_second="+cpp_id);

            if (rs != null) {
                while (rs.next()) {
                    this.dbHandler.dbUpdate("update cpa set disabled=true where cpa_id=" + rs.getInt("cpa_id"));
                    System.out.print("olele olele update cpa set disabled=true where cpa_id=" + rs.getInt("cpa_id"));
                }}


            this.dbHandler.dbUpdate("DELETE FROM dataannotations WHERE cpp_id="+cpp_id);

            this.dbHandler.dbUpdate("DELETE FROM cpp WHERE cpp_id="+cpp_id);

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }



    /*

    public LinkedList<Service> getWebServicesInstallations(String software_id, String organization_name)
    {
        ResultSet rs;
        LinkedList<Service> ServList = new LinkedList<Service>();

        try{
            int organization_id = this.getUserID(organization_name);
            String software_id_restriction=(!software_id.toString().equalsIgnoreCase("-1"))?" and ws.software_id="+software_id:"";
            this.dbHandler.dbOpen();

            rs = this.dbHandler.dbQuery("select ws.software_id as software_id, sc.name  as software_name ,sc.version as software_version ,ws.service_id as service_id, ws.name as service_name, ws.version as service_version, cpp.name as cpp_name,cpp.cpp_id as cpp_id, ws.exposed as exposed, ws.wsdl as wsdl, ws.namespace as namespace\n" +
                    "from web_service ws,cvp cvp,cpp cpp, softwarecomponent sc where ws.software_id=sc.software_id and  ws.service_id=cvp.service_id and cpp.cvp_id=cvp.cvp_id and ws.exposed=1 and cpp.organization_id="+organization_id+"  and ws.wsdl IS NOT NULL  "+ software_id_restriction +"  order by sc.software_id, ws.service_id ");


            if (rs != null) {
                while (rs.next()) {

                    ServList.add(new Service(rs.getInt("software_id"), rs.getString("software_name"),rs.getString("software_version") ,rs.getInt("service_id"), rs.getString("service_name"),rs.getString("cpp_name"),rs.getInt("cpp_id"),rs.getString("service_version"), rs.getString("wsdl"), rs.getString("namespace"), rs.getBoolean("exposed")));
                }}

            this.dbHandler.dbClose();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
        return ServList;
    } */
    
}
