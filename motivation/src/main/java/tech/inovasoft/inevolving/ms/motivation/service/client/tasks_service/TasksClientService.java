package tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.dto.PostponeDayRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.dto.Task;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "tasks-service", url = "${inevolving.uri.ms.task}")
public interface TasksClientService {

    @GetMapping("/late/{idUser}/{token}")
    ResponseEntity<List<Task>> getTasksLate(
            @PathVariable UUID idUser,
            @PathVariable String token
    );

    @PostMapping("/date/postpone-day/{token}")
    ResponseEntity<Void> postponeDay(
            @PathVariable String token,
            @RequestBody PostponeDayRequestDTO dto
    );

}
