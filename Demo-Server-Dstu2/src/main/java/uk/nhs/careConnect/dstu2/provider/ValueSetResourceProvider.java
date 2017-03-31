package uk.nhs.careConnect.dstu2.provider;

import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.model.dstu2.resource.ValueSet;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hibernate.SessionFactory;
import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import uk.nhs.careConnect.dstu2.dao.ValueSetDAO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public class ValueSetResourceProvider extends BaseProvider implements IResourceProvider {

	@Override
	public Class<ValueSet> getResourceType() {
		// TODO Auto-generated method stub
		return ValueSet.class;
	}
	
	@Autowired
	protected SessionFactory sessionFactory;
	
	private static final Logger log = LoggerFactory.getLogger(ValueSetResourceProvider.class);
	
	private WebApplicationContext myAppCtx;
	
	@Create()
	public MethodOutcome createValueSet(HttpServletRequest theRequest,@ResourceParam ValueSet theValueSet
			
			) {
		
		log.trace("Called createValueSet");
		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		OperationOutcome opOutcome = new OperationOutcome();
		
		method.setOperationOutcome(opOutcome);
		ValueSet theNewValueSet = null;
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
			ValueSetDAO valueSetDAO = new ValueSetDAO(sessionFactory);
			log.trace("Calling valueSetDAO.create");
			theNewValueSet = valueSetDAO.create(theValueSet);
			log.info("Return the New ValueSet id = "+theNewValueSet.getId());
			method.setId(theNewValueSet.getIdElement());
			method.setResource(theNewValueSet);
			
					
		}
		finally
		{
			//endRequest(theRequest);
			log.debug("Finished call createValueSet");
		}
		return method;  //, theRequestDetails
	}
	
	@Update()
	public MethodOutcome updateValueSet(HttpServletRequest theRequest,@ResourceParam ValueSet theValueSet
			
			) {
		
		log.trace("Called createValueSet");
		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		OperationOutcome opOutcome = new OperationOutcome();
		
		method.setOperationOutcome(opOutcome);
		ValueSet theNewValueSet = null;
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
			ValueSetDAO valueSetDAO = new ValueSetDAO(sessionFactory); 
			log.trace("Calling valueSetDAO.create");
			theNewValueSet = valueSetDAO.create(theValueSet);
			log.debug("Return the New ValueSet id = "+theNewValueSet.getId());
			method.setId(theNewValueSet.getIdElement());
			method.setResource(theNewValueSet);
			
					
		}
		finally
		{
			//endRequest(theRequest);
			log.debug("Finished call createValueSet");
		}
		return method;  //, theRequestDetails
	}
	
	
	 @Read()
	    public ValueSet getResourceById(HttpServletRequest theRequest, @IdParam IIdType theId) {
		 
		 myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
		 	sessionFactory = myAppCtx.getBean(SessionFactory.class);
			if (sessionFactory != null)
			{
				log.trace("session 2nd Attempt - ValueSet Read");
			}
			startRequest(theRequest);
			ValueSetDAO valueSetDAO = new ValueSetDAO(sessionFactory); 
			log.trace("Calling ValueSetDAO.read");
			
			MethodOutcome method = new MethodOutcome();
			method.setResource(valueSetDAO.read(theId));
			return (ValueSet) method.getResource();   //, theRequestDetails
	    }
	 
	 @Search()
	    public List<ValueSet> getValueSet(HttpServletRequest theRequest 
	    		,@OptionalParam(name = ValueSet.SP_NAME) StringParam theName 
	    		) {
		 
		 
		 
		  	 myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
			 	sessionFactory = myAppCtx.getBean(SessionFactory.class);
				if (sessionFactory != null)
				{
					log.trace("session 2nd Attempt - DocumentReference Read");
				}
				startRequest(theRequest);
				ValueSetDAO valueSetDAO = new ValueSetDAO(sessionFactory); 
				log.trace("Calling valueSetDAO.search");
		  	
			return valueSetDAO.search(theName);
		
	    }
}
