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
@Table(name="TermConceptRelationship", uniqueConstraints= @UniqueConstraint(name="PK_CONCEPT_RELATIONSHIP", columnNames={"CONCEPT_RELATIONSHIP_ID"}))
public class TermConceptRelationship extends BaseReference  {
	
	public TermConceptRelationship() {
		
	}
	
	public TermConceptRelationship(TermConcept ep) {
		//setPatientId(ep); 
		this.concept = ep;
	}
	
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "CONCEPT_RELATIONSHIP_ID")
	public Long getConceptRelationshipId() { return conceptRelationshipId; }
	public void setConceptRelationshipId(Long conceptRelationshipId) { this.conceptRelationshipId = conceptRelationshipId; }
	private Long conceptRelationshipId;

	@Column(name= "RELATIONSHIP_ID")
	public Long getRelationshipId() { return relationshipId; }
	public void setRelationshipId(Long relationshipId) { this.relationshipId = relationshipId; }
	private Long relationshipId;

	
	private TermConcept concept;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn (name = "CONCEPT_ID",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_TERM_CONCEPT_RELATIONSHIP"))
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
	@JoinColumn(name = "moduleId",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_RELATIONSHIP_MODULE"))
	public TermConcept getModuleId() { return this.moduleId;  }
	public void setModuleId(TermConcept moduleId) {   this.moduleId = moduleId;   }
	
	/*
	 * Error source should be main concept. Use concept instead
	 *  
	private TermConcept sourceId;
	@ManyToOne
	@JoinColumn (name = "sourceId",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_RELATIONSHIP_SOURCE"))
	public TermConcept getSourceId() {    return this.sourceId;    }
	public void setSourceId(TermConcept sourceId) {    this.sourceId = sourceId;  }
	*/
	
	private TermConcept destinationId;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn (name = "destinationId",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_RELATIONSHIP_DESTINATION"))
	public TermConcept getDestinationId() {    return this.destinationId;    }
	public void setDestinationId(TermConcept myDestinationId) {    this.destinationId = myDestinationId;  }
	
	private Long relationshipGroup;
	@Column (name = "relationshipGroup")
	public Long getRelationshipGroup() {    return this.relationshipGroup;    }
	public void setRelationshipGroup(Long myRelationshipGroup) {    this.relationshipGroup = myRelationshipGroup;  }
		
	private TermConcept typeId;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn (name = "typeId",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_RELATIONSHIP_TYPE"))
	public TermConcept getTypeId() {    return this.typeId;    }
	public void setTypeId(TermConcept myTypeId) {    this.typeId = myTypeId;  }
		
	private TermConcept characteristicTypeId;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn (name = "characteristicTypeId",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_RELATIONSHIP_CHARACTERISTIC_TYPE"))
	public TermConcept getCharacteristicTypeId() {    return this.characteristicTypeId;    }
	public void setCharacteristicTypeId(TermConcept myCharacteristicTypeId) {    this.characteristicTypeId = myCharacteristicTypeId;  }

	private TermConcept modifierId;
	@ManyToOne
	@JoinColumn (name = "modifierId",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_RELATIONSHIP_MODIFIER"))
	public TermConcept getModifierId() {    return this.modifierId;    }
	public void setModifierId(TermConcept myModifierId) {    this.modifierId = myModifierId;  }

	
}
