package tech.inovasoft.inevolving.ms.motivation.unitary;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ResponseDeleteDream;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ResponseVisionBord;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.*;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;
import tech.inovasoft.inevolving.ms.motivation.repository.DreamsRepository;
import tech.inovasoft.inevolving.ms.motivation.service.DreamsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class DreamsServiceSuccessTest {

    @Mock
    private DreamsRepository repository;

    @InjectMocks
    private DreamsService service;

    @Test
    public void addDreamOk() throws MaximumNumberOfRegisteredDreamsException, NotSavedDTOInDbException {
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
    public void updateDreamOk() throws UserWithoutAuthorizationAboutThisDreamException, DreamNotFoundException {
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
                dream.getIdUser()
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
        when(repository.save(any(Dreams.class))).thenReturn(newDream);
        Dreams updatedDream = service.updateDream(dream.getId(), dto);


        // Then (Então)
        assertEquals(dream.getId(), updatedDream.getId());
        assertEquals(dream.getIdUser(), updatedDream.getIdUser());
        assertNotEquals(dream.getName(), updatedDream.getName());
        assertNotEquals(dream.getDescription(), updatedDream.getDescription());
        assertNotEquals(dream.getUrlImage(), updatedDream.getUrlImage());

        verify(repository, times(1)).findById(any(UUID.class));
        verify(repository, times(1)).save(any(Dreams.class)); // Garante que o repositório foi chamado corretamente

    }

    @Test
    public void deleteDreamOk() throws UserWithoutAuthorizationAboutThisDreamException, DataBaseException, DreamNotFoundException {
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
        doNothing().when(repository).delete(dream);
        ResponseDeleteDream responseDeleteDream = service.deleteDream(dream.getId(), dream.getIdUser());

        // Then (Então)
        assertEquals("Dream deleted!", responseDeleteDream.message());
        verify(repository, times(1)).findById(any(UUID.class));
        verify(repository, times(1)).delete(dream);
    }

    @Test
    public void getDreamByIdOk() throws UserWithoutAuthorizationAboutThisDreamException, DataBaseException, DreamNotFoundException {
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
        Dreams dreamBd = service.getDreamByID(dream.getId(), dream.getIdUser());

        // Then (Então)
        assertEquals(dream.getId(), dreamBd.getId());
        assertEquals(dream.getIdUser(), dreamBd.getIdUser());
        assertEquals(dream.getName(), dreamBd.getName());
        assertEquals(dream.getDescription(), dreamBd.getDescription());
        assertEquals(dream.getUrlImage(), dreamBd.getUrlImage());
        verify(repository, times(1)).findById(dream.getId());
    }

    @Test
    public void getDreamsByUserIdOk() throws DataBaseException, DreamNotFoundException {
        // Given (Dado)
        Dreams dreamMock = new Dreams(
                UUID.randomUUID(),
                "Dinheiro",
                "Ganhar muito dinheiro",
                "Urldaimagem.com",
                UUID.randomUUID()
        );

        List<Dreams> dreamsMock = new ArrayList<>();
        for (int i = 1; i <= 200; i++){
            dreamsMock.add(dreamMock);
        }

        // When (Quando)
        // Mockando a resposta do repository
        when(repository.findAllByUserId(dreamMock.getIdUser())).thenReturn(dreamsMock);
        List<Dreams> dreamsBd = service.getDreamsByUserId(dreamMock.getIdUser());

        // Then (Então)
        assertEquals(200, dreamsBd.size());
        assertEquals(dreamMock.getIdUser(), dreamsBd.get(120).getIdUser());
        verify(repository, times(1)).findAllByUserId(dreamMock.getIdUser());
    }

    @Test
    public void generateVisionBordByUserId() throws DataBaseException, DreamNotFoundException {
        // Given (Dado)
        Dreams dreamMock = new Dreams(
                UUID.randomUUID(),
                "Dinheiro",
                "Ganhar muito dinheiro",
                "Urldaimagem.com",
                UUID.randomUUID()
        );

        List<Dreams> dreamsMock = new ArrayList<>();
        for (int i = 1; i <= 200; i++){
            dreamsMock.add(dreamMock);
        }

        ResponseVisionBord expectedVisionBord = new ResponseVisionBord("urlvisionbord");

        // When (Quando)
        // Mockando a resposta do repository
        when(service.getDreamsByUserId(dreamMock.getIdUser())).thenReturn(dreamsMock);
        ResponseVisionBord visionBordResult = service.generateVisionBordByUserId(dreamMock.getIdUser());

        // Then (Então)
        //TODO Fazer primeiro o teste de selectedDreams
//        assertEquals(200, dreamsBd.size());
//        assertEquals(dreamMock.getIdUser(), dreamsBd.get(120).getIdUser());
        verify(service, times(1)).getDreamsByUserId(dreamMock.getIdUser());
    }


}
