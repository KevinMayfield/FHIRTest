package uk.nhs.careConnect.entity;


import java.util.ArrayList;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hl7.fhir.dstu3.model.EpisodeOfCare;


@Entity
@Table(name="EpisodeOfCare", uniqueConstraints= @UniqueConstraint(name="PK_EPISODE_OF_CARE", columnNames={"EPISODE_OF_CARE_ID"}))
public class EpisodeOfCareEntity extends BaseResource {
	
	

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="EPISODE_OF_CARE_ID")
	private Integer id;	
	public void setId(Integer id) { this.id = id; }
	public Integer getId() { return id; }

		@Temporal(TemporalType.TIMESTAMP)
		@Column(name = "RES_UPDATED", nullable = true)
		private Date myUpdated;
		public Date getUpdatedDate() { return myUpdated; }
	
		@Column(name = "status")
		private EpisodeOfCare.EpisodeOfCareStatus status;
		public EpisodeOfCare.EpisodeOfCareStatus getStatus() { return this.status; }
		public void setStatus(EpisodeOfCare.EpisodeOfCareStatus status) { 	this.status = status; 	}
		
		@Column(name="condition_desc")
		private String condition;
		public String getCondition() { return this.condition; }
		public void setCondition(String condition) { this.condition = condition; }
		
		@Column(name = "period", nullable = true)
		private Date period;
		public Date getPeriod() {
			return period;
		}
		public void setPeriod(Date period) {
			this.period = period;
		}
		
		@Column(name="careTeam")
		private String careTeam;
		public String getCareTeam() { return this.careTeam; }
		public void setCareTeam(String careTeam) { this.careTeam = careTeam; }

		@Column(name="careManager")
		private String careManager;
		public String getCareManager() { return this.careManager; }
		public void setCareManager(String careManager) { this.careManager = careManager; }

		@ManyToOne
		@JoinColumn(name="PATIENT_ID", referencedColumnName="id", foreignKey= @ForeignKey(name="FK_PATIENT_EPISODE_OF_CARE"))
		public PatientEntity patientEntity;
		public PatientEntity getPatientEntity() {
		        return this.patientEntity;
		}
		public void setPatientEntity(PatientEntity patientEntity) {
		        this.patientEntity = patientEntity;
		}
			
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "typeCT", referencedColumnName = "CONCEPT_ID", foreignKey = @ForeignKey(name = "FK_EPISODE_OF_CARE_TYPE_CONCEPT_PID"))
		private TermConcept typeCT;
		public TermConcept getTypeCT() { return this.typeCT; }
		public void setTypeCT(TermConcept typeCT) { 	this.typeCT = typeCT; 	}
		
		
		@OneToMany(mappedBy="episodeOfCareEntity", targetEntity=EpisodeOfCareIdentifier.class)
		private List<EpisodeOfCareIdentifier> identifiers;
		public void setIdentifiers(List<EpisodeOfCareIdentifier> identifiers) {
	        this.identifiers = identifiers;
	    }
		public List<EpisodeOfCareIdentifier> getIdentifiers( ) {
			if (identifiers == null) {
		        identifiers = new ArrayList<EpisodeOfCareIdentifier>();
		    }
	        return this.identifiers;
	    }
		public List<EpisodeOfCareIdentifier> addIdentifier(EpisodeOfCareIdentifier pi) { 
			identifiers.add(pi);
			return identifiers; }
		
		public List<EpisodeOfCareIdentifier> removeIdentifier(EpisodeOfCareIdentifier identifier){ 
			identifiers.remove(identifiers); return identifiers; }
		
		@OneToMany(mappedBy="episodeOfCareEntity", targetEntity=EpisodeOfCareReferralRequest.class)
		private List<EpisodeOfCareReferralRequest> referrals;
		public void setReferrals(List<EpisodeOfCareReferralRequest> referrals) {
	        this.referrals = referrals;
	    }
		public List<EpisodeOfCareReferralRequest> getReferrals( ) {
			if (referrals == null) {
		        referrals = new ArrayList<EpisodeOfCareReferralRequest>();
		    }
	        return this.referrals;
	    }
		public List<EpisodeOfCareReferralRequest> addReferral(EpisodeOfCareReferralRequest pi) { 
			referrals.add(pi);
			return referrals; }
		
		public List<EpisodeOfCareReferralRequest> removeReferral(EpisodeOfCareReferralRequest referral){ 
			referrals.remove(referral); 
			return referrals; 
		}
		
		
	
			
}
