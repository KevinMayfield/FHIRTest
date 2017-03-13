package uk.nhs.jorvik.fhirTest.careConnectPatientTest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Meta;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.HumanName.NameUse;


public final class CareConnectPatientExamples {
	
	public static Patient customPatientExtensionV1()
	{
		Patient patient = buildCareConnectFHIRPatient();
		
		patient.addExtension()
			.setUrl("https://fhir.jorvik.nhs.uk/Extension/OnClinicalTrial")
			.setValue(new BooleanType(true));
		
		return patient;
	}
	
	public static Patient customPatientExtensionV2()
	{
		Patient patient = buildCareConnectFHIRPatient();
				
		Extension trialExtension = patient.addExtension()
			.setUrl("https://fhir.jorvik.nhs.uk/Extension/ClinicalTrial");
				
		trialExtension.addExtension()
			.setUrl("TrialID")
			.setValue(new Identifier().setSystem("http://fhir.jorvik.nhs.uk/ClinicalTrials").setValue("12"));
		
		trialExtension.addExtension("TrialDate", new Period().setStart(new Date()));
		
		return patient;
	}
	
	public static Patient customPatientExtensionV3()
	{
		Patient patient = buildCareConnectFHIRPatient();
		
		patient.addExtension()
			.setUrl("https://fhir.jorvik.nhs.uk/Extension/OnClinicalTrial")
			.setValue(new BooleanType(true));
		
		Extension trialExtension = patient.addExtension()
			.setUrl("https://fhir.jorvik.nhs.uk/Extension/ClinicalTrial");
				
		trialExtension.addExtension()
			.setUrl("TrialID")
			.setValue(new Identifier().setSystem("http://fhir.jorvik.nhs.uk/ClinicalTrials").setValue("12"));
		
		trialExtension.addExtension("TrialDate", new Period().setStart(new Date()));
		
		return patient;
	}
		
	public static Patient buildCareConnectFHIRPatient()
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			Patient patient = new Patient();
	        
			// Add profile reference
			patient.setMeta(new Meta().addProfile("https://fhir.leedsth.nhs.uk/Dstu2/StructureDefinition/LTHT-Patient-1"));
	        
			
	        Identifier nhsNumber = patient.addIdentifier();
	        nhsNumber
		    	.setSystem(new String("https://fhir.nhs.net/Id/nhs-number"))
		    	.setValue("9480431963");
	       	
	        CodeableConcept verificationStatusCode = new CodeableConcept();
	        verificationStatusCode
	        	.addCoding()
	        	.setSystem("https://fhir.hl7.org.uk/fhir/ValueSet/CareConnect-NhsNumberVerificationStatus")
	        	.setDisplay("Number present and verified")
	        	.setCode("01");
	        nhsNumber.addExtension()
	        		.setUrl("http://hl7.org.uk/CareConnect-NhsNumberVerificatnStatus-1-Extension.structuredefinition.xml")
	        		.setValue(verificationStatusCode);
	        		
	       	
	        
	        patient.addIdentifier()
      		.setSystem(new String("https://fhir.jorvik.nhs.uk/PAS/Patient"))
      		.setValue("9437718");
	        
	        patient.addName()
	        	.setUse(NameUse.USUAL)
	        	.setFamily("DUFFY")
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
	        patient.setGender(AdministrativeGender.FEMALE);
	        
	        Date birth;
			try {
				birth = dateFormat.parse("1926-03-31");
				patient.setBirthDate(birth);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			//
			
			patient.setManagingOrganization(new Reference("https://fhir.nhs.net/Id/ods-organization-code/B86675"));
			
			
			// CareConnect Patient Profile extensions
			
			CodeableConcept religionCode = new CodeableConcept();
			religionCode.addCoding()
				.setSystem("http://snomed.info/sct")
				.setDisplay("Druid, follower of religion")
				.setCode("428506007");
			patient.addExtension()
				.setUrl("http://hl7.org.uk/CareConnect-ReligiousAffiliation-1-Extension.structuredefinition.xml")
				.setValue(religionCode);
			
			CodeableConcept ethnicCode = new CodeableConcept();
			ethnicCode.addCoding()
				.setSystem("http://hl7.org.uk/fhir/ValueSet/CareConnect-EthnicCategory")
				.setDisplay("Other white European, European unspecified, European mixed")
				.setCode("CY");
			patient.addExtension()
				.setUrl("http://hl7.org.uk/CareConnect-EthnicCategory-1-Extension.structuredefinition.xml")
				.setValue(ethnicCode);
			
			
			
			return patient;
		}
	
	  /*
	   *  
	   *  HapiContext ctxhapihl7v2 = new DefaultHapiContext();
	    	ctxhapihl7v2.getParserConfiguration().setValidating(false);
	        		//HL7DataFormat hapihl7v2 = new HL7DataFormat();
	        ADT_A05 adt = buildPatientV2();
	        Parser parser = ctxhapihl7v2.getPipeParser();
	        String encodedMessage = parser.encode(adt);
	        template.sendBody("direct:startV2",encodedMessage);
	     */   
	   /*
	  public static ADT_A05 buildPatientV2() throws HL7Exception, IOException
		{
			//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			ADT_A05 adt = new ADT_A05();
			adt.initQuickstart("ADT", "A28", "P");
			          
	          // Populate the MSH Segment
	        MSH mshSegment = adt.getMSH();
	        mshSegment.getSendingApplication().getNamespaceID().setValue("TestSendingSystem");
	        mshSegment.getSequenceNumber().setValue("123");
	          
	          // Populate the PID Segment
	        PID pid = adt.getPID(); 
	        pid.getPatientName(0).getFamilyName().getSurname().setValue("May");
	        pid.getPatientName(0).getGivenName().setValue("Trees");
	        pid.getPatientName(1).getGivenName().setValue("Are");
	        pid.getPatientIdentifierList(0).getID().setValue("123456");
	        
	        //Date birth;
			
			//birth = dateFormat.parse("2003-07-23");
			TSComponentOne tm = pid.getDateTimeOfBirth()
				.getTimeOfAnEvent();
			tm.setValue("20030623");
			
		
	  
			        
			return adt;        
		}
	*/

}
