package tech.inovasoft.inevolving.ms.motivation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import tech.inovasoft.inevolving.ms.motivation.service.MotivationService;

@EnableScheduling
@Configuration
public class SchedulingConfig {

    @Autowired
    private MotivationService motivationService;

    @Scheduled(cron = "0 0 6 1 * *", zone = "America/Sao_Paulo")
    public void sendEmailForUsersWithLateTasks() {
        // lógica que será executada
        motivationService.sendEmailForUsersWithLateTasks();
    }

}
