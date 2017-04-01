package uk.nhs.careConnect.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;



@Entity
@Table(name="EpisodeOfCareIdentifier", uniqueConstraints= @UniqueConstraint(name="PK_EPISODE_OF_CARE_IDENTIFIER", columnNames={"EPISODE_OF_CARE_IDENTIFIER_ID"}))
public class EpisodeOfCareIdentifier extends BaseIdentifier {
	
	public EpisodeOfCareIdentifier() {
		
	}
	
	public EpisodeOfCareIdentifier(EpisodeOfCareEntity ep) {
		
		this.episodeOfCareEntity = ep;
	}
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "EPISODE_OF_CARE_IDENTIFIER_ID")
	public Integer getIdentifierId() { return identifierId; }
	public void setIdentifierId(Integer identifierId) { this.identifierId = identifierId; }
	private Integer identifierId;

	
	private EpisodeOfCareEntity episodeOfCareEntity;

	@ManyToOne
	@JoinColumn (name = "EPISODE_OF_CARE_ID", foreignKey= @ForeignKey(name="FK_EPISODE_OF_CARE_EPISODE_OF_CARE_IDENTIFIER"))
	public EpisodeOfCareEntity getEpisodeOfCareEntity() {
	        return this.episodeOfCareEntity;
	    }

	    public void setEpisodeOfCareEntity(EpisodeOfCareEntity episodeOfCareEntity) {
	        this.episodeOfCareEntity = episodeOfCareEntity;
	    }
}
