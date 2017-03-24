package uk.nhs.jorvik.ers.processor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WorkFlowReferralSplit implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		
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
        String jsonString = "";
        JSONObject jsonObject = new JSONObject(buffer.toString());
    	try
    	{
	        JSONArray jsonArray = jsonObject.getJSONArray("entry");
	        
	        for (int f= 0; f<jsonArray.length(); f++)
	        {
	        	JSONObject item = jsonArray.getJSONObject(f);
	        	JSONObject ref = item.getJSONObject("item");
	        	jsonString = jsonString + ref.getString("reference") +",";
	        }
    	}
        catch (JSONException ex)
        {
        	// Do nothing the entry hasn't been found. This should mean no referrals on the worklist
        }
        exchange.getIn().setBody(jsonString);
	}

}
