package tech.inovasoft.inevolving.ms.motivation.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.dto.TaskViewDTO;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TaskViewDTODeserializationTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void deveDesserializarTaskViewDTOComTimestampsOffsetDateTime() throws Exception {
        String json = """
                {
                  "id": "22222222-2222-2222-2222-222222222222",
                  "nameTask": "Estudar Java",
                  "descriptionTask": "Revisar collections",
                  "status": "LATE",
                  "dateTask": "2026-05-28",
                  "idObjective": "33333333-3333-3333-3333-333333333333",
                  "idUser": "11111111-1111-1111-1111-111111111111",
                  "idParentTask": null,
                  "idOriginalTask": null,
                  "hasSubtasks": false,
                  "blockedByObjective": false,
                  "isCopy": false,
                  "cancellationReason": null,
                  "idResponsibleUser": "44444444-4444-4444-4444-444444444444",
                  "createdAt": "2026-05-29T10:15:00-03:00",
                  "inProgressAt": null,
                  "completedAt": null,
                  "cancelledAt": null
                }
                """;

        TaskViewDTO dto = objectMapper.readValue(json, TaskViewDTO.class);

        assertThat(dto.id()).isEqualTo(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        assertThat(dto.nameTask()).isEqualTo("Estudar Java");
        assertThat(dto.dateTask()).isEqualTo(LocalDate.of(2026, 5, 28));
        assertThat(dto.idResponsibleUser()).isEqualTo(UUID.fromString("44444444-4444-4444-4444-444444444444"));
        assertThat(dto.createdAt()).isEqualTo(OffsetDateTime.of(2026, 5, 29, 10, 15, 0, 0, ZoneOffset.of("-03:00")));
        assertThat(dto.inProgressAt()).isNull();
        assertThat(dto.completedAt()).isNull();
        assertThat(dto.cancelledAt()).isNull();
    }

    @Test
    void deveDesserializarRegistroLegadoComTimestampsNulos() throws Exception {
        String json = """
                {
                  "id": "55555555-5555-5555-5555-555555555555",
                  "nameTask": "Tarefa legada",
                  "descriptionTask": "Sem timestamps",
                  "status": "LATE",
                  "dateTask": "2026-05-27",
                  "idObjective": "33333333-3333-3333-3333-333333333333",
                  "idUser": "11111111-1111-1111-1111-111111111111",
                  "idParentTask": null,
                  "idOriginalTask": null,
                  "hasSubtasks": false,
                  "blockedByObjective": false,
                  "isCopy": false,
                  "cancellationReason": null,
                  "idResponsibleUser": null,
                  "createdAt": null,
                  "inProgressAt": null,
                  "completedAt": null,
                  "cancelledAt": null
                }
                """;

        TaskViewDTO dto = objectMapper.readValue(json, TaskViewDTO.class);

        assertThat(dto.createdAt()).isNull();
        assertThat(dto.inProgressAt()).isNull();
        assertThat(dto.completedAt()).isNull();
        assertThat(dto.cancelledAt()).isNull();
        assertThat(dto.idResponsibleUser()).isNull();
    }
}
