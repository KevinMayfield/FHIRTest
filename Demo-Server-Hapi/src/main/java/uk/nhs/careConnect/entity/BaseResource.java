package uk.nhs.careConnect.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import ca.uhn.fhir.context.FhirVersionEnum;


@MappedSuperclass
public abstract class BaseResource  {
	/**
	 * 
	 */
	
	
	@Column(name = "RES_UPDATED",insertable=false, updatable=false)
	@UpdateTimestamp
	private Date resUpdated;
	public Date getResourceUpdated() {
		return this.resUpdated;
	}
	
	@Column(name = "RES_CREATED", nullable = true)
	@CreationTimestamp
	private Date resCreated;
	public Date getResourceCreated() {
		return this.resCreated;
	}
	
	
	@Column(name = "RES_DELETED", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date myDeleted;
	public Date getDeleted() {
		return this.myDeleted;
	}
	public void setDeleted(Date theDate) {
		this.myDeleted = theDate;
	}
	
	@Column(name = "RES_MESSAGE_REF", nullable = true)
	
	private String resMessage;
	public String getResourceMessage() {
		return this.resMessage;
	}
	public void setResourceMessage(String resMessage) {
		this.resMessage = resMessage;
	}

		
	@Column(name = "RES_VERSION", nullable = true, length = 7)
	@Enumerated(EnumType.STRING)
	private FhirVersionEnum myFhirVersion;
	public void setFhirVersion(FhirVersionEnum theFhirVersion) {
		this.myFhirVersion = theFhirVersion;
	}


	
	
}
