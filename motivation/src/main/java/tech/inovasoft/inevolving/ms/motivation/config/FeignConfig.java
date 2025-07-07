package tech.inovasoft.inevolving.ms.motivation.config;

import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public Request.Options requestOptions() {
        // 10 minutos de timeout (600000 ms)
        return new Request.Options(60000, 600000);
    }
}
