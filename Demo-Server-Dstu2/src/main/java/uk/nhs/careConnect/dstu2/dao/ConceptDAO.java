package uk.nhs.careConnect.dstu2.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.nhs.careConnect.entity.TermCodeSystem;
import uk.nhs.careConnect.entity.TermConcept;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.*;
import java.util.LinkedList;
import java.util.List;

@Repository
@Transactional
public class ConceptDAO 
{
	@PersistenceUnit
	protected EntityManagerFactory emf;
	
	private static final Logger log = LoggerFactory.getLogger(ConceptDAO.class);
	
	public ConceptDAO() {
	    
	}
	
	public ConceptDAO(EntityManagerFactory entityManagerFactory) {
		
		this.emf = entityManagerFactory;
	}
	
		
		
	public TermConcept search(
			String code, 
    		Integer codeSystemVersion 
			) {
		log.info("Looking for code="+code);
		TermConcept result = null;
		try
		{
			
			
			EntityManager em = emf.createEntityManager();
			
			CriteriaBuilder builder = em.getCriteriaBuilder();
		    
		    CriteriaQuery<TermConcept> criteria = builder.createQuery(TermConcept.class);
		   	Root<TermConcept> root = criteria.from(TermConcept.class);
		   	Join<TermConcept, TermCodeSystem> join = root.join("codeSystem", JoinType.LEFT);
		  // 	Join<TermCodeSystemVersion, TermCodeSystem> csjoin = join.join("termCodeSystem", JoinType.LEFT);
			
		   	List<TermConcept> qryResults = null;
		   	List<Predicate> predList = new LinkedList<Predicate>();
		   	
		    if (code != null)
		    {
		    	Predicate p = builder.equal(root.get("code"),code);
		    	predList.add(p);
		    	log.info("code = "+code);
		    }
			// Should really lookup both code and system.. at present hard code to Db Id's
		    if (codeSystemVersion != null)
		    {
		    	Predicate p = builder.equal(join.get("myPid"),codeSystemVersion.toString());
		    	predList.add(p);
		    	log.info("codeSystemVersion = "+codeSystemVersion.toString());
		    }
		    
		    Predicate[] predArray = new Predicate[predList.size()];
		    predList.toArray(predArray);
		    criteria.select(root).where(predArray);
		    
		    log.info("Running Concept Query");
		    qryResults = em.createQuery(criteria).getResultList();
		    
		    if (qryResults.size()>0)
		    {
		    	result = qryResults.get(0);
		    	log.info("Found Concept Id = "+result.getId().toString());
		    }
		    log.info("Concept search Count = "+qryResults.size());
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
		return result;
	
	}
	
	
}
