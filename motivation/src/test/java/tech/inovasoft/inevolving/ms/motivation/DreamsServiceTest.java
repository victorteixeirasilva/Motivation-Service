package tech.inovasoft.inevolving.ms.motivation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.MaximumNumberOfRegisteredDreamsException;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;
import tech.inovasoft.inevolving.ms.motivation.repository.DreamsRepository;
import tech.inovasoft.inevolving.ms.motivation.service.DreamsService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class DreamsServiceTest {

    @Mock
    private DreamsRepository repository;

    @InjectMocks
    private DreamsService service;

    @Test
    public void addDreamOk() {
        // Given (Dado)
        DreamRequestDTO dto = new DreamRequestDTO(
                "Dinheiro",
                "Ganhar muito dinheiro",
                "Urldaimagem.com",
                UUID.randomUUID() // Correção: gera um UUID válido
        );

        Dreams newDream = new Dreams(
                UUID.randomUUID(),
                dto.name(),
                dto.description(),
                dto.urlImage(),
                dto.idUser()
        );

        Dreams dreamResult = new Dreams(
                newDream.getId(),
                dto.name(),
                dto.description(),
                dto.urlImage(),
                dto.idUser()
        );

        // Mockando a resposta do repository
        when(repository.save(any(Dreams.class))).thenReturn(dreamResult);

        // When (Quando)
        Dreams savedDream = service.addDream(dto);

        // Then (Então)
        assertNotNull(savedDream);
        assertEquals(dto.name(), savedDream.getName());
        assertEquals(dto.description(), savedDream.getDescription());
        assertEquals(dto.urlImage(), savedDream.getUrlImage());
        assertEquals(dto.idUser(), savedDream.getIdUser());
        assertNotNull(savedDream.getId());

        verify(repository, times(1)).save(any(Dreams.class)); // Garante que o repositório foi chamado corretamente
    }

    @Test
    public void addDreamMax200() {
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
}
