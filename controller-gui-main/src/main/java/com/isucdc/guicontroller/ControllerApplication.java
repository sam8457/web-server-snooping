package com.isucdc.guicontroller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.isucdc.guicontroller.controller"})
@EnableConfigurationProperties
public class ControllerApplication {

	public static void main(String[] args) {
		System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "false");
		SpringApplication.run(ControllerApplication.class, args);
	}

}
