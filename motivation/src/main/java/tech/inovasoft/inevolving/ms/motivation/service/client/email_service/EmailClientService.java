package tech.inovasoft.inevolving.ms.motivation.service.client.email_service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tech.inovasoft.inevolving.ms.motivation.service.client.email_service.dto.EmailRequest;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.dto.Task;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@FeignClient(name = "email-service", url = "${inevolving.uri.ms.email}")
public interface EmailClientService {

    @PostMapping
    ResponseEntity<String> sendEmail(@RequestBody EmailRequest request);

}
