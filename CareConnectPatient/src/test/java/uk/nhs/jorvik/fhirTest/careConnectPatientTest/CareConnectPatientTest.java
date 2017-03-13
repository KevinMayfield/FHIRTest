package uk.nhs.jorvik.fhirTest.careConnectPatientTest;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;
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

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Patient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.test.context.ContextConfiguration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import uk.nhs.jorvik.fhirTest.javaconfig.CareConnectPatientApp;


@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = {CareConnectPatientApp.class}, loader = CamelSpringDelegatingTestContextLoader.class)
@MockEndpoints
public class CareConnectPatientTest  {
	
	 @Produce(uri = "direct:startFHIRPatientV1")
     protected ProducerTemplate templateFHIRPatientV1;
	 
	 @Produce(uri = "direct:startFHIRPatientV2")
     protected ProducerTemplate templateFHIRPatientV2;
	 
	 @EndpointInject(uri = "mock:log:uk.nhs.jorvik.fhirTest.javaconfig.IntegrationTestPatient?showAll=true&multiline=true&level=INFO")
	 protected MockEndpoint resultEndpointFHIRPatient;
		 
	 private static final Logger log = LoggerFactory.getLogger(CareConnectPatientTest.class);
	
	 private static final FhirContext ctxhapiHL7Fhir = FhirContext.forDstu3();
	 
	  @Test
	  public void testSendPatientExtensionV1() throws Exception 
	  {
			
	    	Patient patient = CareConnectPatientExamples.customPatientExtensionV3();
	        
	    	IParser parser = ctxhapiHL7Fhir.newXmlParser().setPrettyPrint(true);
	    	
	    	String response = parser.encodeResourceToString(patient);
	        
	        templateFHIRPatientV1.sendBody("direct:startFHIRPatientV1",response);
	        
	        resultEndpointFHIRPatient.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 201); 
	        resultEndpointFHIRPatient.assertIsSatisfied();
	  }
	  
	  @Test
	  public void testSendPatientV1() throws Exception 
	  {
			
	    	Patient patient = CareConnectPatientExamples.buildCareConnectFHIRPatient();
	        
	    	IParser parser = ctxhapiHL7Fhir.newXmlParser().setPrettyPrint(true);
	    	
	    	String response = parser.encodeResourceToString(patient);
	        
	        templateFHIRPatientV1.sendBody("direct:startFHIRPatientV1",response);
	        
	        resultEndpointFHIRPatient.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 201); 
	        resultEndpointFHIRPatient.assertIsSatisfied();
	        
	  }
	
	
	  
		 
	  @SuppressWarnings("unchecked")
	  @Test
	  public void testSendPatientV2() throws Exception 
	  {
			
	    	Patient patient = CareConnectPatientExamples.buildCareConnectFHIRPatient();
	        
	    	IParser parser = ctxhapiHL7Fhir.newXmlParser().setPrettyPrint(true);
	    	
	    	String response = parser.encodeResourceToString(patient);
	        
	        templateFHIRPatientV2.sendBody("direct:startFHIRPatientV2",response);
	        
	        resultEndpointFHIRPatient.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 201); 
	        resultEndpointFHIRPatient.assertIsSatisfied();
	        
	        if (resultEndpointFHIRPatient.getExchanges().size()>0)
	        {
	        	// Convert returned resource into a HAPI Patient pojo for assertion testing
	        	
	        	Exchange exchange = resultEndpointFHIRPatient.getExchanges().get(0);
	        	
	        	// Need to reset the stream. 
	        	InputStream is = (InputStream) exchange.getIn().getBody();
	        	is.reset();
	        	Reader reader = new InputStreamReader(new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class)));
	        	Patient returnedPatient = null;
	        	parser = ctxhapiHL7Fhir.newXmlParser();
				try
				{
					returnedPatient = parser.parseResource(Patient.class,reader);
					
				}
				catch (Exception ex)
				{
					log.error(ex.getMessage());
					ex.printStackTrace();
				}
				
				// Tests
				
				assertEquals("Family Name",patient.getName().get(0).getFamily(),returnedPatient.getName().get(0).getFamily());
				
				assertEquals("Given Name",patient.getName().get(0).getGivenAsSingleString(),returnedPatient.getName().get(0).getGivenAsSingleString());
				
				assertEquals("Profile url present",patient.getMeta().getProfile().get(0).getValue(),returnedPatient.getMeta().getProfile().get(0).getValue());
				
				assertTrue("Extensions returned",returnedPatient.getExtension().size() > 1);
				
				String ethnicCode = null;
				String religionCode = null;
				
				assertThat("Expected extensions",returnedPatient.getExtension(), containsInAnyOrder(
						hasProperty("url",is("http://hl7.org.uk/CareConnect-ReligiousAffiliation-1-Extension.structuredefinition.xml")),
						hasProperty("url",is("http://hl7.org.uk/CareConnect-EthnicCategory-1-Extension.structuredefinition.xml"))
						));
				
				
				for (Extension extension : returnedPatient.getExtension())
				{
					if (extension.getUrl().equals("http://hl7.org.uk/CareConnect-ReligiousAffiliation-1-Extension.structuredefinition.xml"))
					{
						CodeableConcept code = (CodeableConcept) extension.getValue();
						religionCode = code.getCoding().get(0).getCode();
					}
					if (extension.getUrl().equals("http://hl7.org.uk/CareConnect-EthnicCategory-1-Extension.structuredefinition.xml"))
					{
						CodeableConcept code = (CodeableConcept) extension.getValue();
						ethnicCode = code.getCoding().get(0).getCode();
					}
				}
				
				
				// check religion code stored ok
				CodeableConcept code = (CodeableConcept) patient.getExtension().get(0).getValue();
 				assertEquals("Religion Code",code.getCoding().get(0).getCode(),religionCode);
 				
 				// Check ethnic code stored ok
 				code = (CodeableConcept) patient.getExtension().get(1).getValue();
 				assertEquals("Ethnic Code",code.getCoding().get(0).getCode(),ethnicCode);
			}
	        else
	        {
	        	fail();
	        }
	  }
	  
	  
	
}
