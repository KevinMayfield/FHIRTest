package uk.nhs.careConnect.dstu2.provider;

import ca.uhn.fhir.model.dstu2.resource.DocumentReference;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hibernate.SessionFactory;
import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import uk.nhs.careConnect.dstu2.dao.DocumentReferenceDAO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;



public class DocumentReferenceResourceProvider extends BaseProvider implements IResourceProvider {

	@Override
	public Class<DocumentReference> getResourceType() {
		// TODO Auto-generated method stub
		return DocumentReference.class;
	}
	
	@Autowired
	protected SessionFactory sessionFactory;
	
	private static final Logger log = LoggerFactory.getLogger(DocumentReferenceResourceProvider.class);
	
	private WebApplicationContext myAppCtx;
	
	@Create()
	public MethodOutcome createDocumentReference(HttpServletRequest theRequest,@ResourceParam DocumentReference theDocRef) {
		
		log.trace("Called createDocumentReference");
		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		OperationOutcome opOutcome = new OperationOutcome();
		
		method.setOperationOutcome(opOutcome);
		DocumentReference theNewDocRef = null;
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
			DocumentReferenceDAO docRefDAO = new DocumentReferenceDAO(sessionFactory); 
			log.trace("Calling documentReferenceDAO.create");
			theNewDocRef = docRefDAO.create(theDocRef);
			log.debug("Return the New DocumentReference id = "+theDocRef.getId());
			method.setId(theDocRef.getIdElement());
			method.setResource(docRefDAO.read(theNewDocRef.getIdElement()));
			
					
		}
		finally
		{
			//endRequest(theRequest);
			log.debug("Finished call createDocumentReference");
		}
		return method;  //, theRequestDetails
	}
	
	
	 @Read()
	    public DocumentReference getResourceById(HttpServletRequest theRequest,@IdParam IIdType theId) {
		 
		 myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
		 	sessionFactory = myAppCtx.getBean(SessionFactory.class);
			if (sessionFactory != null)
			{
				log.trace("session 2nd Attempt - DocumentReference Read");
			}
			startRequest(theRequest);
			DocumentReferenceDAO docRefDAO = new DocumentReferenceDAO(sessionFactory); 
			log.trace("Calling documentReferenceDAO.read");
			
			MethodOutcome method = new MethodOutcome();
			method.setResource(docRefDAO.read(theId));
			return (DocumentReference) method.getResource();   //, theRequestDetails
	    }
	 
	 @Search()
	    public List<DocumentReference> getDocumentReference(HttpServletRequest theRequest, 
	    		@OptionalParam(name = DocumentReference.SP_TYPE) TokenParam theType, 
	    		@OptionalParam(name=DocumentReference.SP_SETTING) TokenParam theSpecialty, 
	    		@OptionalParam(name=DocumentReference.SP_SUBJECT) ReferenceParam thePatient,
				@OptionalParam(name=DocumentReference.SP_CREATED) DateRangeParam created,
				@OptionalParam(name=DocumentReference.SP_INDEXED) DateRangeParam indexed,
				@OptionalParam(name=DocumentReference.SP_IDENTIFIER) TokenParam identifier) {
		 
		 
		 
		 	// KGM 2016-12-12 Initial work on searching
		 
		  	 myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
			 	sessionFactory = myAppCtx.getBean(SessionFactory.class);
				if (sessionFactory != null)
				{
					log.trace("session 2nd Attempt - DocumentReference Read");
				}
				startRequest(theRequest);
				DocumentReferenceDAO docRefDAO = new DocumentReferenceDAO(sessionFactory); 
				log.trace("Calling documentReferenceDAO.search");
		  	
			return docRefDAO.search(theType, theSpecialty, thePatient, created, indexed, identifier);
		
	    }
}
