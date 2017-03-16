package uk.nhs.jorvik.E_RS.Processor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Bundle.Entry;
import ca.uhn.fhir.model.dstu2.resource.DocumentReference;
import ca.uhn.fhir.model.dstu2.resource.ReferralRequest;
import ca.uhn.fhir.parser.IParser;
import uk.nhs.jorvik.fhirTest.E_RS.eRSTest;

public class ReferralRequestSupportedInformationSplit implements Processor {

	private FhirContext ctxhapiHL7Fhir;
	
	private static final Logger log = LoggerFactory.getLogger(eRSTest.class);
	
	public ReferralRequestSupportedInformationSplit(FhirContext ctxhapiHL7Fhir)
	{
		this.ctxhapiHL7Fhir = ctxhapiHL7Fhir;
	}
	@Override
	public void process(Exchange exchange) throws Exception {
		
    	
    	// Need to reset the stream. 
		InputStream is = (InputStream) exchange.getIn().getBody();
		is.reset();
		Reader reader = new InputStreamReader(new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class)));
		IParser parser = ctxhapiHL7Fhir.newJsonParser();
		
		// Build a simple list of documents to retrieve
		
		// Begin converting the ReferralRequest into a ReferralRequest Bundle
		
		Bundle bundle = new Bundle();
		Entry referralEntry = bundle.addEntry();
		
		ReferralRequest referral = parser.parseResource(ReferralRequest.class,reader);
		String documents="Binary/$ers.generateCRI~"+referral.getId().getIdPart()+"-ReferralCRI.pdf~UBRN="+referral.getId().getIdPart()+",";
		for (ResourceReferenceDt support : referral.getSupportingInformation())
		{
			log.info("ref support = "+support.getReference().getIdPart());
			for (int c=0; c<referral.getContained().getContainedResources().size();c++)
			{
				bundle.addEntry().setResource(referral.getContained().getContainedResources().get(c));
				log.info("ref contained = "+referral.getContained().getContainedResources().get(c).getId().getIdPart());
				
				if (referral.getContained().getContainedResources().get(c).getId().getIdPart().equals(support.getReference().getIdPart()))								
				{
					DocumentReference docRef = (DocumentReference) referral.getContained().getContainedResources().get(c);
					documents = documents + docRef.getContent().get(0).getAttachment().getUrl()+"~"+referral.getId().getIdPart()+"-"+docRef.getContent().get(0).getAttachment().getTitle()+",";
				}
			}
		}
				
		referral.getContained().setContainedResources(null);	
		referralEntry.setResource(referral);
				
		exchange.getIn().setHeader("UBRN",referral.getId().getIdPart() );
		exchange.getIn().setBody(documents);
		
		parser = ctxhapiHL7Fhir.newJsonParser();
		//exchange.setOut(exchange.getIn().copy());
		//exchange.getOut().setBody(parser.encodeResourceToString(bundle).getBytes());
		exchange.setProperty("MasterBundle", parser.encodeResourceToString(bundle).getBytes());
	}

}
