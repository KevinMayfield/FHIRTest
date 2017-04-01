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
@Table(name="TermConceptRefsetMap", uniqueConstraints= @UniqueConstraint(name="PK_CONCEPT_REFSET_MAP", columnNames={"CONCEPT_REFSET_MAP_ID"}))
public class TermConceptRefsetMap   {
	
	public TermConceptRefsetMap() {
		
	}
	
	public TermConceptRefsetMap(TermConcept ep) {
		//setPatientId(ep); 
		this.concept = ep;
	}
	
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "CONCEPT_REFSET_MAP_ID")
	public Long getConceptRefsetMapId() { return conceptRefsetMapId; }
	public void setConceptRefsetMapId(Long myConceptRefsetMapId) { this.conceptRefsetMapId = myConceptRefsetMapId; }
	private Long conceptRefsetMapId;

	@Column(name= "REFSET_MAP_ID")
	public String getRefsetMapId() { return refsetMapId; }
	public void setRefsetMapId(String myRefsetMapId) { this.refsetMapId = myRefsetMapId; }
	private String refsetMapId;

	
	private TermConcept concept;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn (name = "CONCEPT_ID",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_TERM_CONCEPT_REFSET_MAP"))
	public TermConcept getConcept() { return this.concept; }
	public void setConcept( TermConcept concept) {  this.concept = concept; }
	
		
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
	@JoinColumn(name = "moduleId",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_REFSET_MAP_MODULE"))
	public TermConcept getModuleId() { return this.moduleId;  }
	public void setModuleId(TermConcept myModuleId) {   this.moduleId = myModuleId;   }
	
	private TermConcept refsetId;
	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn (name = "refsetId",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_REFSET_MAP_REFSET"))
	public TermConcept getRefsetId() {    return this.refsetId;    }
	public void setRefsetId(TermConcept myRefsetId) {    this.refsetId = myRefsetId;  }
		
		
	@Column(name = "mapTarget")
	private String mapTarget;
	public void setMapTarget(String myMapTarget) {   this.mapTarget = myMapTarget;   }
	public String getMapTarget() { return this.mapTarget;  }
	
}
