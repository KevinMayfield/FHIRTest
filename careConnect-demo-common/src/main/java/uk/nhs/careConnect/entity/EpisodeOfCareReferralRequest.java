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
@Table(name="EpisodeOfCareReferralRequest", uniqueConstraints= @UniqueConstraint(name="PK_EPISODE_OF_CARE_REFERRAL_REQUEST", columnNames={"EPISODE_OF_CARE_REFERRAL_REQUEST_ID"}))
public class EpisodeOfCareReferralRequest extends BaseReference  {
	
public EpisodeOfCareReferralRequest() {
		
	}
	
	public EpisodeOfCareReferralRequest(EpisodeOfCareEntity ep) {
		
		this.episodeOfCareEntity = ep;
	}
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "EPISODE_OF_CARE_REFERRAL_REQUEST_ID")
	public Integer getIdentifierId() { return identifierId; }
	public void setIdentifierId(Integer identifierId) { this.identifierId = identifierId; }
	private Integer identifierId;
	
	private EpisodeOfCareEntity episodeOfCareEntity;

	@ManyToOne
	@JoinColumn (name = "EPISODE_OF_CARE_ID", foreignKey= @ForeignKey(name="FK_EPISODE_OF_CARE_EPISODE_OF_CARE_REQUEST_REFERRAL"))
	public EpisodeOfCareEntity getEpisodeOfCareEntity() {
	        return this.episodeOfCareEntity;
	}
    public void setEpisodeOfCareEntity(EpisodeOfCareEntity episodeOfCareEntity) {
        this.episodeOfCareEntity = episodeOfCareEntity;
    }
    
	
}
