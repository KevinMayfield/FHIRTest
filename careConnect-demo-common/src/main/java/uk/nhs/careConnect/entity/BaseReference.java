package uk.nhs.careConnect.entity;
import javax.persistence.Column;

import javax.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class BaseReference {
	
	
	
		
	@Column(name = "ORDER")
	private Integer order;
	
	
	
}
