/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orgports;

import com.google.gson.Gson;
import dataaccesslayer.CPA;
import dataaccesslayer.Schema;
import dataaccesslayer.SoftwareComponent;
import dataaccesslayer.dbConnector;
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

            rs = this.dbHandler.dbQuery("select ws.software_id as software_id,ws.service_id as service_id,ws.name as ws_name,o.operation_id as operation_id,o.name as operation_name,o.taxonomy_id as op_taxonomy_id,os.inputoutput as inputoutput, s.schema_id as schema_id, s.location as schema_location, s.name as schema_name, cvp.cvp_id as cvp_id , da.selections as selections  from operation o LEFT JOIN web_service ws on o.service_id=ws.service_id LEFT JOIN operation_schema os  on o.operation_id = os.operation_id "
                    + "LEFT JOIN schema_xsd  s  on os.schema_id = s.schema_id LEFT JOIN dataannotations da on da.schema_id=s.schema_id LEFT JOIN cvp cvp on cvp.cvp_id = da.cvp_id where ws.software_id="+software_id+" and os.inputoutput='input'  and cvp.cvp_id IS NOT NULL order by ws.service_id ");

            if (rs != null) {
                while (rs.next()) {
                    XSDList.add(new Schema(rs.getInt("service_id"), rs.getString("ws_name"),rs.getInt("operation_id"),rs.getString("operation_name"),rs.getString("op_taxonomy_id"),rs.getString("inputoutput"),rs.getInt("schema_id"),rs.getString("schema_location"), rs.getString("schema_name"), rs.getInt("cvp_id")));
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

           
            rs = this.dbHandler.dbQuery("select ws.software_id as software_id,ws.service_id as service_id,ws.name as ws_name,o.operation_id as operation_id,o.name as operation_name,o.taxonomy_id as op_taxonomy_id,os.inputoutput as inputoutput, s.schema_id as schema_id, s.location as schema_location, s.name as schema_name , cvp.cvp_id as cvp_id, da.selections"
                    + " from operation o LEFT JOIN web_service ws on o.service_id=ws.service_id "
                    + " LEFT JOIN operation_schema os  on o.operation_id =os.operation_id  "
                    + " LEFT JOIN schema_xsd  s  on os.schema_id = s.schema_id "
                    + " LEFT JOIN dataannotations da on da.schema_id = s.schema_id "
                    + " LEFT JOIN cvp cvp on cvp.cvp_id = da.cvp_id"
                    + " where  o.taxonomy_id = '"+taxonomy_id+"' and os.inputoutput='output' and cvp.cvp_id IS NOT NULL order by ws.service_id ");
            
            
            
            if (rs != null) {
                while (rs.next()) {
                    XSDList.add(new Schema(rs.getInt("service_id"), rs.getString("ws_name"),rs.getInt("operation_id"),rs.getString("operation_name"),rs.getString("op_taxonomy_id"),rs.getString("inputoutput"),rs.getInt("schema_id"),rs.getString("schema_location"), rs.getString("schema_name"),rs.getInt("cvp_id")));
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
        
        
   public Map<String,Integer> insertBridging(int cvp_source , int cvp_target , String organization , String json) {
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
                data.put("existing",rs1.getInt("cpa_id"));
                 this.dbHandler.dbUpdate("update cpa set cpa_info='"+ json +"' where cpa_id="+rs1.getInt("cpa_id"));
            } 
            else{
                cpa_id = this.dbHandler.dbUpdate("insert into cpa(cpp_id_first,cpp_id_second,cpa_info) values('"
                    + cvp_source + "','" + cvp_target + "','"+json+"')");
            
                organization_cpa = this.dbHandler.dbUpdate("insert into organization_cpa(organization_id,cpa_id) values("+organization_id+","+cpa_id+")");
                data.put("new_cpa_id", cpa_id);
            }
            rs1.close();
            
            this.dbHandler.dbClose();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return data;
    }
   
   
    public String retrieveXLST(int cpp_id,String inputoutput, String cpa_info)
    {
        String xsltCode = null;
        ResultSet rs;
        
	try{
            
            JSONObject o = new JSONObject();
            o = (JSONObject) JSONSerializer.toJSON(cpa_info); 
            JSONObject info= (inputoutput=="input")? (JSONObject) JSONSerializer.toJSON(o.get("cppinfo_first")):(JSONObject) JSONSerializer.toJSON(o.get("cppinfo_second")); 
            
            System.out.println("inputoutput:"+inputoutput+" cpp_id: "+cpp_id+" schmema_id: "+info.get("schema_id"));
            
            this.dbHandler.dbOpen();
            //rs = this.dbHandler.dbQuery("select da.xslt_annotations as xslt_annotations from dataannotations da, cvp cvp, cpp cpp  where cpp.cpp_id =" +cpp_id+ " and cpp.cvp_id = cvp.cvp_id and da.cvp_id=cvp.cvp_id ");
            rs = this.dbHandler.dbQuery("select da.xslt_annotations as xslt_annotations "
                    + " from dataannotations da, cvp cvp, cpp cpp ,operation_schema os  "
                    + " where cpp.cvp_id = cvp.cvp_id and da.cvp_id=cvp.cvp_id  and os.schema_id = da.schema_id "
                    + " and os.inputoutput='"+inputoutput+"' and cpp.cpp_id ="+cpp_id+"  and da.schema_id="+info.get("schema_id") +"  and da.selections LIKE '%"+info.get("schema_complexType")+"%'  and os.operation_id="+info.get("operation_id"));
            if(rs.next())
                xsltCode = new String(rs.getString("xslt_annotations"));

            this.dbHandler.dbClose();
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
            rs = this.dbHandler.dbQuery("select cpa.cpp_id_first as cpp_id_first,cpa.cpp_id_second as cpp_id_second, cpa.cpa_info as cpa_info from cpa where cpa.cpa_id=" + cpa_id );
            
            if(rs.next())
               cpa = new CPA(cpa_id,rs.getInt("cpp_id_first"),rs.getInt("cpp_id_second"),rs.getString("cpa_info"));

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
            
            rs = this.dbHandler.dbQuery("select cpa.cpa_id as cpa_id,cpa.cpp_id_first as cpp_id_first,cpa.cpp_id_second as cpp_id_second,cpa.cpa_info as cpa_info from organization_cpa ib, cpa cpa where ib.cpa_id=cpa.cpa_id and organization_id="+organization_id);

            if(rs != null)
            {
                while(rs.next())
                    cpaList.add(new CPA(rs.getInt("cpa_id"),rs.getInt("cpp_id_first"),rs.getInt("cpp_id_second"),rs.getString("cpa_info")));
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
   
    
}
