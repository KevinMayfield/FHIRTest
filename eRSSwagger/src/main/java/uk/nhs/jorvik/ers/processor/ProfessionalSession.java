package uk.nhs.jorvik.ers.processor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ProfessionalSession implements Processor {

	private static final Logger log = LoggerFactory.getLogger(ProfessionalSession.class);
	 
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
	      
	      log.info("Json user.firstName" + jsonObject.getJSONObject("user").getString("firstName"));
	      
	      String orgIdentifier = null;
	      String orgName = null;
	      
	      JSONArray jsonArray = jsonObject.getJSONObject("user").getJSONArray("permissions");
	      for (int f= 0; f<jsonArray.length(); f++)
	      {
	        	JSONObject item = jsonArray.getJSONObject(f);
	        	
	        	if (item.getString("businessFunction").equals("SERVICE_PROVIDER_CLINICIAN"))
	        	{
	        		log.info("Json Permission orgIdentifier = "+ item.getString("orgIdentifier"));
	        		orgIdentifier = item.getString("orgIdentifier");
	        		log.info("Json Permission orgName = "+ item.getString("orgName"));
	        		orgName = item.getString("orgName");
	        	}
	      }
	      
	      // Need to fail if correct permission not found
	      
	      // Change the permission object, to request a permission
	      JSONObject permission = new JSONObject();
	      permission
	      		.put("businessFunction","SERVICE_PROVIDER_CLINICIAN")
	      		.put("orgIdentifier", orgIdentifier)
	      		.put("orgName",orgName);
	      
	      jsonObject.put("permission", permission);
	      
	
        
        // Now request the permission.
        
        exchange.getIn().setHeader("HTTP_X_SESSION_KEY", jsonObject.getString("id"));
		exchange.getIn().setHeader(Exchange.HTTP_PATH,"ProfessionalSession/"+jsonObject.getString("id"));
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "PUT");
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
		exchange.getIn().setHeader("FileRef","2-ProfessionalSessionPut.json");
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/json");
	
        exchange.getIn().setBody(jsonObject.toString());
		
	}

}
