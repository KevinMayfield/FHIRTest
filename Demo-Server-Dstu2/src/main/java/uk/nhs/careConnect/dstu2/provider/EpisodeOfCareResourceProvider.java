package uk.nhs.careConnect.dstu2.provider;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.SessionFactory;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;


import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;

import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import uk.nhs.careConnect.dstu2.dao.EpisodeOfCareDAO;


public class EpisodeOfCareResourceProvider extends BaseProvider implements IResourceProvider {

	@Override
	public Class<EpisodeOfCare> getResourceType() {
		// TODO Auto-generated method stub
		return EpisodeOfCare.class;
	}
	
	@Autowired
	protected SessionFactory sessionFactory;
	
	private static final Logger log = LoggerFactory.getLogger(EpisodeOfCareResourceProvider.class);
	
	private WebApplicationContext myAppCtx;
	
	@Create()
	public MethodOutcome createEpisodeOfCare(HttpServletRequest theRequest,@ResourceParam EpisodeOfCare episode) {
		
		log.trace("Called createEpisodeOfCare");
		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		OperationOutcome opOutcome = new OperationOutcome();
		
		method.setOperationOutcome(opOutcome);
		EpisodeOfCare newEpisode = null;
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
			EpisodeOfCareDAO episodeDAO = new EpisodeOfCareDAO(sessionFactory);
			log.trace("Calling EpisodeOfCareDAO.create");
			newEpisode = episodeDAO.create(episode);
			log.info("Return the New EpisodeOfCare id = "+newEpisode.getId());
			method.setId(episode.getIdElement());
			method.setResource(newEpisode);
			
					
		}
		finally
		{
			//endRequest(theRequest);
			log.debug("Finished call createEpisodeOfCare");
		}
		return method;  //, theRequestDetails
	}
	
	
	 @Read()
	    public EpisodeOfCare getResourceById(HttpServletRequest theRequest,@IdParam IdType theId) {
		 
		 myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
		 	sessionFactory = myAppCtx.getBean(SessionFactory.class);
			if (sessionFactory != null)
			{
				log.trace("session 2nd Attempt - EpisodeOfCare Read");
			}
			startRequest(theRequest);
			EpisodeOfCareDAO episodeDAO = new EpisodeOfCareDAO(sessionFactory); 
			log.trace("Calling EpisodeOfCareDAO.read");
			
			MethodOutcome method = new MethodOutcome();
			method.setResource(episodeDAO.read(theId));
			return (EpisodeOfCare) method.getResource();   //, theRequestDetails
	    }
	 
	 @Search()
	    public List<EpisodeOfCare> getEpisodeOfCare(HttpServletRequest theRequest, 
	    		@OptionalParam(name = EpisodeOfCare.SP_TYPE) TokenParam theType, 
	    		@OptionalParam(name=EpisodeOfCare.SP_PATIENT) ReferenceParam thePatient,
	    		@OptionalParam(name=EpisodeOfCare.SP_DATE) DateRangeParam period
	    		 
        
				) {
		 
		 
		 
		 	// KGM 2016-12-12 Initial work on searching
		 
		  	 myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
			 	sessionFactory = myAppCtx.getBean(SessionFactory.class);
				if (sessionFactory != null)
				{
					log.trace("session 2nd Attempt - EpisodeOfCareDAO Read");
				}
				startRequest(theRequest);
				EpisodeOfCareDAO episodeDAO = new EpisodeOfCareDAO(sessionFactory); 
				log.trace("Calling EpisodeOfCareDAO.search");
		  	
			return episodeDAO.search(theType, thePatient, period);
		
	    }
}
