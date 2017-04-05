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
@Table(name="PatientIdentifier", uniqueConstraints= @UniqueConstraint(name="PK_PATIENT_IDENTIFIER", columnNames={"PATIENT_IDENTIFIER_ID"}))
public class PatientIdentifier extends BaseIdentifier {
	
	public PatientIdentifier() {
		
	}
	
	public PatientIdentifier(PatientEntity patientEntity) {
		this.patientEntity = patientEntity;
	}
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "PATIENT_IDENTIFIER_ID")
	public Integer getIdentifierId() { return identifierId; }
	public void setIdentifierId(Integer identifierId) { this.identifierId = identifierId; }
	private Integer identifierId;
	
	private PatientEntity patientEntity;
	
	@ManyToOne
	@JoinColumn (name = "PATIENT_ID",foreignKey= @ForeignKey(name="FK_PATIENT_PATIENT_IDENTIFIER"))
	public PatientEntity getPatientEntity() {
	        return this.patientEntity;
	}
	public void setPatientEntity(PatientEntity patientEntity) {
	        this.patientEntity = patientEntity;
	}
}
