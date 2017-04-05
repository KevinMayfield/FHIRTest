package uk.nhs.digital.CareConnect.server.entity;
import javax.persistence.Column;

import javax.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class BaseReference {
	
	
	
		
	@Column(name = "ORDER")
	private Integer order;
	
	
	
}
