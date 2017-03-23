package uk.nhs.jorvik.E_RS.Processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class Logoff implements Processor {
	
	
	@Override
	public void process(Exchange exchange) throws Exception {
		exchange.getIn().setHeader(Exchange.HTTP_PATH,"ProfessionalSession/"+exchange.getIn().getHeader("HTTP_X_SESSION_KEY"));
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "DELETE");
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/json");
		exchange.getIn().setHeader("FileRef","10-ProfessionalSessionDelete.json");
		exchange.getIn().setBody("");
	}

}
