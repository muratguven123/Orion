package org.murat.orion.invest_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "org.murat.orion.invest_service.interfaces")
@EnableScheduling
public class InvestServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvestServiceApplication.class, args);
	}

}
