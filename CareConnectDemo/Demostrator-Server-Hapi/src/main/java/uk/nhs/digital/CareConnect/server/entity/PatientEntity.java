package uk.nhs.digital.CareConnect.server.entity;


import java.util.ArrayList;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="Patient", uniqueConstraints= @UniqueConstraint(name="PK_PATIENT", columnNames={"ID"}))
public class PatientEntity extends BaseResource {
	
	

	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID")
	private Integer id;	
	public void setId(Integer id) { this.id = id; }
	public Integer getId() { return id; }

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modifiedDate", nullable = true)
	private Date updated;
	public Date getUpdatedDate() { return updated; }
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createdDate", nullable = true)
	private Date createdDate;
	public Date getCreatedDate() { return createdDate; }
	
		@Column(name = "surname")
		private String familyName;
		public void setFamilyName(String familyName) {   this.familyName = familyName;   }
		public String getFamilyName() { return this.familyName;  }
		
		@Column(name = "forename")
		private String givenName;
		public void setGivenName(String givenName)
		{  this.givenName = givenName; }
		public String getGivenName()  {  return this.givenName;  }
		
		@Column(name = "initials")
		private String initials;
		public void setInitials(String initials)
		{  this.initials = initials; }
		public String getInitials()  {  return this.initials;  }
		
		@Column(name = "sex")
		private String gender;
		public void setGender(String gender)
		{  this.gender = gender; }
		public String getGender()  {  return this.gender;  }
		
		@Column(name = "title")
		private String title;
		public void setTitle(String title)
		{  this.title = title; }
		public String getTitle()  {  return this.title;  }
		
		@Column(name = "birthDate")
		private Date birthDate;
		public void setBirthDate(Date birthDate)
		{  this.birthDate = birthDate; }
		public Date getBirthDate()  {  return this.birthDate;  }
		
		@Column(name = "address1")
		private String address1;
		public void setAddress1(String address1)
		{  this.address1 = address1; }
		public String getAddress1()  {  return this.address1;  }
		
		@Column(name = "address2")
		private String address2;
		public void setAddress2(String address2)
		{  this.address2 = address2; }
		public String getAddress2()  {  return this.address2;  }
		
		@Column(name = "address3")
		private String address3;
		public void setAddress3(String address3)
		{  this.address3 = address3; }
		public String getAddress3()  {  return this.address3;  }
		
		@Column(name = "address4")
		private String address4;
		public void setAddress4(String address4)
		{  this.address4 = address4; }
		public String getAddress4()  {  return this.address4;  }
		
		@Column(name = "postCode")
		private String postCode;
		public void setpostCode(String postCode)
		{  this.postCode = postCode; }
		public String getPostCode()  {  return this.postCode;  }
		
		@Column(name = "GPCode")
		private String gpCode;
		public void setGPCode(String gpCode)
		{  this.gpCode = gpCode; }
		public String getGPCode()  {  return this.gpCode;  }
		
		@Column(name = "PASIdentifier")
		private String pasIdentifier;
		public void setPASIdentifier(String pasIdentifier)
		{  this.pasIdentifier = pasIdentifier; }
		public String getPASIdentifier()  {  return this.pasIdentifier;  }
		
		@Column(name = "NHSNumber")
		private String nhsNumber;
		public void setNHSNumber(String nhsNumber)
		{  this.nhsNumber = nhsNumber; }
		public String getNHSNumber()  {  return this.nhsNumber;  }
		
		// Patient IDENTIFIERS
		@OneToMany(mappedBy="patientEntity", targetEntity=PatientIdentifier.class)
		private List<PatientIdentifier> identifiers;
		public void setIdentifiers(List<PatientIdentifier> identifiers) {
	        this.identifiers = identifiers;
	    }
		public List<PatientIdentifier> getIdentifiers( ) {
			if (identifiers == null) {
		        identifiers = new ArrayList<PatientIdentifier>();
		    }
	        return this.identifiers;
	    }
		public List<PatientIdentifier> addIdentifier(PatientIdentifier pi) { 
			identifiers.add(pi);
			return identifiers; }
		
		public List<PatientIdentifier> removeIdentifier(PatientIdentifier identifier){ 
			identifiers.remove(identifiers); return identifiers; }
	
		/*
		
		 @OneToMany(mappedBy = "patientEntity", targetEntity=ReferralRequestEntity.class)
		 private List<ReferralRequestEntity> referrals;
		 
		 @OneToMany(mappedBy = "patientEntity", targetEntity=DocumentReferenceEntity.class)
		 private List<DocumentReferenceEntity> documents;
		*/
}
