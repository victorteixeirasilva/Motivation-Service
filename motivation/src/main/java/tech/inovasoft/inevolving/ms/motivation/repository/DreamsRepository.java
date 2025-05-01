package tech.inovasoft.inevolving.ms.motivation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;

import java.util.UUID;

@Repository
public interface DreamsRepository extends JpaRepository<Dreams, UUID> {
}
