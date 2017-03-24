package uk.nhs.jorvik.ers.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class WorkFlowGet implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// The security headers should be present on the exchange, so modifying this for next request 
		exchange.getIn().setHeader(Exchange.HTTP_PATH,"ReferralRequest/$ers.fetchworklist");
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/json+fhir");
		exchange.getIn().setHeader("FileRef","3-ReferralRequestFetchWorklistPost.json");
		String request = "{\"resourceType\":\"Parameters\",\"meta\":{\"profile\":[\"http://fhir.nhs.net/OperationDefinition/ers-fetchworklist-operation-1\"]},\"parameter\":[{\"name\":\"listType\",\"valueCodeableConcept\":{\"coding\":[{\"system\":\"http://fhir.nhs.net/ValueSet/ers-referrallistselector-1\",\"code\":\"REFERRALS_FOR_REVIEW\"}]}}]}";
		exchange.getIn().setBody(request);

	}

}
