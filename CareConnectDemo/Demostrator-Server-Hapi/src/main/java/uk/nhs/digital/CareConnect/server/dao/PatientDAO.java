package uk.nhs.digital.CareConnect.server.dao;


import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.instance.model.api.IBaseMetaType;

import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.method.RequestDetails;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import uk.nhs.digital.CareConnect.server.entity.PatientEntity;
import uk.nhs.digital.CareConnect.server.entity.PatientIdentifier;




public class PatientDAO extends BaseDAO<Patient>
implements IPatientDAO {
	
	private static final Logger log = LoggerFactory.getLogger(uk.nhs.digital.CareConnect.server.dao.PatientDAO.class);
	
	public PatientDAO() {
	    super(Patient.class);
	}
	
	public PatientDAO(EntityManagerFactory entityManagerFactory) {
		super(Patient.class);
		this.emf = entityManagerFactory;
	}
	
	@Override
	public Class<Patient> getResourceType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <MT extends IBaseMetaType> MT metaGetOperation(Class<MT> theType, RequestDetails theRequestDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <MT extends IBaseMetaType> MT metaGetOperation(Class<MT> theType, IIdType theId,
			RequestDetails theRequestDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	public ca.uhn.fhir.rest.server.IBundleProvider patientTypeEverything(
			javax.servlet.http.HttpServletRequest theServletRequest,
				ca.uhn.fhir.model.primitive.UnsignedIntDt theCount			)
	{
		//SearchBuilder builder = new SearchBuilder();
		//builder.setType(getResourceType(), getResourceName());
		return null;
	}
	
	@Override
	public Patient create(Patient patient) {
		log.trace("called PatientDAO create");
		
		try
		{
			
			EntityManager em = emf.createEntityManager();
			log.trace("Obtained entityManager");
			em.getTransaction().begin();
			
			log.trace("Call persist");
			
			StringParam nullP = null;
			DateRangeParam nullD = null;
			TokenParam token = null;
			PatientEntity edr = null;
			for (Identifier ident : patient.getIdentifier())
			{
			
				if (ident.getSystem().contains("http://fhir.leedsth.nhs.uk/PAS/Patient"))
				{
					token = new TokenParam("http://fhir.leedsth.nhs.uk/PAS/Patient/",ident.getValue());
				}
				if (token == null && ident.getSystem().contains("http://fhir.nhs.net/Id/nhs-number"))
				{
					token = new TokenParam("http://fhir.nhs.net/Id/nhs-number/",ident.getValue());
				}
			}
			if (token != null)
			{
				log.info("Running Patient Search token.getSystem()="+token.getSystem()+" token.getValue()="+token.getValue());
				
				List<Patient> patients = search(nullP, nullP  , nullP , nullD , token);
				if (patients.size()>0)
				{
					log.trace("Found patient with id = "+patients.get(0).getIdElement().getIdPart()  );
					edr = em.find(PatientEntity.class,Integer.parseInt(patients.get(0).getIdElement().getIdPart()));
					// This is now an update as strong identifiers found a match
				}
				else
				{
					log.trace("No results found Patient Search");
					edr = new PatientEntity();
				}
			}
			else
			{
				// Shoud throw a wobbly here .... no strong identifier passed so shouldn't store the patient.
				edr = new PatientEntity();
			}
			if (patient.getName().size()>0)
			{
				edr.setFamilyName(patient.getName().get(0).getFamily());
				edr.setGivenName(patient.getName().get(0).getGivenAsSingleString());
			}
			
			if (patient.hasBirthDate())
			{
				edr.setBirthDate(patient.getBirthDate());
			}
			if (patient.hasGender())
			{
				edr.setGender(patient.getGender().toString());
			}
			if (patient.hasAddress())
			{
				for (int f=0;f<patient.getAddress().get(0).getLine().size();f++)
				{
					switch (f)
					{
					case 0:
						edr.setAddress1(patient.getAddress().get(0).getLine().get(f).getValue());
						break;
					case 1:
						edr.setAddress2(patient.getAddress().get(0).getLine().get(f).getValue());
						break;
					case 2:
						edr.setAddress3(patient.getAddress().get(0).getLine().get(f).getValue());
						break;
					case 3:
						edr.setAddress4(patient.getAddress().get(0).getLine().get(f).getValue());
						break;
					}
				}
				if (patient.getAddress().get(0).hasPostalCode())
				{
					edr.setpostCode(patient.getAddress().get(0).getPostalCode());
				}
				
			}
			for (Extension extension : patient.getExtension())
			{
				if (extension.getUrl().equals("https://fhir.leedsth.nhs.uk/MessageHeader"))
				{
					StringType value = (StringType) extension.getValue();
					edr.setResourceMessage(value.getValueAsString());
				}
			}
			
			log.trace("Call em.persist edr");
			em.persist(edr);
		
			List<PatientIdentifier> drids = new ArrayList<PatientIdentifier>();
			for(Identifier ident : patient.getIdentifier())
			{
				if (ident.getSystem().contains("http://fhir.nhs.net/Id/nhs-number"))
				{
					edr.setNHSNumber(ident.getValue());
				}
				else if (ident.getSystem().contains("http://fhir.leedsth.nhs.uk/PAS/Patient"))
				{
					edr.setPASIdentifier(ident.getValue());
				}
				else
				{
					PatientIdentifier dri = new PatientIdentifier(edr);
					dri.setSystem(ident.getSystem());
					dri.setValue(ident.getValue());
					edr.setIdentifiers(drids);
					log.trace("Call em.persist dri");    
					em.persist(dri);
				}
			}
			em.persist(edr);
			
			em.getTransaction().commit();
			
			log.info("Called PERSIST id="+edr.getId().toString());
			patient.setId(edr.getId().toString());
			
			em.close();
			
			log.debug("Finished call to persist DocumentReference");
		}
		catch (PersistenceException pe) {
			log.error("PersistenceException: "+pe.getMessage());
			log.error("PersistenceException: Cause="+pe.getCause().toString());
	        // do your operation based on sql errocode
	    }
		catch (Exception ex)
		{
			log.error("Exception: ",ex);
		}
		
		log.trace("In the finally");
		return patient;	
	}

	private Patient convert(PatientEntity edr)
	{
		Patient patient = new Patient();
		
		for(int f=0;f<edr.getIdentifiers().size();f++)
		{
			patient.addIdentifier()
				.setSystem(edr.getIdentifiers().get(f).getSystem())
				.setValue(edr.getIdentifiers().get(f).getValue());
		}
		if (edr.getNHSNumber()!=null)
		{
			patient.addIdentifier()
			.setSystem("http://fhir.nhs.net/Id/nhs-number")
			.setValue(edr.getNHSNumber());
		}
		if (edr.getPASIdentifier()!=null)
		{
			patient.addIdentifier()
			.setSystem("http://fhir.leedsth.nhs.uk/PAS/Patient")
			.setValue(edr.getPASIdentifier());
		}
		patient.setId(edr.getId().toString());
		
		patient.addName()
			.setFamily(edr.getFamilyName())
			.addGiven(edr.getGivenName());
		if (edr.getBirthDate() != null)
		{
			patient.setBirthDate(edr.getBirthDate());
		}
		Address adr= new Address();
		if (edr.getAddress1()!="")
		{
			adr.addLine(edr.getAddress1());
		}
		if (edr.getAddress2()!="")
		{
			adr.addLine(edr.getAddress2());
		}		
		if (edr.getAddress3()!="")
		{
			adr.addLine(edr.getAddress3());
		}
		if (edr.getAddress4()!="")
		{
			adr.addLine(edr.getAddress4());
		}
		if (edr.getPostCode() !=null)
		{
			adr.setPostalCode(edr.getPostCode());
		}
		patient.addAddress(adr);
	    if (edr.getGender() !=null)
	    {
			switch (edr.getGender())
			{
				case "MALE":
					patient.setGender(AdministrativeGender.MALE);
					break;
				case "FEMALE":
					patient.setGender(AdministrativeGender.FEMALE);
					break;
				case "OTHER":
					patient.setGender(AdministrativeGender.OTHER);
					break;
				case "UNKNOWN":
					patient.setGender(AdministrativeGender.UNKNOWN);
					break;
			}
	    }
		return patient;
	}
	
	@Override
	public Patient read(IdType theId) {
		log.info("called read theId="+ theId.toString());
		log.info("called read Id="+ theId.getIdPart());
		Patient patient  = null;
		try
		{
			
			EntityManager em = emf.createEntityManager();
			log.info("Obtained entityManager Patient.read");
			
			
			PatientEntity edr = (PatientEntity) em.find(PatientEntity.class,Integer.parseInt(theId.getIdPart()));
			
			patient = convert(edr);
			
			em.close();
	        log.info("Built the Patient");
	       	
			return patient;
		}
		catch (Exception ex)
		{
			log.error("Exception: ",ex);
			
		}
		finally
		{
			log.info("In the finally");
		}
		
		
		MethodOutcome method = new MethodOutcome();
		method.setResource(patient);
		return patient;
	}

	
	public List<Patient> search(
			@OptionalParam(name = Patient.SP_FAMILY) StringParam familyName, 
    		@OptionalParam(name=Patient.SP_GIVEN) StringParam givenName, 
    		@OptionalParam(name=Patient.SP_ADDRESS_POSTALCODE) StringParam postCode,
    		@OptionalParam(name=Patient.SP_BIRTHDATE) DateRangeParam birthDate,
    		@OptionalParam(name=Patient.SP_IDENTIFIER) TokenParam identifier 
			) {
		
		List<PatientEntity> qryResults = null;
		List<Patient> results = new ArrayList<Patient>();
		try
		{
			// https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#criteria
			
			EntityManager em = emf.createEntityManager();
			
			CriteriaBuilder builder = em.getCriteriaBuilder();
		    
		    CriteriaQuery<PatientEntity> criteria = builder.createQuery(PatientEntity.class);
		   	Root<PatientEntity> root = criteria.from(PatientEntity.class);
		   	Join<PatientEntity, PatientIdentifier> join = root.join("identifiers", JoinType.LEFT);
		    
		   	List<Predicate> predList = new LinkedList<Predicate>();
		  
		    if (familyName != null)
		    {
		    	Predicate p = builder. equal(root.get("familyName"),familyName.getValue());
		    	predList.add(p);
		    	log.info("foreName = "+familyName.toString());
		    	
		    }
		    if (givenName != null)
		    {
		    	Predicate p = builder.equal(root.get("givenName"),givenName.getValue());
		    	predList.add(p);
		    	log.info("givenName = "+givenName.toString());
		    	
		    }
		    if (postCode != null)
		    {
		    	Predicate p = builder.equal(root.get("PostCode"),postCode.getValue());
		    	predList.add(p);
		    	log.info("postCode = "+postCode.toString());
		    }
		    if (identifier !=null)
	    	{
	    		log.info("System="+identifier.getSystem()+" and Value="+identifier.getValue());
	    		if (identifier.getSystem() == null || identifier.getSystem().contains("http://fhir.leedsth.nhs.uk/PAS/Patient") )
	    		{
	    			Predicate p = builder.equal(root.get("pasIdentifier"),identifier.getValue());
	    			predList.add(p);
	    		}
	    		else if (identifier.getSystem().contains("http://fhir.nhs.net/Id/nhs-number") )
	    		{
	    			Predicate p = builder.equal(root.get("nhsNumber"),identifier.getValue());
	    			predList.add(p);
	    		}
	    		else
	    		{
	    			Predicate p = builder.equal(join.get("value"),identifier.getValue());
			    	predList.add(p);
			    	predList.add(builder.equal(join.get("system"),identifier.getSystem()));
			    	log.info("identifier = "+identifier.toString());
	    		}
	    	}
		    if (birthDate !=null)
		    {
		    	Date from = birthDate.getLowerBoundAsInstant();
		    	Date to = birthDate.getUpperBoundAsInstant();
		    	
		    	switch (birthDate.getValuesAsQueryTokens().get(0).getPrefix())
		    	{
		    		case GREATERTHAN :
		    		{
		    			Predicate p = builder.greaterThan(root.<Date>get("BirthDate"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case GREATERTHAN_OR_EQUALS :
		    		{
		    			Predicate p = builder.greaterThanOrEqualTo(root.<Date>get("BirthDate"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case APPROXIMATE :
		    		case EQUAL :
		    		{
		    			Predicate p = builder.equal(root.<Date>get("BirthDate"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		
		    		case NOT_EQUAL :
		    		{
		    			Predicate p = builder.notEqual(root.<Date>get("BirthDate"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case STARTS_AFTER :
		    		{
		    			Predicate p = builder.greaterThan(root.<Date>get("BirthDate"),from);
		    			predList.add(p);
		    			break;
		    			
		    		}
		    		case LESSTHAN_OR_EQUALS :
		    		{
		    			Predicate p = builder.lessThanOrEqualTo(root.<Date>get("BirthDate"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case ENDS_BEFORE :
		    		case LESSTHAN :
		    		{
		    			Predicate p = builder.lessThan(root.<Date>get("BirthDate"),from);
		    			predList.add(p);
		    			break;
		    		}
		    	}
		    	if (birthDate.getValuesAsQueryTokens().size()>1)
		    	{
		    		
			    	log.info("created = "+birthDate.toString());
			    	
			    	switch (birthDate.getValuesAsQueryTokens().get(1).getPrefix())
			    	{
			    		case GREATERTHAN :
			    		{
			    			Predicate p = builder.greaterThan(root.<Date>get("BirthDate"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case GREATERTHAN_OR_EQUALS :
			    		{
			    			Predicate p = builder.greaterThanOrEqualTo(root.<Date>get("BirthDate"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case APPROXIMATE :
			    		case EQUAL :
			    		{
			    			Predicate p = builder.equal(root.<Date>get("BirthDate"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		
			    		case NOT_EQUAL :
			    		{
			    			Predicate p = builder.notEqual(root.<Date>get("BirthDate"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case STARTS_AFTER :
			    		{
			    			Predicate p = builder.greaterThan(root.<Date>get("BirthDate"),to);
			    			predList.add(p);
			    			break;
			    			
			    		}
			    		case LESSTHAN_OR_EQUALS :
			    		{
			    			Predicate p = builder.lessThanOrEqualTo(root.<Date>get("BirthDate"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case ENDS_BEFORE :
			    		case LESSTHAN :
			    		{
			    			Predicate p = builder.lessThan(root.<Date>get("BirthDate"),to);
			    			predList.add(p);
			    			break;
			    		}
			    	}
		    	}
		    }
		    
		    
		    Predicate[] predArray = new Predicate[predList.size()];
		    predList.toArray(predArray);
		    if (predList.size()>0)
		    {
		    	criteria.select(root).where(predArray);
		    }
		    else
		    {
		    	criteria.select(root);
		    }
		    
		    qryResults = em.createQuery(criteria).getResultList();
		    
		    for (PatientEntity doc : qryResults)
		    {
		    	log.trace("HAPI Custom = "+doc.getId());
		    	Patient patient = convert(doc);
		    	results.add(patient);
		    }
		    log.debug("Custom HAPI Count = "+qryResults.size());
	  	}
		catch (PersistenceException pe) {
			log.error("PersistenceException: "+pe.getMessage());
			log.error("PersistenceException: Cause="+pe.getCause().toString());
	        // do your operation based on sql errocode
	    }
		catch (Exception ex)
		{
			log.error("Exception: ",ex);
		}
		return results;
		
        //return Collections. singletonList(documentReference);
	}
}
