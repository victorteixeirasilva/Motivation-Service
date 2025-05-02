package tech.inovasoft.inevolving.ms.motivation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.ResponseDeleteDream;
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

    public Dreams addDream(DreamRequestDTO dto) {
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

    public Dreams updateDream(UUID id, DreamRequestDTO dto) throws UserWithoutAuthorizationAboutThisDreamException {
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

    public ResponseDeleteDream deleteDream(UUID idDream, UUID idUser){
        Optional<Dreams> dreamOpt = repository.findById(idDream);
        if (dreamOpt.isEmpty()){
            //TODO erro caso não encontre o sonho cujo id foi informado.
            return null;
        }

        UUID dreamUserId = dreamOpt.get().getIdUser();
        if (dreamUserId != idUser){
            //TODO erro caso o usuário titular do sonho for diferente do usuário da requisição.
            return null;
        }

        try {
            repository.delete(dreamOpt.get());
            return new ResponseDeleteDream("Sonho deletado!");
        } catch (Exception e) {
            throw new DataBaseException("Problema na hora de deletar Sonhos.");
        }
    }
}
