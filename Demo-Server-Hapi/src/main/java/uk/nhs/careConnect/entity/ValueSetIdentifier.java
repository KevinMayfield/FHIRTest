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
@Table(name="ValueSetIdentifier", uniqueConstraints= @UniqueConstraint(name="PK_VALUESET_IDENTIFIER", columnNames={"VALUESET_IDENTIFIER_ID"}))
public class ValueSetIdentifier extends BaseIdentifier {
	
	public ValueSetIdentifier() {
		
	}
	
	public ValueSetIdentifier(ValueSetEntity valueSetEntity) {
		this.valueSetEntity = valueSetEntity;
	}
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "VALUESET_IDENTIFIER_ID")
	public Integer getIdentifierId() { return identifierId; }
	public void setIdentifierId(Integer identifierId) { this.identifierId = identifierId; }
	private Integer identifierId;
	
	
	private ValueSetEntity valueSetEntity;
	@ManyToOne
	@JoinColumn (name = "VALUESET_ID",foreignKey= @ForeignKey(name="FK_VALUESET_VALUESET_IDENTIFIER"))
	public ValueSetEntity getValueSetEntity() {
	        return this.valueSetEntity;
	}
	public void setValueSetEntity(ValueSetEntity valueSetEntity) {
	        this.valueSetEntity = valueSetEntity;
	}
}
