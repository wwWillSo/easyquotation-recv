package com.szw.easyquotation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;


@Configuration
public class WebConfig {
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

	// @Bean(initMethod = "init")
	// public RabbitMQRecv rabbitMQRecv() {
	// return new RabbitMQRecv();
	// }
}
