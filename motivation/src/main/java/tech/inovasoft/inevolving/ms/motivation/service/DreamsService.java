package tech.inovasoft.inevolving.ms.motivation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.DreamNotFoundException;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.MaximumNumberOfRegisteredDreamsException;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.NotSavedDTOInDbException;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.UserWithoutAuthorizationAboutThisDreamException;
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
        Optional<Dreams> oldDreamOpt = repository.findById(id);
        if (oldDreamOpt.isEmpty()){
            throw new DreamNotFoundException();
        } else {
            Dreams newDream = new Dreams(dto);
            if (oldDreamOpt.get().getIdUser() == newDream.getIdUser()) {
                newDream.setId(oldDreamOpt.get().getId());
                return repository.save(newDream);
            } else {
                throw new UserWithoutAuthorizationAboutThisDreamException();
            }
        }
    }


}
