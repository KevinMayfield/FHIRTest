package uk.nhs.careConnect.dstu2.provider;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.SessionFactory;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.CodeType;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.UriType;
import org.hl7.fhir.dstu3.model.Meta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.method.RequestDetails;
import ca.uhn.fhir.rest.param.StringParam;

import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import uk.nhs.careConnect.dstu2.dao.CodeSystemDAO;





public class CodeSystemResourceProvider extends BaseProvider implements IResourceProvider {

	public static final String OPERATION_NAME_LOOKUP = "$lookup";
	
	@Override
	public Class<CodeSystem> getResourceType() {
		// TODO Auto-generated method stub
		return CodeSystem.class;
	}
	
	@Autowired
	protected SessionFactory sessionFactory;
	
	private static final Logger log = LoggerFactory.getLogger(CodeSystemResourceProvider.class);
	
	private WebApplicationContext myAppCtx;
	
	@Operation(name=OPERATION_NAME_LOOKUP, idempotent=true, returnParameters= {
			@OperationParam(name="return", type=Meta.class)
		})
		//@formatter:on
		public Parameters lookupOperation(HttpServletRequest servletRequest,
				@OperationParam(name="code", min=0, max=1) CodeType code, 
				@OperationParam(name="system", min=0, max=1) UriType system,
				@OperationParam(name="coding", min=0, max=1) Coding coding, 
				RequestDetails requestDetails )
			{
			if (system == null) {
				throw new InvalidRequestException("Input contains no parameter with name 'system'");
			}
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
			
			 
			//startRequest(theRequest);
			CodeSystemDAO codeSystemDAO = new CodeSystemDAO(sessionFactory); 
			Parameters parameters = codeSystemDAO.lookupOperation(servletRequest,code,system,coding, requestDetails);
			return parameters;
			
		}
	
	@Create()
	public MethodOutcome createCodeSystem(HttpServletRequest theRequest,@ResourceParam CodeSystem theCodeSystem
			
			) {
		
		log.trace("Called createCodeSystem");
		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		OperationOutcome opOutcome = new OperationOutcome();
		
		method.setOperationOutcome(opOutcome);
		CodeSystem theNewCodeSystem = null;
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
			CodeSystemDAO valueSetDAO = new CodeSystemDAO(sessionFactory); 
			log.trace("Calling CodeSystemDAO.create");
			theNewCodeSystem = valueSetDAO.create(theCodeSystem);
			log.info("Return the New ValueSet id = "+theNewCodeSystem.getId());
			method.setId(theNewCodeSystem.getIdElement());
			method.setResource(theNewCodeSystem);
			
					
		}
		finally
		{
			//endRequest(theRequest);
			log.debug("Finished call createCodeSystem");
		}
		return method;  //, theRequestDetails
	}
	
	@Update()
	public MethodOutcome updateCodeSystem(HttpServletRequest theRequest,@ResourceParam CodeSystem theCodeSystem
			
			) {
		
		log.trace("Called createCodeSystem");
		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		OperationOutcome opOutcome = new OperationOutcome();
		
		method.setOperationOutcome(opOutcome);
		CodeSystem theNewCodeSystem = null;
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
			CodeSystemDAO codeSystemDAO = new CodeSystemDAO(sessionFactory); 
			log.trace("Calling CodeSystemDAO.create");
			theNewCodeSystem = codeSystemDAO.create(theCodeSystem);
			log.info("Return the New CodeSystem id = "+theNewCodeSystem.getId());
			method.setId(theNewCodeSystem.getIdElement());
			method.setResource(theNewCodeSystem);
			
					
		}
		finally
		{
			//endRequest(theRequest);
			log.debug("Finished call createCodeSystem");
		}
		return method;  //, theRequestDetails
	}
	
	
	 @Read()
	    public CodeSystem getResourceById(HttpServletRequest theRequest,@IdParam IdType theId) {
		 
		 myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
		 	sessionFactory = myAppCtx.getBean(SessionFactory.class);
			if (sessionFactory != null)
			{
				log.trace("session 2nd Attempt - ValueSet Read");
			}
			startRequest(theRequest);
			CodeSystemDAO codeSystemDAO = new CodeSystemDAO(sessionFactory); 
			log.trace("Calling CodeSystemDAO.read");
			
			MethodOutcome method = new MethodOutcome();
			method.setResource(codeSystemDAO.read(theId));
			return (CodeSystem) method.getResource();   //, theRequestDetails
	    }
	 
	 @Search()
	    public List<CodeSystem> getCodeSystem(HttpServletRequest theRequest 
	    		,@OptionalParam(name = CodeSystem.SP_NAME) StringParam theName 
	    		) {
		 
		 
		 
		  	 myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
			 	sessionFactory = myAppCtx.getBean(SessionFactory.class);
				if (sessionFactory != null)
				{
					log.trace("session 2nd Attempt - DocumentReference Read");
				}
				startRequest(theRequest);
				CodeSystemDAO codeSystemDAO = new CodeSystemDAO(sessionFactory); 
				log.trace("Calling CodeSystemDAO.search");
		  	
			return codeSystemDAO.search(theName);
		
	    }
}
