package tech.inovasoft.inevolving.ms.motivation.domain.dto.request;

import java.util.UUID;

public record UpdateDreamRequestDTO(
        String name,
        String description,
        String urlImage
) {
}
