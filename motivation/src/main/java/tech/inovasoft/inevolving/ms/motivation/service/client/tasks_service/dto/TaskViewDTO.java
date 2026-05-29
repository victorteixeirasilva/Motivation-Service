package tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TaskViewDTO(
        UUID id,
        String nameTask,
        String descriptionTask,
        String status,
        LocalDate dateTask,
        UUID idObjective,
        UUID idUser,
        UUID idParentTask,
        UUID idOriginalTask,
        Boolean hasSubtasks,
        Boolean blockedByObjective,
        Boolean isCopy,
        String cancellationReason,
        UUID idResponsibleUser,
        OffsetDateTime createdAt,
        OffsetDateTime inProgressAt,
        OffsetDateTime completedAt,
        OffsetDateTime cancelledAt
) {
}
