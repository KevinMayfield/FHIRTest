package uk.nhs.jorvik.fhirTest.careConnectCompositionTest;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Composition;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.test.context.ContextConfiguration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import uk.nhs.jorvik.fhirTest.javaconfig.CareConnectCompositionApp;


@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = {CareConnectCompositionApp.class}, loader = CamelSpringDelegatingTestContextLoader.class)
@MockEndpoints
public class CareConnectCompositionTest  {
	
	 @Produce(uri = "direct:startFHIRComposition")
     protected ProducerTemplate templateFHIRComposition;
	 
	 @Produce(uri = "direct:startFHIRCompositionBundle")
     protected ProducerTemplate templateFHIRCompositionBundle;
	 
	 @EndpointInject(uri = "mock:log:uk.nhs.jorvik.fhirTest.javaconfig.IntegrationTestComposition?showAll=true&multiline=true&level=INFO")
	 protected MockEndpoint resultEndpointFHIRComposition;
	
	 @EndpointInject(uri = "mock:log:uk.nhs.jorvik.fhirTest.javaconfig.IntegrationTestBundle?showAll=true&multiline=true&level=INFO")
	 protected MockEndpoint resultEndpointFHIRCompositionBundle;
	 
	 private static final Logger log = LoggerFactory.getLogger(CareConnectCompositionTest.class);
	
	 private static final FhirContext ctxhapiHL7Fhir = FhirContext.forDstu3();
		 
	  @Test
	  public void testSendCompositionV1() throws Exception 
	  {
			
	    	Composition careRecord = CareConnectExamples.buildCareConnectFHIRCompositionV1();
	        IParser parser = ctxhapiHL7Fhir.newXmlParser().setPrettyPrint(true);
	    	String response = parser.encodeResourceToString(careRecord);
	    	
	        templateFHIRComposition.sendBody("direct:startFHIRComposition",response);
	        resultEndpointFHIRComposition.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 201); 
	        resultEndpointFHIRComposition.assertIsSatisfied();
	        
	        if (resultEndpointFHIRComposition.getExchanges().size()>0)
	        {
	        	// Convert returned resource into a HAPI Patient pojo for assertion testing
	        	Exchange exchange = resultEndpointFHIRComposition.getExchanges().get(0);
	     
	        	InputStream is = (InputStream) exchange.getIn().getBody();
	        	is.reset();
	        	Reader reader = new InputStreamReader(new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class)));
	        	parser = ctxhapiHL7Fhir.newXmlParser();
				try
				{
					parser.parseResource(Composition.class,reader);
				}
				catch (Exception ex)
				{
					log.error(ex.getMessage());
					ex.printStackTrace();
				}
			}
	        else
	        {
	        	fail();
	        }
	  }
	  
	  @Test
	  public void testSendCompositionBundleV1() throws Exception 
	  {
			
	    	Bundle careRecordBundle = CareConnectExamples.buildCareConnectFHIRCompositionBundleV1();
	        IParser parser = ctxhapiHL7Fhir.newXmlParser().setPrettyPrint(true);
	    	String response = parser.encodeResourceToString(careRecordBundle);
	    	
	        templateFHIRCompositionBundle.sendBody("direct:startFHIRCompositionBundle",response);
	        resultEndpointFHIRCompositionBundle.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 201); 
	        resultEndpointFHIRCompositionBundle.assertIsSatisfied();
	        
	  }
}
