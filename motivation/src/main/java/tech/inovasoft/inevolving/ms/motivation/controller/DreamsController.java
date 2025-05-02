package tech.inovasoft.inevolving.ms.motivation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.MaximumNumberOfRegisteredDreamsException;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.NotSavedDTOInDbException;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;
import tech.inovasoft.inevolving.ms.motivation.service.DreamsService;

import java.util.concurrent.CompletableFuture;

@Tag(name = "Motivation", description = "Gerenciador dos end-poits do serviço de Motivation")
@RestController
@RequestMapping("/ms/motivation/dreams")
public class DreamsController {

    @Autowired
    private DreamsService service;

    @Operation(summary = "Adiciona um novo sonho.", description = "Retorna o sonho cadastrado")
    @Async("asyncExecutor")
    @PostMapping
    public CompletableFuture<ResponseEntity> addDream (@RequestBody DreamRequestDTO dreamDTO) throws MaximumNumberOfRegisteredDreamsException, NotSavedDTOInDbException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.addDream(dreamDTO)));
    }
}
