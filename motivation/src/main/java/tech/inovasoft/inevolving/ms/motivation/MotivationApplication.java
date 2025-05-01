package tech.inovasoft.inevolving.ms.motivation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MotivationApplication {

	public static void main(String[] args) {
		SpringApplication.run(MotivationApplication.class, args);
	}

}
