package uk.nhs.jorvik.fhirTest.E_RS;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;

import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import uk.nhs.jorvik.fhir.E_RS.javaconfig.E_RSApp;


@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = {E_RSApp.class}, loader = CamelSpringDelegatingTestContextLoader.class)
@MockEndpoints
public class eRSTest  {
	
	 @Produce(uri = "direct:startTestWorkflow")
     protected ProducerTemplate templateStartTestWorkflow;
		 
	 @EndpointInject(uri = "mock:resultProfessionalSessionPost")
	 protected MockEndpoint resultEndpointProfessionalSessionPost;
	 
	 @EndpointInject(uri = "mock:resultProfessionalSessionPut")
	 protected MockEndpoint resultEndpointProfessionalSessionPut;
	
	 @EndpointInject(uri = "mock:resultReferralRequestFetchWorklist")
	 protected MockEndpoint resultReferralRequestFetchWorklist;
	 
	 @EndpointInject(uri = "mock:resultReferralRequestGet")
	 protected MockEndpoint resultReferralRequestGet;
	 
	 @EndpointInject(uri = "mock:resultBinaryGet")
	 protected MockEndpoint resultBinaryGet;
	 
	 @EndpointInject(uri = "mock:resultAggregatedDocuments")
	 protected MockEndpoint resultAggregatedDocuments;
	// private static final Logger log = LoggerFactory.getLogger(eRSTest.class);
	

	 
	  @Test
	  public void testGetReferrals() throws Exception 
	  {
			String request = "{"
	    					+" \"typeInfo\": \"uk.nhs.ers.xapi.dto.v1.session.ProfessionalSession\" ,"
	    					+" \"token\": \"021600556514 x x x\" "
	  						+" }";
	        
			templateStartTestWorkflow.sendBody("direct:startTestWorkflow",request);
	        
			resultEndpointProfessionalSessionPost.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 201); 
			resultEndpointProfessionalSessionPost.assertIsSatisfied();
	        
	        
	        resultReferralRequestFetchWorklist.expectedHeaderReceived(Exchange.HTTP_RESPONSE_CODE, 200); 
  	       	resultReferralRequestFetchWorklist.assertIsSatisfied();
  	       	resultReferralRequestFetchWorklist.allMessages();
  	       	
  	       	resultAggregatedDocuments.allMessages();
  	       	resultAggregatedDocuments.assertIsSatisfied();
  	       		        
	  }	
	  
	
}
