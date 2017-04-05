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
@Table(name="DocumentReferenceIdentifier", uniqueConstraints= @UniqueConstraint(name="PK_DOCUMENT_REFERENCE_IDENTIFIER", columnNames={"DOCUMENT_REFERENCE_IDENTIFIER_ID"}))
public class DocumentReferenceIdentifier extends BaseIdentifier {
	
	
	public DocumentReferenceIdentifier() {
		
		
	}
	public DocumentReferenceIdentifier(DocumentReferenceEntity edr) {
		
		this.documentReferenceEntity = edr;
	}
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "DOCUMENT_REFERENCE_IDENTIFIER_ID")
	public Integer getIdentifierId() { return identifierId; }
	public void setIdentifierId(Integer identifierId) { this.identifierId = identifierId; }
	private Integer identifierId;

	/*
	@Column(name = "IDENTIFIER_POSITION",updatable=false, insertable = false )
	public Integer getIdentifierPosition() { return identifierPosition; }
	public void setIdentifierPosition(Integer identifierPosition) { this.identifierPosition = identifierPosition; }
	private Integer identifierPosition;
	*/
	
	@Column(name="MASTER_IDENTIFIER")
	public Boolean getMasterIdentifier() { return masterIdentifier; }
	public void setMasterIdentifier(Boolean masterIdentifier ) { this.masterIdentifier = masterIdentifier; }
	private Boolean masterIdentifier;


	private DocumentReferenceEntity documentReferenceEntity;
	
	@ManyToOne
	@JoinColumn (name = "DOCUMENT_REFERENCE_ID",foreignKey= @ForeignKey(name="FK_DOCUMENT_REFERENCE_DOCUMENT_REFERENCE_IDENTIFIER"))
	

	// remember this is getting picked up by the mappedBy column
	 public DocumentReferenceEntity getDocumentReferenceEntity() {
	        return this.documentReferenceEntity;
	    }

	    public void setDocumentReferenceEntity(DocumentReferenceEntity documentReferenceEntity) {
	        this.documentReferenceEntity = documentReferenceEntity;
	    }

}
