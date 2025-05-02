package tech.inovasoft.inevolving.ms.motivation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ResponseDeleteDream;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ResponseVisionBord;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.*;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;
import tech.inovasoft.inevolving.ms.motivation.repository.DreamsRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DreamsService {

    @Autowired
    private DreamsRepository repository;

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

    public Dreams updateDream(UUID id, DreamRequestDTO dto) throws DreamNotFoundException, UserWithoutAuthorizationAboutThisDreamException {
        Optional<Dreams> dreamOpt = repository.findById(id);
        if (dreamOpt.isEmpty()){
            throw new DreamNotFoundException();

        } else {
            Dreams newDream = new Dreams(dto);
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

        UUID dreamUserId = dreamOpt.get().getIdUser();
        if (dreamUserId != idUser){
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

    private List<Dreams> selectedDreams(List<Dreams> allDreams) {
        return null;
    }

    public ResponseVisionBord generateVisionBordByUserId(UUID idUser){
        //TODO Fazer primeiro o selectedDreams
        return null;
    }
}
