package uk.nhs.careConnect.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import javax.persistence.ForeignKey;

@Entity
@Immutable
@Table(name="TermParentChildLink")
public class TermConceptParentChildLink {
	

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="CHILD_CONCEPT_ID", nullable=false, referencedColumnName="CONCEPT_ID", foreignKey=@ForeignKey(name="FK_TERM_CONCEPTPC_CHILD"))
	private TermConcept child;

	@ManyToOne()
	@JoinColumn(name="CODESYSTEM_ID", nullable=false, foreignKey=@ForeignKey(name="FK_TERM_CONCEPTPC_CS"))
	private TermCodeSystem myCodeSystem;

	@ManyToOne(fetch = FetchType.EAGER,cascade= {})
	@JoinColumn(name="PARENT_CONCEPT_ID", nullable=false, referencedColumnName="CONCEPT_ID", foreignKey=@ForeignKey(name="FK_TERM_CONCEPTPC_PARENT"))
	private TermConcept parent;

	@Id()

	
	@Column(name="PARENT_CHILD_ID")
	private Long myPid;

	@Enumerated(EnumType.ORDINAL)
	@Column(name="REL_TYPE", length=5, nullable=true)
	private RelationshipTypeEnum myRelationshipType;

	public TermConcept getChild() {
		return child;
	}

	public RelationshipTypeEnum getRelationshipType() {
		return myRelationshipType;
	}

	public TermCodeSystem getCodeSystem() {
		return myCodeSystem;
	}
	
	public TermConcept getParent() {
		return parent;
	}

	public void setChild(TermConcept theChild) {
		child = theChild;
	}
	
	public void setCodeSystem(TermCodeSystem theCodeSystem) {
		myCodeSystem = theCodeSystem;
	}

	public void setParent(TermConcept theParent) {
		parent = theParent;
	}
	
	
	public void setRelationshipType(RelationshipTypeEnum theRelationshipType) {
		myRelationshipType = theRelationshipType;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((child == null) ? 0 : child.hashCode());
		result = prime * result + ((myCodeSystem == null) ? 0 : myCodeSystem.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((myRelationshipType == null) ? 0 : myRelationshipType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TermConceptParentChildLink other = (TermConceptParentChildLink) obj;
		if (child == null) {
			if (other.child != null)
				return false;
		} else if (!child.equals(other.child))
			return false;
		if (myCodeSystem == null) {
			if (other.myCodeSystem != null)
				return false;
		} else if (!myCodeSystem.equals(other.myCodeSystem))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (myRelationshipType != other.myRelationshipType)
			return false;
		return true;
	}


	public enum RelationshipTypeEnum{
		// ********************************************
		// IF YOU ADD HERE MAKE SURE ORDER IS PRESERVED
		ISA
	}


	public Long getId() {
		return myPid;
	}
}
