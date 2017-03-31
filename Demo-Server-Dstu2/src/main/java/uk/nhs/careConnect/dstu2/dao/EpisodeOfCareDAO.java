package uk.nhs.careConnect.dstu2.dao;


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

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;

import org.hl7.fhir.dstu3.model.EpisodeOfCare;

import org.hl7.fhir.dstu3.model.IdType;

import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.instance.model.api.IBaseMetaType;

import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.api.MethodOutcome;

import ca.uhn.fhir.rest.method.RequestDetails;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import uk.nhs.careConnect.entity.EpisodeOfCareEntity;
import uk.nhs.careConnect.entity.EpisodeOfCareIdentifier;
import uk.nhs.careConnect.entity.PatientEntity;


public class EpisodeOfCareDAO extends BaseDAO<EpisodeOfCare>
implements IEpisodeOfCareDAO {
	
	private static final Logger log = LoggerFactory.getLogger(EpisodeOfCareDAO.class);
	
	public EpisodeOfCareDAO() {
	    super(EpisodeOfCare.class);
	}
	
	public EpisodeOfCareDAO(EntityManagerFactory entityManagerFactory) {
		super(EpisodeOfCare.class);
		this.emf = entityManagerFactory;
	}
	
	@Override
	public Class<EpisodeOfCare> getResourceType() {
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

	
	
	@Override
	public EpisodeOfCare create(EpisodeOfCare episode) {
		log.trace("called EpisodeOfCareDAO create");
		
		try
		{
			
			EntityManager em = emf.createEntityManager();
			log.trace("Obtained entityManager");
			em.getTransaction().begin();
			
			log.trace("Call persist");
			EpisodeOfCareEntity edr = new EpisodeOfCareEntity();
			
			/*
			ConceptDAO conceptDAO = new ConceptDAO(this.emf);
			
			if (referralRequest.getType().getCoding().size()>0)
			{
				//edr.setTypeSnmdCT(referralRequest.getType().getCoding().get(0).getCode());
				//edr.setTypeSnmdCTName(referralRequest.getType().getCoding().get(0).getDisplay());
				TermConcept concept =conceptDAO.search(referralRequest.getType().getCoding().get(0).getCode(), 1);
				
				if (concept != null)
				{
					log.debug("Returned Concept="+concept.getId().toString());
					edr.setTypeCT(concept); // hard coded CodeSystem
				}
			}
			if (referralRequest.getSpecialty().getCoding().size()>0)
			{
				//edr.setSpecialitySnmdCT(referralRequest.getSpecialty().getCoding().get(0).getCode());
				//edr.setSpecialitySnmdCTName(referralRequest.getSpecialty().getCoding().get(0).getDisplay());
				TermConcept concept = conceptDAO.search(referralRequest.getSpecialty().getCoding().get(0).getCode(), 1);
				
				if (concept!=null)
				{
					log.debug("Returned Concept="+concept.getId().toString());
					edr.setSpecialityCT(concept); // hard coded CodeSystem
				}
				
			}
			if (referralRequest.getSpecialty().getCoding().size()>1)
			{
				//edr.setSpecialityLocalCode(referralRequest.getSpecialty().getCoding().get(1).getCode());
				//edr.setSpecialityLocalCodeSystem(referralRequest.getSpecialty().getCoding().get(1).getSystem());
				TermConcept concept = conceptDAO.search(referralRequest.getSpecialty().getCoding().get(1).getCode(), 2);
				
				if (concept != null)
				{
					log.debug("Returned Concept="+concept.getId().toString());
					edr.setSpecialityLocalCT(concept); // hard coded CodeSystem
				}
			}
			
			if (referralRequest.hasPatient())
			{
				PatientDAO patientDAO = new PatientDAO(this.emf);
				
				StringParam nullP = null;
				DateRangeParam nullD = null;
				TokenParam token = null;
				log.trace("referralRequest.getPatient().getReference() = "+referralRequest.getPatient().getReference());
				
				
				if (referralRequest.getPatient().getReference().contains("http://fhir.leedsth.nhs.uk/PAS/Patient/"))
				{
					token = new TokenParam("http://fhir.leedsth.nhs.uk/PAS/Patient/",referralRequest.getPatient().getReference().replace("http://fhir.leedsth.nhs.uk/PAS/Patient/", ""));
					log.trace("referralRequest.getPatient() PAS Replace = "+referralRequest.getPatient().getReference().replace("http://fhir.leedsth.nhs.uk/PAS/Patient/", ""));
				}
				if (referralRequest.getPatient().getReference().contains("http://fhir.nhs.net/Id/nhs-number/"))
				{
					token = new TokenParam("http://fhir.nhs.net/Id/nhs-number/",referralRequest.getPatient().getReference().replace("http://fhir.nhs.net/Id/nhs-number/", ""));
					log.trace("referralRequest.getPatient() NHSNumber Replace = "+referralRequest.getPatient().getReference().replace("http://fhir.nhs.net/Id/nhs-number/", ""));
				}
				if (token != null)
				{
					log.trace("Running Patient Search token.getSystem()="+token.getSystem()+" token.getValue()="+token.getValue());
					
					List<Patient> patients = patientDAO.search(nullP, nullP  , nullP , nullD , token);
					if (patients.size()>0)
					{
						log.trace("Found patient with id = "+patients.get(0).getIdElement().getIdPart()  );
						edr.setPatientEntity((PatientEntity) em.find(PatientEntity.class,Integer.parseInt(patients.get(0).getIdElement().getIdPart())));
					}
					else
					{
						log.trace("No results found Patient Search");
					}
				}
			}
			if (referralRequest.hasStatus())
			{
				edr.setStatus(referralRequest.getStatus().toString());
			}
			if (referralRequest.hasDescription())
			{
				edr.setDescription(referralRequest.getDescription());
			}
			
			if (referralRequest.hasAuthored())
			{
				edr.setAuthoured(referralRequest.getAuthored());
			}
			
			for (Extension extension : referralRequest.getExtension())
			{
				if (extension.getUrl().equals("https://fhir.leedsth.nhs.uk/MessageHeader"))
				{
					StringType value = (StringType) extension.getValue();
					edr.setResourceMessage(value.getValueAsString());
				}
			}
			*/
			log.trace("Call em.persist edr");
			em.persist(edr);
		
			List<EpisodeOfCareIdentifier> drids = new ArrayList<EpisodeOfCareIdentifier>();
			for(int f=0;f<episode.getIdentifier().size();f++)
			{
				EpisodeOfCareIdentifier dri = new EpisodeOfCareIdentifier(edr);
				dri.setSystem(episode.getIdentifier().get(f).getSystem());
				dri.setValue(episode.getIdentifier().get(f).getValue());
				edr.setIdentifiers(drids);
				log.trace("Call em.persist dri");    
				em.persist(dri);
			}
			
			
			em.getTransaction().commit();
			
			log.debug("Called PERSIST id="+edr.getId().toString());
			episode.setId(edr.getId().toString());
			
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
		return episode;	
	}

	private EpisodeOfCare convert(EpisodeOfCareEntity edr)
	{
		EpisodeOfCare episode = new EpisodeOfCare();
		
		for(int f=0;f<edr.getIdentifiers().size();f++)
		{
			episode.addIdentifier()
				.setSystem(edr.getIdentifiers().get(f).getSystem())
				.setValue(edr.getIdentifiers().get(f).getValue());
		}
		episode.setId(edr.getId().toString());
		
		
		if (edr.getTypeCT() != null)
		{
			CodeableConcept type = new CodeableConcept();
			Coding code = type.addCoding()
				.setCode(edr.getTypeCT().getCode())
				.setDisplay(edr.getTypeCT().getDisplay());
			if (edr.getTypeCT().getTermCodeSystem() != null )
			{
				code.setSystem(edr.getTypeCT().getTermCodeSystem().getCodeSystemUri());
			}
			else
			{
				code.setSystem("http://snomed.info/sct");
			}
			episode.addType(type);
		}
		if (edr.getPatientEntity() !=null)
		{
			episode.setPatient(new Reference("Patient/"+edr.getPatientEntity().getId()));
		}
		
		return episode;
	}
	
	@Override
	public EpisodeOfCare  read(IdType theId) {
		log.trace("called read theId="+ theId.toString());
		log.trace("called read Id="+ theId.getIdPart());
		EpisodeOfCare episode  = null;
		try
		{
			
			EntityManager em = emf.createEntityManager();
			log.trace("Obtained entityManager EpisodeOfCare.read");
			
			
			EpisodeOfCareEntity edr = (EpisodeOfCareEntity) em.find(EpisodeOfCareEntity.class,Integer.parseInt(theId.getIdPart()));
			
			episode = convert(edr);
			
			em.close();
	        log.trace("Built the ReferralRequest");
	       	
			return episode;
		}
		catch (Exception ex)
		{
			log.error("Exception: ",ex);
			
		}
		finally
		{
			log.trace("In the finally");
		}
		
		
		MethodOutcome method = new MethodOutcome();
		method.setResource(episode);
		return episode;
	}

	
	public List<EpisodeOfCare> search(
			@OptionalParam(name = ReferralRequest.SP_TYPE) TokenParam theType, 
    		@OptionalParam(name=ReferralRequest.SP_PATIENT) ReferenceParam thePatient,
    		@OptionalParam(name=ReferralRequest.SP_OCCURRENCE_DATE) DateRangeParam period
    		
			) {
		
		List<EpisodeOfCareEntity> qryResults = null;
		List<EpisodeOfCare> results = new ArrayList<EpisodeOfCare>();
		try
		{
			// https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#criteria
			
			EntityManager em = emf.createEntityManager();
			
			CriteriaBuilder builder = em.getCriteriaBuilder();
		    
		    CriteriaQuery<EpisodeOfCareEntity> criteria = builder.createQuery(EpisodeOfCareEntity.class);
		   	Root<EpisodeOfCareEntity> root = criteria.from(EpisodeOfCareEntity.class);
			Join<EpisodeOfCareEntity, PatientEntity> patjoin = root.join("patientEntity", JoinType.LEFT);
			
		   	
		   	List<Predicate> predList = new LinkedList<Predicate>();
		   
		    if (thePatient != null)
		    {
		    	Predicate p = builder.equal(patjoin.get("id"),thePatient.getValue());
		    	predList.add(p);
		    	log.debug("thePatient = "+thePatient.toString());
		    	
		    }
			
		    if (theType != null)
		    {
		    	Predicate p = builder.equal(root.get("typeSnmdCT"),theType.getValue());
		    	predList.add(p);
		    	log.debug("theType = "+theType.toString());
		    	
		    }
		    
		    if (period !=null)
		    {
		    	
		    	Date from = period.getLowerBoundAsInstant();
		    	Date to = period.getUpperBoundAsInstant();
		    	  
		    	switch (period.getValuesAsQueryTokens().get(0).getPrefix())
		    	{
		    		case GREATERTHAN :
		    		{
		    			Predicate p = builder.greaterThan(root.<Date>get("period"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case GREATERTHAN_OR_EQUALS :
		    		{
		    			Predicate p = builder.greaterThanOrEqualTo(root.<Date>get("period"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case APPROXIMATE :
		    		case EQUAL :
		    		{
		    			Predicate p = builder.equal(root.<Date>get("period"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case STARTS_AFTER :
		    		{
		    			Predicate p = builder.greaterThan(root.<Date>get("period"),from);
		    			predList.add(p);
		    			break;
		    			
		    		}
		    		case NOT_EQUAL :
		    		{
		    			Predicate p = builder.notEqual(root.<Date>get("period"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case LESSTHAN_OR_EQUALS :
		    		{
		    			Predicate p = builder.lessThanOrEqualTo(root.<Date>get("period"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case ENDS_BEFORE :
		    		case LESSTHAN :
		    		{
		    			Predicate p = builder.lessThan(root.<Date>get("period"),from);
		    			predList.add(p);
		    			break;
		    		}
		    	}
		    	if (period.getValuesAsQueryTokens().size()>1)
		    	{
		    		switch (period.getValuesAsQueryTokens().get(1).getPrefix())
			    	{
			    		case GREATERTHAN :
			    		{
			    			Predicate p = builder.greaterThan(root.<Date>get("period"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case GREATERTHAN_OR_EQUALS :
			    		{
			    			Predicate p = builder.greaterThanOrEqualTo(root.<Date>get("period"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case APPROXIMATE :
			    		case EQUAL :
			    		{
			    			Predicate p = builder.equal(root.<Date>get("period"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case STARTS_AFTER :
			    		{
			    			Predicate p = builder.greaterThan(root.<Date>get("period"),to);
			    			predList.add(p);
			    			break;
			    			
			    		}
			    		case NOT_EQUAL :
			    		{
			    			Predicate p = builder.notEqual(root.<Date>get("period"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case LESSTHAN_OR_EQUALS :
			    		{
			    			Predicate p = builder.lessThanOrEqualTo(root.<Date>get("period"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case ENDS_BEFORE :
			    		case LESSTHAN :
			    		{
			    			Predicate p = builder.lessThan(root.<Date>get("period"),to);
			    			
			    			predList.add(p);
			    			break;
			    		}
			    	}
		    	}
		    	
		    	
		    	log.debug("period = "+period.toString());
		    }
		    
		    //Order order = 
		    Predicate[] predArray = new Predicate[predList.size()];
		    predList.toArray(predArray);
		    if (predList.size()>0)
		    {
		    	
		    		
		    	criteria.select(root).where(predArray).orderBy(builder.asc(root.get("period")));
		    	//
		    }
		    else
		    {
		    	criteria.select(root);
		    }
		    
		    qryResults = em.createQuery(criteria).getResultList();
		    
		    for (EpisodeOfCareEntity doc : qryResults)
		    {
		    	log.trace("HAPI Custom = "+doc.getId());
		    	EpisodeOfCare episode = convert(doc);
		    	
		    	results.add(episode);
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
	
	}
	
	
}
