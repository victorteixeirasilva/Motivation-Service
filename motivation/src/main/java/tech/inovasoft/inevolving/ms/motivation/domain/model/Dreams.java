package tech.inovasoft.inevolving.ms.motivation.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "dreams")
public class Dreams {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;
    private String urlImage;
    private UUID idUser;

    public Dreams(DreamRequestDTO dto) {
        this.name = dto.name();
        this.description = dto.description();
        this.urlImage = dto.urlImage();
        this.idUser = dto.idUser();
    }

}
