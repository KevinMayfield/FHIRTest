package uk.nhs.careConnect.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name="TermConceptReferenceSet", uniqueConstraints= @UniqueConstraint(name="PK_CONCEPT_REFERENCE_SET", columnNames={"CONCEPT_REFERENCE_SET_ID"}))
public class TermConceptReferenceSet extends BaseReference  {
	
	public TermConceptReferenceSet() {
		
	}
	
	public TermConceptReferenceSet(TermConcept ep) {
		//setPatientId(ep); 
		this.concept = ep;
	}
	
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "CONCEPT_REFERENCE_SET_ID")
	public Long getConceptReferenceSetId() { return conceptReferenceSetId; }
	public void setConceptReferenceSetId(Long myConceptReferenceSetId) { this.conceptReferenceSetId = myConceptReferenceSetId; }
	private Long conceptReferenceSetId;

	@Column(name= "REFERENCE_SET_ID", columnDefinition="uniqueidentifier")
	public String getReferenceSetId() { return referenceSetId; }
	public void setReferenceSetId(String myReferenceSetId) { this.referenceSetId = myReferenceSetId; }
	private String referenceSetId;
	
	@Column(name= "referenceSetType")
	public TermConceptReferenceSetType getReferenceSetType() { return referenceSetType; }
	public void setReferenceSetType(TermConceptReferenceSetType myReferenceSetType) { this.referenceSetType = myReferenceSetType; }
	private TermConceptReferenceSetType referenceSetType;

	// This is the refereence Set link
	private TermConcept concept;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn (name = "CONCEPT_ID",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_TERM_CONCEPT_REFERENCE_SET"))
	public TermConcept getConcept() { return this.concept; }
	public void setConcept( TermConcept myConcept) {  this.concept = myConcept; }
	
		
	@Column(name = "active")
	private Boolean active;
	public void setActive(Boolean active) {   this.active = active;   }
	public Boolean getActive() { return this.active;  }
	
	@Column(name = "effectiveDate")
	private Date effectiveDate;
	public void setEffectiveDate(Date effectiveDate) {   this.effectiveDate = effectiveDate;   }
	public Date getEffectiveDate() { return this.effectiveDate;  }
	
	private TermConcept moduleId;
	@ManyToOne
	@JoinColumn(name = "moduleId",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_REFERENCE_SET_MODULE"))
	public TermConcept getModuleId() { return this.moduleId;  }
	public void setModuleId(TermConcept moduleId) {   this.moduleId = moduleId;   }
		
	private TermConcept referencedComponentId;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn (name = "referencedComponentId",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_RELATIONSHIP_REFERENCED_COMPONENT"))
	public TermConcept getReferencedComponentId() {    return this.referencedComponentId;    }
	public void setReferencedComponentId(TermConcept myReferencedComponentId) {    this.referencedComponentId = myReferencedComponentId;  }

	private TermConcept targetComponentId;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn (name = "targetComponentId",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_RELATIONSHIP_TARGET_COMPONENT"))
	public TermConcept getTargetComponentId() {    return this.targetComponentId;    }
	public void setTargetComponentId(TermConcept myTargetComponentId) {    this.targetComponentId = myTargetComponentId;  }
	
	private TermConcept valueId;
	@ManyToOne
	@JoinColumn (name = "valueId",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_RELATIONSHIP_VALUE"))
	public TermConcept getValueId() {    return this.valueId;    }
	public void setValueId(TermConcept myValueId) {    this.valueId = myValueId;  }
}
