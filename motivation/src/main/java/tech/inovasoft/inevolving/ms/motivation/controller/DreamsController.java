package tech.inovasoft.inevolving.ms.motivation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.RequestDeleteDream;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ResponseDeleteDream;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ResponseVisionBord;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.*;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;
import tech.inovasoft.inevolving.ms.motivation.service.DreamsService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Motivation", description = "Gerenciador dos endpoints do serviço de Motivation | Motivation Service Endpoint Manager")
@RestController
@RequestMapping("/ms/motivation/dreams")
public class DreamsController {

    @Autowired
    private DreamsService service;

    @Operation(summary = "Adicionar um novo sonho. | Add a new dream.", description = "Retorna o sonho cadastrado | Return the registered dream")
    @Async("asyncExecutor")
    @PostMapping
    public CompletableFuture<ResponseEntity<Dreams>> addDream (
            @RequestBody DreamRequestDTO dreamDTO
    ) throws MaximumNumberOfRegisteredDreamsException, NotSavedDTOInDbException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                service.addDream(dreamDTO)
        ));
    }

    @Operation(summary = "Editar um sonho. | Edit a dream.", description = "Retorna o sonho editado. | Returns the edited dream.")
    @Async("asyncExecutor")
    @PatchMapping
    public CompletableFuture<ResponseEntity<Dreams>> updateDream (
            @RequestBody Dreams dreamDTO
    ) throws UserWithoutAuthorizationAboutThisDreamException, DreamNotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                service.updateDream(dreamDTO)
        ));
    }

    @Operation(summary = "Deletar um sonho. | Delete a dream.", description = "Retorna uma confirmação que o sonho foi deletado. | Returns a confirmation that the dream has been deleted.")
    @Async("asyncExecutor")
    @DeleteMapping
    public CompletableFuture<ResponseEntity<ResponseDeleteDream>> deleteDream (
            @RequestBody RequestDeleteDream dto
    ) throws UserWithoutAuthorizationAboutThisDreamException, DreamNotFoundException, DataBaseException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                service.deleteDream(dto.idDream(), dto.idUser())
        ));
    }

    @Operation(summary = "Consultar Sonhos | Consult Dreams", description = "Retorna uma lista com todos os sonhos do usuário. | Returns a list of all the user's dreams.")
    @Async("asyncExecutor")
    @GetMapping("/user/{id}")
    public CompletableFuture<ResponseEntity<List<Dreams>>> getDreamsByUserId(
            @PathVariable UUID id
    ) throws DataBaseException, DreamNotFoundException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                service.getDreamsByUserId(id)
        ));
    }

    @Operation(summary = "Consultar Sonho | Consult Dream", description = "Retorna o sonho do usuário. | Returns the user's dream.")
    @Async("asyncExecutor")
    @GetMapping("/{idDream}/{idUser}")
    public CompletableFuture<ResponseEntity<Dreams>> getDreamByID(
            @PathVariable UUID idDream,
            @PathVariable UUID idUser
    ) throws DataBaseException, DreamNotFoundException, UserWithoutAuthorizationAboutThisDreamException {
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                service.getDreamByID(idDream, idUser)
        ));
    }

    @Operation(summary = "Criar Vision Board | Create Vision Board", description = "Retorna uma url de um vision bord na amazon s3. | Returns a url of a vision board in amazon s3.")
    @Async("asyncExecutor")
    @GetMapping("/visionbord/generate/{idUser}")
    public CompletableFuture<ResponseEntity<ResponseVisionBord>> generateVisionBordByUserId(
            @PathVariable UUID idUser
    ) throws DataBaseException, DreamNotFoundException{
        return CompletableFuture.completedFuture(ResponseEntity.ok(
                service.generateVisionBordByUserId(idUser)
        ));
    }


}
