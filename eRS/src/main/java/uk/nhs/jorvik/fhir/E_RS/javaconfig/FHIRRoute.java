package uk.nhs.jorvik.fhir.E_RS.javaconfig;


import java.io.InputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.gemplus.gemauth.api.GATicket;

import ca.uhn.fhir.context.FhirContext;
import uk.nhs.jorvik.E_RS.Processor.SupportingInformationAggregation;
import uk.nhs.jorvik.E_RS.Processor.BinaryGet;
import uk.nhs.jorvik.E_RS.Processor.ReferralRequestSupportedInformationSplit;
import uk.nhs.jorvik.E_RS.Processor.ProfessionalSession;
import uk.nhs.jorvik.E_RS.Processor.WorkFlowGet;
import uk.nhs.jorvik.E_RS.Processor.WorkFlowReferralSplit;


@Component
@PropertySource("classpath:ERS.properties")
public class FHIRRoute extends RouteBuilder {
	
	@Autowired
	protected Environment env;
	
	private static final FhirContext ctxhapiHL7Fhir = FhirContext.forDstu2();
	
	String ssoTicket = null;
	
	@Override
	public void configure() throws Exception {
		
		// Simple connection to smartcard to ensure the user has been authenticated
		
		GATicket gaticket = null;
		
		
	    try {
	      log.info("TEST - MAIN: Starting.");
	      gaticket = new GATicket();
	      gaticket.setIsDebug(true);
	      ssoTicket = gaticket.getTicket();
	      log.info(("TEST - MAIN: GATicket -> " + ssoTicket));
	      Long ssoTicketCode = gaticket.getLastError();
	    }
	    catch (Throwable err) {
	      err.printStackTrace();
	      if (null!=gaticket)
	    	  log.info(("error code: " + gaticket.getLastError()));
	    }
	    		
	    // So this url should work 
	    // http://localhost:8181/eRS/dstu2/ReferralRequest/000000090007
	    // http://localhost:8181/eRS/dstu2/ReferralRequest?_id=000000090007&_revinclude=*
	    
	    Endpoint httpsEndpoint = setupSSLConext(getContext());  
	   	    
	    restConfiguration()
			.component("jetty")
			.bindingMode(RestBindingMode.off)
			.contextPath("eRS")
			.port(8181)
			.dataFormatProperty("prettyPrint","true");
	
	    
		ProfessionalSession session = new ProfessionalSession();
		WorkFlowGet workFlowGet = new WorkFlowGet();
		WorkFlowReferralSplit workFlowReferralSplit = new WorkFlowReferralSplit();
		ReferralRequestSupportedInformationSplit binaryGetSplit = new ReferralRequestSupportedInformationSplit(ctxhapiHL7Fhir);
		BinaryGet binaryGet = new BinaryGet();
		
		SupportingInformationAggregation binaryAggregation = new SupportingInformationAggregation(ctxhapiHL7Fhir);
	
		 rest("/")
	    	.description("eRS")
	    	.get("/{version}/ReferralRequest/{_id}")
				.description("Interface to query the eRS")
				.param().type(RestParamType.path).name("_id").required(true).description("Resource Id e.g. ").dataType("string").endParam()
				.responseMessage().code(200).message("OK").endResponseMessage()
				.route()
					.routeId("eRS ReferralRequest Get")
					.to("direct:Logon")
					.process(new Processor() {
				        public void process(Exchange exchange) throws Exception {
				            exchange.getIn().setBody("ReferralRequest/"+exchange.getIn().getHeader("_id"));
				        }
					})
					.to("direct:ReferralRequestGet")
				.endRest()
			 .get("/{version}/ReferralRequest")
				.description("Interface to query the eRS")
				.param().type(RestParamType.query).name("_id").required(true).description("Resource Id e.g. ").dataType("string").endParam()
				.param().type(RestParamType.query).name("_revinclude").required(false).description("Include referenced resources ").dataType("string").endParam()
				.param().type(RestParamType.query).name("status").required(false).description("Defaults to request and is hard coded  ").dataType("string").endParam()
				.responseMessage().code(200).message("OK").endResponseMessage()
				.route()
					.routeId("eRS ReferralRequest Search")
					.to("direct:Logon")
					.process(new Processor() {
				        public void process(Exchange exchange) throws Exception {
				            exchange.getIn().setBody("ReferralRequest/"+exchange.getIn().getHeader("_id"));
				        }
					})
					.choice()
						.when(header("status").isNotNull())
							.process(workFlowGet)
							.to("direct:eRSCall")
							.process(new Processor() {
							    public void process(Exchange exchange) throws Exception {
							    	// We reset the stream - only needed following a file output operation. This process can be removed if not using it.
							    	InputStream is = (InputStream) exchange.getIn().getBody();
								    is.reset();
							    }})
						.otherwise()
							.to("direct:ReferralRequestGet")
					.endChoice()
				.endRest();
		    
		
		from("direct:startTestWorkflow")
			.routeId("startTestWorkflow")
			.to("direct:Logon")
			.process(new Processor() {
		        public void process(Exchange exchange) throws Exception {
		            exchange.getIn().setHeader("_revinclude","*");
		        }
			})
			.process(workFlowGet)
			.to("direct:eRSCall")
			.to("mock:resultReferralRequestFetchWorklist")
			// Convert stream to String
			.process(workFlowReferralSplit)
			// The response from the referral request is split into separate messages - no combination back
			.split(body().tokenize(","))
				.to("direct:ReferralRequestGet")
				// Output results
				.to("direct:PostBundleReferralRequest")
			.end();
			
		
		// Calls eRS to establish a session
		from("direct:Logon")
			.process(new Processor() {
				public void process(Exchange exchange) throws Exception {
					exchange.getIn().removeHeaders("*","_id|version|_revinclude|status");
					exchange.getIn().setHeader(Exchange.HTTP_PATH,"ProfessionalSession");
					exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
					exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
					exchange.getIn().setHeader("XAPI_ASID",env.getProperty("ASID"));
					exchange.getIn().setHeader("XAPI_FQDN",env.getProperty("FQDN"));
					exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/json");
					exchange.getIn().setHeader("FileRef","1-ProfessionalSessionPost.json");
					exchange.getIn().setHeader("token", getToken());
					String request = getRequest(exchange);
									
					exchange.getIn().setBody(request);
				}
			})
			.to("direct:eRSCall")
			.to("mock:resultProfessionalSessionPost")
			// Amend the message to add permission request
			.process(session)
			.to("direct:eRSCall")
			.to("mock:resultProfessionalSessionPut");
			
		
		
		
		from("direct:ReferralRequestGet")
			.routeId("ReferralRequestGet")
			.process(new Processor() {
			    public void process(Exchange exchange) throws Exception {
			        String payload = exchange.getIn().getBody(String.class);
			        String[] part = payload.split("/");
			        exchange.getIn().setHeader(Exchange.HTTP_PATH, payload);
			        exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
			        exchange.getIn().setHeader("FileRef","4-ReferralRequestGet-"+part[1]+".json");
			        exchange.getIn().setBody("");
			    }})
			.to("direct:eRSCall")
			.to("mock:resultReferralRequestGet")
			.choice()
				.when(header("_revinclude").isNotNull())
					.to("direct:GetAttachedDocuments")
					
				.otherwise()
					.process(new Processor() {
					    public void process(Exchange exchange) throws Exception {
					    	// We reset the stream - only needed following a file output operation. This process can be removed if not using it.
					    	InputStream is = (InputStream) exchange.getIn().getBody();
						    is.reset();
					    }})
			.endChoice();
				
		
		from("direct:GetAttachedDocuments")
			.routeId("GetAttachedDocuments")
			.process(binaryGetSplit)
			.split(body().tokenize(","),binaryAggregation)
				// Build the retrieval call
				.process(binaryGet)
				.to("direct:eRSCall")
				.to("mock:resultBinaryGet")
			.end();
			
		from("direct:eRSCall")
			.routeId("eRS Call")
			.to("log:uk.nhs.jorvik.fhirTest.E_RS.PreHttp?showAll=true&multiline=true&level=INFO")
			.to(httpsEndpoint)
			.to("log:uk.nhs.jorvik.fhirTest.E_RS.PostHttp?showAll=true&multiline=true&level=INFO")
			.to("file:C://test//e-RS?fileName=$simple{date:now:yyyyMMdd}-${id}-${in.header.FileRef}");
			// only include file for debug. 
		
		from("direct:PostBundleReferralRequest")
			.routeId("PostBundleReferralRequest")
			.process(new Processor() {
			    public void process(Exchange exchange) throws Exception {
			        exchange.getIn().setHeader(Exchange.HTTP_PATH, "ReferralRequest");
			        exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
			    }})
			.to(env.getProperty("tie.endpoint"))
			.to("file:C://test//e-RS?fileName=$simple{date:now:yyyyMMdd}-${id}-${in.header.FileRef}")
			.to("mock:resultAggregatedDocuments");
		
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
	
	private String getRequest(Exchange exchange)
	{
		String request = "{"
					+" \"typeInfo\": \"uk.nhs.ers.xapi.dto.v1.session.ProfessionalSession\" ,"
					//
					+" \"token\": \""+exchange.getIn().getHeader("token").toString()+"\" "
						+" }";
		return request;
	}
	
	private Endpoint setupSSLConext(CamelContext camelContext) throws Exception {
		HttpComponent httpComponent = null; 
		if (env.getProperty("secure").equals("true"))
		{
	        KeyStoreParameters keyStoreParameters = new KeyStoreParameters();
	        // Change this path to point to your truststore/keystore as jks files
	        keyStoreParameters.setResource(env.getProperty("keystore.resource"));
	        keyStoreParameters.setPassword(env.getProperty("keystore.password"));
	
	        KeyManagersParameters keyManagersParameters = new KeyManagersParameters();
	        keyManagersParameters.setKeyStore(keyStoreParameters);
	        keyManagersParameters.setKeyPassword(env.getProperty("keymanager.password"));
	
	        TrustManagersParameters trustManagersParameters = new TrustManagersParameters();
	        trustManagersParameters.setKeyStore(keyStoreParameters);
	
	        SSLContextParameters sslContextParameters = new SSLContextParameters();
	        sslContextParameters.setKeyManagers(keyManagersParameters);
	        sslContextParameters.setTrustManagers(trustManagersParameters);
	
	        httpComponent = camelContext.getComponent("https4", HttpComponent.class);
	        httpComponent.setSslContextParameters(sslContextParameters);
	        //This is important to make your cert skip CN/Hostname checks
	       // httpComponent.setX509HostnameVerifier(new AllowAllHostnameVerifier());
		}
        else
        {
        	httpComponent = camelContext.getComponent("http4", HttpComponent.class);
	        
        }

        return httpComponent.createEndpoint(env.getProperty("ers.endpoint"));
      
    }
	
}
