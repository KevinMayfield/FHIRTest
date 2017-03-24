package uk.nhs.jorvik.ers.processor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.ValueSet;
import ca.uhn.fhir.parser.IParser;

public class ValueSetProcessor implements Processor {

	private FhirContext ctxhapiHL7Fhir;
	
	public ValueSetProcessor(FhirContext ctxhapiHL7Fhir)
	{
		this.ctxhapiHL7Fhir = ctxhapiHL7Fhir;
	}
	
	@Override
	
	    public void process(Exchange exchange) throws Exception {
	    	// We reset the stream - only needed following a file output operation. This process can be removed if not using it.
	    	InputStream is = (InputStream) exchange.getIn().getBody();
		    is.reset();
		    if ((exchange.getIn().getHeader("_format") != null) && (exchange.getIn().getHeader("_format").toString().contains("xml")))
	        {
	        	Reader reader = new InputStreamReader(new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class)));
	    		IParser parser = ctxhapiHL7Fhir.newJsonParser();
	    		
	    		ValueSet valueSet = parser.parseResource(ValueSet.class,reader);
	    		exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/xml+fhir");
	    		exchange.getIn().setBody(new ByteArrayInputStream(ctxhapiHL7Fhir.newXmlParser().setPrettyPrint(true).encodeResourceToString(valueSet).getBytes()));
	        }
	    }

}
