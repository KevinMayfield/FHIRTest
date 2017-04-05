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
@Table(name="TermConceptDescription", uniqueConstraints= @UniqueConstraint(name="PK_CONCEPT_DESCRIPTION", columnNames={"CONCEPT_DESCRIPTION_ID"}))
public class TermConceptDescription extends BaseReference  {
	
	public TermConceptDescription() {
		
	}
	
	public TermConceptDescription(TermConcept ep) {
		//setPatientId(ep); 
		this.concept = ep;
	}
	
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "CONCEPT_DESCRIPTION_ID")
	public Long getConceptDescriptionId() { return conceptDescriptionId; }
	public void setConceptDescriptionId(Long conceptDescriptionId) { this.conceptDescriptionId = conceptDescriptionId; }
	private Long conceptDescriptionId;

	@Column(name= "DESCRIPTION_ID")
	public Long getDescriptionId() { return descriptionId; }
	public void setDescriptionId(Long descriptionId) { this.descriptionId = descriptionId; }
	private Long descriptionId;

	
	private TermConcept concept;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn (name = "CONCEPT_ID",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_TERM_CONCEPT_DESCRIPTION"))
	public TermConcept getConcept() {
	        return this.concept;
	}

	public void setConcept( TermConcept concept) {
	        this.concept = concept;
	}
	
	@Column(name = "term")
	private String term;
	public void setTerm(String term) {   this.term = term;   }
	public String getTerm() { return this.term;  }
		
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
	@JoinColumn(name = "moduleId",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_DESCRIPTION_MODULE"))
	public TermConcept getModuleId() { return this.moduleId;  }
	public void setModuleId(TermConcept moduleId) {   this.moduleId = moduleId;   }
	
	
	private TermConcept typeId;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn (name = "typeId",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_DESCRIPTION_TYPE"))
	public TermConcept getTypeId() {    return this.typeId;    }
	public void setTypeId(TermConcept typeId) {    this.typeId = typeId;  }
	
	
	private TermConcept caseSignificanceId;
	@ManyToOne
	@JoinColumn (name = "caseSignificanceId",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_DESCRIPTION_CASE_SIGNIFICANCE"))
	public TermConcept getCaseSignificanceId() {    return this.caseSignificanceId;    }
	public void setCaseSignificanceId(TermConcept caseSignificanceId) {    this.caseSignificanceId = caseSignificanceId;  }
	
	
		
}
