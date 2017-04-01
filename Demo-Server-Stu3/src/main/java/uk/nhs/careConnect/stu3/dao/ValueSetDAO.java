package uk.nhs.careConnect.stu3.dao;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hl7.fhir.dstu3.model.ValueSet.ConceptReferenceComponent;
import org.hl7.fhir.dstu3.model.ValueSet.ConceptSetComponent;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.hl7.fhir.instance.model.api.IBaseMetaType;

import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.method.RequestDetails;
import ca.uhn.fhir.rest.param.StringParam;
import uk.nhs.careConnect.entity.ValueSetContent;
import uk.nhs.careConnect.stu3.provider.ValueSetResourceProvider;
import uk.nhs.careConnect.entity.ValueSetEntity;



public class ValueSetDAO extends BaseDAO<ValueSet>
implements IValueSetDAO {
	
	private static final Logger log = LoggerFactory.getLogger(ValueSetResourceProvider.class);
	
	public ValueSetDAO() {
	    super(ValueSet.class);
	}
	
	public ValueSetDAO(EntityManagerFactory entityManagerFactory) {
		super(ValueSet.class);
		this.emf = entityManagerFactory;
	}
	
	@Override
	public Class<ValueSet> getResourceType() {
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
	public ValueSet create(ValueSet valueSet) {
		log.trace("called ValueSetDAO create");
		
		try
		{
			
			EntityManager em = emf.createEntityManager();
			log.trace("Obtained entityManager");
			em.getTransaction().begin();
			ConceptDAO conceptDAO = new ConceptDAO(this.emf);
			
			log.trace("Call persist");
			
			ValueSetEntity vse = null;
			if (valueSet.hasId())
			{
				vse = (ValueSetEntity) em.find(ValueSetEntity.class,valueSet.getId());
				// if null try a search on strId
				if (vse == null)
				{
					CriteriaBuilder builder = em.getCriteriaBuilder();
			    
					CriteriaQuery<ValueSetEntity> criteria = builder.createQuery(ValueSetEntity.class);
					Root<ValueSetEntity> root = criteria.from(ValueSetEntity.class);
			   		List<Predicate> predList = new LinkedList<Predicate>();
			   		Predicate p = builder.equal(root.<String>get("strId"),valueSet.getId());
	    			predList.add(p);
			   		Predicate[] predArray = new Predicate[predList.size()];
			   		predList.toArray(predArray);
			   		if (predList.size()>0)
			   		{
			   			criteria.select(root).where(predArray);
			    
			   			List<ValueSetEntity> qryResults = em.createQuery(criteria).getResultList();
			    
				    	for (ValueSetEntity cme : qryResults)
				    	{
				    		log.trace("HAPI Custom = "+cme.getId());	    	
				    		vse = cme;
				    		break;
				    	}
			   		}
			    }
			}
			if (vse == null)
			{
				vse = new ValueSetEntity();
			}
			if (valueSet.hasId())
			{
				vse.setStrId(valueSet.getId());
			}
			if (valueSet.hasUrl())
			{
				vse.setUrl(valueSet.getUrl());
			}
			if (valueSet.hasName())
			{
				vse.setName(valueSet.getName());
			}
			if (valueSet.hasStatus())
			{
				vse.setStatus(valueSet.getStatus());
			}
			if (valueSet.hasDescription())
			{
				vse.setDescription(valueSet.getDescription());
			}
			
			log.info("Call em.persist ValueSetEntity");
			em.persist(vse);
			
			if (valueSet.hasCompose())
			{
				ValueSet.ValueSetComposeComponent component = valueSet.getCompose();
				
				for (ConceptSetComponent include : component.getInclude())
				{
					if (include.hasSystem())
					{
						vse.setTermCodeSystemUrl(include.getSystem());
					}
					for (ConceptReferenceComponent concept: include.getConcept())
					{
						// Ideally should record against TRM_CONCEPT but for ad-hocs this is fine
						ValueSetContent vsec = new ValueSetContent(vse);
						if (concept.hasCode())
						{
							vsec.setCode(concept.getCode());
						}
						if (concept.hasDisplay())
						{
							vsec.setDisplay(concept.getDisplay());
						}
						// Should support more than Snomed here
						if ((include.hasSystem()) && (include.getSystem().contains("http://snomed.info/sct")))
						{
							vsec.setCodeCT(conceptDAO.search(concept.getCode(), 1));
						}
						
						em.persist(vsec);
					}
				}
			}
				
			em.getTransaction().commit();
			
			log.info("Called PERSIST id="+vse.getId().toString());
			valueSet.setId(vse.getId().toString());
			
			em.close();
			
			log.debug("Finished call to persist ValueSet");
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
		return valueSet;	
	}
	
	
	public ValueSet convert(ValueSetEntity cme)
	{
		ValueSet valueSet = new ValueSet();
		
		valueSet.setId(cme.getId().toString());
		if (cme.getStrId()!=null)
		{
			valueSet.setId(cme.getStrId());
		}
		if (cme.getDescription() != null)
		{
			valueSet.setDescription(cme.getDescription());
		}
		if (cme.getName() !=null)
		{
			valueSet.setName(cme.getName());
		}
		if (cme.getStatus() !=null)
		{
			valueSet.setStatus(cme.getStatus());
		}
		if (cme.getUrl() !=null)
		{
			valueSet.setUrl(cme.getUrl());
		}
		
		if (cme.getContents().size()>0)
		{
			ValueSet.ValueSetComposeComponent compose = new ValueSet.ValueSetComposeComponent();
			ValueSet.ConceptSetComponent include = compose.addInclude();
			valueSet.setCompose(compose);
			for (ValueSetContent content : cme.getContents())
			{
				if (content.getCodeCT() != null)
				{
					include.addConcept()
						.setCode(content.getCodeCT().getCode())
						.setDisplay(content.getCodeCT().getDisplay());
				}
				else
				{
					if (content.getCode() != null)
					{
						ConceptReferenceComponent comp = include.addConcept().setCode(content.getCode());
						if (content.getDisplay() != null)
						{
							comp.setDisplay(content.getDisplay());
						}
					}
				}
			}
		}
		
	
		return valueSet;
	}
	@Override
	public ValueSet read(IdType theId) {
		log.info("called read theId="+ theId.toString());
		log.info("called read Id="+ theId.getIdPart());
		ValueSet valueSet  = null;
		try
		{
			
			EntityManager em = emf.createEntityManager();
			log.info("Obtained entityManager Patient.read");
			
			
			ValueSetEntity cme = (ValueSetEntity) em.find(ValueSetEntity.class,Integer.parseInt(theId.getIdPart()));
			
			valueSet = convert(cme);
			
			em.close();
	        log.info("Built the Patient");
	       	
			return valueSet;
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
		method.setResource(valueSet);
		return valueSet;
	}

	
	public List<ValueSet> search(
			@OptionalParam(name = ValueSet.SP_NAME) StringParam theName 
			) {
		
		List<ValueSetEntity> qryResults = null;
		List<ValueSet> results = new ArrayList<ValueSet>();
		try
		{
			// https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#criteria
			
			EntityManager em = emf.createEntityManager();
			
			CriteriaBuilder builder = em.getCriteriaBuilder();
		    
		    CriteriaQuery<ValueSetEntity> criteria = builder.createQuery(ValueSetEntity.class);
		   	Root<ValueSetEntity> root = criteria.from(ValueSetEntity.class);
		   	//Join<ValueSetEntity, ValueSetIdentifier> join = root.join("identifiers", JoinType.LEFT);
		    
		   	List<Predicate> predList = new LinkedList<Predicate>();
		   	if (theName != null && !theName.isEmpty())
		   	{
		   		Predicate p = builder.like(root.<String>get("name"),"%"+theName.getValue()+"%");
    			predList.add(p);
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
		    
		    for (ValueSetEntity cme : qryResults)
		    {
		    	log.trace("HAPI Custom = "+cme.getId());	    	
		    	ValueSet valueSet = convert(cme);
		    	results.add(valueSet);
		    }
		    log.info("ValueSet Search Count = "+qryResults.size());
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
