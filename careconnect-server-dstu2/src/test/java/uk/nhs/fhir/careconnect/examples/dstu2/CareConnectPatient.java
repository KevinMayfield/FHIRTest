package uk.nhs.fhir.careconnect.examples.dstu2;


import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;
import ca.uhn.fhir.model.primitive.DateDt;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public final class CareConnectPatient {

		
	public static Patient buildCareConnectFHIRPatient()
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			Patient patient = new Patient();
	        
			// Add profile reference - where in dstu2??
			//patient.setMeta(new Meta().addProfile("https://fhir.leedsth.nhs.uk/Dstu2/StructureDefinition/LTHT-Patient-1"));
	        
			
	        IdentifierDt nhsNumber = patient.addIdentifier();
	        nhsNumber
		    	.setSystem(new String("https://fhir.nhs.net/Id/nhs-number"))
		    	.setValue("9480431963");
	       	
	        CodeableConceptDt verificationStatusCode = new CodeableConceptDt();
	        verificationStatusCode
	        	.addCoding()
	        	.setSystem("https://fhir.hl7.org.uk/fhir/ValueSet/CareConnect-NhsNumberVerificationStatus")
	        	.setDisplay("Number present and verified")
	        	.setCode("01");

	        ExtensionDt verificationStatus = new ExtensionDt()
					.setUrl("http://hl7.org.uk/CareConnect-NhsNumberVerificatnStatus-1-Extension.structuredefinition.xml")
					.setValue(verificationStatusCode);
	        nhsNumber.addUndeclaredExtension(verificationStatus);

	        		
	       	
	        
	        patient.addIdentifier()
      		.setSystem(new String("https://fhir.jorvik.nhs.uk/PAS/Patient"))
      		.setValue("9437718");
	        
	        patient.addName()
	        	.setUse(NameUseEnum.USUAL)
	        	.addFamily("DUFFY")
	        	.addGiven("Gideon")
	        	.addGiven("Brian")
	        	.addPrefix("Mr");
	        
	        patient.addAddress()
	        	.addLine("1 CHURCH SQUARE")
	        	.addLine("Garforth")
	        	.setCity("LEEDS")
	        	.setPostalCode("LS25 1JF");
	       
	        
	        patient.addContact().addRelationship()
	    		.addCoding()
	    			.setCode("01")
	    			.setDisplay("Spouse")
	    			.setSystem("https://fhir.hl7.org.uk/fhir/ValueSet/CareConnect-PersonRelationshipType");
	        
	        // Not CareConnect compliant
	        patient.setGender(AdministrativeGenderEnum.MALE);
	        
	        Date birth;
			try {
				birth = dateFormat.parse("1926-03-31");
				patient.setBirthDate(new DateDt(birth));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			//
			
			//patient.setManagingOrganization(new ResourceReferenceDt("https://fhir.nhs.net/Id/ods-organization-code/B86675"));
			
			
			// CareConnect Patient Profile extensions
			
			CodeableConceptDt religionCode = new CodeableConceptDt();
			religionCode.addCoding()
				.setSystem("http://snomed.info/sct")
				.setDisplay("Druid, follower of religion")
				.setCode("428506007");

			ExtensionDt religion = new ExtensionDt()
					.setUrl("http://hl7.org.uk/CareConnect-ReligiousAffiliation-1-Extension.structuredefinition.xml")
					.setValue(religionCode);
			patient.addUndeclaredExtension(religion);

			
			CodeableConceptDt ethnicCode = new CodeableConceptDt();
			ethnicCode.addCoding()
				.setSystem("http://hl7.org.uk/fhir/ValueSet/CareConnect-EthnicCategory")
				.setDisplay("Other white European, European unspecified, European mixed")
				.setCode("CY");

			ExtensionDt ethnic = new ExtensionDt()
					.setUrl("http://hl7.org.uk/CareConnect-EthnicCategory-1-Extension.structuredefinition.xml")
					.setValue(ethnicCode);
			patient.addUndeclaredExtension(ethnic);

			
			
			
			return patient;
		}
	


}
