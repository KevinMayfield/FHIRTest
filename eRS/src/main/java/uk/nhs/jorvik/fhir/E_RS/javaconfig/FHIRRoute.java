package uk.nhs.jorvik.fhir.E_RS.javaconfig;

import java.io.InputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.camel.util.jsse.TrustManagersParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.gemplus.gemauth.api.GATicket;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.model.dstu2.valueset.IssueTypeEnum;
import ca.uhn.fhir.parser.IParser;
import uk.nhs.jorvik.E_RS.Processor.SupportingInformationAggregation;
import uk.nhs.jorvik.E_RS.Processor.ValueSetProcessor;
import uk.nhs.jorvik.E_RS.Processor.BinaryGet;
import uk.nhs.jorvik.E_RS.Processor.Logoff;
import uk.nhs.jorvik.E_RS.Processor.Logon;
import uk.nhs.jorvik.E_RS.Processor.ReferralRequestSupportedInformationSplit;
import uk.nhs.jorvik.E_RS.Processor.ProfessionalSession;
import uk.nhs.jorvik.E_RS.Processor.ReferralRequestPost;
import uk.nhs.jorvik.E_RS.Processor.WorkFlowGet;
import uk.nhs.jorvik.E_RS.Processor.WorkFlowReferralSplit;


@Component
@PropertySource("classpath:ERS.properties")
public class FHIRRoute extends RouteBuilder {
	
	@Autowired
	protected Environment env;
	
	private static final FhirContext ctxhapiHL7Fhir = FhirContext.forDstu2();
	
	String ssoTicket = null;
	
	private class Headers implements Processor
	{
		@Override
		public void process(Exchange exchange) throws Exception {
			exchange.getIn().setHeader("XAPI_ASID",env.getProperty("ASID"));
			exchange.getIn().setHeader("XAPI_FQDN",env.getProperty("FQDN"));
		}
	}
	
	private class Rewind implements Processor
	{
    	// We reset the stream - only needed following a file output operation. This process can be removed if not using it.
		 public void process(Exchange exchange) throws Exception {

		    	InputStream is = (InputStream) exchange.getIn().getBody();
			    is.reset();
		   }
	}
	
	private class ReturnOperationOutcome implements Processor
	{
    	// We reset the stream - only needed following a file output operation. This process can be removed if not using it.
		 public void process(Exchange exchange) throws Exception {
			 	OperationOutcome outcome = new OperationOutcome();
			 	
			 	switch (exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString())
			 	{
			 		case "403":
			 			outcome.addIssue().setCode(IssueTypeEnum.LOGIN_REQUIRED);
			 			break;
			 		case "401":
			 			outcome.addIssue().setCode(IssueTypeEnum.FORBIDDEN);
			 			break;
			 		case "404":
			 			outcome.addIssue().setCode(IssueTypeEnum.NOT_FOUND);
		 				break;
			 		case "500":
			 			outcome.addIssue().setCode(IssueTypeEnum.EXCEPTION);
		 				break;
			 		case "200":
			 		case "204":
			 			outcome.addIssue().setCode(IssueTypeEnum.INFORMATIONAL_NOTE);
		 				break;
			 	}
			 	
			 	IParser parser = null;
			 	if ((exchange.getIn().getHeader("_format") != null) && (exchange.getIn().getHeader("_format").toString().contains("xml")))
		        {
			 		parser = ctxhapiHL7Fhir.newXmlParser();
		        }
			 	else if ((exchange.getIn().getHeader(Exchange.CONTENT_TYPE) != null) && (exchange.getIn().getHeader(Exchange.CONTENT_TYPE).toString().contains("xml")))
			 	{
			 		parser = ctxhapiHL7Fhir.newXmlParser();
			 	}
			 	else
			 	{
			 		parser = ctxhapiHL7Fhir.newJsonParser();
			 	}
			 	exchange.getIn().setBody(parser.setPrettyPrint(true).encodeResourceToString(outcome));
		   }
	}
	
	@Override
	public void configure() throws Exception {
		
		// Simple connection to smartcard to ensure the user has been authenticated
		
		GATicket gaticket = null;
		
		
	    try {
	      log.info("TEST - MAIN: Starting.");
	      gaticket = new GATicket();
	      gaticket.setIsDebug(true);
	      ssoTicket = gaticket.getTicket();
	      log.info(("TEST - MAIN: GATicket -> " + ssoTicket));
	      //Long ssoTicketCode = gaticket.getLastError();
	    }
	    catch (Throwable err) {
	      err.printStackTrace();
	      if (null!=gaticket)
	    	  log.info(("error code: " + gaticket.getLastError()));
	    }
	    		
	    // So this url should work 
	    // http://localhost:8181/eRS/dstu2/ReferralRequest/000000090007
	    // http://localhost:8181/eRS/dstu2/ReferralRequest?_id=000000090007&_revinclude=*
	    
	    Endpoint httpsEndpoint = setupSSLConext(getContext());  
	   	    
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
		Logon logon = new Logon(env, ssoTicket);
		
		ReferralRequestPost referralRequestPost = new ReferralRequestPost(ctxhapiHL7Fhir);
		ValueSetProcessor valueSetProcessor = new ValueSetProcessor(ctxhapiHL7Fhir);
		
		SupportingInformationAggregation binaryAggregation = new SupportingInformationAggregation(ctxhapiHL7Fhir);
	
		// This section defines the rest endpoints
		rest("/")
	    	.description("eRS")
	    	.get("/{version}/Logon")
		    	.description("Interface to logon to eRS")
				.responseMessage().code(200).message("OK").endResponseMessage()
				.route()
					.routeId("eRS Logon Get")
					.to("direct:Logon")
				.endRest()
	    	.delete("/{version}/Logoff")
		    	.description("Interface to logoff eRS")
				.responseMessage().code(200).message("OK").endResponseMessage()
				.route()
					.routeId("eRS Logoff Get")
					.to("direct:Logoff")
				.endRest()
	    	.get("/{version}/Binary/{attachmentId}")
		    	.description("eRS Binary Get")
		    	.param().type(RestParamType.path).name("attachmentId").required(true).description("Attachment Id").dataType("string").endParam()
		    	.param().type(RestParamType.query).name("UBRN").required(false).description("UBRN").dataType("string").endParam()
				.responseMessage().code(200).message("OK").endResponseMessage()
				.route()
					.routeId("eRS Binary Get")
					.process(new Processor() {
				        public void process(Exchange exchange) throws Exception {
				            exchange.getIn().setHeader(Exchange.HTTP_PATH, "Binary/"+exchange.getIn().getHeader("attachmentId"));
				        }
					})
					.to("direct:eRSCall")
					.choice()
						.when(header(Exchange.HTTP_RESPONSE_CODE).isNotEqualTo("200"))
							.process(new ReturnOperationOutcome())
						.otherwise()
							.process(new Rewind() )
					.end()
					.to("mock:resultBinaryGet")
				.endRest()
	    	.get("/{version}/Valueset/{valueSetId}")
		    	.description("eRS ValueSet Get")
		    	.param().type(RestParamType.path).name("valueSetId").required(true).description("Attachment Id").dataType("string").endParam()
		    	.param().type(RestParamType.query).required(false).defaultValue("xml").allowableValues("xml","json").name("_format").description("Format of the FHIR response: json or xml").dataType("string").endParam()
				.responseMessage().code(200).message("OK").endResponseMessage()
				.route()
					.routeId("eRS ValueSet Get")
					.process(new Processor() {
				        public void process(Exchange exchange) throws Exception {
				            exchange.getIn().setHeader(Exchange.HTTP_PATH, "ValueSet/"+exchange.getIn().getHeader("valueSetId"));
				            exchange.getIn().setHeader("FileRef","9-ValueSet.json");
				        }
					})
					.to("direct:eRSCall")
					.choice()
						.when(header(Exchange.HTTP_RESPONSE_CODE).isNotEqualTo("200"))
							.process(new ReturnOperationOutcome())
						.otherwise()
							.process(valueSetProcessor)
					.end()
					.to("mock:resultValueSetGet")
				.endRest()
	    	.get("/{version}/ReferralRequest/{_id}")
				.description("Interface to retrieve Referral")
				.param().type(RestParamType.path).name("_id").required(true).description("Resource Id e.g. ").dataType("string").endParam()
				.param().type(RestParamType.query).required(false).defaultValue("xml").allowableValues("xml","json").name("_format").description("Format of the FHIR response: json or xml").dataType("string").endParam()
				.responseMessage().code(200).message("OK").endResponseMessage()
				.route()
					.routeId("eRS ReferralRequest Get")
					.process(new Processor() {
				        public void process(Exchange exchange) throws Exception {
				            exchange.getIn().setBody("ReferralRequest/"+exchange.getIn().getHeader("_id"));
				        }
					})
					.to("direct:ReferralRequestGet")
				.endRest()
			 .get("/{version}/ReferralRequest")
				.description("Interface to query the eRS")
				.param().type(RestParamType.query).name("_id").required(true).description("Resource Id e.g. ").dataType("string").endParam()
				.param().type(RestParamType.query).name("_revinclude").required(false).description("Include referenced resources ").dataType("string").endParam()
				.param().type(RestParamType.query).name("status").required(false).description("Defaults to request and is hard coded  ").dataType("string").endParam()
				.param().type(RestParamType.query).required(false).defaultValue("xml").allowableValues("xml","json").name("_format").description("Format of the FHIR response: json or xml").dataType("string").endParam()
				.responseMessage().code(200).message("OK").endResponseMessage()
				.route()
					.routeId("eRS ReferralRequest Search")
					.process(new Processor() {
				        public void process(Exchange exchange) throws Exception {
				            exchange.getIn().setBody("ReferralRequest/"+exchange.getIn().getHeader("_id"));
				        }
					})
					.choice()
						.when(header("status").isNotNull())
							.process(workFlowGet)
							.to("direct:eRSCall")
							.process(new Rewind() )
						.otherwise()
							.to("direct:ReferralRequestGet")
					.endChoice()
				.endRest();
		    
		
		from("direct:startTestWorkflow")
			.routeId("startTestWorkflow")
			.to("direct:Logon")
			.process(new Processor() {
		        public void process(Exchange exchange) throws Exception {
		            exchange.getIn().setHeader("_revinclude","*");
		        }
			})
			.process(workFlowGet)
			.to("direct:eRSCall")
			.to("mock:resultReferralRequestFetchWorklist")
			// Convert stream to String
			.process(workFlowReferralSplit)
			// The response from the referral request is split into separate messages - no combination back
			.split(body().tokenize(","))
				.to("direct:ReferralRequestGet")
				// Output results
				.to("direct:PostBundleReferralRequest")
			.end()
			.to("direct:Logoff");
			
		
		// Calls eRS to establish a session
		from("direct:Logon")
			.routeId("eRS Logon")
			.process(logon)
			.to("direct:eRSCall")
			.to("mock:resultProfessionalSessionPost")
			// Amend the message to add permission request
			.process(session)
			.to("direct:eRSCall")
			.to("mock:resultProfessionalSessionPut")
			.process(new ReturnOperationOutcome());
			
		from("direct:Logoff")
			.routeId("eRS Logoff")
			.process(new Logoff())
			.to("direct:eRSCall")
			.to("mock:resultProfessionalSessionDelete")
			.process(new ReturnOperationOutcome());
			// Add in a FHIR Operationoutcome. Currently returns a null body
		
		
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
			.choice()
				.when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo("200"))
					.process(referralRequestPost)
					.choice()
						.when(header("_revinclude").isNotNull())
							// revinclude adds all referenced resources to the returned search. (i.e. all the documents relating to the referral)
							.to("direct:GetAttachedDocuments")
							
						.otherwise()
							.process(new Rewind())
					.endChoice()
				.otherwise()
					.process(new ReturnOperationOutcome())
			.endChoice();
				
		
		from("direct:GetAttachedDocuments")
			.routeId("GetAttachedDocuments")
			.process(binaryGetSplit)
			.split(body().tokenize(","),binaryAggregation)
				// Build the retrieval call
				.process(binaryGet)
				.to("direct:eRSCall")
				.to("mock:resultBinaryGet")
				.choice()
					.when(header(Exchange.HTTP_RESPONSE_CODE).isNotEqualTo("200"))
						.process(new ReturnOperationOutcome())
				.endChoice()
			.end()
			.choice()
				.when(header(Exchange.HTTP_RESPONSE_CODE).isNotEqualTo("200"))
					.process(new ReturnOperationOutcome())
			.endChoice();
			
		from("direct:eRSCall")
			.routeId("eRS Call")
			.process(new Headers())
			.to("log:uk.nhs.jorvik.fhirTest.E_RS.PreHttp?showAll=true&multiline=true&level=INFO")
			.to(httpsEndpoint)
			.to("log:uk.nhs.jorvik.fhirTest.E_RS.PostHttp?showAll=true&multiline=true&level=INFO")
			.process(new Processor() {
				public void process(Exchange exchange) throws Exception {
					// To cope with null bodies. Can be removed if not using file 
					if (exchange.getIn().getBody() == null)
					{
						exchange.getIn().setBody("");
					}
				}
			})
			// File output is for development only and can be removed. Also remove Rewind() process calls
			.to("file:C://test//e-RS?fileName=$simple{date:now:yyyyMMdd}-${id}-${in.header.FileRef}");
			//
		
		from("direct:PostBundleReferralRequest")
			.routeId("PostBundleReferralRequest")
			.process(new Processor() {
			    public void process(Exchange exchange) throws Exception {
			        exchange.getIn().setHeader(Exchange.HTTP_PATH, "ReferralRequest");
			        exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
			    }})
			.to(env.getProperty("tie.endpoint"))
			// File output is for development only and can be removed. Also remove Rewind() process calls
			.to("file:C://test//e-RS?fileName=$simple{date:now:yyyyMMdd}-${id}-${in.header.FileRef}")
			//
			.to("mock:resultAggregatedDocuments");
		
  	}
	
	
	
	
	private Endpoint setupSSLConext(CamelContext camelContext) throws Exception {
		HttpComponent httpComponent = null; 
		if (env.getProperty("secure").equals("true"))
		{
	        KeyStoreParameters keyStoreParameters = new KeyStoreParameters();
	        // Change this path to point to your truststore/keystore as jks files
	        keyStoreParameters.setResource(env.getProperty("keystore.resource"));
	        keyStoreParameters.setPassword(env.getProperty("keystore.password"));
	
	        KeyManagersParameters keyManagersParameters = new KeyManagersParameters();
	        keyManagersParameters.setKeyStore(keyStoreParameters);
	        keyManagersParameters.setKeyPassword(env.getProperty("keymanager.password"));
	
	        TrustManagersParameters trustManagersParameters = new TrustManagersParameters();
	        trustManagersParameters.setKeyStore(keyStoreParameters);
	
	        SSLContextParameters sslContextParameters = new SSLContextParameters();
	        sslContextParameters.setKeyManagers(keyManagersParameters);
	        sslContextParameters.setTrustManagers(trustManagersParameters);
	
	        httpComponent = camelContext.getComponent("https4", HttpComponent.class);
	        httpComponent.setSslContextParameters(sslContextParameters);
	        //This is important to make your cert skip CN/Hostname checks
	       // httpComponent.setX509HostnameVerifier(new AllowAllHostnameVerifier());
		}
        else
        {
        	httpComponent = camelContext.getComponent("http4", HttpComponent.class);
	        
        }

        return httpComponent.createEndpoint(env.getProperty("ers.endpoint"));
      
    }
	
}
