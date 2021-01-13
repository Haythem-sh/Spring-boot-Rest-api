package com.Personalmanagement.webapp.model;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class Admin {
	
	private String userName   ;
	private String adminPassword ;

}
