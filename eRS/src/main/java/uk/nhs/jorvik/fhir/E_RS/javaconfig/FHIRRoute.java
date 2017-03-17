package uk.nhs.jorvik.fhir.E_RS.javaconfig;


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
				.param().type(RestParamType.header).required(true).name("Content-Type").defaultValue("application/json+fhir").allowableValues("application/json+fhir","application/xml+fhir").description("http Content Type").dataType("string").endParam()
				.responseMessage().code(200).message("OK").endResponseMessage()
				.route()
					.routeId("RLS-RIE DocumentReference FIND")
					.to("direct:Logon")
					/*
					.process(new Processor() {
				        public void process(Exchange exchange) throws Exception {
				            exchange.getIn().setHeader(Exchange.HTTP_PATH, "/DocumentReference");
				        }
					})*/
				.endRest();
	    
		
		from("direct:startTestWorkflow")
			.routeId("startTestWorkflow")
			.to("direct:Logon")
			.process(workFlowGet)
			.to("direct:ReferralRequestFetchWorklist")
			// Convert stream to String
			.process(workFlowReferralSplit)
			// The response from the referral request is split into separate messages - no combination back
			.split(body().tokenize(","))
				.to("direct:ReferralRequestGet")
			.end();
			
		
		
		// Calls eRS to establish a session
		from("direct:Logon")
			.process(new Processor() {
				public void process(Exchange exchange) throws Exception {
					exchange.getIn().setHeader(Exchange.HTTP_PATH,"ProfessionalSession");
					exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
					exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
					exchange.getIn().setHeader("XAPI_ASID","999000000045");
					exchange.getIn().setHeader("XAPI_FQDN","all.bjss.com");
					exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/json");
					exchange.getIn().setHeader("FileRef","1-ProfessionalSessionPost.json");
				}
			})
			.to("direct:eRSCall")
			.to("mock:resultProfessionalSessionPost")
			// Amend the message to add permission request
			.process(session)
			.to("direct:eRSCall")
			.to("mock:resultProfessionalSessionPut");
			
		
		from("direct:ReferralRequestFetchWorklist")
			.routeId("ReferralRequestFetchWorklist")
			.to("direct:eRSCall")
			.to("mock:resultReferralRequestFetchWorklist");
		
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
			.to("direct:GetAttachedDocuments");
				
		
		from("direct:GetAttachedDocuments")
			.routeId("GetAttachedDocuments")
			.process(binaryGetSplit)
			.split(body().tokenize(","),binaryAggregation)
				// Build the retrieval call
				.process(binaryGet)
				.to("direct:eRSCall")
				.to("mock:resultBinaryGet")
			.end()
			// Output results
			.to("file:C://test//e-RS?fileName=$simple{date:now:yyyyMMdd}-${in.header.breadcrumbId}-${in.header.FileRef}")
			.to("mock:resultAggregatedDocuments");
			
		
		from("direct:eRSCall")
			.routeId("eRS Call")
			.to("log:uk.nhs.jorvik.fhirTest.E_RS.PreHttp?showAll=true&multiline=true&level=DEBUG")
			.to("http://api-ers.spine2.ncrs.nhs.uk:88/ers-external-service/v1/?throwExceptionOnFailure=false&connectionsPerRoute=60")
			.to("log:uk.nhs.jorvik.fhirTest.E_RS.PostHttp?showAll=true&multiline=true&level=DEBUG")
			.to("file:C://test//e-RS?fileName=$simple{date:now:yyyyMMdd}-${in.header.breadcrumbId}-${in.header.FileRef}");
		
		from("direct:PostBundleReferralRequest")
			.routeId("PostBundleReferralRequest")
			.to("file:C://test//e-RS//output?fileName=$simple{date:now:yyyyMMdd}-${in.header.breadcrumbId}-ReferralRequestBundle-${in.header.UBRN}.xml");
	
  	}
	
	
}
