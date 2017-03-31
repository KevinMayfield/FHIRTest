package uk.nhs.careConnect.dstu2.provider;

import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hibernate.SessionFactory;
import org.hl7.fhir.dstu3.model.ConceptMap;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import uk.nhs.careConnect.dstu2.dao.ConceptMapDAO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;




public class ConceptMapResourceProvider extends BaseProvider implements IResourceProvider {

	@Override
	public Class<ConceptMap> getResourceType() {
		// TODO Auto-generated method stub
		return ConceptMap.class;
	}
	
	@Autowired
	protected SessionFactory sessionFactory;
	
	private static final Logger log = LoggerFactory.getLogger(ConceptMapResourceProvider.class);
	
	private WebApplicationContext myAppCtx;
	

	@Create()
	public MethodOutcome createConceptMap(HttpServletRequest theRequest,@ResourceParam ConceptMap theConceptMap) {
		
		log.trace("Called createPatient");
		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		OperationOutcome opOutcome = new OperationOutcome();
		
		method.setOperationOutcome(opOutcome);
		ConceptMap theNewConceptMap = null;
		myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
		
		if (sessionFactory != null)
		{
			log.trace("session");
		}
		else
		{
			log.trace("session NULL");
			sessionFactory = myAppCtx.getBean(SessionFactory.class);
			if (sessionFactory != null)
			{
				log.trace("session 2nd Attempt - Patient Create");
			}
		}
		
		try 
		{
			//startRequest(theRequest);
			ConceptMapDAO conceptMapDAO = new ConceptMapDAO(sessionFactory); 
			log.trace("Calling patientDAO.create");
			theNewConceptMap = conceptMapDAO.create(theConceptMap);
			log.info("Return the New ConceptMap id = "+theNewConceptMap.getId());
			method.setId(theNewConceptMap.getIdElement());
			method.setResource(theNewConceptMap);
			
					
		}
		finally
		{
			//endRequest(theRequest);
			log.debug("Finished call createConceptMap");
		}
		return method;  //, theRequestDetails
	}
	
	
	 @Read()
	    public ConceptMap getResourceById(HttpServletRequest theRequest,@IdParam IdType theId) {
		 
		 myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
		 	sessionFactory = myAppCtx.getBean(SessionFactory.class);
			if (sessionFactory != null)
			{
				log.trace("session 2nd Attempt - ConceptMap Read");
			}
			startRequest(theRequest);
			ConceptMapDAO conceptMapDAO = new ConceptMapDAO(sessionFactory); 
			log.trace("Calling ConceptMapDAO.read");
			
			MethodOutcome method = new MethodOutcome();
			method.setResource(conceptMapDAO.read(theId));
			return (ConceptMap) method.getResource();   //, theRequestDetails
	    }
	 
	 @Search()
	    public List<ConceptMap> getConceptMap(HttpServletRequest theRequest 
	    		 
	    		) {
		 
		 
		 
		  	 myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
			 	sessionFactory = myAppCtx.getBean(SessionFactory.class);
				if (sessionFactory != null)
				{
					log.trace("session 2nd Attempt - DocumentReference Read");
				}
				startRequest(theRequest);
				ConceptMapDAO conceptMapDAO = new ConceptMapDAO(sessionFactory); 
				log.trace("Calling patientDAO.search");
		  	
			return conceptMapDAO.search();
		
	    }
}
