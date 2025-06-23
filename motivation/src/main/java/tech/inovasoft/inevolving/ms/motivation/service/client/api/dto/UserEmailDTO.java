package tech.inovasoft.inevolving.ms.motivation.service.client.api.dto;

import java.util.UUID;

public record UserEmailDTO(
        UUID id,
        String email,
        boolean emailVerified,
        boolean isActive
) {
}
