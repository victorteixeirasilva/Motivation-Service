package tech.inovasoft.inevolving.ms.motivation.unitary;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.DreamNotFoundException;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.MaximumNumberOfRegisteredDreamsException;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.UserWithoutAuthorizationAboutThisDreamException;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;
import tech.inovasoft.inevolving.ms.motivation.repository.DreamsRepository;
import tech.inovasoft.inevolving.ms.motivation.service.DreamsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DreamsServiceFailureTest {

    @Mock
    private DreamsRepository repository;

    @InjectMocks
    private DreamsService service;

    @Test
    public void notAddDreamBecauseMax() {
        // Given (Dado)
        DreamRequestDTO dto = new DreamRequestDTO(
                "Dinheiro",
                "Ganhar muito dinheiro",
                "Urldaimagem.com",
                UUID.randomUUID() // Correção: gera um UUID válido
        );



        List<Dreams> dreams = new ArrayList<>();
        for (int i = 1; i <= 200; i++){
            dreams.add(new Dreams(
                UUID.randomUUID(),
                dto.name(),
                dto.description(),
                dto.urlImage(),
                dto.idUser()
            ));
        }


        // When (Quando)
        when(repository.findAllByUserId(dto.idUser())).thenReturn(dreams);
        // When & Then (Verificação da exceção)
        Exception exception = assertThrows(MaximumNumberOfRegisteredDreamsException.class, () -> {
            service.addDream(dto);
        });


        // Then (Então)
        // Verifica se a mensagem de erro está correta
        assertEquals("Não foi possível cadastrar o sonho pois o mesmo já tem 200 sonhos cadastrados.", exception.getMessage());

        verify(repository, times(1)).findAllByUserId(dto.idUser()); // Garante que o repositório foi chamado corretamente
    }

    @Test
    public void notUpdateDreamBecauseUserWithoutAuthorizationAboutThisDream() {
        // Given (Dado)
        Dreams dream = new Dreams(
                UUID.randomUUID(),
                "Dinheiro",
                "Ganhar muito dinheiro",
                "Urldaimagem.com",
                UUID.randomUUID()
        );

        DreamRequestDTO dto = new DreamRequestDTO(
                "Dinheiro2",
                "Ganhar muito dinheiro2",
                "Urldaimagem.com2",
                UUID.randomUUID()
        );

        Dreams newDream = new Dreams(
                dream.getId(),
                dto.name(),
                dto.description(),
                dto.urlImage(),
                dream.getIdUser()
        );

        // When (Quando)
        // Mockando a resposta do repository
        when(repository.findById(dream.getId())).thenReturn(Optional.of(dream));

        // Then (Então)
        Exception exception = assertThrows(UserWithoutAuthorizationAboutThisDreamException.class, () -> {
            service.updateDream(dream.getId(), dto);
        });

        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void notUpdateDreamBecauseDreamNotFoundException() {
        // Given (Dado)
        Dreams dream = new Dreams(
                UUID.randomUUID(),
                "Dinheiro",
                "Ganhar muito dinheiro",
                "Urldaimagem.com",
                UUID.randomUUID()
        );

        DreamRequestDTO dto = new DreamRequestDTO(
                "Dinheiro2",
                "Ganhar muito dinheiro2",
                "Urldaimagem.com2",
                UUID.randomUUID()
        );

        // When (Quando)
        // Mockando a resposta do repository
        when(repository.findById(dream.getId())).thenReturn(Optional.empty());

        // Then (Então)
        Exception exception = assertThrows(DreamNotFoundException.class, () -> {
            service.updateDream(dream.getId(), dto);
        });

        verify(repository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void notDeletedDreamBecauseDreamNotFoundException() {
        // Given (Dado)
        Dreams dream = new Dreams(
                UUID.randomUUID(),
                "Dinheiro",
                "Ganhar muito dinheiro",
                "Urldaimagem.com",
                UUID.randomUUID()
        );

        // When (Quando)
        // Mockando a resposta do repository
        when(repository.findById(dream.getId())).thenReturn(Optional.of(dream));

        // Then (Então)
        Exception exception = assertThrows(DreamNotFoundException.class, () -> {
            service.deleteDream(UUID.randomUUID(), dream.getIdUser());
        });

        verify(repository, times(1)).findById(any(UUID.class));
    }
}
