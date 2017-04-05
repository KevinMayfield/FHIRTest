package uk.nhs.careConnect.entity;


import java.util.ArrayList;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="ConceptMap", uniqueConstraints= @UniqueConstraint(name="PK_CONCEPT_MAP", columnNames={"ID"}))
public class ConceptMapEntity extends BaseResource {
	
	

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID")
	private Integer id;	
	public void setId(Integer id) { this.id = id; }
	public Integer getId() { return id; }

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modifiedDate", nullable = true)
	private Date updated;
	public Date getUpdatedDate() { return updated; }
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createdDate", nullable = true)
	private Date createdDate;
	public Date getCreatedDate() { return createdDate; }
	
		
		@Column(name = "name")
		private String name;
		public void setName(String name)
		{  this.name = name; }
		public String getName()  {  return this.name;  }
		
		@Column(name = "status")
		private String status;
		public void setStatus(String status)
		{  this.status = status; }
		public String getStatus()  {  return this.status;  }
		
		@Column(name = "description")
		private String description;
		public void setDescription(String description)
		{  this.description = description; }
		public String getDescription()  {  return this.description;  }
		
		
		@ManyToOne()
		@JoinColumn(name = "SOURCE_VALUESET",referencedColumnName = "VALUESET_ID", foreignKey = @ForeignKey(name = "FK_CONCEPT_MAP_SOURCE_VALUESET_PID"))
		private ValueSetEntity sourceValueSet;
		public ValueSetEntity getSourceValueSet() {
			return this.sourceValueSet;
		}
		public void setSourceValueSet(ValueSetEntity valueSet) {
			this.sourceValueSet = valueSet;
		}
		
		@ManyToOne()
		@JoinColumn(name = "TARGET_VALUESET",referencedColumnName = "VALUESET_ID", foreignKey = @ForeignKey(name = "FK_CONCEPT_MAP_TARGET_VALUESET_PID"))
		private ValueSetEntity targetValueSet;
		public ValueSetEntity getTargetValueSet() {
			return this.targetValueSet;
		}
		public void setTargetValueSet(ValueSetEntity valueSet) {
			this.targetValueSet = valueSet;
		}
		
		/*
		 *  Moved to ValueSets
		@Column(name = "source")
		private String source;
		public void setSource(String source)
		{  this.source = source; }
		public String getSource()  {  return this.source;  }
		
		@Column(name = "target")
		private String target;
		public void setTarget(String target)
		{  this.target = target; }
		public String getTarget()  {  return this.target;  }
		*/
		// ConceptMap IDENTIFIERS
		@OneToMany(mappedBy="conceptMapEntity", targetEntity=ConceptMapIdentifier.class)
		private List<ConceptMapIdentifier> identifiers;
		public void setIdentifiers(List<ConceptMapIdentifier> identifiers) {
	        this.identifiers = identifiers;
	    }
		public List<ConceptMapIdentifier> getIdentifiers( ) {
			if (identifiers == null) {
		        identifiers = new ArrayList<ConceptMapIdentifier>();
		    }
	        return this.identifiers;
	    }
		public List<ConceptMapIdentifier> addIdentifier(ConceptMapIdentifier pi) { 
			identifiers.add(pi);
			return identifiers; }
		
		public List<ConceptMapIdentifier> removeIdentifier(ConceptMapIdentifier identifier){ 
			identifiers.remove(identifier); return identifiers; }
		
		
		
		@OneToMany(mappedBy="conceptMapEntity", targetEntity=ConceptMapGroup.class)
		private List<ConceptMapGroup> groups;
		public List<ConceptMapGroup> getGroups( ) {
			if (this.groups == null) 
			{
		        this.groups = new ArrayList<ConceptMapGroup>();
		    }
	        return this.groups;
	    }
		public void setGroups(List<ConceptMapGroup> groups) {
	        this.groups = groups;
	    }
		public List<ConceptMapGroup> addGroup(ConceptMapGroup pi) { 
			this.groups.add(pi);
			return this.groups; }
		public List<ConceptMapGroup> removeGroup(ConceptMapGroup group){ 
			this.groups.remove(group); return this.groups; }
		
}
