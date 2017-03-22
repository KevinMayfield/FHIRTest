package uk.nhs.jorvik.E_RS.Processor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Binary;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.parser.IParser;

public class SupportingInformationAggregation implements AggregationStrategy {

	private FhirContext ctxhapiHL7Fhir;
	

	
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
			bundle = new Bundle();
			oldExchange = newExchange.copy();
			oldExchange.getIn().setHeader("FileRef", "6-BundleReferralRequest-"+oldExchange.getIn().getHeader("UBRN")+".xml");
			oldExchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/fhir+xml");
			// Referral Bundle has been stored as a copy
			Reader reader = new InputStreamReader(new ByteArrayInputStream ((byte[]) oldExchange.getProperty("MasterBundle")));
			IParser parser = ctxhapiHL7Fhir.newJsonParser();
			
			bundle = parser.parseResource(Bundle.class,reader);
		}
		else
		{
			try
			{
				Reader reader = new InputStreamReader(new ByteArrayInputStream ((byte[]) oldExchange.getIn().getBody(byte[].class)));
				IParser parser = null;
				if (oldExchange.getIn().getHeader(Exchange.CONTENT_TYPE).toString().contains("json"))
				{
					parser = ctxhapiHL7Fhir.newJsonParser();
				}
				else
				{
					parser = ctxhapiHL7Fhir.newXmlParser();
				}
				bundle = parser.parseResource(Bundle.class,reader);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		InputStream is = (InputStream) newExchange.getIn().getBody();
		try
		{
		      is.reset();
		      Reader reader = new InputStreamReader(new ByteArrayInputStream ((byte[]) newExchange.getIn().getBody(byte[].class)));
		      	
		      char[] arr = new char[8 * 1024];
		      StringBuilder buffer = new StringBuilder();
		      int numCharsRead;
		      while ((numCharsRead = reader.read(arr, 0, arr.length)) != -1) {
		          buffer.append(arr, 0, numCharsRead);
		      }
		      reader.close();
		
			
			Binary binary = new Binary();
			binary.setContentType(newExchange.getIn().getHeader(Exchange.CONTENT_TYPE).toString());
			binary.setId(newExchange.getIn().getHeader("BinaryId").toString());
			// This appears to work but not convinced it's valid base64
			binary.setContent(buffer.toString().getBytes());
			
			
			bundle.addEntry().setResource(binary);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		 if ((oldExchange.getIn().getHeader("_format") != null) && (oldExchange.getIn().getHeader("_format").toString().contains("json")))
	        {
	        	oldExchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json+fhir");
	        	oldExchange.getIn().setBody(ctxhapiHL7Fhir.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle).getBytes());
	        }
		 else
		 {
			 oldExchange.getIn().setBody(ctxhapiHL7Fhir.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle).getBytes());
		 }
		
		return oldExchange;
	}

}
