package uk.nhs.careConnect.dstu2.dao;


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


import org.hl7.fhir.dstu3.model.ConceptMap;
import org.hl7.fhir.dstu3.model.ConceptMap.SourceElementComponent;
import org.hl7.fhir.dstu3.model.ConceptMap.TargetElementComponent;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.instance.model.api.IBaseMetaType;

import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.method.RequestDetails;
import uk.nhs.careConnect.entity.ConceptMapEntity;
import uk.nhs.careConnect.dstu2.provider.ConceptMapResourceProvider;
import uk.nhs.careConnect.entity.ConceptMapElement;
import uk.nhs.careConnect.entity.ConceptMapElementTarget;
import uk.nhs.careConnect.entity.ConceptMapGroup;



public class ConceptMapDAO extends BaseDAO<ConceptMap>
implements IConceptMapDAO {
	
	private static final Logger log = LoggerFactory.getLogger(ConceptMapResourceProvider.class);
	
	public ConceptMapDAO() {
	    super(ConceptMap.class);
	}
	
	public ConceptMapDAO(EntityManagerFactory entityManagerFactory) {
		super(ConceptMap.class);
		this.emf = entityManagerFactory;
	}
	
	@Override
	public Class<ConceptMap> getResourceType() {
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
	public ConceptMap create(ConceptMap conceptMap) {
		log.trace("called ConceptMapDAO create");
		
		try
		{
			
			EntityManager em = emf.createEntityManager();
			log.trace("Obtained entityManager");
			em.getTransaction().begin();
			
			log.trace("Call persist");
			
			
				
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
		return conceptMap;	
	}
	public ConceptMap convert(ConceptMapEntity cme)
	{
		ConceptMap conceptMap = new ConceptMap();
		
		conceptMap.setId(cme.getId().toString());
		
		if (cme.getDescription() != null)
		{
			conceptMap.setDescription(cme.getDescription());
		}
		if (cme.getName() !=null)
		{
			conceptMap.setName(cme.getName());
		}
		if (cme.getSourceValueSet() != null)
		{
			conceptMap.setSource(new Reference(cme.getSourceValueSet().getUrl()));
		}
		if (cme.getTargetValueSet()!=null)
		{
			conceptMap.setTarget(new Reference(cme.getTargetValueSet().getUrl()));
		}
		if (cme.getGroups().size()>0)
		{
			for (ConceptMapGroup group : cme.getGroups())
			{
				ConceptMap.ConceptMapGroupComponent conceptGroup = new ConceptMap.ConceptMapGroupComponent();
				if (group.getSource() != null)
				{
					conceptGroup.setSource(group.getSource());
				}
				if (group.getTarget() != null)
				{
					conceptGroup.setTarget(group.getTarget());
				}
				for (ConceptMapElement conceptElement : group.getElements())
				{
					SourceElementComponent element = conceptGroup.addElement();
					if (conceptElement.getCodeCT() != null)
					{
						element.setCode(conceptElement.getCodeCT().getCode());
						for (ConceptMapElementTarget conceptTarget: conceptElement.getElementTargets())
						{
							if (conceptTarget.getCodeCT() != null)
							{
								TargetElementComponent elementTarget = element.addTarget();
								elementTarget.setCode(conceptTarget.getCodeCT().getCode());
							}
						}
					}
				}
				conceptMap.addGroup(conceptGroup);
			}
		}
		
		return conceptMap;
	}
	@Override
	public ConceptMap read(IdType theId) {
		log.info("called read theId="+ theId.toString());
		log.info("called read Id="+ theId.getIdPart());
		ConceptMap conceptMap  = null;
		try
		{
			
			EntityManager em = emf.createEntityManager();
			log.info("Obtained entityManager Patient.read");
			
			
			ConceptMapEntity cme = (ConceptMapEntity) em.find(ConceptMapEntity.class,Integer.parseInt(theId.getIdPart()));
			
			conceptMap = convert(cme);
			
			em.close();
	        log.info("Built the Patient");
	       	
			return conceptMap;
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
		method.setResource(conceptMap);
		return conceptMap;
	}

	
	public List<ConceptMap> search(
			 
			) {
		
		List<ConceptMapEntity> qryResults = null;
		List<ConceptMap> results = new ArrayList<ConceptMap>();
		try
		{
			// https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#criteria
			
			EntityManager em = emf.createEntityManager();
			
			CriteriaBuilder builder = em.getCriteriaBuilder();
		    
		    CriteriaQuery<ConceptMapEntity> criteria = builder.createQuery(ConceptMapEntity.class);
		   	Root<ConceptMapEntity> root = criteria.from(ConceptMapEntity.class);
		   //	Join<ConceptMapEntity, ConceptMapIdentifier> join = root.join("identifiers", JoinType.LEFT);
		    
		   	List<Predicate> predList = new LinkedList<Predicate>();
		  
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
		    
		    for (ConceptMapEntity cme : qryResults)
		    {
		    	log.trace("HAPI Custom = "+cme.getId());	    	
		    	ConceptMap conceptMap = convert(cme);
		    	results.add(conceptMap);
		    }
		    log.info("ConceptMap Search Count = "+qryResults.size());
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
