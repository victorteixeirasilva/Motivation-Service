package tech.inovasoft.inevolving.ms.motivation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;

import java.util.List;
import java.util.UUID;

@Repository
public interface DreamsRepository extends JpaRepository<Dreams, UUID> {

    @Query("SELECT d FROM Dreams d WHERE d.idUser = :userId")
    List<Dreams> findAllByUserId(@Param("userId") UUID userId);

}
