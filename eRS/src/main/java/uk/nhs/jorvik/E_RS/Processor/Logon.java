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
		exchange.getIn().removeHeaders("*","_id|version|_revinclude|status");
		exchange.getIn().setHeader(Exchange.HTTP_PATH,"ProfessionalSession");
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
		exchange.getIn().setHeader("XAPI_ASID",env.getProperty("ASID"));
		exchange.getIn().setHeader("XAPI_FQDN",env.getProperty("FQDN"));
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/json");
		exchange.getIn().setHeader("FileRef","1-ProfessionalSessionPost.json");
		//exchange.getIn().setHeader("token", );
		String request = getRequest(getToken());
						
		exchange.getIn().setBody(request);
	}

	private String getRequest(String token)
	{
		String request = "{"
					+" \"typeInfo\": \"uk.nhs.ers.xapi.dto.v1.session.ProfessionalSession\" ,"
					//
					+" \"token\": \""+token+"\" "
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
