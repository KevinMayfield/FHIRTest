package uk.nhs.jorvik.fhirTest.careConnectCompositionTest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.hl7.fhir.dstu3.model.AllergyIntolerance.AllergyIntoleranceCertainty;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Composition.CompositionStatus;
import org.hl7.fhir.dstu3.model.Composition.DocumentConfidentiality;
import org.hl7.fhir.dstu3.model.Composition.SectionComponent;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Meta;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Flag;
import org.hl7.fhir.dstu3.model.Flag.FlagStatus;
import org.hl7.fhir.dstu3.model.HumanName.NameUse;


public final class CareConnectExamples {
	
	public static Bundle buildCareConnectFHIRCompositionBundleV1()
	{
		Bundle compositionBundle = new Bundle();
		
		compositionBundle.setId("bundleTest1");
		compositionBundle.setType(BundleType.DOCUMENT);
		
		// Composition
		
		Composition composition = CareConnectExamples.buildCareConnectFHIRCompositionV1();
		composition.setSubject(new Reference("#pat"));
		compositionBundle.addEntry()
			.setResource(composition);
		
		// Patient
		
		Patient patient = CareConnectExamples.buildCareConnectFHIRPatient();
		patient.setId("#pat");
		CodeableConcept patientSectionCode = new CodeableConcept();
		patientSectionCode.addCoding()
			.setSystem("http://snomed.info/sct")
			.setCode("886731000000109")
			.setDisplay("Patient demographics (record artifact)");
		
		composition.addSection()
			// .setText(value) an exercise for the reader
			.setCode(patientSectionCode)
			.addEntry(new Reference("#pat"));
		compositionBundle.addEntry()
			.setResource(patient);
		
		// Alerts / Flags
		
		CodeableConcept alertSectionCode = new CodeableConcept();
		alertSectionCode.addCoding()
			.setSystem("http://snomed.info/sct")
			.setCode("886931000000107")
			.setDisplay("alerts (record artifact)");
		SectionComponent alertSectionComponent = composition.addSection().setCode(alertSectionCode);
		for (Flag flag : CareConnectExamples.buildCareConnectFHIRAlertsV1())
		{
			flag.setId("#flag-"+flag.getId());
			alertSectionComponent.addEntry(new Reference(flag.getId()));
			if (flag.getSubject() != null)
			{
				// Change to resource that is within the bundle
				flag.setSubject(new Reference("#pat"));
			}
			//Add the resource to the bundle
			compositionBundle.addEntry().setResource(flag);
		}
		
		// Allergy
		
		CodeableConcept allergySectionCode = new CodeableConcept();
		allergySectionCode.addCoding()
			.setSystem("http://snomed.info/sct")
			.setCode("886921000000105")
			.setDisplay("Allergies and adverse reactions");
		SectionComponent allergySectionComponent = composition.addSection().setCode(allergySectionCode);
		for (AllergyIntolerance allergy : CareConnectExamples.buildCareConnectFHIRAllergyIntoleranceV1())
		{
			allergy.setId("#allergy-"+allergy.getId());
			allergySectionComponent.addEntry(new Reference(allergy.getId()));
			if (allergy.getPatient() != null)
			{
				// Change to resource that is within the bundle
				allergy.setPatient(new Reference("#pat"));
			}
			//Add the resource to the bundle
			compositionBundle.addEntry().setResource(allergy);
		}

	
		return compositionBundle;
	}
	
	public static List<AllergyIntolerance> buildCareConnectFHIRAllergyIntoleranceV1()
	{
		List<AllergyIntolerance> listAllergyIntolerance = new ArrayList<AllergyIntolerance>();
		
		AllergyIntolerance allergy = new AllergyIntolerance();
		
		Meta meta = new Meta();
		meta.addProfile("https://fhir.hl7.org.uk/CareConnect-AllergyIntolerance-1.structuredefinition.xml");
		allergy.setMeta(meta);
		
		allergy.setId("atest1");
		allergy.setPatient(new Reference("https://fhir.nhs.net/Id/nhs-number/9480431963"));
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2017);
		cal.set(Calendar.MONTH, Calendar.FEBRUARY);
		cal.set(Calendar.DAY_OF_MONTH, 18);
		allergy.setAssertedDate(cal.getTime());
		
		CodeableConcept substanceCode = new CodeableConcept();
		substanceCode.addCoding()
			.setSystem("http://snomed.info/sct")
			.setCode("311434007")
			.setDisplay("Polyurethane fumes (substance)");
		allergy.addReaction()
			.setSubstance(substanceCode)
			.setCertainty(AllergyIntoleranceCertainty.LIKELY)
			.setDescription("Came out with a severe rash after the Garforth Velo Club peloton passed by the Podgers.");
		
		listAllergyIntolerance.add(allergy);
		
		return listAllergyIntolerance;
	}
	
	public static List<Flag> buildCareConnectFHIRAlertsV1()
	{
		List<Flag> listFlag = new ArrayList<Flag>();
		
		Flag flag1 = new Flag();
		Meta meta1 = new Meta();
		meta1.addProfile("https://fhir.hl7.org.uk/CareConnect-Flag-1.structuredefinition.xml");
		flag1.setMeta(meta1);
		flag1.setId("ftest1");
		flag1.setStatus(FlagStatus.ACTIVE);
		flag1.setSubject(new Reference("https://fhir.nhs.net/Id/nhs-number/9480431963"));
		CodeableConcept code1 = new CodeableConcept();
		code1.addCoding()
			.setSystem("http://snomed.info/sct")
			.setCode("2622007")
			.setDisplay("Infected ulcer of skin (disorder)");
		flag1.setCode(code1);
		listFlag.add(flag1);
		
		Flag flag2 = new Flag();
		Meta meta2 = new Meta();
		meta2.addProfile("https://fhir.hl7.org.uk/CareConnect-Flag-1.structuredefinition.xml");
		flag2.setMeta(meta2);
		flag2.setId("ftest2");
		flag2.setStatus(FlagStatus.ACTIVE);
		flag2.setSubject(new Reference("https://fhir.nhs.net/Id/nhs-number/9480431963"));
		CodeableConcept code2 = new CodeableConcept();
		code2.addCoding()
			.setSystem("http://snomed.info/sct")
			.setCode("1816003")
			.setDisplay("Panic disorder with agoraphobia, severe agoraphobic avoidance AND mild panic attacks (disorder)");
		flag2.setCode(code2);
		listFlag.add(flag2);
	
		return listFlag;
	}
	
	public static Composition buildCareConnectFHIRCompositionV1()
	{
		Composition careRecord = new Composition();
		
		// Add profile the resource is based on 
		Meta meta = new Meta();
		meta.addProfile("https://fhir.leedsth.nhs.uk/Dstu2/StructureDefinition/LTHT-Leeds-Care-Record-Composition-2");
		careRecord.setMeta(meta);
		
		careRecord.setId("test1");
		careRecord.setDate(new Date());
		
		CodeableConcept type = new CodeableConcept();
		type.addCoding()
			.setSystem("http://snomed.info/sct")
			.setCode("425173008")
			.setDisplay("record extract (record artifact)");
		careRecord.setType(type);
		
		CodeableConcept class_ = new CodeableConcept();
		class_.addCoding()
			.setSystem("http://snomed.info/sct")
			.setCode("708168004")
			.setDisplay("Mental health service");
		careRecord.setClass_(class_);
		
		careRecord.setTitle("Mental Health Record Extract");
		careRecord.setStatus(CompositionStatus.FINAL);
		// A virtual reference based on patient's NHS Number
		careRecord.setSubject(new Reference("https://fhir.nhs.net/Id/nhs-number/9480431963"));
		careRecord.setConfidentiality(DocumentConfidentiality.N); // Normal
		careRecord.addAuthor(new Reference("Device/c4c8d038-913a-490c-9682-47047f4155fb"));
		// A virtual reference to ODS organisation code
		careRecord.setCustodian(new Reference("https://fhir.nhs.net/Id/ods-organization-code/R19"));
				
		return careRecord;
	}
	
	public static Patient buildCareConnectFHIRPatient()
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			Patient patient = new Patient();
	        
			// Add profile reference
			Meta meta = new Meta();
			meta.addProfile("https://fhir.hl7.org.uk/CareConnect-Patient-1.structuredefinition.xml");
			patient.setMeta(meta);
	        
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
