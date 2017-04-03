package uk.nhs.careConnect.dstu2.test;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.ValueSet;
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
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DemoServerTest {

	private static IGenericClient ourClient;
	private static final FhirContext ourCtx = FhirContext.forDstu2();
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DemoServerTest.class);

	private static int ourPort;

	private static Server ourServer;
	private static String ourServerBase;

	@Test
	public void testCreateAndRead() throws IOException {
			
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		
		// ValueSet 
		
		ValueSet valueSet = new ValueSet();
		
		valueSet.setDescription("The status of the referral.");
		valueSet.setUrl("https://fhir.nhs.net/ValueSet/referral-status-1");
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
		
		log.info("Test Id = "+valueSet.getId().getValue());
		
		MethodOutcome outcome = ourClient.update().resource(valueSet)
				.prettyPrint()
				.encodedXml()
				.execute();

			assertNotNull(outcome);
			
		
		ValueSet valueSet2 = ourClient.read().resource(ValueSet.class).withId(outcome.getId().getIdPart()).execute();
		assertEquals("https://fhir.nhs.net/ValueSet/referral-status-1", valueSet2.getUrl());
		
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

		log.info("Project base path is: {}", path);

		ourPort = RandomServerPortProvider.findFreePort();
		ourServer = new Server(ourPort);

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setDescriptor(path + "/src/main/webapp/WEB-INF/web.xml");
		webAppContext.setResourceBase(path + "/target/demo-server-dstu2");
		webAppContext.setParentLoaderPriority(true);

		ourServer.setHandler(webAppContext);
		ourServer.start();

		ourCtx.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
		ourCtx.getRestfulClientFactory().setSocketTimeout(1200 * 1000);
		ourServerBase = "http://localhost:" + ourPort + "/Dstu2";
		ourClient = ourCtx.newRestfulGenericClient(ourServerBase);
		ourClient.registerInterceptor(new LoggingInterceptor(true));

	}

}
