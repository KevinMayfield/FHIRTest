package uk.nhs.careConnect.dstu2.dao;


import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.resource.DocumentReference;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.method.RequestDetails;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.instance.model.api.IBaseMetaType;
import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.nhs.careConnect.entity.DocumentReferenceEntity;
import uk.nhs.careConnect.entity.DocumentReferenceIdentifier;
import uk.nhs.careConnect.entity.PatientEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;





public class DocumentReferenceDAO extends BaseDAO<DocumentReference>
implements IDocumentReferenceDAO {
	
	private static final Logger log = LoggerFactory.getLogger(DocumentReferenceDAO.class);
	
	public DocumentReferenceDAO() {
	    super(DocumentReference.class);
	}
	
	public DocumentReferenceDAO(EntityManagerFactory entityManagerFactory) {
		super(DocumentReference.class);
		this.emf = entityManagerFactory;
	}
	
	@Override
	public Class<DocumentReference> getResourceType() {
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
	public DocumentReference create(DocumentReference documentReference) {
		log.trace("called DocumentReferenceDAO create");
		
		try
		{
			
			EntityManager em = emf.createEntityManager();
			log.trace("Obtained entityManager");
			em.getTransaction().begin();
			
			log.trace("Call persist");
			DocumentReferenceEntity edr = new DocumentReferenceEntity();
			ConceptDAO conceptDAO = new ConceptDAO(this.emf);
			
			if (documentReference.getType().getCoding().size()>0)
			{
				//edr.setTypeSnmdCT(documentReference.getType().getCoding().get(0).getCode());
				//edr.setTypeSnmdCTName(documentReference.getType().getCoding().get(0).getDisplay());
				// Replacement linked to term_concept - Note hard coded code system
				for (CodingDt concept : documentReference.getType().getCoding())
				{
					if (concept.getSystem().contains("http://snomed.info/sct"))
					{
						edr.setTypeCT(conceptDAO.search(concept.getCode(), 1));
					}
				}
				
			}
			
			
			log.trace("Call getPracticeSetting");
			if (documentReference.getContext() !=null && !documentReference.getContext().isEmpty() && documentReference.getContext().getPracticeSetting() != null && documentReference.getContext().getPracticeSetting().getCoding().size() > 0)
			{
				//edr.setPracticeSettingSnmdCT(documentReference.getContext().getPracticeSetting().getCoding().get(0).getCode());
				//edr.setPracticeSettingSnmdCTName(documentReference.getContext().getPracticeSetting().getCoding().get(0).getDisplay());
				// Replacement linked to term_concept - Note hard coded code system
				edr.setPracticeSettingCT(conceptDAO.search(documentReference.getContext().getPracticeSetting().getCoding().get(0).getCode(), 1));
			}
			if (documentReference.getSubject() != null && !documentReference.getSubject().isEmpty())
			{
				PatientDAO patientDAO = new PatientDAO(this.emf);
				
				StringParam nullP = null;
				DateRangeParam nullD = null;
				TokenParam token = null;
				log.info("documentReference.getPatient().getReference() = "+documentReference.getSubject().getReference());
				
				
				if (documentReference.getSubject().getReference().getValue().contains("http://fhir.leedsth.nhs.uk/PAS/Patient/"))
				{
					log.info("documentReference.getPatient() PAS Replace = "+documentReference.getSubject().getReference().getValue().replace("http://fhir.leedsth.nhs.uk/PAS/Patient/", ""));
					token = new TokenParam("http://fhir.leedsth.nhs.uk/PAS/Patient/",documentReference.getSubject().getReference().getValue().replace("http://fhir.leedsth.nhs.uk/PAS/Patient/", ""));
				}
				if (documentReference.getSubject().getReference().getValue().contains("http://fhir.nhs.net/Id/nhs-number/"))
				{
					log.info("documentReference.getPatient() NHSNumber Replace = "+documentReference.getSubject().getReference().getValue().replace("http://fhir.nhs.net/Id/nhs-number/", ""));
					token = new TokenParam("http://fhir.nhs.net/Id/nhs-number/",documentReference.getSubject().getReference().getValue().replace("http://fhir.nhs.net/Id/nhs-number/", ""));
				}
				if (token != null)
				{
					log.info("Running Patient Search token.getSystem()="+token.getSystem()+" token.getValue()="+token.getValue());
					List<Patient> patients = patientDAO.search(nullP, nullP  , nullP , nullD , token);
					if (patients.size()>0)
					{
						log.info("Found patient with id = "+patients.get(0).getId().getIdPart()  );
						edr.setPatientEntity((PatientEntity) em.find(PatientEntity.class,Integer.parseInt(patients.get(0).getIdElement().getIdPart())));
					}
					else
					{
						log.info("No results found Patient Search");
					}
				}
			}
			
			if (documentReference.getAuthor()!=null && !documentReference.getAuthor().isEmpty())
			{
				if (documentReference.getAuthor().get(0).getDisplay() != null)
				{
					edr.setAuthourDisplay(documentReference.getAuthor().get(0).getDisplay().getValue());
				}
				if (documentReference.getAuthor().get(0).getReference() != null)
				{
					edr.setAuthourRef(documentReference.getAuthor().get(0).getReference().getValue());
				}
			}
			
			//edr.setPASPatient(documentReference.getSubject().getReference().replace("http://fhir.leedsth.nhs.uk/PAS/Patient/",""));
			log.trace("Built DocumentReference entity");
			
			edr.setCreated(documentReference.getCreated());
			edr.setIndexed(documentReference.getIndexed());
			if (documentReference.getContent().size()>0)
			{
				edr.setAttachmentContentType(documentReference.getContent().get(0).getAttachment().getContentType());
				edr.setAttachmentUri(documentReference.getContent().get(0).getAttachment().getUrl());
				edr.setAttachmentTitle(documentReference.getContent().get(0).getAttachment().getTitle());
			}
			log.trace("Call em.persist edr");
			em.persist(edr);
		
			List<DocumentReferenceIdentifier> drids = new ArrayList<DocumentReferenceIdentifier>();
			for(int f=0;f<documentReference.getIdentifier().size();f++)
			{
				DocumentReferenceIdentifier dri = new DocumentReferenceIdentifier(edr);
				dri.setSystem(documentReference.getIdentifier().get(f).getSystem());
				dri.setValue(documentReference.getIdentifier().get(f).getValue());
				edr.setIdentifiers(drids);
				log.trace("Call em.persist dri");    
				em.persist(dri);
			}
			

			
			if (documentReference.getMasterIdentifier() != null && !documentReference.getMasterIdentifier().isEmpty())
			{
				DocumentReferenceIdentifier dri = new DocumentReferenceIdentifier(edr);
				dri.setSystem(documentReference.getMasterIdentifier().getSystem());
				dri.setValue(documentReference.getMasterIdentifier().getValue());
				dri.setMasterIdentifier(true);
				edr.setIdentifiers(drids);
				log.trace("Call em.persist dri");    
				em.persist(dri);
			}
			
			em.getTransaction().commit();
			
			log.info("Called PERSIST id="+edr.getId().toString());
			documentReference.setId(edr.getId().toString());
			
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
		return documentReference;	
	}

	private DocumentReference convert(DocumentReferenceEntity edr)
	{
		DocumentReference docRef = new DocumentReference();
		
		for(int f=0;f<edr.getIdentifiers().size();f++)
		{
			if (edr.getIdentifiers().get(f).getMasterIdentifier() != null && edr.getIdentifiers().get(f).getMasterIdentifier().booleanValue())
			{
				IdentifierDt identifier = new IdentifierDt();
				if (edr.getIdentifiers().get(f).getSystem() != null)
				{
					identifier.setSystem(edr.getIdentifiers().get(f).getSystem());
				}
				if (edr.getIdentifiers().get(f).getValue() != null)
				{
					identifier.setValue(edr.getIdentifiers().get(f).getValue());
				}
				docRef.setMasterIdentifier(identifier);
		
			}
			else
			{
				docRef.addIdentifier()
					.setSystem(edr.getIdentifiers().get(f).getSystem())
					.setValue(edr.getIdentifiers().get(f).getValue());
			}
			
		}
		docRef.setId(edr.getId().toString());
		
		if (edr.getTypeCT() != null)
		{
			CodeableConceptDt typeCode = new CodeableConceptDt();
			typeCode.addCoding()
				.setCode(edr.getTypeCT().getCode())
				.setDisplay(edr.getTypeCT().getDisplay())
				.setSystem("http://snomed.info/sct");
			docRef.setType(typeCode);
		}
		
		if (edr.getTypeLocalCT() != null)
		{
			CodeableConceptDt typeCode = new CodeableConceptDt();
			typeCode.addCoding()
				.setCode(edr.getTypeLocalCT().getCode())
				.setDisplay(edr.getTypeLocalCT().getDisplay())
				.setSystem(edr.getTypeLocalCT().getTermCodeSystem().getCodeSystemUri());

			
		}
		if (edr.getResourceMessage() != null)
		{
			
			ResourceReferenceDt msgRef = new ResourceReferenceDt(edr.getResourceMessage());
			

		}
		
		if (edr.getPatientEntity() != null)
		{
			String PASNumber = edr.getPatientEntity().getId().toString();
			docRef.setSubject(new ResourceReferenceDt("Patient/"+PASNumber));
		}
		docRef.setCreated(new DateTimeDt(edr.getCreated()));
		
		docRef.setIndexed(new InstantDt(edr.getIndexed()));
		ResourceReferenceDt auth= docRef.addAuthor();
		if (edr.getAuthourDisplay()!=null)
		{
			auth.setDisplay(edr.getAuthourDisplay());
		}
		if (edr.getAuthourRef()!=null)
		{
			auth.setReference(edr.getAuthourRef());
		}
		DocumentReference.Content contentComponent = new DocumentReference.Content();
		AttachmentDt attach = new AttachmentDt();
		attach.setContentType(edr.getAttachmentContentType());
		attach.setUrl(edr.getAttachmentUri());
		attach.setTitle(edr.getAttachmentTitle());
		contentComponent.setAttachment(attach);
		
		docRef.addContent(contentComponent);
		
		DocumentReference.Context contextComponent = new DocumentReference.Context();
		
		if (edr.getPracticeSettingCT() != null)
		{
			CodeableConceptDt classCode = new CodeableConceptDt();
			classCode.addCoding()
				.setCode(edr.getPracticeSettingCT().getCode())
				.setDisplay(edr.getPracticeSettingCT().getDisplay())
				.setSystem("http://snomed.info/sct");
			contextComponent.setPracticeSetting(classCode);
		}
		docRef.setContext(contextComponent);
		
		
		return docRef;
	}
	@Override
	public DocumentReference  read(IIdType theId) {
		log.trace("called read theId="+ theId.toString());
		log.trace("called read Id="+ theId.getIdPart());
		DocumentReference docRef  = null;
		try
		{
			
			EntityManager em = emf.createEntityManager();
			log.info("Obtained entityManager DocumentReference.read");
			
			
			DocumentReferenceEntity edr = (DocumentReferenceEntity) em.find(DocumentReferenceEntity.class,Integer.parseInt(theId.getIdPart()));
			
			docRef = convert(edr);
			
			em.close();
	        log.info("Built the DocumentReference");
	       	
			return docRef;
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
		method.setResource(docRef);
		return docRef;
	}

	
	public List<DocumentReference> search(
			@OptionalParam(name = DocumentReference.SP_TYPE) TokenParam theType, 
			@OptionalParam(name=DocumentReference.SP_SETTING) TokenParam theSpeciality, 
			@OptionalParam(name=DocumentReference.SP_SUBJECT) ReferenceParam thePatient,
			@OptionalParam(name=DocumentReference.SP_CREATED) DateRangeParam created,
			@OptionalParam(name=DocumentReference.SP_INDEXED) DateRangeParam indexed,
			@OptionalParam(name=DocumentReference.SP_IDENTIFIER) TokenParam identifier
			) {
		
		List<DocumentReferenceEntity> qryResults = null;
		List<DocumentReference> results = new ArrayList<DocumentReference>();
		try
		{
			// https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#criteria
			
			EntityManager em = emf.createEntityManager();
			
			CriteriaBuilder builder = em.getCriteriaBuilder();
		    
		    CriteriaQuery<DocumentReferenceEntity> criteria = builder.createQuery(DocumentReferenceEntity.class);
		   	Root<DocumentReferenceEntity> root = criteria.from(DocumentReferenceEntity.class);
		   	Join<DocumentReferenceEntity, DocumentReferenceIdentifier> join = root.join("identifiers", JoinType.LEFT);
		   	Join<DocumentReferenceEntity, PatientEntity> patjoin = root.join("patientEntity", JoinType.LEFT);
		    
		   	List<Predicate> predList = new LinkedList<Predicate>();
		   	
		    if (thePatient != null)
		    {
		    	Predicate p = builder.equal(patjoin.get("id"),thePatient.getValue());
		    	predList.add(p);
		    	log.info("thePatient = "+thePatient.toString());
		    	
		    }
		    if (theType != null)
		    {
		    	Predicate p = builder.equal(root.get("typeSnmdCT"),theType.getValue());
		    	predList.add(p);
		    	log.info("theType = "+theType.toString());
		    	
		    }
		    if (theSpeciality != null)
		    {
		    	Predicate p = builder.equal(root.get("practiceSettingSnmdCT"),theSpeciality.getValue());
		    	predList.add(p);
		    	log.info("theSpeciality = "+theSpeciality.toString());
		    }
		    if (identifier != null)
		    {
		    	//.get("value")
		    	Predicate p = builder.equal(join.get("value"),identifier.getValue());
		    	predList.add(p);
		    	predList.add(builder.equal(join.get("masterIdentifier"),true));
		    	log.info("identifier = "+identifier.toString());
		    }
		    if (created !=null)
		    {
		    	
		    	Date from = created.getLowerBoundAsInstant();
		    	Date to = created.getUpperBoundAsInstant();
		    	  
		    	switch (created.getValuesAsQueryTokens().get(0).getPrefix())
		    	{
		    		case GREATERTHAN :
		    		{
		    			Predicate p = builder.greaterThan(root.<Date>get("created"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case GREATERTHAN_OR_EQUALS :
		    		{
		    			Predicate p = builder.greaterThanOrEqualTo(root.<Date>get("created"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case APPROXIMATE :
		    		case EQUAL :
		    		{
		    			Predicate p = builder.equal(root.<Date>get("created"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case STARTS_AFTER :
		    		{
		    			Predicate p = builder.greaterThan(root.<Date>get("created"),from);
		    			predList.add(p);
		    			break;
		    			
		    		}
		    		case NOT_EQUAL :
		    		{
		    			Predicate p = builder.notEqual(root.<Date>get("created"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case LESSTHAN_OR_EQUALS :
		    		{
		    			Predicate p = builder.lessThanOrEqualTo(root.<Date>get("created"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case ENDS_BEFORE :
		    		case LESSTHAN :
		    		{
		    			Predicate p = builder.lessThan(root.<Date>get("created"),from);
		    			predList.add(p);
		    			break;
		    		}
		    	}
		    	if (created.getValuesAsQueryTokens().size()>1)
		    	{
		    		switch (created.getValuesAsQueryTokens().get(1).getPrefix())
			    	{
			    		case GREATERTHAN :
			    		{
			    			Predicate p = builder.greaterThan(root.<Date>get("created"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case GREATERTHAN_OR_EQUALS :
			    		{
			    			Predicate p = builder.greaterThanOrEqualTo(root.<Date>get("created"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case APPROXIMATE :
			    		case EQUAL :
			    		{
			    			Predicate p = builder.equal(root.<Date>get("created"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case STARTS_AFTER :
			    		{
			    			Predicate p = builder.greaterThan(root.<Date>get("created"),to);
			    			predList.add(p);
			    			break;
			    			
			    		}
			    		case NOT_EQUAL :
			    		{
			    			Predicate p = builder.notEqual(root.<Date>get("created"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case LESSTHAN_OR_EQUALS :
			    		{
			    			Predicate p = builder.lessThanOrEqualTo(root.<Date>get("created"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case ENDS_BEFORE :
			    		case LESSTHAN :
			    		{
			    			Predicate p = builder.lessThan(root.<Date>get("created"),to);
			    			predList.add(p);
			    			break;
			    		}
			    	}
		    	}
		    	
		    	
		    	log.info("created = "+created.toString());
		    }
		    
		    if (indexed !=null)
		    {
		    	
		    	Date from = indexed.getLowerBoundAsInstant();
		    	Date to = indexed.getUpperBoundAsInstant();
		    	  
		    	switch (indexed.getValuesAsQueryTokens().get(0).getPrefix())
		    	{
		    		case GREATERTHAN :
		    		{
		    			Predicate p = builder.greaterThan(root.<Date>get("indexed"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case GREATERTHAN_OR_EQUALS :
		    		{
		    			Predicate p = builder.greaterThanOrEqualTo(root.<Date>get("indexed"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case APPROXIMATE :
		    		case EQUAL :
		    		{
		    			Predicate p = builder.equal(root.<Date>get("indexed"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case STARTS_AFTER :
		    		{
		    			Predicate p = builder.greaterThan(root.<Date>get("indexed"),from);
		    			predList.add(p);
		    			break;
		    			
		    		}
		    		case NOT_EQUAL :
		    		{
		    			Predicate p = builder.notEqual(root.<Date>get("indexed"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case LESSTHAN_OR_EQUALS :
		    		{
		    			Predicate p = builder.lessThanOrEqualTo(root.<Date>get("indexed"),from);
		    			predList.add(p);
		    			break;
		    		}
		    		case ENDS_BEFORE :
		    		case LESSTHAN :
		    		{
		    			Predicate p = builder.lessThan(root.<Date>get("indexed"),from);
		    			predList.add(p);
		    			break;
		    		}
		    	}
		    	if (indexed.getValuesAsQueryTokens().size()>1)
		    	{
		    		switch (indexed.getValuesAsQueryTokens().get(1).getPrefix())
			    	{
			    		case GREATERTHAN :
			    		{
			    			Predicate p = builder.greaterThan(root.<Date>get("indexed"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case GREATERTHAN_OR_EQUALS :
			    		{
			    			Predicate p = builder.greaterThanOrEqualTo(root.<Date>get("indexed"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case APPROXIMATE :
			    		case EQUAL :
			    		{
			    			Predicate p = builder.equal(root.<Date>get("indexed"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case STARTS_AFTER :
			    		{
			    			Predicate p = builder.greaterThan(root.<Date>get("indexed"),to);
			    			predList.add(p);
			    			break;
			    			
			    		}
			    		case NOT_EQUAL :
			    		{
			    			Predicate p = builder.notEqual(root.<Date>get("indexed"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case LESSTHAN_OR_EQUALS :
			    		{
			    			Predicate p = builder.lessThanOrEqualTo(root.<Date>get("indexed"),to);
			    			predList.add(p);
			    			break;
			    		}
			    		case ENDS_BEFORE :
			    		case LESSTHAN :
			    		{
			    			Predicate p = builder.lessThan(root.<Date>get("indexed"),to);
			    			predList.add(p);
			    			break;
			    		}
			    	}
		    	}
		    	
		    	
		    	log.info("indexed = "+indexed.toString());
		    }
		    
		    Predicate[] predArray = new Predicate[predList.size()];
		    predList.toArray(predArray);
		    if (predList.size()>0)
		    {
		    	criteria.select(root).distinct(true).where(predArray);
		    }
		    else
		    {
		    	criteria.select(root);
		    }
		    
		    qryResults = em.createQuery(criteria).getResultList();
		    
		    for (DocumentReferenceEntity doc : qryResults)
		    {
		    	log.trace("HAPI Custom = "+doc.getId());
		    	DocumentReference docRef = convert(doc);
		    	results.add(docRef);
		    }
		    log.debug("Custom HAPI Count = "+qryResults.size());
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
		
        //return Collections. singletonList(documentReference);
	}
	
	
	
/*
@Override
public List<DocumentReferenceEntity> findAll(boolean withBids) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    
    CriteriaQuery<Item> criteria = cb.createQuery(Item.class);
    Root<Item> i = criteria.from(Item.class);
    criteria.select(i)
        .distinct(true) // In-memory "distinct"!
        .orderBy(cb.asc(i.get("auctionEnd")));
    if (withBids)
        i.fetch("bids", JoinType.LEFT);
   
    return em.createQuery(criteria).getResultList();
}

@Override
public List<Item> findByName(String name, boolean substring) {
    return em.createNamedQuery(
        substring ? "getItemsByNameSubstring" : "getItemsByName"
    ).setParameter(
        "itemName",
        substring ? ("%" + name + "%") : name
    ).getResultList();
}

@Override
public List<ItemBidSummary> findItemBidSummaries() {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<ItemBidSummary> criteria =
        cb.createQuery(ItemBidSummary.class);
    Root<Item> i = criteria.from(Item.class);
    Join<Item, Bid> b = i.join("bids", JoinType.LEFT);
    criteria.select(
        cb.construct(
            ItemBidSummary.class,
            i.get("id"), i.get("name"), i.get("auctionEnd"),
            cb.max(b.<BigDecimal>get("amount"))
        )
    );
    criteria.orderBy(cb.asc(i.get("auctionEnd")));
    criteria.groupBy(i.get("id"), i.get("name"), i.get("auctionEnd"));
    return em.createQuery(criteria).getResultList();
}
*/
}
