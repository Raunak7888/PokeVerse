package com.poke.dex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DexApplication {

	public static void main(String[] args) {
		SpringApplication.run(DexApplication.class, args);
	}

}
