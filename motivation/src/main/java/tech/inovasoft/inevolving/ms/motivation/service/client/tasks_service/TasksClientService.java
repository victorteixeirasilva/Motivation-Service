package tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.dto.Task;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "tasks-service", url = "${inevolving.uri.ms.task}")
public interface TasksClientService {

    @GetMapping("/{idUser}")
    ResponseEntity<List<Task>> getTasksLate(
            @PathVariable UUID idUser
    );

}
