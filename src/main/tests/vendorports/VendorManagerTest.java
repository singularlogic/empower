package vendorports;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import dataaccesslayer.dbConnector;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;

import static net.sf.ezmorph.test.ArrayAssertions.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: eleni
 * Date: 11/22/12
 * Time: 5:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class VendorManagerTest {

    private static String xml_rep_path = "/home/eleni/Documents/ubi/empower/empower-deliverable-september/empowerTest/";


    /*
    * Test the following scenario:
    * 1.Register two web services
    * 2.Annotate them
    * 3.Create a Bridge between them
    * 4.Delete a web service that participates at the bridge.
    * 5.Check that the bridge is disabled.
    * */
    @Test
    public void testWebServicesLifeCycle() throws Exception {
        // assertEquals(1, 2);

        //1.Register two web services
        //int first_service = testRegisterService(75,"ResellerImplService","http://ws.eleni.com/","/home/eleni/Documents/ubi/empower/empower-deliverable-september/empowerTest/wsdl/ResellerImpl.wsdl");
        //int second_service = testRegisterService(75,"ERPImplService","http://wserp.eleni.com/","/home/eleni/Documents/ubi/empower/empower-deliverable-september/empowerTest/wsdl/ERPImplService.wsdl");

        //int second_service = testRegisterService(77,"TestServicefail","http://ws.demo/","/home/eleni/Desktop/Table114122012153701ORIGINAL.wsdl");
       // int second_service = testRegisterService(77,"UserService"," http://web.feedback.roo.dw.com/","/home/eleni/Desktop/SpringRοοWS.wsdl");


        //2.Register two web services

    }
    /*

    public int testRegisterService(int software_id, String service_name ,String service_namespace, String file_location) throws Exception {

        ResultSet rs;


        String serviceFilename = new String(this.xml_rep_path + "/wsdl/" + software_id + "_" + service_name + "_Test.wsdl");

        File source=new File(file_location);
        File destination=new File(serviceFilename);

        this.copyFile(source, destination);



        VendorDBConnector vendorDBConnector = new VendorDBConnector();
        System.out.println("3 mundo cruel");
        //int service_id = vendorDBConnector.insertServiceInfo(software_id, service_name ,serviceFilename, xml_rep_path,service_namespace);

        dbConnector dbHandlerTest=new dbConnector();

        dbHandlerTest.dbOpen();

       // rs = dbHandlerTest.dbQuery("SELECT * FROM web_service WHERE service_id="+service_id);

        if (rs.next()) {

            System.out.println("4 mundo cruel");

            assertEquals(service_name, rs.getString("name"));
            assertEquals(service_namespace, rs.getString("namespace"));
            assertEquals(serviceFilename, rs.getString("wsdl"));

        } else Assert.fail();

        //return  service_id;


    }

      */

    @Test
    public void testGetServletInfo() throws Exception {
        assertEquals(1, 3);
    }


    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }



    @Test
    public void htmlAddSofComp() throws Exception {
        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3_6);
        final HtmlPage page = webClient.getPage("http://127.0.0.1:8080/empower/signin.jsp");

    }


}
