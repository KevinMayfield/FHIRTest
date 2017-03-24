package uk.nhs.jorvik.ers.processor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.AttachmentDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Bundle.Entry;
import ca.uhn.fhir.model.dstu2.resource.DocumentReference;
import ca.uhn.fhir.model.dstu2.resource.ReferralRequest;
import ca.uhn.fhir.parser.IParser;

public class ReferralRequestSupportedInformationSplit implements Processor {

	private FhirContext ctxhapiHL7Fhir;
	
	
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
		IParser parser = null;
		if (exchange.getIn().getHeader(Exchange.CONTENT_TYPE) != null && exchange.getIn().getHeader(Exchange.CONTENT_TYPE).toString().contains("xml"))
		{
			parser = ctxhapiHL7Fhir.newXmlParser();	
		}
		else
		{
			parser = ctxhapiHL7Fhir.newJsonParser();
		}
		
		// Build a simple list of documents to retrieve
		
		// Begin converting the ReferralRequest into a ReferralRequest Bundle
		
		Bundle bundle = new Bundle();
		Entry referralEntry = bundle.addEntry();
		Integer docCount=1;
		
		ReferralRequest referral = parser.parseResource(ReferralRequest.class,reader);
		String documents="Binary/$ers.generateCRI~"+referral.getId().getIdPart()+"-ReferralCRI.pdf~UBRN="+referral.getId().getIdPart()+"~doc-1,";
		
		referral
			.addIdentifier()
			.setSystem("http://fhir.nhs.net/ers/ReferralRequest")
			.setValue(referral.getId().getIdPart());
		
		// Create a resource for the CRI document 
				
		DocumentReference criDocumentReference = new DocumentReference();
		criDocumentReference.setId("doc-1");
		criDocumentReference.addIdentifier()
			.setSystem("http://fhir.nhs.net/ers/Binary")
			.setValue("UBRN-"+referral.getId().getIdPart());
		CodeableConceptDt typeCode = new CodeableConceptDt();
		typeCode.addCoding()
			.setCode("25611000000107")
			.setSystem("http://snomed.info/sct")
			.setDisplay("Referral letter");
		criDocumentReference.setType(typeCode);
		criDocumentReference.setSubject(referral.getPatient());
		criDocumentReference.setDescription("Clinical Referral Information");
		AttachmentDt attachment = new AttachmentDt();
		attachment
			.setContentType("application/pdf")
			.setTitle(referral.getId().getIdPart()+"-ReferralCRI.pdf")
			.setUrl("Binary/doc-1");
		
		criDocumentReference.addContent().setAttachment(attachment);
		
			
		
		bundle.addEntry().setResource(criDocumentReference);
		
		for (ResourceReferenceDt support : referral.getSupportingInformation())
		{
	
			for (int c=0; c<referral.getContained().getContainedResources().size();c++)
			{
				if (referral.getContained().getContainedResources().get(c).getResourceName().equals("DocumentReference"))
				{
					bundle.addEntry().setResource(referral.getContained().getContainedResources().get(c));
	
					
					if (referral.getContained().getContainedResources().get(c).getId().getIdPart().equals(support.getReference().getIdPart()))								
					{
						docCount++;
						DocumentReference docRef = (DocumentReference) referral.getContained().getContainedResources().get(c);
						docRef.setId("doc-"+docCount.toString());
						docRef.setSubject(referral.getPatient());
						docRef.setCreatedWithSecondsPrecision(docRef.getContent().get(0).getAttachment().getCreation());
						String[] parts = docRef.getContent().get(0).getAttachment().getUrl().split("/");
						docRef.addIdentifier()
							.setSystem("http://fhir.nhs.net/ers/Binary")
							.setValue(parts[1]);
						documents = documents + docRef.getContent().get(0).getAttachment().getUrl()+"~"+referral.getId().getIdPart()+"-"+docRef.getContent().get(0).getAttachment().getTitle()+"~~doc-"+docCount.toString()+",";
						// Now amend the Referral Request with the new references
						docRef.getContent().get(0).getAttachment().setUrl("Binary/doc-"+docCount.toString());
						support.setReference(docRef.getId().getIdPart());
						
						
					}
				}
			}
		}
				
		referral.getContained().setContainedResources(null);	
		referralEntry.setResource(referral);
				
		exchange.getIn().setHeader("UBRN",referral.getId().getIdPart() );
		exchange.getIn().setBody(documents);
		parser = ctxhapiHL7Fhir.newJsonParser();
		exchange.setProperty("MasterBundle", parser.encodeResourceToString(bundle).getBytes());
	}

}
