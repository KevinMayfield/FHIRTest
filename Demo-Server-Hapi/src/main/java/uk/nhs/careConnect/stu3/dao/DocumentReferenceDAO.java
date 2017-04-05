package uk.nhs.careConnect.stu3.dao;


import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DocumentReference;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.instance.model.api.IBaseMetaType;

import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.method.RequestDetails;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import uk.nhs.careConnect.entity.PatientEntity;
import uk.nhs.careConnect.entity.DocumentReferenceEntity;
import uk.nhs.careConnect.entity.DocumentReferenceIdentifier;


//import java.util.List;

//import javax.persistence.criteria.CriteriaBuilder;



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
				for (Coding concept : documentReference.getType().getCoding())
				{
					if (concept.getSystem().contains("http://snomed.info/sct"))
					{
						edr.setTypeCT(conceptDAO.search(concept.getCode(), 1));
					}
				}
				
			}
			
			
			log.trace("Call getPracticeSetting");
			if (documentReference.hasContext() && documentReference.getContext().hasPracticeSetting() && documentReference.getContext().getPracticeSetting().getCoding().size() > 0)
			{
				//edr.setPracticeSettingSnmdCT(documentReference.getContext().getPracticeSetting().getCoding().get(0).getCode());
				//edr.setPracticeSettingSnmdCTName(documentReference.getContext().getPracticeSetting().getCoding().get(0).getDisplay());
				// Replacement linked to term_concept - Note hard coded code system
				edr.setPracticeSettingCT(conceptDAO.search(documentReference.getContext().getPracticeSetting().getCoding().get(0).getCode(), 1));
			}
			if (documentReference.hasSubject())
			{
				PatientDAO patientDAO = new PatientDAO(this.emf);
				
				StringParam nullP = null;
				DateRangeParam nullD = null;
				TokenParam token = null;
				log.info("documentReference.getPatient().getReference() = "+documentReference.getSubject().getReference());
				
				
				if (documentReference.getSubject().getReference().contains("http://fhir.leedsth.nhs.uk/PAS/Patient/"))
				{
					log.info("documentReference.getPatient() PAS Replace = "+documentReference.getSubject().getReference().replace("http://fhir.leedsth.nhs.uk/PAS/Patient/", ""));
					token = new TokenParam("http://fhir.leedsth.nhs.uk/PAS/Patient/",documentReference.getSubject().getReference().replace("http://fhir.leedsth.nhs.uk/PAS/Patient/", ""));
				}
				if (documentReference.getSubject().getReference().contains("http://fhir.nhs.net/Id/nhs-number/"))
				{
					log.info("documentReference.getPatient() NHSNumber Replace = "+documentReference.getSubject().getReference().replace("http://fhir.nhs.net/Id/nhs-number/", ""));
					token = new TokenParam("http://fhir.nhs.net/Id/nhs-number/",documentReference.getSubject().getReference().replace("http://fhir.nhs.net/Id/nhs-number/", ""));
				}
				if (token != null)
				{
					log.info("Running Patient Search token.getSystem()="+token.getSystem()+" token.getValue()="+token.getValue());
					List<Patient> patients = patientDAO.search(nullP, nullP  , nullP , nullD , token);
					if (patients.size()>0)
					{
						log.info("Found patient with id = "+patients.get(0).getIdElement().getIdPart()  );
						edr.setPatientEntity((PatientEntity) em.find(PatientEntity.class,Integer.parseInt(patients.get(0).getIdElement().getIdPart())));
					}
					else
					{
						log.info("No results found Patient Search");
					}
				}
			}
			
			if (documentReference.hasAuthor())
			{
				if (documentReference.getAuthor().get(0).getDisplay() != null)
				{
					edr.setAuthourDisplay(documentReference.getAuthor().get(0).getDisplay());
				}
				if (documentReference.getAuthor().get(0).getReference() != null)
				{
					edr.setAuthourRef(documentReference.getAuthor().get(0).getReference());
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
			
			for (Extension extension : documentReference.getExtension())
			{
				if (extension.getUrl().equals("https://fhir.leedsth.nhs.uk/Extension/MessageReference"))
				{
					Reference value = (Reference) extension.getValue();
					edr.setResourceMessage(value.getReference());
				}
				if (extension.getUrl().equals("https://fhir.leedsth.nhs.uk/Extension/WinDipInformationType"))
				{
					CodeableConcept concept = (CodeableConcept) extension.getValue();
					if (concept.hasCoding())
					{
						if (concept.getCoding().get(0).getSystem().contains("http://fhir.leedsth.nhs.uk/ValueSet/leedsth-windip-information-type-1"))
						{
							edr.setTypeLocalCT(conceptDAO.search(concept.getCoding().get(0).getCode(), 2));
						}
					}
				}
			}
			
			if (documentReference.hasMasterIdentifier())
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
				Identifier identifier = new Identifier();
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
			CodeableConcept typeCode = new CodeableConcept();
			typeCode.addCoding()
				.setCode(edr.getTypeCT().getCode())
				.setDisplay(edr.getTypeCT().getDisplay())
				.setSystem("http://snomed.info/sct");
			docRef.setType(typeCode);
		}
		
		if (edr.getTypeLocalCT() != null)
		{
			CodeableConcept typeCode = new CodeableConcept();
			typeCode.addCoding()
				.setCode(edr.getTypeLocalCT().getCode())
				.setDisplay(edr.getTypeLocalCT().getDisplay())
				.setSystem(edr.getTypeLocalCT().getTermCodeSystem().getCodeSystemUri());
			docRef.addExtension() 
					.setUrl("https://fhir.leedsth.nhs.uk/Extension/WinDipInformationType")
					.setValue(typeCode);
			
		}
		if (edr.getResourceMessage() != null)
		{
			
			Reference msgRef = new Reference(edr.getResourceMessage());
			
			docRef.addExtension()
				.setUrl("https://fhir.leedsth.nhs.uk/Extension/MessageReference")
				.setValue(msgRef);
		}
		
		if (edr.getPatientEntity() != null)
		{
			String PASNumber = edr.getPatientEntity().getId().toString();
			docRef.setSubject(new Reference("Patient/"+PASNumber));
		}
		docRef.setCreated(edr.getCreated());
		
		docRef.setIndexed(edr.getIndexed());
		Reference auth= docRef.addAuthor();
		if (edr.getAuthourDisplay()!=null)
		{
			auth.setDisplay(edr.getAuthourDisplay());
		}
		if (edr.getAuthourRef()!=null)
		{
			auth.setReference(edr.getAuthourRef());
		}
		DocumentReference.DocumentReferenceContentComponent contentComponent = new DocumentReference.DocumentReferenceContentComponent();
		Attachment attach = new Attachment();
		attach.setContentType(edr.getAttachmentContentType());
		attach.setUrl(edr.getAttachmentUri());
		attach.setTitle(edr.getAttachmentTitle());
		contentComponent.setAttachment(attach);
		
		docRef.addContent(contentComponent);
		
		DocumentReference.DocumentReferenceContextComponent contextComponent = new DocumentReference.DocumentReferenceContextComponent(); 
		
		if (edr.getPracticeSettingCT() != null)
		{
			CodeableConcept classCode = new CodeableConcept();
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
	public DocumentReference  read(IdType theId) {
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
