package uk.nhs.careConnect.dstu2.dao;


import ca.uhn.fhir.model.dstu2.resource.ValueSet;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.method.RequestDetails;
import ca.uhn.fhir.rest.param.StringParam;
import org.apache.commons.lang3.math.NumberUtils;
import org.hl7.fhir.instance.model.api.IBaseMetaType;
import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.careConnect.dstu2.provider.ValueSetResourceProvider;
import uk.nhs.careConnect.entity.ValueSetContent;
import uk.nhs.careConnect.entity.ValueSetEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;



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

			if (valueSet.getId() != null && !valueSet.getId().isEmpty())
			{
				// strId is the exernally facing Id

				CriteriaBuilder builder = em.getCriteriaBuilder();

				CriteriaQuery<ValueSetEntity> criteria = builder.createQuery(ValueSetEntity.class);
				Root<ValueSetEntity> root = criteria.from(ValueSetEntity.class);
				List<Predicate> predList = new LinkedList<Predicate>();
				Predicate p = builder.equal(root.<String>get("strId"),valueSet.getId().getIdPart());
				predList.add(p);
				Predicate[] predArray = new Predicate[predList.size()];
				predList.toArray(predArray);
				if (predList.size()>0)
				{
					criteria.select(root).where(predArray);

					List<ValueSetEntity> qryResults = em.createQuery(criteria).getResultList();

					for (ValueSetEntity cme : qryResults)
					{
						vse = cme;
						break;
					}

			    }
			    else
				{
					log.info("No existing ValueSet resource");
				}
			}
			if (vse == null)
			{
				vse = new ValueSetEntity();
			}
			log.info("strId -= "+valueSet.getId().getIdPart());
			log.info("strId -= "+valueSet.getId().getValue());
			log.info("strId -= "+valueSet.getId().getValueAsString());
			if (valueSet.getId() != null && !valueSet.getId().isEmpty())
			{
				log.info("strId -= "+valueSet.getId().getIdPart());
				vse.setStrId(valueSet.getId().getIdPart());
			}
			if (valueSet.getUrl() !=null && !valueSet.getUrl().isEmpty())
			{
				vse.setUrl(valueSet.getUrl());
			}
			if (valueSet.getName() != null && !valueSet.getName().isEmpty())
			{
				vse.setName(valueSet.getName());
			}
			/*
			if (valueSet.getStatus() != null && !valueSet.getStatus().isEmpty())
			{
				vse.setStatus(valueSet.getStatus());
			}
			*/
			if (valueSet.getDescription() != null && !valueSet.getDescription().isEmpty())
			{
				vse.setDescription(valueSet.getDescription());
			}
			
			log.debug("1 Call em.persist ValueSetEntity contents size = "+vse.getContents().size());


            em.persist(vse);

			if (valueSet.getCodeSystem() != null && !valueSet.getCodeSystem().isEmpty())
            {
                ValueSet.CodeSystem system = valueSet.getCodeSystem();
                vse.setTermCodeSystemUrl(system.getSystem());
                for (ValueSet.CodeSystemConcept concept : system.getConcept())
                {
                    Boolean found = false;
                    ValueSetContent vsec = null;
                    for (ValueSetContent ocontent : vse.getContents())
                    {
                        if (ocontent.getCode().equals(concept.getCode()))
                        {
                            vsec = ocontent;
                            log.info("Found existing code");
                        }
                    }
                    if (vsec == null)
                    {
                        vsec = new ValueSetContent(vse);
                    }

                    if (concept.getCode() != null && !concept.getCode().isEmpty())
                    {
                        vsec.setCode(concept.getCode());
                    }
                    if (concept.getDisplay() != null && !concept.getCode().isEmpty())
                    {
                        vsec.setDisplay(concept.getDisplay());
                    }

                    em.persist(vsec);
                }
            }
            // These entries should really link into CodeSystem table
			if (valueSet.getCompose() !=null && !valueSet.getCompose().isEmpty())
			{
				ValueSet.Compose component = valueSet.getCompose();


				for (ValueSet.ComposeInclude include : component.getInclude())
				{
					if (include.getSystem() != null && !include.getSystem().isEmpty())
					{
						vse.setTermCodeSystemUrl(include.getSystem());
					}
					for (ValueSet.ComposeIncludeConcept concept: include.getConcept())
					{
						// Ideally should record against TRM_CONCEPT but for ad-hocs this is fine
                        ValueSetContent vsec = null;
                        for (ValueSetContent ocontent : vse.getContents())
                        {
                            if (ocontent.getCode().equals(concept.getCode()))
                            {
                                vsec = ocontent;
                                log.info("Found existing code");
                            }
                        }
                        if (vsec == null)
                        {
                            vsec = new ValueSetContent(vse);
                        }

						if (concept.getCode() != null && !concept.getCode().isEmpty())
						{
							vsec.setCode(concept.getCode());
						}
						if (concept.getDisplay() != null && !concept.getCode().isEmpty())
						{
							vsec.setDisplay(concept.getDisplay());
						}
						// Should support more than Snomed here
						if ((include.getSystem() != null) && !include.getSystem().isEmpty() && (include.getSystem().contains("http://snomed.info/sct")))
						{
							vsec.setCodeCT(conceptDAO.search(concept.getCode(), 1));
						}
						
						em.persist(vsec);
					}
				}
			}

			em.getTransaction().commit();
			
			log.info("Called PERSIST id="+vse.getId().toString());
			log.info("Called PERSIST strId="+vse.getStrId());
			valueSet.setId(vse.getStrId());
			
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
		/*
		if (cme.getStatus() !=null)
		{
			valueSet.setStatus(cme.getStatus());
		}
		*/
		if (cme.getUrl() !=null)
		{
			valueSet.setUrl(cme.getUrl());
		}
		
		if (cme.getContents().size()>0)
		{
			ValueSet.Compose compose = new ValueSet.Compose();
			ValueSet.ComposeInclude include = compose.addInclude();
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
						ValueSet.ComposeIncludeConcept comp = include.addConcept().setCode(content.getCode());
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
	public ValueSet read(IIdType theId) {

		ValueSet valueSet  = null;
		try
		{
			
			EntityManager em = emf.createEntityManager();
			log.info("Obtained entityManager Patient.read " + theId.getIdPart() );

			ValueSetEntity cme = null;
			// strId is the exernally facing Id

			CriteriaBuilder builder = em.getCriteriaBuilder();

			CriteriaQuery<ValueSetEntity> criteria = builder.createQuery(ValueSetEntity.class);
			Root<ValueSetEntity> root = criteria.from(ValueSetEntity.class);
			List<Predicate> predList = new LinkedList<Predicate>();
			Predicate p = builder.equal(root.<String>get("strId"),theId.getIdPart());
			predList.add(p);
			Predicate[] predArray = new Predicate[predList.size()];
			predList.toArray(predArray);
			if (predList.size()>0)
			{
				criteria.select(root).where(predArray);

				List<ValueSetEntity> qryResults = em.createQuery(criteria).getResultList();

				for (ValueSetEntity vse : qryResults)
				{
					log.trace("HAPI Custom = "+vse.getId());
					cme = vse;
					break;
				}

			}
			else
			{
				log.debug("No existing ValueSet resource");
			}
			if (cme == null && NumberUtils.isNumber(theId.getIdPart())  ) {
				// Need to check for strings.
				cme = (ValueSetEntity) em.find(ValueSetEntity.class, Integer.parseInt(theId.getIdPart()));
			}
			valueSet = convert(cme);
			
			em.close();
	        log.trace("Built the Patient");
	       	
			return valueSet;
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
