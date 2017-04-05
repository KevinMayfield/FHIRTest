package uk.nhs.careConnect.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity
@Table(name="ConceptMapIdentifier", uniqueConstraints= @UniqueConstraint(name="PK_CONCEPT_MAP_IDENTIFIER", columnNames={"CONCEPT_MAP_IDENTIFIER_ID"}))
public class ConceptMapIdentifier extends BaseIdentifier {
	
	public ConceptMapIdentifier() {
		
	}
	
	public ConceptMapIdentifier(ConceptMapEntity conceptMapEntity) {
		this.conceptMapEntity = conceptMapEntity;
	}
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "CONCEPT_MAP_IDENTIFIER_ID")
	public Integer getIdentifierId() { return identifierId; }
	public void setIdentifierId(Integer identifierId) { this.identifierId = identifierId; }
	private Integer identifierId;
	
	private ConceptMapEntity conceptMapEntity;
	
	@ManyToOne
	@JoinColumn (name = "CONCEPT_MAP_ID",foreignKey= @ForeignKey(name="FK_CONCEPT_MAP_CONCEPT_MAP_IDENTIFIER"))
	public ConceptMapEntity getConceptMapEntity() {
	        return this.conceptMapEntity;
	}
	public void setConceptMapEntity(ConceptMapEntity conceptMapEntity) {
	        this.conceptMapEntity = conceptMapEntity;
	}
}
