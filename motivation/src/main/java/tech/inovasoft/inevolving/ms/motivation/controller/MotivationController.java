package tech.inovasoft.inevolving.ms.motivation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import tech.inovasoft.inevolving.ms.motivation.config.SchedulingConfig;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.MessageResponseDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ResponseAgendamentosDTO;
import tech.inovasoft.inevolving.ms.motivation.service.MotivationService;
import tech.inovasoft.inevolving.ms.motivation.service.client.Auth_For_MService.TokenService;
import tech.inovasoft.inevolving.ms.motivation.service.client.Auth_For_MService.dto.TokenValidateResponse;

import java.util.concurrent.CompletableFuture;

@Tag(name = "Motivation")
@RestController
@RequestMapping("/ms/motivation")
public class MotivationController {

    @Autowired
    private MotivationService service;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SchedulingConfig schedulingConfig;

    @Operation(description = "Envia email motivacional para todos os usuários, com suas tarefas atrasadas")
    @Async("asyncExecutor")
    @GetMapping("/tasks/late/{token}")
    public CompletableFuture<ResponseEntity<MessageResponseDTO>> sendEmailForUsersWithLateTasks(
        @PathVariable String token
    ) {
        TokenValidateResponse tokenValidateResponse = null;

        try {
            tokenValidateResponse = tokenService.validateToken(token);
            if (tokenValidateResponse == null) {
                return CompletableFuture.completedFuture(ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED
                ).build());
            }
        } catch (Exception e) {
            if (e.getMessage().equals("Invalid token")) {
                return CompletableFuture.completedFuture(ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED
                ).build());
            }
        }

        return CompletableFuture.completedFuture(ResponseEntity.ok(
                service.sendEmailForUsersWithLateTasks()
        ));
    }

    @Operation(description = "Envia email motivacional para todos os usuários que estão offline a um dia.")
    @Async("asyncExecutor")
    @GetMapping("/disconnected/{token}")
    public CompletableFuture<ResponseEntity<MessageResponseDTO>> sendEmailForUsersDisconnected(
        @PathVariable String token
    ) {
        TokenValidateResponse tokenValidateResponse = null;

        try {
            tokenValidateResponse = tokenService.validateToken(token);
            if (tokenValidateResponse == null) {
                return CompletableFuture.completedFuture(ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED
                ).build());
            }
        } catch (Exception e) {
            if (e.getMessage().equals("Invalid token")) {
                return CompletableFuture.completedFuture(ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED
                ).build());
            }
        }

        return CompletableFuture.completedFuture(ResponseEntity.ok(
                service.sendEmailForUsersDisconnected()
        ));
    }

    @Operation(description = "Força o reagendamento dos emails motivacionais e retorna os agendamentos gerados para o dia.")
    @Async("asyncExecutor")
    @GetMapping("/scheduling/force/{token}")
    public CompletableFuture<ResponseEntity<ResponseAgendamentosDTO>> forcarReagendamento(
        @PathVariable String token
    ) {
        try {
            TokenValidateResponse tokenValidateResponse = tokenService.validateToken(token);
            if (tokenValidateResponse == null) {
                return CompletableFuture.completedFuture(ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED
                ).build());
            }
        } catch (Exception e) {
            if (e.getMessage().equals("Invalid token")) {
                return CompletableFuture.completedFuture(ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED
                ).build());
            }
        }

        return CompletableFuture.completedFuture(ResponseEntity.ok(
                schedulingConfig.forcarReagendamento()
        ));
    }

}
