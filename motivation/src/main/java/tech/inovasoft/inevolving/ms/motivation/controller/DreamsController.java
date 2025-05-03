package tech.inovasoft.inevolving.ms.motivation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.RequestDeleteDream;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.*;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;
import tech.inovasoft.inevolving.ms.motivation.service.DreamsService;

import java.util.UUID;
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

    @Operation(summary = "Editar um sonho.", description = "Retorna o sonho editado.")
    @Async("asyncExecutor")
    @PatchMapping
    public CompletableFuture<ResponseEntity> updateDream (@RequestBody Dreams dreamDTO) throws UserWithoutAuthorizationAboutThisDreamException, DreamNotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.updateDream(dreamDTO)));
    }

    @Operation(summary = "Deletar um sonho.", description = "Retorna uma confirmação que o sonho foi deletado.")
    @Async("asyncExecutor")
    @DeleteMapping
    public CompletableFuture<ResponseEntity> deleteDream (@RequestBody RequestDeleteDream dto) throws UserWithoutAuthorizationAboutThisDreamException, DreamNotFoundException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(service.deleteDream(dto.idDream(), dto.idUser())));
    }

    @Operation(summary = "Consultar Sonhos", description = "Retorna uma lista com todos os sonhos do usuário.")
    @Async("asyncExecutor")
    @GetMapping("/user/{id}")
    public CompletableFuture<ResponseEntity> getDreamsByUserId(@PathVariable UUID id) throws DataBaseException, DreamNotFoundException {
        return CompletableFuture.completedFuture(
                ResponseEntity.ok(service.getDreamsByUserId(id)));
    }

    @Operation(summary = "Consultar Sonho", description = "Retorna uma lista com todos os sonhos do usuário.")
    @Async("asyncExecutor")
    @GetMapping("/{idDream}/{idUser}")
    public CompletableFuture<ResponseEntity> getDreamByID(@PathVariable UUID idDream, @PathVariable UUID idUser) throws DataBaseException, DreamNotFoundException, UserWithoutAuthorizationAboutThisDreamException {
        return CompletableFuture.completedFuture(
                ResponseEntity.ok(service.getDreamByID(idDream, idUser)));
    }


    @Operation(summary = "Gerar VisionBord", description = "Retorna uma url de um vision bord na amazon s3.")
    @Async("asyncExecutor")
    @GetMapping("/visionbord/generate/{idUser}")
    public CompletableFuture<ResponseEntity> generateVisionBordByUserId(@PathVariable UUID idUser) throws DataBaseException, DreamNotFoundException{
        return CompletableFuture.completedFuture(
                ResponseEntity.ok(service.generateVisionBordByUserId(idUser)));
    }


}
