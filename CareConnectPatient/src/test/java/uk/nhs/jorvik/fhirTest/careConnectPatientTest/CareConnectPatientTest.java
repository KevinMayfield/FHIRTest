package uk.nhs.jorvik.fhirTest.careConnectPatientTest;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import org.hl7.fhir.dstu3.model.HumanName.NameUse;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Meta;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
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
	
	 @Produce(uri = "direct:startFHIRPatient")
     protected ProducerTemplate templateFHIRPatient;
	 
	 @EndpointInject(uri = "mock:file:C://test//Patient")
	 protected MockEndpoint resultEndpointFHIRPatient;
		 
	 private static final Logger log = LoggerFactory.getLogger(CareConnectPatientTest.class);
	
	 private static final FhirContext ctxhapihl7fhir = FhirContext.forDstu3();
		
	 
	  @SuppressWarnings("unchecked")
	  @Test
	  public void testSendPatient() throws Exception 
	  {
			
	    	Patient patient = buildCareConnectFHIRPatient();
	        
	    	IParser parser = ctxhapihl7fhir.newXmlParser().setPrettyPrint(true);
	    	
	    	String response = parser.encodeResourceToString(patient);
	        
	        templateFHIRPatient.sendBody("direct:startFHIRPatient",response);
	        
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
	        	parser = ctxhapihl7fhir.newXmlParser();
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
	
	  public static Patient buildCareConnectFHIRPatient()
			{
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				
				Patient patient = new Patient();
		        
				// Add profile reference
				Meta meta = new Meta();
				meta.addProfile("http://hl7.org.uk/CareConnect-Patient-1.structuredefinition.xml");
				patient.setMeta(meta);
		        
		        Identifier nhsNumber = patient.addIdentifier();
		        nhsNumber
			    	.setSystem(new String("http://fhir.nhs.net/Id/nhs-number"))
			    	.setValue("9480431963");
		       	
		        CodeableConcept verificationStatusCode = new CodeableConcept();
		        verificationStatusCode
		        	.addCoding()
		        	.setSystem("http://hl7.org.uk/fhir/ValueSet/CareConnect-NhsNumberVerificationStatus")
		        	.setDisplay("Number present and verified")
		        	.setCode("01");
		        nhsNumber.addExtension()
		        		.setUrl("http://hl7.org.uk/CareConnect-NhsNumberVerificatnStatus-1-Extension.structuredefinition.xml")
		        		.setValue(verificationStatusCode);
		        		
		       	
		        
		        patient.addIdentifier()
	        		.setSystem(new String("http://fhir.jorvik.nhs.uk/PAS/Patient"))
	        		.setValue("9437718");
		        
		        patient.addName()
		        	.setUse(NameUse.USUAL)
		        	.setFamily("DUFFY")
		        	.addGiven("Gideon")
		        	.addGiven("Brian")
		        	.addPrefix("Mr");
		        
		        patient.addAddress()
		        	.addLine("1 CHURCH SQUARE")
		        	.setCity("LEEDS")
		        	.setPostalCode("LS25 1JF");
		       
		        
		        patient.addContact().addRelationship()
		    		.addCoding()
		    			.setCode("01")
		    			.setDisplay("Spouse")
		    			.setSystem("http://hl7.org.uk/fhir/ValueSet/CareConnect-PersonRelationshipType");
		        
		        // Not CareConnect compliant
		        patient.setGender(AdministrativeGender.FEMALE);
		        
		        Date birth;
				try {
					birth = dateFormat.parse("1926-03-31");
					patient.setBirthDate(birth);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				//
				
				patient.setManagingOrganization(new Reference("http://fhir.nhs.net/Id/ods-organization-code/B86675"));
				
				
				// CareConnect Patient Profile extensions
				
				CodeableConcept religionCode = new CodeableConcept();
				religionCode.addCoding()
					.setSystem("http://snomed.info/sct")
					.setDisplay("Druid, follower of religion")
					.setCode("428506007");
				patient.addExtension()
					.setUrl("http://hl7.org.uk/CareConnect-ReligiousAffiliation-1-Extension.structuredefinition.xml")
					.setValue(religionCode);
				
				CodeableConcept ethnicCode = new CodeableConcept();
				ethnicCode.addCoding()
					.setSystem("http://hl7.org.uk/fhir/ValueSet/CareConnect-EthnicCategory")
					.setDisplay("Other white European, European unspecified, European mixed")
					.setCode("CY");
				patient.addExtension()
					.setUrl("http://hl7.org.uk/CareConnect-EthnicCategory-1-Extension.structuredefinition.xml")
					.setValue(ethnicCode);
				
				
				
				return patient;
			}

	
		
	  /*
	   *  
	   *  HapiContext ctxhapihl7v2 = new DefaultHapiContext();
	    	ctxhapihl7v2.getParserConfiguration().setValidating(false);
	        		//HL7DataFormat hapihl7v2 = new HL7DataFormat();
	        ADT_A05 adt = buildPatientV2();
	        Parser parser = ctxhapihl7v2.getPipeParser();
	        String encodedMessage = parser.encode(adt);
	        template.sendBody("direct:startV2",encodedMessage);
	     */   
	   /*
	  public static ADT_A05 buildPatientV2() throws HL7Exception, IOException
		{
			//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			ADT_A05 adt = new ADT_A05();
			adt.initQuickstart("ADT", "A28", "P");
			          
	          // Populate the MSH Segment
	        MSH mshSegment = adt.getMSH();
	        mshSegment.getSendingApplication().getNamespaceID().setValue("TestSendingSystem");
	        mshSegment.getSequenceNumber().setValue("123");
	          
	          // Populate the PID Segment
	        PID pid = adt.getPID(); 
	        pid.getPatientName(0).getFamilyName().getSurname().setValue("May");
	        pid.getPatientName(0).getGivenName().setValue("Trees");
	        pid.getPatientName(1).getGivenName().setValue("Are");
	        pid.getPatientIdentifierList(0).getID().setValue("123456");
	        
	        //Date birth;
			
			//birth = dateFormat.parse("2003-07-23");
			TSComponentOne tm = pid.getDateTimeOfBirth()
				.getTimeOfAnEvent();
			tm.setValue("20030623");
			
		
	  
			        
			return adt;        
		}
	*/
	  
	
}
