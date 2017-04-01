package uk.nhs.careConnect.stu3.dao;


import java.util.ArrayList;
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
import javax.servlet.http.HttpServletRequest;


import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.CodeType;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.UriType;
import org.hl7.fhir.instance.model.api.IBaseMetaType;
import org.hl7.fhir.instance.model.api.IIdType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.method.RequestDetails;
import ca.uhn.fhir.rest.param.StringParam;
import uk.nhs.careConnect.stu3.provider.CodeSystemResourceProvider;
import uk.nhs.careConnect.entity.TermCodeSystem;
import uk.nhs.careConnect.entity.TermConcept;
import uk.nhs.careConnect.entity.TermConceptDescription;
import uk.nhs.careConnect.entity.TermConceptParentChildLink;
import uk.nhs.careConnect.entity.TermConceptReferenceSet;
import uk.nhs.careConnect.entity.TermConceptRefsetMap;
import uk.nhs.careConnect.entity.TermConceptRelationship;


public class CodeSystemDAO extends BaseDAO<CodeSystem>
implements ICodeSystemDAO {
	
	private static final Logger log = LoggerFactory.getLogger(CodeSystemResourceProvider.class);
	
	public CodeSystemDAO() {
	    super(CodeSystem.class);
	}
	
	public CodeSystemDAO(EntityManagerFactory entityManagerFactory) {
		super(CodeSystem.class);
		this.emf = entityManagerFactory;
	}
	
	@Override
	public Class<CodeSystem> getResourceType() {
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

	private EntityManager em;
	
	@Override
	public CodeSystem create(CodeSystem codeSystem) {
		log.trace("called CodeSystemDAO create");
		
		try
		{
			
			EntityManager em = emf.createEntityManager();
			log.trace("Obtained entityManager");
			em.getTransaction().begin();
			//ConceptDAO conceptDAO = new ConceptDAO(this.emf);
			
			log.trace("Call persist");
			
			TermCodeSystem vse = null;
			if (codeSystem.hasId())
			{
				vse = (TermCodeSystem) em.find(TermCodeSystem.class,codeSystem.getId());
				// if null try a search on strId
				if (vse == null)
				{
					CriteriaBuilder builder = em.getCriteriaBuilder();
			    
					CriteriaQuery<TermCodeSystem> criteria = builder.createQuery(TermCodeSystem.class);
					Root<TermCodeSystem> root = criteria.from(TermCodeSystem.class);
			   		List<Predicate> predList = new LinkedList<Predicate>();
			   		Predicate p = builder.equal(root.<String>get("strId"),codeSystem.getId());
	    			predList.add(p);
			   		Predicate[] predArray = new Predicate[predList.size()];
			   		predList.toArray(predArray);
			   		if (predList.size()>0)
			   		{
			   			criteria.select(root).where(predArray);
			    
			   			List<TermCodeSystem> qryResults = em.createQuery(criteria).getResultList();
			    
				    	for (TermCodeSystem cme : qryResults)
				    	{
				    		log.trace("HAPI Custom = "+cme.getPID());	    	
				    		vse = cme;
				    		break;
				    	}
			   		}
			    }
			}
			if (vse == null)
			{
				vse = new TermCodeSystem();
			}
			if (codeSystem.hasId())
			{
				//vse.set StrId(codeSystem.getId());
			}
			if (codeSystem.hasUrl())
			{
				//vse.setUrl(codeSystem.getUrl());
			}
			if (codeSystem.hasName())
			{
				//vse.setName(codeSystem.getName());
			}
			if (codeSystem.hasStatus())
			{
				//vse.setStatus(codeSystem.getStatus());
			}
			if (codeSystem.hasDescription())
			{
				//vse.setDescription(codeSystem.getDescription());
			}
			
			log.info("Call em.persist ValueSetEntity");
			em.persist(vse);
				em.getTransaction().commit();
			
			log.info("Called PERSIST id="+vse.getPID().toString());
			codeSystem.setId(vse.getPID().toString());
			
			em.close();
			
			log.debug("Finished call to persist ValueSet");
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
		return codeSystem;	
	}
	
	public Parameters lookupOperation(HttpServletRequest servletRequest,
			@OperationParam(name="code", min=0, max=1) CodeType code, 
			@OperationParam(name="system", min=0, max=1) UriType system,
			@OperationParam(name="coding", min=0, max=1) Coding coding, 
			RequestDetails requestDetails ) 
	{
		Parameters parameters = new Parameters();
		Boolean getChild = false;
		Boolean getParent = false;
		Boolean getDesignation = false;
		Boolean getRelationships = false;
		Boolean getReferences = false;
		Boolean getPartOfReferences = false;
		Boolean getMaps = false;
		
		String[] format = requestDetails.getParameters().get("property");
		if (format != null) {
			for (String nextFormat : format) {
				if (nextFormat.equals("child"))
				{
					getChild = true;
				}
				if (nextFormat.equals("parent"))
				{
					getParent = true;
				}
				if (nextFormat.equals("designation"))
				{
					getDesignation = true;
				}
				if (nextFormat.equals("reference"))
				{
					getReferences = true;
				}
				if (nextFormat.equals("partOfReference"))
				{
					getPartOfReferences = true;
				}
				if (nextFormat.equals("relationships"))
				{
					getRelationships = true;
				}
				if (nextFormat.equals("maps"))
				{
					getMaps = true;
				}
			}
		}
		else
		{
			getDesignation= true;
			getMaps=true;
			getRelationships = true;
		}
		em = emf.createEntityManager();
		List<TermConcept> qryResults = null;
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
	    
	    CriteriaQuery<TermConcept> criteria = builder.createQuery(TermConcept.class);
	   	Root<TermConcept> root = criteria.from(TermConcept.class);
	   	Join<TermConcept, TermCodeSystem > join = root.join("codeSystem", JoinType.LEFT);
	   	
	   	
	   	
	   	List<Predicate> predList = new LinkedList<Predicate>();
	   	if (system != null && !system.isEmpty())
	   	{
	   		
	   		log.info("codeSystemUri="+system.getValue());
	   		Predicate p = builder.equal(join.<String>get("codeSystemUri"),system.getValue());
	   		predList.add(p);
	   	}
	    if (code != null && !code.isEmpty())
	   	{
	   		log.info("code="+code.asStringValue());
	   		Predicate p = builder.equal(root.<String>get("code"),code.asStringValue());
	   		predList.add(p);
	   	}
	  	   	
	    Predicate[] predArray = new Predicate[predList.size()];
	    predList.toArray(predArray);
	    
	    if (predList.size()>0)
	    {
	    	criteria.select(root).where(predArray);
	    
		    qryResults = em.createQuery(criteria).getResultList();
		    
		    for (TermConcept cme : qryResults)
		    {
		    	
		    	parameters.addParameter()
					.setName("display")
					.setValue(new StringType(cme.getDisplay()));	    	
		    	
		    	if (getDesignation)
		    	{
		    		List<TermConceptDescription> synonyms = getDesignations(cme.getId().intValue());
		    		
		    		for (TermConceptDescription description : synonyms)
		    		{
		    			ParametersParameterComponent desc = parameters.addParameter()
		    				.setName("designation");
		    			Coding codeType = new Coding();
		    			codeType
		    				.setSystem("http://snomed.info/sct")
		    				.setCode(description.getTypeId().getCode())
		    				.setDisplay(description.getTypeId().getDisplay());
		    			desc.addPart()
	    					.setName("use")
	    					.setValue(codeType);
		    			
		    			desc.addPart()
		    				.setName("value")
		    				.setValue(new StringType(description.getTerm()));
		    			
		    		}
		    	}
		    	if (getMaps)
		    	{
		    		List<TermConceptRefsetMap> maps = getMaps(cme.getId().intValue());
		    		
		    		for (TermConceptRefsetMap map : maps)
		    		{
		    			ParametersParameterComponent mapdesc = parameters.addParameter()
		    				.setName("map");
		    			Coding codeType = new Coding();
		    			codeType
		    				.setSystem("http://snomed.info/sct")
		    				.setCode(map.getRefsetId().getCode())
		    				.setDisplay(map.getRefsetId().getDisplay());
		    			mapdesc.addPart()
	    					.setName("use")
	    					.setValue(codeType);
		    			mapdesc.addPart()
	    					.setName("value")
	    					.setValue(new StringType(map.getMapTarget()));
		    		}
		    	}
		    	if (getRelationships)
		    	{
		    		List<TermConceptRelationship> relationships = getRelationships(cme.getId().intValue());
		    		
		    		for (TermConceptRelationship relationship : relationships)
		    		{
		    			ParametersParameterComponent desc = parameters.addParameter()
		    				.setName("relationship");
		    			Coding codeCharacterType = new Coding();
		    			codeCharacterType
		    				.setSystem("http://snomed.info/sct")
		    				.setCode(relationship.getCharacteristicTypeId().getCode())
		    				.setDisplay(relationship.getCharacteristicTypeId().getDisplay());
		    			desc.addPart()
	    					.setName("use")
	    					.setValue(codeCharacterType);
		    			Coding codeType = new Coding();
		    			codeType
		    				.setSystem("http://snomed.info/sct")
		    				.setCode(relationship.getTypeId().getCode())
		    				.setDisplay(relationship.getTypeId().getDisplay());
		    			desc.addPart()
	    					.setName("type")
	    					.setValue(codeType);
		    			Coding descodeType = new Coding();
		    			descodeType
	    					.setSystem("http://snomed.info/sct")
	    					.setCode(relationship.getDestinationId().getCode())
	    					.setDisplay(relationship.getDestinationId().getDisplay());
		    			desc.addPart()
    						.setName("value")
    						.setValue(descodeType);
		    		}
		    	}
		    	if (getChild)
		    	{
		    		List<TermConcept> children = getParentChildLinks(cme.getId().intValue(),false);
		    		
		    		for (TermConcept childCT : children)
		    		{
		    			ParametersParameterComponent child = parameters.addParameter()
		    				.setName("property");
		    			
	    				child.addPart()
	    					.setName("code")
	    					.setValue(new StringType("child"));
	    				
	    				child.addPart()
	    					.setName("value")
	    					.setValue(new StringType(childCT.getCode()));
	    				
	    				child.addPart()
	    					.setName("description")
	    					.setValue(new StringType(childCT.getDisplay()));
		    				
		    		}
		    	}
		    	if (getParent)
		    	{
		    		List<TermConcept> parents = getParentChildLinks(cme.getId().intValue(),true);
		    		
		    		for (TermConcept parentCT : parents)
		    		{
		    			ParametersParameterComponent parent = parameters.addParameter()
		    				.setName("property");
		    			
		    			parent.addPart()
		    				.setName("code")
		    				.setValue(new StringType("parent"));
		    			
		    			parent.addPart()
		    				.setName("value")
		    				.setValue(new StringType(parentCT.getCode()));
		    			
		    			parent.addPart()
	    					.setName("description")
	    					.setValue(new StringType(parentCT.getDisplay()));
		    			
		    		}
		    	}
		    	if (getReferences)
		    	{
		    		List<TermConceptReferenceSet> references = getReferences(cme.getId().intValue());
		    		
		    		for (TermConceptReferenceSet reference : references)
		    		{
		    			ParametersParameterComponent desc = parameters.addParameter()
		    				.setName("reference");
		    			Coding codeType = new Coding();
		    			codeType
		    				.setSystem("http://snomed.info/sct")
		    				.setCode(reference.getReferencedComponentId().getCode())
		    				.setDisplay(reference.getReferencedComponentId().getDisplay());
		    			desc.addPart()
	    					.setName("use")
	    					.setValue(codeType);
		    			
		    		}
		    	}
		    	if (getPartOfReferences)
		    	{
		    		List<TermConceptReferenceSet> references = getPartOfReferences(cme.getId().intValue());
		    		
		    		for (TermConceptReferenceSet reference : references)
		    		{
		    			ParametersParameterComponent desc = parameters.addParameter()
		    				.setName("partOfReference");
		    			Coding codeType = new Coding();
		    			codeType
		    				.setSystem("http://snomed.info/sct")
		    				.setCode(reference.getConcept().getCode())
		    				.setDisplay(reference.getConcept().getDisplay());
		    			desc.addPart()
	    					.setName("use")
	    					.setValue(codeType);
		    			
		    		}
		    	}
		    }
	    }
		return parameters;
	}
	
	public List<TermConceptDescription> getDesignations(Integer conceptId)
	{
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		
		//CriteriaQuery<Tuple> criteria = builder.createTupleQuery();
		CriteriaQuery<TermConceptDescription> criteria = builder.createQuery(TermConceptDescription.class);
	   	Root<TermConceptDescription> root = criteria.from(TermConceptDescription.class);
	   	Join<TermConceptDescription, TermConcept > join = root.join("typeId", JoinType.LEFT);
	   	Join<TermConceptDescription, TermConcept > concept = root.join("concept", JoinType.LEFT);
	   	
	   	List<Predicate> predList = new LinkedList<Predicate>();
	   	
	   	Predicate p = builder.equal(root.<Boolean>get("active"),true);
	   	predList.add(p);
	   	//p = builder.equal(join.<String>get("code"),"900000000000013009");
	   	//predList.add(p);
	   	p = builder.equal(root.<Integer>get("concept"),conceptId);
	   	predList.add(p);
	   	
	    Predicate[] predArray = new Predicate[predList.size()];
	    predList.toArray(predArray);
	    	    
	    criteria.select(root).where(predArray);
	    List<TermConceptDescription> qryResults = em.createQuery(criteria).getResultList();
	   	
		return qryResults;
	}
	
	public List<TermConceptRefsetMap> getMaps(Integer conceptId)
	{
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		
		CriteriaQuery<TermConceptRefsetMap> criteria = builder.createQuery(TermConceptRefsetMap.class);
	   	Root<TermConceptRefsetMap> root = criteria.from(TermConceptRefsetMap.class);
	   	Join<TermConceptRefsetMap, TermConcept > join = root.join("refsetId");
	   	
	   	List<Predicate> predList = new LinkedList<Predicate>();
	   	
	   	Predicate p = builder.equal(root.<Boolean>get("active"),true);
	   	predList.add(p);
	   	p = builder.equal(root.<Integer>get("concept"),conceptId);
	   	predList.add(p);
	   	
	    Predicate[] predArray = new Predicate[predList.size()];
	    predList.toArray(predArray);
	    	    
	    criteria.select(root).where(predArray);
	    List<TermConceptRefsetMap> qryResults = em.createQuery(criteria).getResultList();
	   	
		return qryResults;
	}
	
	public List<TermConceptReferenceSet> getReferences(Integer conceptId)
	{
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		
		CriteriaQuery<TermConceptReferenceSet> criteria = builder.createQuery(TermConceptReferenceSet.class);
	   	Root<TermConceptReferenceSet> root = criteria.from(TermConceptReferenceSet.class);
	   	Join<TermConceptDescription, TermConcept > join = root.join("referencedComponentId", JoinType.LEFT);
	   	
	   	
	   	List<Predicate> predList = new LinkedList<Predicate>();
	   	
	   	Predicate p = builder.equal(root.<Boolean>get("active"),true);
	   	predList.add(p);
	   	p = builder.equal(join.<Boolean>get("active"),true); // Getting active references to inactive codes
	   	predList.add(p);
	   	p = builder.equal(root.<Integer>get("concept"),conceptId);
	   	predList.add(p);
	   	
	    Predicate[] predArray = new Predicate[predList.size()];
	    predList.toArray(predArray);
	    	    
	    criteria.select(root).where(predArray);
	    List<TermConceptReferenceSet> qryResults = em.createQuery(criteria).getResultList();
	   	
		return qryResults;
	}
	
	public List<TermConceptReferenceSet> getPartOfReferences(Integer conceptId)
	{
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		
		CriteriaQuery<TermConceptReferenceSet> criteria = builder.createQuery(TermConceptReferenceSet.class);
	   	Root<TermConceptReferenceSet> root = criteria.from(TermConceptReferenceSet.class);
	   	Join<TermConceptDescription, TermConcept > join = root.join("concept", JoinType.LEFT);
	   	
	   	
	   	List<Predicate> predList = new LinkedList<Predicate>();
	   	
	   	Predicate p = builder.equal(root.<Boolean>get("active"),true);
	   	predList.add(p);
	   	p = builder.equal(join.<Boolean>get("active"),true); // Getting active references to inactive codes
	   	predList.add(p);
	   	p = builder.equal(root.<Integer>get("referencedComponentId"),conceptId);
	   	predList.add(p);
	   	
	    Predicate[] predArray = new Predicate[predList.size()];
	    predList.toArray(predArray);
	    	    
	    criteria.select(root).where(predArray);
	    List<TermConceptReferenceSet> qryResults = em.createQuery(criteria).getResultList();
	   	
		return qryResults;
	}
	
	public List<TermConceptRelationship> getRelationships(Integer conceptId)
	{
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		
		CriteriaQuery<TermConceptRelationship> criteria = builder.createQuery(TermConceptRelationship.class);
	   	Root<TermConceptRelationship> root = criteria.from(TermConceptRelationship.class);
	   	Join<TermConceptRelationship, TermConcept > characteristic = root.join("characteristicTypeId", JoinType.LEFT);
	   	Join<TermConceptRelationship, TermConcept > destination = root.join("destinationId", JoinType.LEFT);
	   	
	   	List<Predicate> predList = new LinkedList<Predicate>();
	   	
	   	Predicate p = builder.equal(root.<Boolean>get("active"),true);
	   	predList.add(p);
	   
	   	p = builder.equal(root.<Integer>get("concept"),conceptId);
	   	predList.add(p);
	   	
	    Predicate[] predArray = new Predicate[predList.size()];
	    predList.toArray(predArray);
	    	    
	    criteria.select(root).where(predArray);
	    List<TermConceptRelationship> qryResults = em.createQuery(criteria).getResultList();
	   	
		return qryResults;
	}
	public List<TermConcept> getParentChildLinks(Integer conceptId, Boolean parent)
	{
		List<TermConcept> qryResults = null;
		CriteriaBuilder builder = em.getCriteriaBuilder();
	    
	    CriteriaQuery<TermConcept> criteria = builder.createQuery(TermConcept.class);
	   	Root<TermConceptParentChildLink> root = criteria.from(TermConceptParentChildLink.class);
	   	Join<TermConceptParentChildLink, TermConcept > join = null;
	   			
	   	List<Predicate> predList = new LinkedList<Predicate>();
	   	if (parent)
	   	{
	   		join = root.join("parent", JoinType.LEFT);
	   		Predicate p = builder.equal(root.<Integer>get("child"),conceptId);
		   	predList.add(p);
		   	
	   	}
	   	else
	   	{
	   		join = root.join("child", JoinType.LEFT);
	   		Predicate p = builder.equal(root.<Integer>get("parent"),conceptId);
		   	predList.add(p);
	   	}
	   	   	
	    Predicate[] predArray = new Predicate[predList.size()];
	    predList.toArray(predArray);
	    
	    
	    criteria.select(join).where(predArray);
	    
		qryResults = em.createQuery(criteria).getResultList();
		    
		return qryResults;
	}
	
	public CodeSystem convert(TermCodeSystem cme)
	{
		CodeSystem codeSystem = new CodeSystem();
		
		/*
		valueSet.setId(cme.getId().toString());
		if (cme.getStrId()!=null)
		{
			valueSet.setId(cme.getStrId());
		}
		if (cme.getDescription() != null)
		{
			valueSet.setDescription(cme.getDescription());
		}
		if (cme.getName() !=null)
		{
			valueSet.setName(cme.getName());
		}
		if (cme.getStatus() !=null)
		{
			valueSet.setStatus(cme.getStatus());
		}
		if (cme.getUrl() !=null)
		{
			valueSet.setUrl(cme.getUrl());
		}
		
		if (cme.getContents().size()>0)
		{
			ValueSet.ValueSetComposeComponent compose = new ValueSet.ValueSetComposeComponent();
			ValueSet.ConceptSetComponent include = compose.addInclude();
			valueSet.setCompose(compose);
			for (ValueSetContent content : cme.getContents())
			{
				if (content.getCodeCT() != null)
				{
					include.addConcept()
						.setCode(content.getCodeCT().getCode())
						.setDisplay(content.getCodeCT().getDisplay());
				}
				else
				{
					if (content.getCode() != null)
					{
						ConceptReferenceComponent comp = include.addConcept().setCode(content.getCode());
						if (content.getDisplay() != null)
						{
							comp.setDisplay(content.getDisplay());
						}
					}
				}
			}
		}
		*/
	
		return codeSystem;
	}
	@Override
	public CodeSystem read(IdType theId) {
		log.info("called read theId="+ theId.toString());
		log.info("called read Id="+ theId.getIdPart());
		CodeSystem codeSystem  = null;
		try
		{
			
			EntityManager em = emf.createEntityManager();
			log.info("Obtained entityManager Patient.read");
			
			
			TermCodeSystem cme = (TermCodeSystem) em.find(TermCodeSystem.class,Integer.parseInt(theId.getIdPart()));
			
			codeSystem = convert(cme);
			
			em.close();
	        log.info("Built the Patient");
	       	
			return codeSystem;
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
		method.setResource(codeSystem);
		return codeSystem;
	}

	
	public List<CodeSystem> search(
			@OptionalParam(name = CodeSystem.SP_NAME) StringParam theName 
			) {
		
		List<TermCodeSystem> qryResults = null;
		List<CodeSystem> results = new ArrayList<CodeSystem>();
		try
		{
			// https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#criteria
			
			EntityManager em = emf.createEntityManager();
			
			CriteriaBuilder builder = em.getCriteriaBuilder();
		    
		    CriteriaQuery<TermCodeSystem> criteria = builder.createQuery(TermCodeSystem.class);
		   	Root<TermCodeSystem> root = criteria.from(TermCodeSystem.class);
		   	//Join<ValueSetEntity, ValueSetIdentifier> join = root.join("identifiers", JoinType.LEFT);
		    
		   	List<Predicate> predList = new LinkedList<Predicate>();
		   	if (theName != null && !theName.isEmpty())
		   	{
		   		Predicate p = builder.like(root.<String>get("name"),"%"+theName.getValue()+"%");
    			predList.add(p);
		   	}
		   	
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
		    
		    for (TermCodeSystem cme : qryResults)
		    {
		    	log.trace("HAPI Custom = "+cme.getPID());	    	
		    	CodeSystem codeSystem = convert(cme);
		    	results.add(codeSystem);
		    }
		    log.info("ValueSet Search Count = "+qryResults.size());
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
