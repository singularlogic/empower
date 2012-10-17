/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maincontrol;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.junit.Assert;

/**
 * @author eleni
 */
public class MediationPortalCommunicator {

    @Test
    public void homePage() throws Exception {
        final WebClient webClient = new WebClient();

        final HtmlPage page = webClient.getPage("http://htmlunit.sourceforge.net");

        org.junit.Assert.assertEquals("HtmlUnit - Welcome to HtmlUnit", page.getTitleText());

        System.out.println("page.getTitleText(): " + page.getTitleText());

        final String pageAsXml = page.asXml();
        org.junit.Assert.assertTrue(pageAsXml.contains("<body class=\"composite\">"));


        final String pageAsText = page.asText();
        org.junit.Assert.assertTrue(pageAsText.contains("Support for the HTTP and HTTPS protocols"));

        webClient.closeAllWindows();
    }

    public void insertSchemaToMediationPortal(String schema_name, String schema_location) throws IOException {

        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
        final HtmlPage page = webClient.getPage("http://54.247.114.191/net.modelbased.mediation.gui-0.0.1-SNAPSHOT/settings.html");

        System.out.println("page.getTitleText(): " + page.getTitleText());

        page.executeJavaScript("$(function() {"
                + "   var $radios = $('input:radio[name=isActive]');"
                + "   $radios.filter('[value=SINTEF-DEMO]').attr('checked', true);"
                + "  $radios.filter('[value=SINTEF-DEMO]').click();"
                + "    });");

        //javascript : " var hola = getAllBackEnds(); $('.span6').append('<div id=skata><%=hola%></div>');"
        //HtmlElement laal = page.getElementById("skata");
        //System.out.println(laal.asText());

        System.out.println(page.asText());



        HtmlPage page_repositories = webClient.getPage("http://54.247.114.191/net.modelbased.mediation.gui-0.0.1-SNAPSHOT/repositories.html");

        String dataOFfile = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>   <xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">  <xs:complexType name=\"Absence\">  <xs:sequence>  <xs:element name=\"SeqNo\" nillable=\"true\" minOccurs=\"0\">  <xs:simpleType>  <xs:restriction base=\"xs:int\"> <xs:minInclusive value=\"-2147483648\" />  <xs:maxInclusive value=\"2147483647\" />  </xs:restriction></xs:simpleType> </xs:element> </xs:sequence> </xs:complexType> </xs:schema>";


        page_repositories.executeJavaScript("console = {log: function() {}, debug: function() {}, warn: function() {} };" //  + " $('#import-button').removeAttr('disabled');"
                // + "$('#import-button').unbind('onchange');"
                // + "$(function() {"
                // + " $('#model-content')[0].value = "+dataOFfile+";"
                //+ "  });"
                );

        System.out.println("page_repositories Title: " + page_repositories.getTitleText());


        // Get the form that we are dealing with and within that form, 
        // find the submit button and the field that we want to change.
        //final HtmlForm form = page_repositories.getFormByName("import-form");

        HtmlElement form = page_repositories.getElementById("import-form");

        final HtmlButton button = (HtmlButton) page_repositories.getElementById("import-button");

        final HtmlTextInput textField = (HtmlTextInput) page_repositories.getElementById("model-name");
        // Change the value of the text field
        textField.setValueAttribute("prueba");

        //final HtmlTextArea md_textField = form.getElementById("model-description");
        //md_textField.setValueAttribute("prueba");

        final HtmlFileInput mf_textField = (HtmlFileInput) page_repositories.getElementById("model-file");
        // Change the value of the text field

        //File file = new File("/home/eleni/Desktop/prueba.xsd");

        mf_textField.setContentType("application/xml");
        //byte[] byteOFfile = dataOFfile.getBytes();
        //mf_textField.setData(byteOFfile);
        //mf_textField.setValueAttribute(dataOFfile);
        mf_textField.setValueAttribute("/home/eleni/Desktop/prueba.xsd");

        page_repositories.executeJavaScript(" $('#model-file').unbind('onchange'); "
                + "  $('#model-file').unbind('click');"
                //+ "var f = new File(\"/home/eleni/Desktop/prueba.xsd\"); "
                //+ " prefetch(f);" // + "$(function() {"
                + " $('#import-button').removeAttr('disabled');"
                + " $('#model-content')[0].value = " + dataOFfile + ";" // + "  });"
                );




        final HtmlRadioButtonInput mt_textField = (HtmlRadioButtonInput) page_repositories.getElementById("model-type");
        // Change the value of the text field
        mt_textField.setValueAttribute("XSD");

        // Now submit the form by clicking the button and get back the second page.
        final HtmlPage page2 = button.click();



        //firstPage.<HtmlElement>getHtmlElementById("mySubmit").click();


        webClient.closeAllWindows();

        //org.junit.Assert.assertEquals("Mediation Portal - SINTEF ICT", page.getTitleText());

    }

    public void insertSchemaToMediationPortal1(String schema_name, String schema_location) throws IOException {

        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
        final HtmlPage page = webClient.getPage("http://54.247.114.191/net.modelbased.mediation.gui-0.0.1-SNAPSHOT/settings.html");

        System.out.println("page.getTitleText(): " + page.getTitleText());

        page.executeJavaScript("$(function() {"
                + "   var $radios = $('input:radio[name=isActive]');"
                + "   $radios.filter('[value=SINTEF-DEMO]').attr('checked', true);"
                + "  $radios.filter('[value=SINTEF-DEMO]').click();"
                + "    });");

        System.out.println(page.asText());

        HtmlPage page_repositories = webClient.getPage("http://54.247.114.191/net.modelbased.mediation.gui-0.0.1-SNAPSHOT/repositories.html");

        String dataOFfile = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>   <xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">  <xs:complexType name=\"Absence\">  <xs:sequence>  <xs:element name=\"SeqNo\" nillable=\"true\" minOccurs=\"0\">  <xs:simpleType>  <xs:restriction base=\"xs:int\"> <xs:minInclusive value=\"-2147483648\" />  <xs:maxInclusive value=\"2147483647\" />  </xs:restriction></xs:simpleType> </xs:element> </xs:sequence> </xs:complexType> </xs:schema>";


        page_repositories.executeJavaScript("console = {log: function() {}, debug: function() {}, warn: function() {} };"
                + "$(function() {"
                + " var name='prueba'; "
                + " var description ='prueba'; "
                + " var type ='XSD'; "
                + " var content =" + dataOFfile + ";"
                + " importModel(name, description, type, content, importModelSuccessful, function (jqXHR, textStatus) {  }	);"
                + "  });");

        System.out.println("page_repositories Title: " + page_repositories.getTitleText());

        webClient.closeAllWindows();

        //

    }

    public void doPostToMediator() throws MalformedURLException, IOException {

        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
        final HtmlPage page = webClient.getPage("http://54.247.114.191/net.modelbased.mediation.gui-0.0.1-SNAPSHOT/settings.html");

        System.out.println("page.getTitleText(): " + page.getTitleText());

        page.executeJavaScript("$(function() {"
                + "   var $radios = $('input:radio[name=isActive]');"
                + "   $radios.filter('[value=SINTEF-DEMO]').attr('checked', true);"
                + "  $radios.filter('[value=SINTEF-DEMO]').click();"
                + "    });");

        System.out.println(page.asText());
        
        
        
        String dataOFfile = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>   <xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">  <xs:complexType name=\"Absence\">  <xs:sequence>  <xs:element name=\"SeqNo\" nillable=\"true\" minOccurs=\"0\">  <xs:simpleType>  <xs:restriction base=\"xs:int\"> <xs:minInclusive value=\"-2147483648\" />  <xs:maxInclusive value=\"2147483647\" />  </xs:restriction></xs:simpleType> </xs:element> </xs:sequence> </xs:complexType> </xs:schema>";

       System.out.println("1");
              
       String url = "http://54.247.114.191/sensapp/importer";  
 
        java.net.URL urlSite = new URL(url);  
        com.gargoylesoftware.htmlunit.WebRequest request = new com.gargoylesoftware.htmlunit.WebRequest(urlSite);  
         System.out.println("2");
        request.setHttpMethod(HttpMethod.POST);   
        System.out.println("3");
        //request.setEncodingType(FormEncodingType.MULTIPART);  
       
       List<String> strings = new ArrayList<String>();  
        
       ArrayList<NameValuePair> request_info = new ArrayList<NameValuePair>();

        System.out.println("4");
        
        
       JSONObject request_json = new JSONObject();
       
       request_json.put("content", dataOFfile);
        request_json.put("description","lala294");
         request_json.put("format","XSD");
          request_json.put("modelId","lala294");
          
    request_info.add(new NameValuePair("JSON","{\"modelId\":\"qqq\",\"description\":\"qqq\",\"format\":\"XSD\",\"content\":\"<?xml version=\"1.0\" encoding=\"UTF-8\" ?> \n <xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">\n <xs:complexType name=\"Absence\">\n <xs:sequence>\n <xs:element name=\"SeqNo\" nillable=\"true\" minOccurs=\"0\">\n <xs:simpleType>\n <xs:restriction base=\"xs:int\">\n<xs:minInclusive value=\"-2147483648\" /> \n<xs:maxInclusive value=\"2147483647\" /> \n</xs:restriction></xs:simpleType>\n</xs:element>\n <xs:element name=\"LastUpdate\" nillable=\"true\" minOccurs=\"0\">\n <xs:simpleType>\n <xs:restriction base=\"xs:dateTime\">\n<xs:pattern value=\"\\p{Nd}{4}-\\p{Nd}{2}-\\p{Nd}{2}T\\p{Nd}{2}:\\p{Nd}{2}:\\p{Nd}{2}(\\.\\p{Nd}{1,3})?\" /> \n<xs:minInclusive value=\"1753-01-01T00:00:00.000\" /> \n<xs:maxInclusive value=\"9999-12-31T23:59:59.999\" /> \n</xs:restriction>\n</xs:simpleType>\n</xs:element>\n <xs:element name=\"StopDate\" nillable=\"true\" minOccurs=\"0\">\n <xs:simpleType>\n <xs:restriction base=\"xs:dateTime\">\n<xs:pattern value=\"\\p{Nd}{4}-\\p{Nd}{2}-\\p{Nd}{2}T\\p{Nd}{2}:\\p{Nd}{2}:\\p{Nd}{2}(\\.\\p{Nd}{1,3})?\" /> \n<xs:minInclusive value=\"1753-01-01T00:00:00.000\" /> \n<xs:maxInclusive value=\"9999-12-31T23:59:59.999\" /> \n</xs:restriction>\n</xs:simpleType>\n</xs:element>\n <xs:element name=\"StartDate\" nillable=\"true\" minOccurs=\"0\">\n <xs:simpleType>\n <xs:restriction base=\"xs:dateTime\">\n<xs:pattern value=\"\\p{Nd}{4}-\\p{Nd}{2}-\\p{Nd}{2}T\\p{Nd}{2}:\\p{Nd}{2}:\\p{Nd}{2}(\\.\\p{Nd}{1,3})?\" /> \n<xs:minInclusive value=\"1753-01-01T00:00:00.000\" /> \n<xs:maxInclusive value=\"9999-12-31T23:59:59.999\" /> \n</xs:restriction>\n</xs:simpleType>\n</xs:element>\n <xs:element name=\"Quantity\" nillable=\"true\" minOccurs=\"0\">\n <xs:simpleType>\n <xs:restriction base=\"xs:double\">\n<xs:minExclusive value=\"-1.7976931348623157e+308\" /> \n<xs:maxExclusive value=\"1.7976931348623157e+308\" /> \n</xs:restriction>\n</xs:simpleType>\n</xs:element>\n <xs:element name=\"AbsenceTypeNo\" nillable=\"true\" minOccurs=\"0\">\n <xs:simpleType>\n <xs:restriction base=\"xs:int\">\n<xs:minInclusive value=\"-2147483648\" /> \n<xs:maxInclusive value=\"2147483647\" /> \n</xs:restriction>\n</xs:simpleType>\n</xs:element>\n <xs:element name=\"EmployeeNo\" nillable=\"true\" minOccurs=\"0\">\n <xs:simpleType>\n <xs:restriction base=\"xs:int\">\n<xs:minInclusive value=\"-2147483648\" /> \n<xs:maxInclusive value=\"2147483647\" /> \n</xs:restriction>\n</xs:simpleType>\n</xs:element>\n <xs:element name=\"LastUpdatedBy\" nillable=\"true\" minOccurs=\"0\">\n <xs:simpleType>\n <xs:restriction base=\"xs:int\">\n<xs:minInclusive value=\"-2147483648\" /> \n<xs:maxInclusive value=\"2147483647\" /> \n</xs:restriction>\n</xs:simpleType>\n</xs:element>\n <xs:element name=\"Created\" nillable=\"true\" minOccurs=\"0\">\n <xs:simpleType>\n <xs:restriction base=\"xs:dateTime\">\n<xs:pattern value=\"\\p{Nd}{4}-\\p{Nd}{2}-\\p{Nd}{2}T\\p{Nd}{2}:\\p{Nd}{2}:\\p{Nd}{2}(\\.\\p{Nd}{1,3})?\" /> \n<xs:minInclusive value=\"1753-01-01T00:00:00.000\" /> \n<xs:maxInclusive value=\"9999-12-31T23:59:59.999\" /> \n</xs:restriction>\n</xs:simpleType>\n</xs:element>\n <xs:element name=\"CreatedBy\" nillable=\"true\" minOccurs=\"0\">\n <xs:simpleType>\n <xs:restriction base=\"xs:int\">\n<xs:minInclusive value=\"-2147483648\" /> \n<xs:maxInclusive value=\"2147483647\" /> \n</xs:restriction>\n</xs:simpleType>\n</xs:element>\n</xs:sequence>\n</xs:complexType>\n</xs:schema>\n\"}"));
    
    //System.out.println("Source"+request_json.toString());
    
      /*
       request_info.add(new NameValuePair("content", dataOFfile));
       request_info.add(new NameValuePair("description","lala294"));
       request_info.add(new NameValuePair("format","XSD"));
       request_info.add(new NameValuePair("modelId","lala294"));
       * */
       
      
        System.out.println("5");
       
       //response.getContentType().equals("application/json")
      
        request.setRequestParameters(request_info);
        System.out.println("6");
        
        Map<String, String>  headers =  request.getAdditionalHeaders() ;	
        
        for (Map.Entry<String, String> entry: headers.entrySet()){
        System.out.println("Header   "+ entry.getKey() + "    " + entry.getValue());
        }
        System.out.println("7");
        Page page_importer = webClient.getPage(request);
        

        System.out.println("8");
        
        
         WebResponse response = page_importer.getWebResponse();
        if (response.getContentType().equals("application/json")) {
            String json = response.getContentAsString();
            //Map<String, String> map = new Gson().fromJson(json, new TypeToken<Map<String, String>>() {}.getType());
            //System.out.println(map.get("displayName")); // Benju
            System.out.println("lalala: " + json);
        }
         System.out.println("9");
         
          webClient.closeAllWindows();
    }
    
 

    public void getJSONMapping() throws IOException {

        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
        final HtmlPage page = webClient.getPage("http://54.247.114.191/net.modelbased.mediation.gui-0.0.1-SNAPSHOT/settings.html");

        System.out.println("page.getTitleText(): " + page.getTitleText());

        page.executeJavaScript("$(function() {"
                + "   var $radios = $('input:radio[name=isActive]');"
                + "   $radios.filter('[value=SINTEF-DEMO]').attr('checked', true);"
                + "  $radios.filter('[value=SINTEF-DEMO]').click();"
                + "    });");

        System.out.println(page.asText());

        Page page_mapping = webClient.getPage("http://54.247.114.191/sensapp/mediation/repositories/mappings/cb6668eb-3311-4987-849b-322d9c1adfc9/content/");


        WebResponse response = page_mapping.getWebResponse();
        if (response.getContentType().equals("application/json")) {
            String json = response.getContentAsString();
            //Map<String, String> map = new Gson().fromJson(json, new TypeToken<Map<String, String>>() {}.getType());
            //System.out.println(map.get("displayName")); // Benju
            System.out.println("Mapping: " + json);
        }

        webClient.closeAllWindows();

    }
    
    public void deleteschama() throws IOException{
        
        
          final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
        final HtmlPage page = webClient.getPage("http://54.247.114.191/net.modelbased.mediation.gui-0.0.1-SNAPSHOT/settings.html");

        System.out.println("page.getTitleText(): " + page.getTitleText());

        page.executeJavaScript("$(function() {"
                + "   var $radios = $('input:radio[name=isActive]');"
                + "   $radios.filter('[value=SINTEF-DEMO]').attr('checked', true);"
                + "  $radios.filter('[value=SINTEF-DEMO]').click();"
                + "    });");

        System.out.println(page.asText());
        
         HtmlPage page_remove = webClient.getPage("http://54.247.114.191/net.modelbased.mediation.gui-0.0.1-SNAPSHOT/repositories.html");
         
         page_remove.executeJavaScript("console = {log: function() {}, debug: function() {}, warn: function() {} }; "
                 + " deleteModelById('12','[tr.even]');");

        System.out.println("page_remove Title: " + page_remove.asText());

        webClient.closeAllWindows();
    
    
    
    }

    public static void main(String[] args) throws Exception {

        MediationPortalCommunicator mp = new MediationPortalCommunicator();
        //mp.getJSONMapping();
        mp.deleteschama();

    }
}
