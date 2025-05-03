package tech.inovasoft.inevolving.ms.motivation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ResponseDeleteDream;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ResponseVisionBord;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.*;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;
import tech.inovasoft.inevolving.ms.motivation.repository.DreamsRepository;
import tech.inovasoft.inevolving.ms.motivation.service.client.GeradorDeVisionBordClientService;
import tech.inovasoft.inevolving.ms.motivation.service.client.dto.ImageUrl;
import tech.inovasoft.inevolving.ms.motivation.service.client.dto.RequestGeradorDeVisionBordDTO;

import java.util.*;

@Service
public class DreamsService {

    @Autowired
    private DreamsRepository repository;

    @Autowired
    private GeradorDeVisionBordClientService geradorDeVisionBordClientService;

    private static final int MAX_DREAMS = 200;

    public Dreams addDream(DreamRequestDTO dto) throws MaximumNumberOfRegisteredDreamsException, NotSavedDTOInDbException {
        List<Dreams> dreams = repository.findAllByUserId(dto.idUser());
        if (dreams.size() >= MAX_DREAMS) {
            throw new MaximumNumberOfRegisteredDreamsException();
        }
        try {
            return repository.save(new Dreams(dto));
        } catch (Exception e) {
            throw new NotSavedDTOInDbException(dto);
        }
    }

    public Dreams updateDream(Dreams dream) throws DreamNotFoundException, UserWithoutAuthorizationAboutThisDreamException {
        Optional<Dreams> dreamOpt = repository.findById(dream.getId());
        if (dreamOpt.isEmpty()){
            throw new DreamNotFoundException();

        } else {
            Dreams newDream = dream;
            UUID idUser = dreamOpt.get().getIdUser();

            if (idUser == newDream.getIdUser()) {
                UUID idDream = dreamOpt.get().getId();
                newDream.setId(idDream);

                return repository.save(newDream);

            } else {
                throw new UserWithoutAuthorizationAboutThisDreamException();
            }
        }
    }

    public ResponseDeleteDream deleteDream(UUID idDream, UUID idUser) throws DataBaseException, DreamNotFoundException, UserWithoutAuthorizationAboutThisDreamException {
        Optional<Dreams> dreamOpt;
        try {
            dreamOpt = repository.findById(idDream);
        } catch (Exception e) {
            throw new DataBaseException("Error when getting Dreams.");
        }

        if (dreamOpt.isEmpty()){
            throw new DreamNotFoundException();
        }

        UUID dreamUserId = dreamOpt.get().getIdUser();
        if (dreamUserId != idUser){
            throw new UserWithoutAuthorizationAboutThisDreamException();
        }

        Dreams dream = dreamOpt.get();
        try {
            repository.delete(dream);
            return new ResponseDeleteDream("Dream deleted!");
        } catch (Exception e) {
            throw new DataBaseException("Error when deleting Dreams.");
        }
    }

    public Dreams getDreamByID(UUID idDream, UUID idUser) throws DataBaseException, DreamNotFoundException, UserWithoutAuthorizationAboutThisDreamException {
        Optional<Dreams> dreamOpt;
        try {
            dreamOpt = repository.findById(idDream);
        } catch (Exception e) {
            throw new DataBaseException("Error when getting Dreams.");
        }

        if (dreamOpt.isEmpty()){
            throw new DreamNotFoundException();
        }

        if (!idUser.equals(dreamOpt.get().getIdUser())){
            throw new UserWithoutAuthorizationAboutThisDreamException();
        }

        return dreamOpt.get();
    }

    public List<Dreams> getDreamsByUserId(UUID idUser) throws DreamNotFoundException, DataBaseException {
        List<Dreams> dreams;
        try {
            dreams = repository.findAllByUserId(idUser);
        } catch (Exception e) {
            throw new DataBaseException("Error when getting Dreams.");
        }

        if (dreams.isEmpty()){
            throw new DreamNotFoundException("No dreams were found");
        }

        return dreams;
    }

    public List<Dreams> selectedDreams(List<Dreams> allDreams) {
        Collections.shuffle(allDreams);
        return allDreams.subList(0, 100);
    }

    public ResponseVisionBord generateVisionBordByUserId(UUID idUser) throws DataBaseException, DreamNotFoundException {
        List<Dreams> allDreams = getDreamsByUserId(idUser);
        if (allDreams.size() < 100) {
            throw new DreamNotFoundException("O usuário possui menos de 100 sonhos, impossível gerar Vision Board.");
        }

        List<Dreams> selectedDreams = selectedDreams(allDreams);
        List<String> links = new ArrayList<>();
        for (Dreams dream : selectedDreams){
            links.add(dream.getUrlImage());
        }

        String user_id = String.valueOf(idUser);
        ImageUrl imageUrl = geradorDeVisionBordClientService.gerador(
                new RequestGeradorDeVisionBordDTO(
                        user_id, links
                )
        );

        return new ResponseVisionBord(imageUrl.image_url());
    }


}
