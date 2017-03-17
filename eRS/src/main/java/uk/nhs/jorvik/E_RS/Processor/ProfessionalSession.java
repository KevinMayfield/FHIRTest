package uk.nhs.jorvik.E_RS.Processor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.jorvik.fhirTest.E_RS.eRSTest;


public class ProfessionalSession implements Processor {

	private static final Logger log = LoggerFactory.getLogger(eRSTest.class);
	 
	@Override
	public void process(Exchange exchange) throws Exception {
		 // Need to reset the stream. 
	      InputStream is = (InputStream) exchange.getIn().getBody();
	      is.reset();
	      Reader reader = new InputStreamReader(new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class)));
	      	
	      char[] arr = new char[8 * 1024];
	      StringBuilder buffer = new StringBuilder();
	      int numCharsRead;
	      while ((numCharsRead = reader.read(arr, 0, arr.length)) != -1) {
	          buffer.append(arr, 0, numCharsRead);
	      }
	      reader.close();
	      String jsonString = buffer.toString();
	      
	      JSONObject jsonObject = new JSONObject(jsonString);
	     
	      log.debug("Id = "+jsonObject.getString("id"));
	     // assertNotNull("Session Id",jsonObject.getString("id") );
	      
	      // Change the permission object, to request a permission
	      JSONObject permission = new JSONObject();
	      permission
	      		.put("businessFunction","SERVICE_PROVIDER_CLINICIAN")
	      		.put("orgIdentifier", "R69")
	      		.put("orgName","BTL NHS TRUST R69");
	      jsonObject.put("permission", permission);
	      
	      log.debug("Amended string = "+jsonObject.toString());
        
        // Now request the permission.
        
        exchange.getIn().setHeader("HTTP_X_SESSION_KEY", jsonObject.getString("id"));
			exchange.getIn().setHeader(Exchange.HTTP_PATH,"ProfessionalSession/"+jsonObject.getString("id"));
			exchange.getIn().setHeader(Exchange.HTTP_METHOD, "PUT");
			exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
			exchange.getIn().setHeader("XAPI_ASID","999000000045");
			exchange.getIn().setHeader("XAPI_FQDN","all.bjss.com");
			exchange.getIn().setHeader("FileRef","2-ProfessionalSessionPut.json");
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/json");
		
        exchange.getIn().setBody(jsonObject.toString());
		
	}

}
