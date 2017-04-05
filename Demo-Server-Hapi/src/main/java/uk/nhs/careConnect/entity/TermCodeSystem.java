package uk.nhs.careConnect.entity;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Immutable;

@Table(name="TermCodeSystem", uniqueConstraints= {
		@UniqueConstraint(name="IDX_CS_CODESYSTEM", columnNames= {"CODE_SYSTEM_URI"})
	})
@Entity()
@Immutable
public class TermCodeSystem extends BaseResource {

	@Id()
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CODESYSTEM_ID")
	private Long myPid;
	public Long getPID()
	{
		return this.myPid;
	}

	
	@Column(name="CODE_SYSTEM_URI", nullable=false)
	private String codeSystemUri;
	public String getCodeSystemUri() {
		return codeSystemUri;
	}
	public void setCodeSystemUri(String theCodeSystemUri) {
		codeSystemUri = theCodeSystemUri;
	}

	/*
	@OneToOne()
	@JoinColumn(name="CURRENT_VERSION_ID", referencedColumnName="CODESYSTEM_VERSION_ID", nullable=true)
	private TermCodeSystemVersion myCurrentVersion;
	public TermCodeSystemVersion getCurrentVersion() {
		return myCurrentVersion;
	}
	public void setCurrentVersion(TermCodeSystemVersion theCurrentVersion) {
		myCurrentVersion = theCurrentVersion;
	}
	*/
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "codeSystemEntity")	
	private Collection<TermCodeSystemVersion> myVersions;
	public Collection<TermCodeSystemVersion> getVersions() {
		if (myVersions == null) {
			myVersions = new ArrayList<TermCodeSystemVersion>();
		}
		return myVersions;
	}
	
	@Column(name = "RES_ID", nullable = false, updatable = false)
	private Long myResource;
	public Long getResource() {
		return myResource;
	}
	public void setResource(Long theResource) {
		myResource = theResource;
	}
	
	@Column(name="name", nullable=false)
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String theName) {
		name = theName;
	}
}
