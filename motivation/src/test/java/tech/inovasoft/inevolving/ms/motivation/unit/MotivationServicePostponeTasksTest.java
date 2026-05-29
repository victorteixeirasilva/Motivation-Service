package tech.inovasoft.inevolving.ms.motivation.unit;

import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.MessageResponseDTO;
import tech.inovasoft.inevolving.ms.motivation.service.MotivationService;
import tech.inovasoft.inevolving.ms.motivation.service.client.Auth_For_MService.TokenCache;
import tech.inovasoft.inevolving.ms.motivation.service.client.api.ApiClientService;
import tech.inovasoft.inevolving.ms.motivation.service.client.api.dto.UserEmailDTO;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.TasksClientService;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.TasksServiceConstants;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.dto.PostponeDayRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.unit.support.TasksTestFixtures;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tech.inovasoft.inevolving.ms.motivation.service.client.Auth_For_MService.MicroServices.GATEWAY_SERVICE;
import static tech.inovasoft.inevolving.ms.motivation.service.client.Auth_For_MService.MicroServices.TASKS_SERVICE;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MotivationServicePostponeTasksTest {

    private static final String GATEWAY_TOKEN = "gateway-token";
    private static final String TASKS_TOKEN = "tasks-token";

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private TasksClientService tasksClientService;

    @Mock
    private TokenCache tokenCache;

    @InjectMocks
    private MotivationService motivationService;

    private UserEmailDTO user;

    @BeforeEach
    void setUp() {
        user = TasksTestFixtures.activeUser();
        when(tokenCache.getToken(GATEWAY_SERVICE)).thenReturn(GATEWAY_TOKEN);
        when(tokenCache.getToken(TASKS_SERVICE)).thenReturn(TASKS_TOKEN);
    }

    @Test
    void postponeTasksForAllUsers_quandoSucesso_deveAdiarComReferenceDayNoTimezonePadrao() {
        // Given
        String expectedReferenceDay = LocalDate.now(ZoneId.of(TasksServiceConstants.DEFAULT_TIMEZONE)).toString();
        when(apiClientService.getUsersIsVerifiedAndActive(GATEWAY_TOKEN))
                .thenReturn(ResponseEntity.ok(List.of(user)));
        when(tasksClientService.postponeDay(eq(TASKS_TOKEN), org.mockito.ArgumentMatchers.any(PostponeDayRequestDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        // When
        MessageResponseDTO response = motivationService.postponeTasksForAllUsers();

        // Then
        assertThat(response.message()).isEqualTo(
                "Tarefas do dia " + expectedReferenceDay + " adiadas com sucesso para todos os usuários"
        );

        ArgumentCaptor<PostponeDayRequestDTO> dtoCaptor = ArgumentCaptor.forClass(PostponeDayRequestDTO.class);
        verify(tasksClientService).postponeDay(eq(TASKS_TOKEN), dtoCaptor.capture());
        assertThat(dtoCaptor.getValue().idUser()).isEqualTo(user.id());
        assertThat(dtoCaptor.getValue().referenceDay()).isEqualTo(expectedReferenceDay);
    }

    @Test
    void postponeTasksForAllUsers_quandoPostponeDayNaoEnviaTimezone_deveManterContratoInalterado() {
        // Given
        when(apiClientService.getUsersIsVerifiedAndActive(GATEWAY_TOKEN))
                .thenReturn(ResponseEntity.ok(List.of(user)));
        when(tasksClientService.postponeDay(eq(TASKS_TOKEN), org.mockito.ArgumentMatchers.any(PostponeDayRequestDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        // When
        motivationService.postponeTasksForAllUsers();

        // Then
        verify(tasksClientService).postponeDay(
                eq(TASKS_TOKEN),
                org.mockito.ArgumentMatchers.any(PostponeDayRequestDTO.class)
        );
    }

    @Test
    void postponeTasksForAllUsers_quandoAlgunsUsuariosFalham_deveRetornarMensagemComContagemDeErros() {
        // Given
        UserEmailDTO secondUser = new UserEmailDTO(UUID.randomUUID(), "other@example.com", true, true);
        when(apiClientService.getUsersIsVerifiedAndActive(GATEWAY_TOKEN))
                .thenReturn(ResponseEntity.ok(List.of(user, secondUser)));
        when(tasksClientService.postponeDay(eq(TASKS_TOKEN), org.mockito.ArgumentMatchers.any(PostponeDayRequestDTO.class)))
                .thenReturn(ResponseEntity.ok().build())
                .thenThrow(new RuntimeException("falha no usuário 2"));

        // When
        MessageResponseDTO response = motivationService.postponeTasksForAllUsers();

        // Then
        assertThat(response.message()).isEqualTo("Adiamento concluído com 1 erro(s)");
    }

    @Test
    void postponeTasksForAllUsers_quandoApiDeUsuariosFalha_deveRetornarErro() {
        // Given
        when(apiClientService.getUsersIsVerifiedAndActive(GATEWAY_TOKEN))
                .thenThrow(new RuntimeException("timeout"));

        // When
        MessageResponseDTO response = motivationService.postponeTasksForAllUsers();

        // Then
        assertThat(response.message()).isEqualTo("Erro em getUsersIsVerifiedAndActive: timeout");
    }

    @Test
    void postponeTasksForAllUsers_quandoApiRetornaRespostaInvalida_deveRetornarErro() {
        // Given
        when(apiClientService.getUsersIsVerifiedAndActive(GATEWAY_TOKEN))
                .thenReturn(ResponseEntity.badRequest().build());

        // When
        MessageResponseDTO response = motivationService.postponeTasksForAllUsers();

        // Then
        assertThat(response.message()).isEqualTo("Erro ao buscar usuários ativos");
    }

    @Test
    void postponeTasksForAllUsers_quandoGatewayRetorna401_deveRenovarTokenERetentar() {
        // Given
        FeignException.Unauthorized unauthorized = TasksTestFixtures.unauthorizedFeignException();
        when(apiClientService.getUsersIsVerifiedAndActive(GATEWAY_TOKEN))
                .thenThrow(unauthorized)
                .thenReturn(ResponseEntity.ok(List.of(user)));
        when(tasksClientService.postponeDay(eq(TASKS_TOKEN), org.mockito.ArgumentMatchers.any(PostponeDayRequestDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        // When
        MessageResponseDTO response = motivationService.postponeTasksForAllUsers();

        // Then
        assertThat(response.message()).contains("adiadas com sucesso");
        verify(apiClientService, times(2)).getUsersIsVerifiedAndActive(GATEWAY_TOKEN);
        verify(tokenCache, times(2)).getToken(GATEWAY_SERVICE);
    }

    @Test
    void postponeTasksForAllUsers_quandoTasksRetorna401_deveRenovarTokenERetentar() {
        // Given
        FeignException.Unauthorized unauthorized = TasksTestFixtures.unauthorizedFeignException();
        when(apiClientService.getUsersIsVerifiedAndActive(GATEWAY_TOKEN))
                .thenReturn(ResponseEntity.ok(List.of(user)));
        doThrow(unauthorized)
                .doReturn(ResponseEntity.ok().build())
                .when(tasksClientService)
                .postponeDay(eq(TASKS_TOKEN), org.mockito.ArgumentMatchers.any(PostponeDayRequestDTO.class));

        // When
        MessageResponseDTO response = motivationService.postponeTasksForAllUsers();

        // Then
        assertThat(response.message()).contains("adiadas com sucesso");
        verify(tasksClientService, times(2)).postponeDay(
                eq(TASKS_TOKEN),
                org.mockito.ArgumentMatchers.any(PostponeDayRequestDTO.class)
        );
        verify(tokenCache, times(2)).getToken(TASKS_SERVICE);
    }
}
