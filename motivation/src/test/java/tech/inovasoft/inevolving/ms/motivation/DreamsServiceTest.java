package tech.inovasoft.inevolving.ms.motivation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;
import tech.inovasoft.inevolving.ms.motivation.repository.DreamsRepository;
import tech.inovasoft.inevolving.ms.motivation.service.DreamsService;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class DreamsServiceTest {

    @Mock
    private DreamsRepository repository;

    @InjectMocks
    private DreamsService service;

    @Test
    public void addDream() {
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
}