package com.Personalmanagement.webapp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix="com.personalmanagement.webapp")
public class CustomProperties {

	private String apiUrl;


}
