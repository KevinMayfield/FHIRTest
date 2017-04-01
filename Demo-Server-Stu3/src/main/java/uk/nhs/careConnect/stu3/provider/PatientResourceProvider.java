package uk.nhs.careConnect.stu3.provider;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.SessionFactory;



import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Patient;
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
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import uk.nhs.careConnect.stu3.dao.PatientDAO;



public class PatientResourceProvider extends BaseProvider implements IResourceProvider {

	@Override
	public Class<Patient> getResourceType() {
		// TODO Auto-generated method stub
		return Patient.class;
	}
	
	@Autowired
	protected SessionFactory sessionFactory;
	
	private static final Logger log = LoggerFactory.getLogger(PatientResourceProvider.class);
	
	private WebApplicationContext myAppCtx;
	
	@Create()
	public MethodOutcome createPatient(HttpServletRequest theRequest,@ResourceParam Patient thePatient) {
		
		log.trace("Called createPatient");
		MethodOutcome method = new MethodOutcome();
		method.setCreated(true);
		OperationOutcome opOutcome = new OperationOutcome();
		
		method.setOperationOutcome(opOutcome);
		Patient theNewPatient = null;
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
			PatientDAO patientDAO = new PatientDAO(sessionFactory); 
			log.trace("Calling patientDAO.create");
			theNewPatient = patientDAO.create(thePatient);
			log.debug("Return the New Patient id = "+theNewPatient.getId());
			// Read the new resource
			method.setResource(patientDAO.read(theNewPatient.getIdElement()));
			method.setId(theNewPatient.getIdElement());
			method.setCreated(true);
					
		}
		finally
		{
			//endRequest(theRequest);
			log.debug("Finished call createPatient");
		}
		return method;  //, theRequestDetails
	}
	/*
	@Operation(name = "everything", idempotent = true, bundleType=BundleTypeEnum.SEARCHSET)
	public ca.uhn.fhir.rest.server.IBundleProvider patientTypeEverything(
			
			javax.servlet.http.HttpServletRequest theServletRequest,
			
			@Description(formalDefinition="Results from this method are returned across multiple pages. This parameter controls the size of those pages.") 
			@OperationParam(name = Constants.PARAM_COUNT) 
			ca.uhn.fhir.model.primitive.UnsignedIntDt theCount			)
	{
			myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
		 	sessionFactory = myAppCtx.getBean(SessionFactory.class);
			if (sessionFactory != null)
			{
				log.trace("session 2nd Attempt - DocumentReference Read");
			}
			startRequest(theServletRequest);
			PatientDAO patientDAO = new PatientDAO(sessionFactory); 
			try {
				return ( patientDAO.patientTypeEverything(theServletRequest, theCount));
			} finally {
				endRequest(theServletRequest);
			}
	};
	*/
	 @Read()
	    public Patient getResourceById(HttpServletRequest theRequest,@IdParam IdType theId) {
		 
		 myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
		 	sessionFactory = myAppCtx.getBean(SessionFactory.class);
			if (sessionFactory != null)
			{
				log.trace("session 2nd Attempt - DocumentReference Read");
			}
			startRequest(theRequest);
			PatientDAO patientDAO = new PatientDAO(sessionFactory); 
			log.trace("Calling patientDAO.read");
			
			MethodOutcome method = new MethodOutcome();
			method.setResource(patientDAO.read(theId));
			return (Patient) method.getResource();   //, theRequestDetails
	    }
	 
	 @Search()
	    public List<Patient> getPatient(HttpServletRequest theRequest, 
	    		@OptionalParam(name = Patient.SP_FAMILY) StringParam familyName, 
	    		@OptionalParam(name=Patient.SP_GIVEN) StringParam givenName , 
	    		@OptionalParam(name=Patient.SP_ADDRESS_POSTALCODE) StringParam postCode,
	    		@OptionalParam(name=Patient.SP_BIRTHDATE) DateRangeParam birthDate,
	    		@OptionalParam(name=Patient.SP_IDENTIFIER) TokenParam identifier 
	    		) {
		 
		 
		 
		  	 myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
			 	sessionFactory = myAppCtx.getBean(SessionFactory.class);
				if (sessionFactory != null)
				{
					log.trace("session 2nd Attempt - DocumentReference Read");
				}
				startRequest(theRequest);
				PatientDAO patientDAO = new PatientDAO(sessionFactory); 
				log.trace("Calling patientDAO.search");
		  	
			return patientDAO.search(familyName, givenName, postCode, birthDate, identifier);
		
	    }
}
