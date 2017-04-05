package uk.nhs.careConnect.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Immutable;

// KGM This is a dumber version of the HAPI class. Just enough to enforce foreign key constraint.

@Entity
@Immutable	
@Table(name="TermConcept", uniqueConstraints= {
	@UniqueConstraint(name="IDX_CONCEPT_CS_CODE", columnNames= {"CODESYSTEM_ID", "CODE"})
}, indexes= {
	@Index(name = "IDX_CONCEPT_INDEXSTATUS", columnList="INDEX_STATUS") 
})

public class TermConcept extends BaseResource {
	private static final int MAX_DESC_LENGTH = 400;
	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(TermConcept.class);

	//private static final long serialVersionUID = 1L;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade= {})
	private Collection<TermConceptParentChildLink> children;

	@Column(name = "CODE", length = 100, nullable = false)
	private String code;
	public String getCode() {
		return code;
	}
	public void setCode(String theCode) {
		code = theCode;
	}

	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CODESYSTEM_ID",referencedColumnName = "CODESYSTEM_ID", foreignKey = @ForeignKey(name = "FK_CONCEPT_PID_CS_PID"))
	private TermCodeSystem codeSystem;
	public TermCodeSystem getTermCodeSystem() {
		return this.codeSystem;
	}
	
	//@formatter:off
	@Column(name="DISPLAY", length=MAX_DESC_LENGTH, nullable=true)
	private String myDisplay;
	public String getDisplay() {
		return myDisplay;
	}
	public TermConcept setDisplay(String theDisplay) {
		myDisplay = theDisplay;
		if (theDisplay != null && !theDisplay.isEmpty() && theDisplay.length() > MAX_DESC_LENGTH) {
			myDisplay = myDisplay.substring(0, MAX_DESC_LENGTH);
		}
		return this;
	}

	@Id()
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CONCEPT_ID")
	private Long conceptId;
	public Long getId() {
		return conceptId;
	}
	
	@Column(name = "INDEX_STATUS", nullable = true)
	private Long myIndexStatus;

	@Transient
	private String myParentPids;

	@OneToMany(cascade = {}, fetch = FetchType.LAZY, mappedBy = "child")
	private Collection<TermConceptParentChildLink> parents;

	public TermConcept() {
		super();
	}
	
	@Column(name = "active")
	private Boolean active;
	public void setActive(Boolean active) {   this.active = active;   }
	public Boolean getActive() { return this.active;  }
	
	@Column(name = "effectiveDate")
	private Date effectiveDate;
	public void setEffectiveDate(Date effectiveDate) {   this.effectiveDate = effectiveDate;   }
	public Date getEffectiveDate() { return this.effectiveDate;  }
	
	
	
	@ManyToOne
	@JoinColumn(name = "moduleId",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_MODULE"))
	private TermConcept moduleId;
	public void setModuleId(TermConcept moduleId) {   this.moduleId = moduleId;   }
	public TermConcept getModuleId() { return this.moduleId;  }
	
	
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn (name = "definitionStatusId",foreignKey= @ForeignKey(name="FK_TERM_CONCEPT_DEFINITION"))
	private TermConcept definitionStatusId;
	public void setDefinitionStatusId(TermConcept definitionStatusId) {    this.definitionStatusId = definitionStatusId;  }
	public TermConcept getDefinitionStatusId() {    return this.definitionStatusId;    }
    
	
	public TermConcept addChild(TermConcept theChild, RelationshipTypeEnum theRelationshipType) {
		Validate.notNull(theRelationshipType, "theRelationshipType must not be null");
		TermConceptParentChildLink link = new TermConceptParentChildLink();
		link.setParent(this);
		link.setChild(theChild);
		// KGM link.setRelationshipType(theRelationshipType);
		getChildren().add(link);

		theChild.getParents().add(link);
		return this;
	}

	public void addChildren(List<TermConcept> theChildren, RelationshipTypeEnum theRelationshipType) {
		for (TermConcept next : theChildren) {
			addChild(next, theRelationshipType);
		}
	}

	@Override
	public boolean equals(Object theObj) {
		if (!(theObj instanceof TermConcept)) {
			return false;
		}
		if (theObj == this) {
			return true;
		}

		TermConcept obj = (TermConcept) theObj;

		EqualsBuilder b = new EqualsBuilder();
		b.append(codeSystem, obj.codeSystem);
		b.append(code, obj.code);
		return b.isEquals();
	}

	public Collection<TermConceptParentChildLink> getChildren() {
		if (children == null) {
			children = new ArrayList<TermConceptParentChildLink>();
		}
		return children;
	}

	
	
	

	public Long getIndexStatus() {
		return myIndexStatus;
	}

	public String getParentPidsAsString() {
		return myParentPids;
	}

	public Collection<TermConceptParentChildLink> getParents() {
		if (parents == null) {
			parents = new ArrayList<TermConceptParentChildLink>();
		}
		return parents;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder b = new HashCodeBuilder();
		b.append(codeSystem);
		b.append(code);
		return b.toHashCode();
	}

	private void parentPids(TermConcept theNextConcept, Set<Long> theParentPids) {
		for (TermConceptParentChildLink nextParentLink : theNextConcept.getParents()) {
			TermConcept parent = nextParentLink.getParent();
			Long parentConceptId = parent.getId();
			Validate.notNull(parentConceptId);
			if (parent != null && theParentPids.add(parentConceptId)) {
				parentPids(parent, theParentPids);
			}
		}
	}

	@PreUpdate
	@PrePersist
	public void prePersist() {
		Set<Long> parentPids = new HashSet<Long>();
		TermConcept entity = this;
		parentPids(entity, parentPids);
		entity.setParentPids(parentPids);

		ourLog.trace("Code {}/{} has parents {}", entity.getId(), entity.getCode(), entity.getParentPidsAsString());
	}


	public void setCodeSystem(TermCodeSystem theCodeSystem) {
		codeSystem = theCodeSystem;
	}


	public void setIndexStatus(Long theIndexStatus) {
		myIndexStatus = theIndexStatus;
	}

	public void setParentPids(Set<Long> theParentPids) {
		StringBuilder b = new StringBuilder();
		for (Long next : theParentPids) {
			if (b.length() > 0) {
				b.append(' ');
			}
			b.append(next);
		}

		if (b.length() == 0) {
			b.append("NONE");
		}

		myParentPids = b.toString();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("code", code).append("display", myDisplay).build();
	}
}
