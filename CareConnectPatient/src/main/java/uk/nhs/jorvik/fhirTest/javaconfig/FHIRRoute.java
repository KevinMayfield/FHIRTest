package uk.nhs.jorvik.fhirTest.javaconfig;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;



@Component
public class FHIRRoute extends RouteBuilder {

	
	
	@Override
	public void configure() throws Exception {
		
		from("direct:startFHIRPatient")
			.routeId("startFHIRPatient")
			.process(new Processor() {
				public void process(Exchange exchange) throws Exception {
					exchange.getIn().setHeader(Exchange.HTTP_PATH,"Patient");
					exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
					exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
					// Default is to return a OperationOutcome but want the updated patient resource 
					exchange.getIn().setHeader("Prefer","return=representation");
					exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
				}
			})
			.to("http://test-rr8.trust.leedsth.nhs.uk:8080/hapi-fhir-jpaserver/baseStu3?throwExceptionOnFailure=false&connectionsPerRoute=60")
			.to("log:uk.nhs.jorvik.fhirTest.javaconfig.IntegrationTestPatient?showAll=true&multiline=true&level=INFO")
			.to("file:C://test//Patient");
		
  	}
	
}
