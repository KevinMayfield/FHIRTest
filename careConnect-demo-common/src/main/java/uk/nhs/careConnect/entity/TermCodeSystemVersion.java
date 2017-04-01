package uk.nhs.careConnect.entity;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Immutable;

@Table(name="TermCodeSystemVersion", uniqueConstraints= {
		@UniqueConstraint(name="IDX_CSV_RESOURCEPID_AND_VER", columnNames= {"RES_ID", "RES_VERSION_ID"})
	})
	@Entity()
@Immutable
public class TermCodeSystemVersion extends BaseResource {
	//private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 */
	public TermCodeSystemVersion() {
		super();
	}


	@Id()
	@Column(name = "CODESYSTEM_VERSION_ID")
	public Long myId;
	public Long getMyId() {
		return myId;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "codeSystem")
	private Collection<TermConcept> concepts;
	public Collection<TermConcept> getConcepts() {
		if (concepts == null) {
			concepts = new ArrayList<TermConcept>();
		}
		return concepts;
	}


	// Removed link to normal HAPI resource table
	@Column(name = "RES_ID", nullable = false, updatable = false)
	private Long myResource;

	@Column(name = "RES_VERSION_ID", nullable = false, updatable = false)
	private String myResourceVersionId;
	public String getResourceVersionId() {
		return myResourceVersionId;
	}
	public void setResourceVersionId(String theResourceVersionId) {
		myResourceVersionId = theResourceVersionId;
	}
	/*
	@OneToOne(mappedBy = "myCurrentVersion")
	private TermCodeSystem termCodeSystem;
	public TermCodeSystem getCurrentTermCodeSystem()  {  return this.termCodeSystem;  }
	*/
	@ManyToOne()
	@JoinColumn (name = "CODESYSTEM_ID",foreignKey= @ForeignKey(name="FK_CODESYSTEM_VERSION"))
	private TermCodeSystem codeSystemEntity;
	public TermCodeSystem getTermCodeSystem() {
        return this.codeSystemEntity;
	}
	public void setTermCodeSystem(TermCodeSystem termCodeSystem) {
        this.codeSystemEntity = termCodeSystem;
}
	
	
}
