package tech.inovasoft.inevolving.ms.motivation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.RequestDeleteDream;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.MessageResponseDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ResponseDeleteDream;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ResponseVisionBord;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.*;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;
import tech.inovasoft.inevolving.ms.motivation.service.DreamsService;
import tech.inovasoft.inevolving.ms.motivation.service.MotivationService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Motivation")
@RestController
@RequestMapping("/ms/motivation")
public class MotivationController {

    @Autowired
    private MotivationService service;

    @Operation(description = "Envia email motivacional para todos os usuários, com suas tarefas atrasadas")
    @Async("asyncExecutor")
    @GetMapping("/tasks/late")
    public CompletableFuture<ResponseEntity<MessageResponseDTO>> sendEmailForUsersWithLateTasks(
    ) {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                service.sendEmailForUsersWithLateTasks()
        ));
    }


}
