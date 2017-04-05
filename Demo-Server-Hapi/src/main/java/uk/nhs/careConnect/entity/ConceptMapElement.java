package uk.nhs.careConnect.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name="ConceptMapElement", uniqueConstraints= @UniqueConstraint(name="PK_CONCEPT_MAP_ELEMENT", columnNames={"CONCEPT_MAP_ELEMENT_ID"}))
public class ConceptMapElement  {
	
	public ConceptMapElement() {
		
	}
	
	public ConceptMapElement(ConceptMapGroup conceptMapGroup) {
		this.conceptMapGroup = conceptMapGroup;
	}
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "CONCEPT_MAP_ELEMENT_ID")
	public Integer getElementId() { return elementId; }
	public void setElementId(Integer elementId) { this.elementId = elementId; }
	private Integer elementId;
	
	private ConceptMapGroup conceptMapGroup;
	
	@ManyToOne
	@JoinColumn (name = "CONCEPT_MAP_GROUP_ID",foreignKey= @ForeignKey(name="FK_CONCEPT_MAP_GROUP_CONCEPT_MAP_ELEMENT"))
	public ConceptMapGroup getConceptMapGroup() {
	        return this.conceptMapGroup;
	}
	public void setConceptMapGroup(ConceptMapGroup conceptMapGroup) {
	        this.conceptMapGroup = conceptMapGroup;
	}
	
	private TermConcept codeCT;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codeCT", referencedColumnName = "CONCEPT_ID", foreignKey = @ForeignKey(name = "FK_CONCEPT_MAP_ELEMENT_CONCEPT_PID"))
	
	public TermConcept getCodeCT() { return this.codeCT; }
	public void setCodeCT(TermConcept codeCT) { 	this.codeCT = codeCT; 	}
	
	// KGM possibly don't need this
	@Column(name = "CODESYSTEM_ID")
	private Long myCodeSystem;
	public Long getCodeSystem() {
		return myCodeSystem;
	}
	public void setCodeSystem(Long theCodeSystem) {
		myCodeSystem = theCodeSystem;
	}
	
	private List<ConceptMapElementTarget> elementTargets;
	@OneToMany(mappedBy="conceptMapElement", targetEntity=ConceptMapElementTarget.class)
	public List<ConceptMapElementTarget> getElementTargets( ) {
		if (this.elementTargets == null) {
			this.elementTargets = new ArrayList<ConceptMapElementTarget>();
	    }
        return this.elementTargets;
    }
	public void setElementTargets(List<ConceptMapElementTarget> elementTargets) {
        this.elementTargets = elementTargets;
    }
	public List<ConceptMapElementTarget> addElementTarget(ConceptMapElementTarget pi) { 
		this.elementTargets.add(pi);
		return this.elementTargets; }
	public List<ConceptMapElementTarget> removeElement(ConceptMapElementTarget element){ 
		this.elementTargets.remove(element); return this.elementTargets; }
	
	
}
