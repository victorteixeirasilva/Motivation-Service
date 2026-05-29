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
import tech.inovasoft.inevolving.ms.motivation.service.client.email_service.EmailClientService;
import tech.inovasoft.inevolving.ms.motivation.service.client.email_service.dto.EmailRequest;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.TasksClientService;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.TasksServiceConstants;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.dto.TaskViewDTO;
import tech.inovasoft.inevolving.ms.motivation.unit.support.TasksTestFixtures;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tech.inovasoft.inevolving.ms.motivation.service.client.Auth_For_MService.MicroServices.EMAIL_SERVICE;
import static tech.inovasoft.inevolving.ms.motivation.service.client.Auth_For_MService.MicroServices.GATEWAY_SERVICE;
import static tech.inovasoft.inevolving.ms.motivation.service.client.Auth_For_MService.MicroServices.TASKS_SERVICE;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MotivationServiceLateTasksTest {

    private static final String GATEWAY_TOKEN = "gateway-token";
    private static final String TASKS_TOKEN = "tasks-token";
    private static final String EMAIL_TOKEN = "email-token";

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private TasksClientService tasksClientService;

    @Mock
    private EmailClientService emailClientService;

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
        when(tokenCache.getToken(EMAIL_SERVICE)).thenReturn(EMAIL_TOKEN);
    }

    @Test
    void sendEmailForUsersWithLateTasks_quandoHaTarefasAtrasadas_deveEnviarEmailComTimezonePadrao() {
        // Given
        TaskViewDTO lateTask = TasksTestFixtures.lateTask();
        when(apiClientService.getUsersIsVerifiedAndActive(GATEWAY_TOKEN))
                .thenReturn(ResponseEntity.ok(List.of(user)));
        when(tasksClientService.getTasksLate(user.id(), TASKS_TOKEN, TasksServiceConstants.DEFAULT_TIMEZONE))
                .thenReturn(ResponseEntity.ok(List.of(lateTask)));
        when(emailClientService.sendEmail(any(EmailRequest.class), eq(EMAIL_TOKEN)))
                .thenReturn(ResponseEntity.ok("sent"));

        // When
        MessageResponseDTO response = motivationService.sendEmailForUsersWithLateTasks();

        // Then
        assertThat(response.message()).isEqualTo("Emails enviado com sucesso");

        ArgumentCaptor<EmailRequest> emailCaptor = ArgumentCaptor.forClass(EmailRequest.class);
        verify(emailClientService).sendEmail(emailCaptor.capture(), eq(EMAIL_TOKEN));
        assertThat(emailCaptor.getValue().to()).isEqualTo(user.email());
        assertThat(emailCaptor.getValue().subject()).isEqualTo("Tarefas atrasadas");
        assertThat(emailCaptor.getValue().body()).contains(lateTask.nameTask());
        assertThat(emailCaptor.getValue().body()).contains(lateTask.dateTask().toString());
        verify(tasksClientService).getTasksLate(user.id(), TASKS_TOKEN, TasksServiceConstants.DEFAULT_TIMEZONE);
    }

    @Test
    void sendEmailForUsersWithLateTasks_quandoListaDeTarefasVazia_deveEnviarEmailComZeroTarefas() {
        // Given
        when(apiClientService.getUsersIsVerifiedAndActive(GATEWAY_TOKEN))
                .thenReturn(ResponseEntity.ok(List.of(user)));
        when(tasksClientService.getTasksLate(user.id(), TASKS_TOKEN, TasksServiceConstants.DEFAULT_TIMEZONE))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));
        when(emailClientService.sendEmail(any(EmailRequest.class), eq(EMAIL_TOKEN)))
                .thenReturn(ResponseEntity.ok("sent"));

        // When
        MessageResponseDTO response = motivationService.sendEmailForUsersWithLateTasks();

        // Then
        assertThat(response.message()).isEqualTo("Emails enviado com sucesso");

        ArgumentCaptor<EmailRequest> emailCaptor = ArgumentCaptor.forClass(EmailRequest.class);
        verify(emailClientService).sendEmail(emailCaptor.capture(), eq(EMAIL_TOKEN));
        assertThat(emailCaptor.getValue().body()).contains("0 tarefa(s) atrasada(s)");
    }

    @Test
    void sendEmailForUsersWithLateTasks_quandoConsultaDeTarefasFalha_deveEnviarEmailDeParabens() {
        // Given
        when(apiClientService.getUsersIsVerifiedAndActive(GATEWAY_TOKEN))
                .thenReturn(ResponseEntity.ok(List.of(user)));
        when(tasksClientService.getTasksLate(user.id(), TASKS_TOKEN, TasksServiceConstants.DEFAULT_TIMEZONE))
                .thenThrow(new RuntimeException("tasks-service indisponível"));
        when(emailClientService.sendEmail(any(EmailRequest.class), eq(EMAIL_TOKEN)))
                .thenReturn(ResponseEntity.ok("sent"));

        // When
        MessageResponseDTO response = motivationService.sendEmailForUsersWithLateTasks();

        // Then
        assertThat(response.message()).isEqualTo("Emails enviado com sucesso");

        ArgumentCaptor<EmailRequest> emailCaptor = ArgumentCaptor.forClass(EmailRequest.class);
        verify(emailClientService).sendEmail(emailCaptor.capture(), eq(EMAIL_TOKEN));
        assertThat(emailCaptor.getValue().body()).contains("você não tem nenhuma tarefa, atrasada");
    }

    @Test
    void sendEmailForUsersWithLateTasks_quandoRegistroLegadoSemTimestamps_deveMontarEmailNormalmente() {
        // Given
        TaskViewDTO legacyTask = TasksTestFixtures.legacyLateTaskWithoutTimestamps();
        when(apiClientService.getUsersIsVerifiedAndActive(GATEWAY_TOKEN))
                .thenReturn(ResponseEntity.ok(List.of(user)));
        when(tasksClientService.getTasksLate(user.id(), TASKS_TOKEN, TasksServiceConstants.DEFAULT_TIMEZONE))
                .thenReturn(ResponseEntity.ok(List.of(legacyTask)));
        when(emailClientService.sendEmail(any(EmailRequest.class), eq(EMAIL_TOKEN)))
                .thenReturn(ResponseEntity.ok("sent"));

        // When
        MessageResponseDTO response = motivationService.sendEmailForUsersWithLateTasks();

        // Then
        assertThat(response.message()).isEqualTo("Emails enviado com sucesso");

        ArgumentCaptor<EmailRequest> emailCaptor = ArgumentCaptor.forClass(EmailRequest.class);
        verify(emailClientService).sendEmail(emailCaptor.capture(), eq(EMAIL_TOKEN));
        assertThat(emailCaptor.getValue().body()).contains(legacyTask.nameTask());
        assertThat(emailCaptor.getValue().body()).contains(legacyTask.dateTask().toString());
    }

    @Test
    void sendEmailForUsersWithLateTasks_quandoApiDeUsuariosFalha_deveRetornarErro() {
        // Given
        when(apiClientService.getUsersIsVerifiedAndActive(GATEWAY_TOKEN))
                .thenThrow(new RuntimeException("timeout"));

        // When
        MessageResponseDTO response = motivationService.sendEmailForUsersWithLateTasks();

        // Then
        assertThat(response.message()).isEqualTo("Erro em getUsersIsVerifiedAndActive");
    }

    @Test
    void sendEmailForUsersWithLateTasks_quandoApiRetornaRespostaInvalida_deveRetornarErroDeEnvio() {
        // Given
        when(apiClientService.getUsersIsVerifiedAndActive(GATEWAY_TOKEN))
                .thenReturn(ResponseEntity.badRequest().build());

        // When
        MessageResponseDTO response = motivationService.sendEmailForUsersWithLateTasks();

        // Then
        assertThat(response.message()).isEqualTo("Erro ao enviar emails");
    }

    @Test
    void sendEmailForUsersWithLateTasks_quandoEnvioDeEmailFalha_deveLancarRuntimeException() {
        // Given
        when(apiClientService.getUsersIsVerifiedAndActive(GATEWAY_TOKEN))
                .thenReturn(ResponseEntity.ok(List.of(user)));
        when(tasksClientService.getTasksLate(user.id(), TASKS_TOKEN, TasksServiceConstants.DEFAULT_TIMEZONE))
                .thenReturn(ResponseEntity.ok(List.of(TasksTestFixtures.lateTask())));
        when(emailClientService.sendEmail(any(EmailRequest.class), eq(EMAIL_TOKEN)))
                .thenThrow(new RuntimeException("smtp error"));

        // When / Then
        assertThatThrownBy(() -> motivationService.sendEmailForUsersWithLateTasks())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("smtp error");
    }

    @Test
    void sendEmailForUsersWithLateTasks_quandoGatewayRetorna401_deveRenovarTokenERetentar() {
        // Given
        FeignException.Unauthorized unauthorized = TasksTestFixtures.unauthorizedFeignException();
        when(apiClientService.getUsersIsVerifiedAndActive(GATEWAY_TOKEN))
                .thenThrow(unauthorized)
                .thenReturn(ResponseEntity.ok(List.of(user)));
        when(tasksClientService.getTasksLate(user.id(), TASKS_TOKEN, TasksServiceConstants.DEFAULT_TIMEZONE))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));
        when(emailClientService.sendEmail(any(EmailRequest.class), eq(EMAIL_TOKEN)))
                .thenReturn(ResponseEntity.ok("sent"));

        // When
        MessageResponseDTO response = motivationService.sendEmailForUsersWithLateTasks();

        // Then
        assertThat(response.message()).isEqualTo("Emails enviado com sucesso");
        verify(apiClientService, times(2)).getUsersIsVerifiedAndActive(GATEWAY_TOKEN);
        verify(tokenCache, times(2)).getToken(GATEWAY_SERVICE);
    }

    @Test
    void sendEmailForUsersWithLateTasks_quandoTasksRetorna401_deveRenovarTokenERetentar() {
        // Given
        FeignException.Unauthorized unauthorized = TasksTestFixtures.unauthorizedFeignException();
        when(apiClientService.getUsersIsVerifiedAndActive(GATEWAY_TOKEN))
                .thenReturn(ResponseEntity.ok(List.of(user)));
        when(tasksClientService.getTasksLate(user.id(), TASKS_TOKEN, TasksServiceConstants.DEFAULT_TIMEZONE))
                .thenThrow(unauthorized)
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));
        when(emailClientService.sendEmail(any(EmailRequest.class), eq(EMAIL_TOKEN)))
                .thenReturn(ResponseEntity.ok("sent"));

        // When
        MessageResponseDTO response = motivationService.sendEmailForUsersWithLateTasks();

        // Then
        assertThat(response.message()).isEqualTo("Emails enviado com sucesso");
        verify(tasksClientService, times(2)).getTasksLate(
                user.id(),
                TASKS_TOKEN,
                TasksServiceConstants.DEFAULT_TIMEZONE
        );
        verify(tokenCache, times(2)).getToken(TASKS_SERVICE);
    }

    @Test
    void sendEmailForUsersWithLateTasks_quandoEmailRetorna401_deveRenovarTokenERetentar() {
        // Given
        FeignException.Unauthorized unauthorized = TasksTestFixtures.unauthorizedFeignException();
        when(apiClientService.getUsersIsVerifiedAndActive(GATEWAY_TOKEN))
                .thenReturn(ResponseEntity.ok(List.of(user)));
        when(tasksClientService.getTasksLate(user.id(), TASKS_TOKEN, TasksServiceConstants.DEFAULT_TIMEZONE))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));
        when(emailClientService.sendEmail(any(EmailRequest.class), eq(EMAIL_TOKEN)))
                .thenThrow(unauthorized)
                .thenReturn(ResponseEntity.ok("sent"));

        // When
        MessageResponseDTO response = motivationService.sendEmailForUsersWithLateTasks();

        // Then
        assertThat(response.message()).isEqualTo("Emails enviado com sucesso");
        verify(emailClientService, times(2)).sendEmail(any(EmailRequest.class), eq(EMAIL_TOKEN));
        verify(tokenCache, times(2)).getToken(EMAIL_SERVICE);
    }
}
