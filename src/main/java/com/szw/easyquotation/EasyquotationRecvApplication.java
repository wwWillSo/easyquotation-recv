package com.szw.easyquotation;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.szw.easyquotation.processor.ZmqEasyQuotationRecvProcessor;


@SpringBootApplication
@EnableScheduling
public class EasyquotationRecvApplication {

	@Autowired
	private ZmqEasyQuotationRecvProcessor zmqEasyQuotationRecvProcessor;

	@PostConstruct
	public void init() {

		zmqEasyQuotationRecvProcessor.execute();
	}

	public static void main(String[] args) {
		SpringApplication.run(EasyquotationRecvApplication.class, args);
	}

}
