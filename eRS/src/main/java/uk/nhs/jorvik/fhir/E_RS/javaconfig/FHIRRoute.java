package uk.nhs.jorvik.fhir.E_RS.javaconfig;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

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
		
		ProfessionalSession session = new ProfessionalSession();
		WorkFlowGet workFlowGet = new WorkFlowGet();
		WorkFlowReferralSplit workFlowReferralSplit = new WorkFlowReferralSplit();
		ReferralRequestSupportedInformationSplit binaryGetSplit = new ReferralRequestSupportedInformationSplit(ctxhapiHL7Fhir);
		BinaryGet binaryGet = new BinaryGet();
		
		SupportingInformationAggregation binaryAggregation = new SupportingInformationAggregation(ctxhapiHL7Fhir);
		
		from("direct:startTestWorkflow")
			.routeId("startTestWorkflow")
			.process(new Processor() {
				public void process(Exchange exchange) throws Exception {
					exchange.getIn().setHeader(Exchange.HTTP_PATH,"ProfessionalSession");
					exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
					exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
					exchange.getIn().setHeader("XAPI_ASID","999000000045");
					exchange.getIn().setHeader("XAPI_FQDN","all.bjss.com");
					exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/json");
				}
			})
			.to("direct:Logon")
			.process(workFlowGet)
			.to("direct:ReferralRequestFetchWorklist")
			// Convert stream to String
			.process(workFlowReferralSplit)
			// The response from the referral request is split into separate messages - no combination back
			.split(body().tokenize(","))
				.to("direct:ReferralRequestGet")
			.end();
			
		
		from("direct:Logon")
			.to("http://api-ers.spine2.ncrs.nhs.uk:88/ers-external-service/v1/?throwExceptionOnFailure=false&connectionsPerRoute=60")
			.to("mock:Logon")
			.to("file:C://test//e-RS?fileName=$simple{date:now:yyyyMMdd}-${in.header.breadcrumbId}-ProfessionalSessionPost.json")
			// Amend the message to add permission request
			.process(session)
			.to("http://api-ers.spine2.ncrs.nhs.uk:88/ers-external-service/v1/?throwExceptionOnFailure=false&connectionsPerRoute=60")
			.to("mock:resultProfessionalSessionPut")
			.to("file:C://test//e-RS?fileName=$simple{date:now:yyyyMMdd}-${in.header.breadcrumbId}-ProfessionalSessionPut.json");
		
		from("direct:ReferralRequestFetchWorklist")
			.routeId("ReferralRequestFetchWorklist")
			.to("http://api-ers.spine2.ncrs.nhs.uk:88/ers-external-service/v1/?throwExceptionOnFailure=false&connectionsPerRoute=60")
			.to("mock:resultReferralRequestFetchWorklist")
			.to("file:C://test//e-RS?fileName=$simple{date:now:yyyyMMdd}-${in.header.breadcrumbId}-ReferralRequestFetchWorklistPost.json");
		
		from("direct:ReferralRequestGet")
			.routeId("ReferralRequestGet")
			.process(new Processor() {
			    public void process(Exchange exchange) throws Exception {
			        String payload = exchange.getIn().getBody(String.class);
			        exchange.getIn().setHeader(Exchange.HTTP_PATH, payload);
			        exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
			        exchange.getIn().setHeader("FileRef",payload.replace("/","-"));
			        exchange.getIn().setBody("");
			    }})
			.to("http://api-ers.spine2.ncrs.nhs.uk:88/ers-external-service/v1/?throwExceptionOnFailure=false&connectionsPerRoute=60")
			.to("mock:resultReferralRequestGet")
			.to("file:C://test//e-RS?fileName=$simple{date:now:yyyyMMdd}-${in.header.breadcrumbId}-ReferralRequest-${in.header.FileRef}.json")
			.to("direct:GetAttachedDocuments");
				
		
		from("direct:GetAttachedDocuments")
			.routeId("GetAttachedDocuments")
			.process(binaryGetSplit)
			.split(body().tokenize(","),binaryAggregation)
				// Build the retrieval call
				.process(binaryGet)
				.to("direct:BinaryGet")
			.end()
			.to("direct:PostBundleReferralRequest");
			
		
		from("direct:BinaryGet")
			.routeId("BinaryGet")
			.to("http://api-ers.spine2.ncrs.nhs.uk:88/ers-external-service/v1/?throwExceptionOnFailure=false&connectionsPerRoute=60")
			.to("mock:resultBinaryGet")
			.to("file:C://test//e-RS?fileName=$simple{date:now:yyyyMMdd}-${in.header.breadcrumbId}-Attach-${in.header.FileRef}");
		
		from("direct:PostBundleReferralRequest")
			.routeId("PostBundleReferralRequest")
			.to("mock:resultAggregatedDocuments")
			.to("file:C://test//e-RS//output?fileName=$simple{date:now:yyyyMMdd}-${in.header.breadcrumbId}-ReferralRequestBundle-${in.header.UBRN}.xml");
	
  	}
	
	
}
