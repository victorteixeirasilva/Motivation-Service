package tech.inovasoft.inevolving.ms.motivation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.UpdateDreamRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.MaximumNumberOfRegisteredDreamsException;
import tech.inovasoft.inevolving.ms.motivation.domain.exception.NotSavedDTOInDbException;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;
import tech.inovasoft.inevolving.ms.motivation.repository.DreamsRepository;

import java.util.List;
import java.util.UUID;

@Service
public class DreamsService {

    @Autowired
    private DreamsRepository repository;

    public Dreams addDream(DreamRequestDTO dto) {
        List<Dreams> dreams = repository.findAllByUserId(dto.idUser());
        if (dreams.size() >= 200) {
            throw new MaximumNumberOfRegisteredDreamsException();
        }
        try {
            return repository.save(new Dreams(dto));
        } catch (Exception e) {
            throw new NotSavedDTOInDbException(dto);
        }
    }

    public Dreams updateDream(UUID id, UpdateDreamRequestDTO dto) {
        return null;
    }
}
