package uk.nhs.careConnect.entity;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;



@Entity
@Table(name="DocumentReference", indexes={@Index(name="IDX_CREATED",columnList="created",unique=false)}, uniqueConstraints= @UniqueConstraint(name="PK_DOCUMENT_REFERENCE", columnNames={"DOCUMENT_REFERENCE_ID"}))
public class DocumentReferenceEntity extends BaseResource {
	
	public DocumentReferenceEntity(PatientEntity patientEntity) {
	//	this.patientEntity = patientEntity;
	}
	public DocumentReferenceEntity() {
	}
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="DOCUMENT_REFERENCE_ID")
	private Integer id;	
	public void setId(Integer id) { this.id = id; }
	public Integer getId() { return id; }
	

	
	
	@Column(name = "authourRef")
	private String authourRef;
	public String getAuthourRef() { return this.authourRef; }
	public void setAuthourRef(String authourRef) { this.authourRef = authourRef; }
	
	@Column(name = "authourDisplay")
	private String authourDisplay;
	public String getAuthourDisplay() { return this.authourDisplay; }
	public void setAuthourDisplay(String authourDisplay) { this.authourDisplay = authourDisplay; }
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "typeCT", referencedColumnName = "CONCEPT_ID", foreignKey = @ForeignKey(name = "FK_DOCUMENT_REFERENCE_TYPE_CONCEPT_PID"))
	private TermConcept typeCT;
	public TermConcept getTypeCT() { return this.typeCT; }
	public void setTypeCT(TermConcept typeCT) { this.typeCT = typeCT; 	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "typeLocalCT", referencedColumnName = "CONCEPT_ID", foreignKey = @ForeignKey(name = "FK_DOCUMENT_REFERENCE_LOCAL_TYPE_CONCEPT_PID"))
	private TermConcept typeLocalCT;
	public TermConcept getTypeLocalCT() { return this.typeLocalCT; }
	public void setTypeLocalCT(TermConcept typeLocalCT) { 	this.typeLocalCT = typeLocalCT; 	}


	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "practiceSettingCT", referencedColumnName = "CONCEPT_ID", foreignKey = @ForeignKey(name = "FK_DOCUMENT_REFERENCE_PRACTICE_SETTING_CONCEPT_PID"))
	private TermConcept practiceSettingCT;
	public TermConcept getPracticeSettingCT() { return this.practiceSettingCT; }
	public void setPracticeSettingCT(TermConcept practiceSettingCT) { 	this.practiceSettingCT = practiceSettingCT; 	}
	
	
	@Column(name = "created", nullable = true)
	private Date created;
	public Date getCreated() {
		return this.created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
	@Column(name = "indexed", nullable = true)
	private Date indexed;
	public Date getIndexed() {
		return indexed;
	}
	public void setIndexed(Date indexed) {
		this.indexed = indexed;
	}
	
	@Column(name = "attachmentContentType")
	private String attachmentContentType;
	public String getAttachmentContentType() { return this.attachmentContentType; }
	public void setAttachmentContentType(String attachmentContentType) { 	this.attachmentContentType = attachmentContentType; 	}
	
	@Column(name = "attachmentUri")
	private String attachmentUri;
	public String getAttachmentUri() { return this.attachmentUri; }
	public void setAttachmentUri(String attachmentUri) { 	this.attachmentUri = attachmentUri; 	}
	
	@Column(name = "attachmentTitle")
	private String attachmentTitle;
	public String getAttachmentTitle() { return this.attachmentTitle; }
	public void setAttachmentTitle(String attachmentTitle) { 	this.attachmentTitle = attachmentTitle; 	}
	
	@OneToMany(mappedBy="documentReferenceEntity", targetEntity=DocumentReferenceIdentifier.class)
	private List<DocumentReferenceIdentifier> identifiers;
	public void setIdentifiers(List<DocumentReferenceIdentifier> identifiers) {
        this.identifiers = identifiers;
    }
	public List<DocumentReferenceIdentifier> getIdentifiers( ) {
		if (identifiers == null) {
	        identifiers = new ArrayList<DocumentReferenceIdentifier>();
	    }
        return this.identifiers;
    }
	public List<DocumentReferenceIdentifier> addIdentifier(DocumentReferenceIdentifier pi) { 
		identifiers.add(pi);
		return identifiers; }
	
	public List<DocumentReferenceIdentifier> removeIdentifier(DocumentReferenceIdentifier identifier){ 
		identifiers.remove(identifiers); return identifiers; }
	
	
	
	@ManyToOne
	@JoinColumn(name="PATIENT_ID", referencedColumnName="id", foreignKey= @ForeignKey(name="FK_PATIENT_DOCUMENT_REFERENCE"))
	public PatientEntity patientEntity;
	public PatientEntity getPatientEntity() {
	        return this.patientEntity;
	}
	public void setPatientEntity(PatientEntity patientEntity) {
	        this.patientEntity = patientEntity;
	}
	
	
	
}
