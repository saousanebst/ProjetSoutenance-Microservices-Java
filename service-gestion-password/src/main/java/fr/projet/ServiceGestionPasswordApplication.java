package fr.projet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServiceGestionPasswordApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceGestionPasswordApplication.class, args);
	}

}
