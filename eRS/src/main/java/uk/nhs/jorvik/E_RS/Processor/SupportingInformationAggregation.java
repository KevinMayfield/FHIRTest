package uk.nhs.jorvik.E_RS.Processor;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Binary;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.parser.IParser;
import uk.nhs.jorvik.fhirTest.E_RS.eRSTest;

public class SupportingInformationAggregation implements AggregationStrategy {

	private FhirContext ctxhapiHL7Fhir;
	
	private static final Logger log = LoggerFactory.getLogger(eRSTest.class);
	
	public SupportingInformationAggregation(FhirContext ctxhapiHL7Fhir)
	{
		this.ctxhapiHL7Fhir = ctxhapiHL7Fhir;
	}
	
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		
		// Note we are changing from Json format to XML here
		Bundle bundle = null;
		
		if (oldExchange == null)
		{
			log.info("Empty exchange - "+ newExchange.getIn().getHeader("UBRN"));
			bundle = new Bundle();
			oldExchange = newExchange.copy();
			// Referral Bundle has been stored as a copy
			Reader reader = new InputStreamReader(new ByteArrayInputStream ((byte[]) oldExchange.getProperty("MasterBundle")));
			IParser parser = ctxhapiHL7Fhir.newJsonParser();
			
			bundle = parser.parseResource(Bundle.class,reader);
		}
		else
		{
			try
			{
				log.info("Non Empty exchange- "+ newExchange.getIn().getHeader("UBRN"));
				Reader reader = new InputStreamReader(new ByteArrayInputStream ((byte[]) oldExchange.getIn().getBody(byte[].class)));
				IParser parser = ctxhapiHL7Fhir.newXmlParser();
				
				bundle = parser.parseResource(Bundle.class,reader);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		byte[] payload = newExchange.getIn().getBody(byte[].class);		
		Binary binary = new Binary();
		binary.setContentType(newExchange.getIn().getHeader(Exchange.CONTENT_TYPE).toString());
		binary.setContent(payload);
		
		
		bundle.addEntry().setResource(binary);
		
		oldExchange.getIn().setBody(ctxhapiHL7Fhir.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle).getBytes());
		
		return oldExchange;
	}

}
