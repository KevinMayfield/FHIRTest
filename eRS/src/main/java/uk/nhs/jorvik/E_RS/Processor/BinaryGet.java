package uk.nhs.jorvik.E_RS.Processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class BinaryGet implements Processor {
	
	@Override
	public void process(Exchange exchange) throws Exception {
		 String payload = exchange.getIn().getBody(String.class);
	
	        String[] parts = payload.split("~");
	        exchange.getIn().setHeader(Exchange.HTTP_PATH, parts[0]);
	        exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
	        exchange.getIn().setHeader("FileRef","5-BinaryGet-"+parts[1].replace("/","-"));
	        if (parts.length < 3)
	        {
	        	exchange.getIn().setHeader(Exchange.HTTP_QUERY, "");
	        }
	        else
	        {
	        	exchange.getIn().setHeader(Exchange.HTTP_QUERY, parts[2]);
	        }
	        if (parts.length > 3)
	        {
	        	exchange.getIn().setHeader("BinaryId", parts[3]);
	        }
	        exchange.getIn().setHeader("Accept","*/*");
	        exchange.getIn().setBody("");
	}

}
