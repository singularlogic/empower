/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maincontrol;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import java.io.IOException;
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
        
    final WebClient webClient = new WebClient();

    // Get the first page
    final HtmlPage page1 = webClient.getPage("http://54.247.114.191/net.modelbased.mediation.gui-0.0.1-SNAPSHOT/settings.html");
    
    
    org.junit.Assert.assertEquals("Mediation Portal - SINTEF ICT", page1.getTitleText());

    System.out.println("page.getTitleText(): " + page1.getTitleText());
    
    /*
    // Get the form that we are dealing with and within that form, 
    // find the submit button and the field that we want to change.
    final HtmlForm form = page1.getFormByName("import-form");

    final HtmlSubmitInput button = form.getInputByName("import-button");
    
    final HtmlTextInput textField = form.getInputByName("model-name");
    // Change the value of the text field
    textField.setValueAttribute("prueba");
    
    final HtmlTextInput md_textField = form.getInputByName("model-description");
    // Change the value of the text field
    md_textField.setValueAttribute("prueba");
    
    final HtmlTextInput mf_textField = form.getInputByName("model-file");
    // Change the value of the text field
    mf_textField.setValueAttribute("/home/eleni/Documents/ubi/empower/empower-deliverable-september/empower/xsd/sourceDefault.xsd");

    final HtmlTextInput mt_textField = form.getInputByName("model-type");
    // Change the value of the text field
    mt_textField.setValueAttribute("XSD");

    // Now submit the form by clicking the button and get back the second page.
    final HtmlPage page2 = button.click();

    webClient.closeAllWindows();
    * /
    */
    }
    
   
}
