package tech.inovasoft.inevolving.ms.motivation.unit.support;

import feign.FeignException;
import feign.Request;
import tech.inovasoft.inevolving.ms.motivation.service.client.api.dto.UserEmailDTO;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.dto.TaskViewDTO;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.UUID;

public final class TasksTestFixtures {

    private TasksTestFixtures() {
    }

    public static UserEmailDTO activeUser() {
        return new UserEmailDTO(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "user@example.com",
                true,
                true
        );
    }

    public static TaskViewDTO lateTask() {
        return new TaskViewDTO(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "Estudar Java",
                "Revisar collections",
                "LATE",
                LocalDate.of(2026, 5, 28),
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                null,
                null,
                false,
                false,
                false,
                null,
                UUID.fromString("44444444-4444-4444-4444-444444444444"),
                OffsetDateTime.of(2026, 5, 29, 10, 15, 0, 0, ZoneOffset.of("-03:00")),
                null,
                null,
                null
        );
    }

    public static TaskViewDTO legacyLateTaskWithoutTimestamps() {
        return new TaskViewDTO(
                UUID.fromString("55555555-5555-5555-5555-555555555555"),
                "Tarefa legada",
                "Sem timestamps",
                "LATE",
                LocalDate.of(2026, 5, 27),
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                null,
                null,
                false,
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static FeignException.Unauthorized unauthorizedFeignException() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/late",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                null
        );
        return new FeignException.Unauthorized("Unauthorized", request, null, null);
    }
}
