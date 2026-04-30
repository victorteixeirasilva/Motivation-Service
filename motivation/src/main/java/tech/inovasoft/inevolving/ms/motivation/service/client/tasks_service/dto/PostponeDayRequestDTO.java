package tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.dto;

import java.util.UUID;

public record PostponeDayRequestDTO(
        UUID idUser,
        String referenceDay
) {
}
