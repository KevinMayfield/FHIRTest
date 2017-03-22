package uk.nhs.jorvik.E_RS.Processor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.ReferralRequest;
import ca.uhn.fhir.parser.IParser;

public class ReferralRequestPost implements Processor {

	private FhirContext ctxhapiHL7Fhir;
		
	public ReferralRequestPost(FhirContext ctxhapiHL7Fhir)
	{
		this.ctxhapiHL7Fhir = ctxhapiHL7Fhir;
	}
	
	
	@Override
	public void process(Exchange exchange) throws Exception {
	        if ((exchange.getIn().getHeader("_format") != null) && (exchange.getIn().getHeader("_format").toString().contains("xml")))
	        {
	        	InputStream is = (InputStream) exchange.getIn().getBody();
	    		is.reset();
	    		Reader reader = new InputStreamReader(new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class)));
	    		IParser parser = ctxhapiHL7Fhir.newJsonParser();
	    		
	    		ReferralRequest referral = parser.parseResource(ReferralRequest.class,reader);
	    		exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/xml+fhir");
	    		exchange.getIn().setBody(new ByteArrayInputStream(ctxhapiHL7Fhir.newXmlParser().setPrettyPrint(true).encodeResourceToString(referral).getBytes()));
	        }
	    }

}
