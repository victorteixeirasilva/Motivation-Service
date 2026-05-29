package tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.dto.PostponeDayRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.dto.TaskViewDTO;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "tasks-service", url = "${inevolving.uri.ms.task}")
public interface TasksClientService {

    @GetMapping("/late/{idUser}/{token}")
    ResponseEntity<List<TaskViewDTO>> getTasksLate(
            @PathVariable UUID idUser,
            @PathVariable String token,
            @RequestHeader(TasksServiceConstants.USER_TIMEZONE_HEADER) String userTimezone
    );

    @PostMapping("/date/postpone-day/{token}")
    ResponseEntity<Void> postponeDay(
            @PathVariable String token,
            @RequestBody PostponeDayRequestDTO dto
    );

}
