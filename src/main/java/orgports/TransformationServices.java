package orgports;


import net.sf.json.JSONObject;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public class TransformationServices {

    @WebMethod
    public String DoBridgeWS (@WebParam(name="cpa_id") int cpa_id,@WebParam(name="xml_source") String  xml_source) {

        JSONObject JsonToReturn =null;

        try {
            DoBridgeForWS doBridgeForWS = new DoBridgeForWS();
            JsonToReturn = doBridgeForWS.doBridgingService(cpa_id,xml_source);

            if (cpa_id == 0 ) return null;


        }catch (Exception e){
            System.out.println(e.toString());
        }
        return JsonToReturn.toString();

    }


}



/*
-------------------------------------------
Example of SOAP REQUEST
-------------------------------------------
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:org="http://orgports/">
   <soapenv:Header/>
   <soapenv:Body>
      <org:DoBridgeWS>
         <cpa_id>114</cpa_id>
         <xml_source><![CDATA[<updateInvoice><invoice><code>3879387</code><description>holala</description><number>2323</number><seriesCode>798797</seriesCode><warehouseCode>9977</warehouseCode><id>8</id></invoice></updateInvoice>]]></xml_source>
      </org:DoBridgeWS>
   </soapenv:Body>
</soapenv:Envelope>
* */
