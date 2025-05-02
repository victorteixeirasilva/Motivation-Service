package tech.inovasoft.inevolving.ms.motivation.domain.dto.request;

import java.util.UUID;

public record DreamRequestDTO(
        String name,
        String description,
        String urlImage,
        UUID idUser
) {
}
