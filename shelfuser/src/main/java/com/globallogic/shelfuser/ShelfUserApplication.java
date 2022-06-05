package com.globallogic.shelfuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ShelfUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShelfUserApplication.class, args);
	}

}
