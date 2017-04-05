package uk.nhs.fhir.careconnect.testserver.dstu2;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.client.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hl7.fhir.instance.model.api.IIdType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static uk.nhs.fhir.careconnect.examples.dstu2.CareConnectPatient.buildCareConnectFHIRPatient;

public class ExampleServerIT {

	private static IGenericClient ourClient;
	private static final FhirContext ourCtx = FhirContext.forDstu2();
	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(ExampleServerIT.class);

	private static int ourPort;

	private static Server ourServer;
	private static String ourServerBase;

	@Test
	public void testCreateAndRead() throws IOException {


		Patient pt = buildCareConnectFHIRPatient();
		String methodName = pt.getName().get(0).getFamily().get(0).getValue();
		IIdType id = ourClient.create().resource(pt).execute().getId();

		Patient pt2 = ourClient.read().resource(Patient.class).withId(id).execute();
		assertEquals(methodName, pt2.getName().get(0).getFamily().get(0).getValue());
	}

	@AfterClass
	public static void afterClass() throws Exception {
		ourServer.stop();
	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		/*
		 * This runs under maven, and I'm not sure how else to figure out the target directory from code..
		 */
		String path = ExampleServerIT.class.getClassLoader().getResource("HAPIJPA.properties").getPath();
		path = new File(path).getParent();
		path = new File(path).getParent();
		path = new File(path).getParent();

		ourLog.info("Project base path is: {}", path);

		ourPort = RandomServerPortProvider.findFreePort();
		ourServer = new Server(ourPort);

		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setDescriptor(path + "/src/main/webapp/WEB-INF/web.xml");
		webAppContext.setResourceBase(path + "/target/careconnect-server-Dstu2");
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
