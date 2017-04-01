package uk.nhs.careConnect.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity
@Table(name="ConceptMapElementTarget", uniqueConstraints= @UniqueConstraint(name="PK_CONCEPT_MAP_ELEMENT_TARGET", columnNames={"CONCEPT_MAP_ELEMENT_TARGET_ID"}))
public class ConceptMapElementTarget  {
	
	public ConceptMapElementTarget() {
		
	}
	
	public ConceptMapElementTarget(ConceptMapElement conceptMapElement) {
		this.conceptMapElement = conceptMapElement;
	}
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "CONCEPT_MAP_ELEMENT_TARGET_ID")
	public Integer getTargetId() { return targetId; }
	public void setTargetId(Integer targetId) { this.targetId = targetId; }
	private Integer targetId;
	
	private ConceptMapElement conceptMapElement;
	
	@ManyToOne
	@JoinColumn (name = "CONCEPT_MAP_ELEMENT_ID",foreignKey= @ForeignKey(name="FK_CONCEPT_MAP_ELEMENT_CONCEPT_MAP_ELEMENT_TARGET"))
	public ConceptMapElement getConceptMapElement() {
	        return this.conceptMapElement;
	}
	public void setConceptMapElement(ConceptMapElement conceptMapElement) {
	        this.conceptMapElement = conceptMapElement;
	}
	
	private TermConcept codeCT;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codeCT", referencedColumnName = "CONCEPT_ID", foreignKey = @ForeignKey(name = "FK_CONCEPT_MAP_ELEMENT_TARGET_CONCEPT_PID"))
	
	public TermConcept getCodeCT() { return this.codeCT; }
	public void setCodeCT(TermConcept codeCT) { 	this.codeCT = codeCT; 	}
	
	@Column(name = "CODESYSTEM_ID")
	private Long myCodeSystem;
	public Long getCodeSystem() {
		return myCodeSystem;
	}
	public void setCodeSystem(Long theCodeSystem) {
		myCodeSystem = theCodeSystem;
	}
	
	private ConceptMapEquivalence equivalence;
	@Column(name="equivalence") 
	@Enumerated(EnumType.ORDINAL) 
	public ConceptMapEquivalence getEquivalence() { 
	    return equivalence; 
	}
	public void setEquivalence(ConceptMapEquivalence equivalence) { 
	    this.equivalence = equivalence; 
	}
}
