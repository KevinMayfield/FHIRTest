package uk.nhs.jorvik.E_RS.Processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.core.env.Environment;


public class Logon implements Processor {

	
	
	private Environment env;
	
	String ssoTicket = null;
	
	public Logon(Environment env, String ssoTicket)
	{
	
		this.env = env;
		this.ssoTicket = ssoTicket;
	}
	@Override
	public void process(Exchange exchange) throws Exception {
		exchange.getIn().removeHeaders("*","_id|version|_revinclude|status|XAPI_ASID|XAPI_FQDN");
		exchange.getIn().setHeader(Exchange.HTTP_PATH,"ProfessionalSession");
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/json");
		exchange.getIn().setHeader("FileRef","1-ProfessionalSessionPost.json");
		exchange.getIn().setBody(getRequest());
	}

	private String getRequest()
	{
		String request = "{"
					+" \"typeInfo\": \"uk.nhs.ers.xapi.dto.v1.session.ProfessionalSession\" ,"
					//
					+" \"token\": \""+getToken()+"\" "
						+" }";
		return request;
	}

	
	private String getToken()
	{
		String token = null;
	
		if (env.getProperty("secure").equals("true"))
		{
			token = ssoTicket;
		}
		else
		{
			token = env.getProperty("token");
		}
		return token;
	}
}
