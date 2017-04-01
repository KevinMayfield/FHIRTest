package uk.nhs.careConnect.dstu2.test;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.DocumentReference;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.ValueSet;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DemoServerTest {

	private static IGenericClient ourClient;
	private static final FhirContext ourCtx = FhirContext.forDstu2();
	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(DemoServerTest.class);

	private static int ourPort;

	private static Server ourServer;
	private static String ourServerBase;

	@Test
	public void testCreateAndRead() throws IOException {
			
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		
        Patient patient = new Patient();
        
        patient.addIdentifier()
	    	.setSystem(new String("https://fhir.nhs.net/Id/nhs-number"))
	    	.setValue("9531258403");
        
        patient.addIdentifier()
    		.setSystem(new String("https://fhir.jorvik.nhs.uk/Diamon/Patient"))
    		.setValue("1234567");
    		
        patient.addName()
        	.addFamily("BLAIR")
        	.addGiven("TONEE");
        
        patient.addContact()
    	.addRelationship()
    		.addCoding()
    			.setCode("01")
    			.setDisplay("Spouse")
    			.setSystem("http://hl7.org.uk/fhir/ValueSet/CareConnect-PersonRelationshipType");
        
        patient.setGender(AdministrativeGenderEnum.FEMALE);
        Date birth;
		try {
			birth = dateFormat.parse("2003-07-23");
			patient.setBirthDate(new DateDt(birth));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String logOutput = ourClient.getFhirContext().newXmlParser().setPrettyPrint(true).encodeResourceToString(patient);
       

		MethodOutcome outcome = ourClient.create()
				.resource(patient)
				.prettyPrint()
				.encodedXml()
				.execute();
		
		assertNotNull(outcome);
		
		Patient pt2 = ourClient.read().resource(Patient.class).withId(outcome.getId().getIdPart()).execute();
		assertEquals("BLAIR", pt2.getName().get(0).getFamily());
		
	
		
		patient = new Patient();
        patient.addIdentifier()
        	.setSystem(new String("http://fhir.leedsth.nhs.uk/PAS/Patient"))
        	.setValue("9437718");
        
        patient.addIdentifier()
	    	.setSystem(new String("http://fhir.nhs.net/Id/nhs-number"))
	    	.setValue("9584035312");
        
    		
        patient.addName()
        	.addFamily("TERRY")
        	.addGiven("MAY");
        
        patient.addContact()
        	.addRelationship()
        		.addCoding()
        			.setCode("01")
        			.setDisplay("Spouse")
        			.setSystem("http://hl7.org.uk/fhir/ValueSet/CareConnect-PersonRelationshipType");
        
        patient.setGender(AdministrativeGenderEnum.MALE);
        
		try {
			birth = dateFormat.parse("1973-02-03");
			patient.setBirthDate(new DateDt(birth));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

		outcome = ourClient.create()
				.resource(patient)
				.prettyPrint()
				.encodedXml()
				.execute();
		
		assertNotNull(outcome);
		
		pt2 = ourClient.read().resource(Patient.class).withId(outcome.getId().getIdPart()).execute();
		
		assertEquals("TERRY", pt2.getName().get(0).getFamily());
		
		
		DocumentReference docRef = new DocumentReference();
		docRef.addIdentifier()
			.setSystem("http://fhir.leedsth.nhs.uk/PPM/FileStoreId/")
			.setValue("999999");
		
		IdentifierDt master = new IdentifierDt()
					.setSystem("http://fhir.leedsth.nhs.uk/DocumentReference")
					.setValue(java.util.UUID.randomUUID().toString());
		docRef.setMasterIdentifier(master);
		
		
		   //get current date time with Date()
		Date date = new Date();
		docRef.setCreated(new DateTimeDt(date));
		docRef.setIndexed(new InstantDt(date));
		//http://fhir.leedsth.nhs.uk/PAS/Patient/
		docRef.setSubject(new ResourceReferenceDt("http://fhir.leedsth.nhs.uk/PAS/Patient/9437718"));
		
		CodeableConceptDt typeCode = new CodeableConceptDt();
		typeCode.addCoding()
			.setCode("823691000000103")
			.setSystem("http://snomed.info/sct")
			.setDisplay("Clinical letter");
		docRef.setType(typeCode);
		
		CodeableConceptDt classCode = new CodeableConceptDt();
		classCode.addCoding()
			.setCode("892611000000105")
			.setSystem("http://snomed.info/sct")
			.setDisplay("Hepatology service");
		// now adding this to practiceSetting docRef.setClass_(classCode);
		
		
		/*
		docRef.setCustodian(new ResourceReferenceDt("https://sds.proxy.nhs.uk/Organization/RR8"));
		
		DocumentReference.DocumentReferenceContentComponent contentComponent = new DocumentReference.DocumentReferenceContentComponent();
		Attachment attach = new Attachment();
		attach.setContentType("application/pdf");
		attach.setUrl("https://fhir.leedsth.nhs.uk/PPMDocumentStore/MagicBeans/1");
		attach.setTitle("Jack and the Beanstalk");
		contentComponent.setAttachment(attach);
		docRef.addContent(contentComponent);
		
		DocumentReference.DocumentReferenceContextComponent contextComponent = new DocumentReference.DocumentReferenceContextComponent();
		CodeableConcept facility = new CodeableConcept();
		facility.addCoding()
			.setDisplay("Secondary care hospital")
			.setCode("46111000")
			.setSystem("http://snomed.info/sct");
		contextComponent.setFacilityType(facility);
		
		contextComponent.setPracticeSetting(classCode);
		
		docRef.setContext(contextComponent);
		
		CodeableConcept security = docRef.addSecurityLabel();
		security.addCoding()
			.setCode("V")
			.setDisplay("very restricted")
			.setSystem("http://hl7.org/fhir/ValueSet/security-labels");
		
		
		outcome = ourClient.create().resource(docRef)
			.prettyPrint()
			.encodedXml()
			.execute();

		assertNotNull(outcome);
		
		DocumentReference docRef2 = ourClient.read().resource(DocumentReference.class).withId(outcome.getId().getIdPart()).execute();
		// Move to master identifier
		// SNOMED not present on dev box
		// assertEquals("823691000000103", docRef2.getType().getCoding().get(0).getCode());
		assertEquals("Jack and the Beanstalk", docRef2.getContent().get(0).getAttachment().getTitle());
		
		// REFERRAL REQUEST
		
		*/
		
		// ValueSet 
		
		ValueSet valueSet = new ValueSet();
		
		valueSet.setDescription("The status of the referral.");
		valueSet.setUrl("http://fhir.nhs.net/ValueSet/referral-status-1");
		valueSet.setId("referral-status-1");
		//valueSet.setStatus(PublicationStatus.ACTIVE);
		ValueSet.Compose component = new ValueSet.Compose();
		valueSet.setCompose(component);
		ValueSet.ComposeInclude include = component.addInclude();
		include.addConcept().setCode("draft").setDisplay("Draft");
		include.addConcept().setCode("active").setDisplay("Active");
		include.addConcept().setCode("cancelled").setDisplay("Cancelled");
		include.addConcept().setCode("accepted").setDisplay("Accepted");
		include.addConcept().setCode("rejected").setDisplay("Rejected");
		include.addConcept().setCode("completed").setDisplay("Completed");
		
		
		
		outcome = ourClient.create().resource(valueSet)
				.prettyPrint()
				.encodedXml()
				.execute();

			assertNotNull(outcome);
			
		
		ValueSet valueSet2 = ourClient.read().resource(ValueSet.class).withId(outcome.getId().getIdPart()).execute();
		assertEquals("http://fhir.nhs.net/ValueSet/referral-status-1", valueSet2.getUrl());
		
		//Parameters params ourClient. read().resource(ValueSet.class).withId(id.getIdPart()).execute();
	}

	@AfterClass
	public static void afterClass() throws Exception {
		ourServer.stop();
	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		/*
		 * This runs under maven, and I'm not sure how else to figure out the target directory from code..

		
		String path = DemoServerTest.class.getClassLoader().getResource(".keep_hapi-fhir-jpaserver-example").getPath();
		path = new File(path).getParent();
		path = new File(path).getParent();
		path = new File(path).getParent();
		 */

		String path = "/Development/FHIRTest/Demo-Server-Dstu2";

		ourLog.info("Project base path is: {}", path);

		ourPort = RandomServerPortProvider.findFreePort();
		ourServer = new Server(ourPort);

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setDescriptor(path + "/src/main/webapp/WEB-INF/web.xml");
		webAppContext.setResourceBase(path + "/target/demo-server-hapi");
		webAppContext.setParentLoaderPriority(true);

		ourServer.setHandler(webAppContext);
		ourServer.start();

		ourCtx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
		ourCtx.getRestfulClientFactory().setSocketTimeout(1200 * 1000);
		ourServerBase = "http://localhost:" + ourPort + "/baseStu3";
		ourClient = ourCtx.newRestfulGenericClient(ourServerBase);
		ourClient.registerInterceptor(new LoggingInterceptor(true));

	}

}
