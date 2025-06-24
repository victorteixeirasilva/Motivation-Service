package tech.inovasoft.inevolving.ms.motivation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableFeignClients
@EnableScheduling
public class MotivationApplication {

	public static void main(String[] args) {
		SpringApplication.run(MotivationApplication.class, args);
	}

}
