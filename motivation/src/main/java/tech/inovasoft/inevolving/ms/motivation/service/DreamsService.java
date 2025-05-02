package tech.inovasoft.inevolving.ms.motivation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;
import tech.inovasoft.inevolving.ms.motivation.repository.DreamsRepository;

@Service
public class DreamsService {

    @Autowired
    private DreamsRepository repository;

    public Dreams addDream(DreamRequestDTO dto) {
        Dreams newDream = new Dreams(dto);
        return repository.save(newDream);
    }
}
