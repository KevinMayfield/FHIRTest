package uk.nhs.jorvik.fhir.E_RS.javaconfig;


import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
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
public class FHIRRoute extends RouteBuilder {
	
	private static final FhirContext ctxhapiHL7Fhir = FhirContext.forDstu2();
	@Override
	public void configure() throws Exception {
		
		// Simple connection to smartcard to ensure the user has been authenticated
		
		GATicket gaticket = null;
		String ssoTicket = null;
		Long ssoTicketCode = null;
	    try {
	      log.info("TEST - MAIN: Starting.");
	      gaticket = new GATicket();
	      gaticket.setIsDebug(true);
	      ssoTicket = gaticket.getTicket();
	      log.info(("TEST - MAIN: GATicket -> " + ssoTicket));
	      ssoTicketCode = gaticket.getLastError();
	    }
	    catch (Throwable err) {
	      err.printStackTrace();
	      if (null!=gaticket)
	    	  log.info(("error code: " + gaticket.getLastError()));
	    }
	    gaticket.getErrorDescription(ssoTicketCode);
		
	    // So this url should work 
	    // http://localhost:8181/eRS/dstu2/ReferralRequest/000000090007
	    // http://localhost:8181/eRS/dstu2/ReferralRequest?_id=000000090007&_revinclude=*
	    
	    
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
				.to("direct:RIE")
				// Output results
				.to("file:C://test//e-RS?fileName=$simple{date:now:yyyyMMdd}-${in.header.breadcrumbId}-${in.header.FileRef}")
				.to("mock:resultAggregatedDocuments")
			.end();
			
		
		// Calls eRS to establish a session
		from("direct:Logon")
			.process(new Processor() {
				public void process(Exchange exchange) throws Exception {
					exchange.getIn().removeHeaders("*","_id|version|_revinclude|status");
					exchange.getIn().setHeader(Exchange.HTTP_PATH,"ProfessionalSession");
					exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
					exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
					exchange.getIn().setHeader("XAPI_ASID","999000000045");
					exchange.getIn().setHeader("XAPI_FQDN","all.bjss.com");
					exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/json");
					exchange.getIn().setHeader("FileRef","1-ProfessionalSessionPost.json");
					String request = "{"
	    					+" \"typeInfo\": \"uk.nhs.ers.xapi.dto.v1.session.ProfessionalSession\" ,"
	    					+" \"token\": \"021600556514 x x x\" "
	  						+" }";
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
			.to("log:uk.nhs.jorvik.fhirTest.E_RS.PreHttp?showAll=true&multiline=true&level=DEBUG")
			.to("http://api-ers.spine2.ncrs.nhs.uk:88/ers-external-service/v1/?throwExceptionOnFailure=false&connectionsPerRoute=60&bridgeEndpoint=true")
			.to("log:uk.nhs.jorvik.fhirTest.E_RS.PostHttp?showAll=true&multiline=true&level=DEBUG")
			.to("file:C://test//e-RS?fileName=$simple{date:now:yyyyMMdd}-${in.header.breadcrumbId}-${in.header.FileRef}");
			// only include file for debug. 
		
		from("direct:PostBundleReferralRequest")
			.routeId("PostBundleReferralRequest")
			.to("file:C://test//e-RS//output?fileName=$simple{date:now:yyyyMMdd}-${in.header.breadcrumbId}-ReferralRequestBundle-${in.header.UBRN}.xml");
	
		from("direct:RIE")
			.routeId("Outbound RIE")
			.process(new Processor() {
			    public void process(Exchange exchange) throws Exception {
			        exchange.getIn().setHeader(Exchange.HTTP_PATH, "ReferralRequest");
			        exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
			    }})
			.to("http://rie-test:20105/fhirbase?throwExceptionOnFailure=false&bridgeEndpoint=true");
  	}
	
	
}
