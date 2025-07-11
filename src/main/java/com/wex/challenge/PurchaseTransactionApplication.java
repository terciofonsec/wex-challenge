package com.wex.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PurchaseTransactionApplication {

	public static void main(String[] args) {
		SpringApplication.run(PurchaseTransactionApplication.class, args);
	}

}
