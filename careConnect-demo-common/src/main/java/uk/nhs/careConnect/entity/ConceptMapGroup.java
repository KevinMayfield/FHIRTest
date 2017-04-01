package uk.nhs.careConnect.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity
@Table(name="ConceptMapGroup", uniqueConstraints= @UniqueConstraint(name="PK_CONCEPT_MAP_GROUP", columnNames={"CONCEPT_MAP_GROUP_ID"}))
public class ConceptMapGroup  {
	
	public ConceptMapGroup() {
		
	}
	
	public ConceptMapGroup(ConceptMapEntity conceptMapEntity) {
		this.conceptMapEntity = conceptMapEntity;
	}
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "CONCEPT_MAP_GROUP_ID")
	public Integer getGroupId() { return groupId; }
	public void setGroupId(Integer groupId) { this.groupId = groupId; }
	private Integer groupId;
	
	private ConceptMapEntity conceptMapEntity;
	
	@ManyToOne
	@JoinColumn (name = "CONCEPT_MAP_ID",foreignKey= @ForeignKey(name="FK_CONCEPT_MAP_CONCEPT_MAP_GROUP"))
	public ConceptMapEntity getConceptMapEntity() {
	        return this.conceptMapEntity;
	}
	public void setConceptMapEntity(ConceptMapEntity conceptMapEntity) {
	        this.conceptMapEntity = conceptMapEntity;
	}
	
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
	
	@Column(name = "sourceVersion")
	private String sourceVersion;
	public void setSourceVersion(String sourceVersion)
	{  this.sourceVersion = sourceVersion; }
	public String getSourceVersion()  {  return this.sourceVersion;  }
	
	@Column(name = "targetVersion")
	private String targetVersion;
	public void setTargetVersion(String targetVersion)
	{  this.targetVersion = targetVersion; }
	public String getTargetVersion()  {  return this.targetVersion;  }
	
	private List<ConceptMapElement> elements;
	
	@OneToMany(mappedBy="conceptMapGroup", targetEntity=ConceptMapElement.class)
	public List<ConceptMapElement> getElements( ) {
		if (this.elements == null) {
			this.elements = new ArrayList<ConceptMapElement>();
	    }
        return this.elements;
    }
	public void setElements(List<ConceptMapElement> elements) {
        this.elements = elements;
    }
	public List<ConceptMapElement> addElement(ConceptMapElement pi) { 
		this.elements.add(pi);
		return this.elements; }
	public List<ConceptMapElement> removeElement(ConceptMapElement element){ 
		this.elements.remove(element); return this.elements; }
	
}
