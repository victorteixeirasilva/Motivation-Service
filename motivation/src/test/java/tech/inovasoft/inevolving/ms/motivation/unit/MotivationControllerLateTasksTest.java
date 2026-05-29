package tech.inovasoft.inevolving.ms.motivation.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tech.inovasoft.inevolving.ms.motivation.config.SchedulingConfig;
import tech.inovasoft.inevolving.ms.motivation.controller.MotivationController;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.response.MessageResponseDTO;
import tech.inovasoft.inevolving.ms.motivation.service.MotivationService;
import tech.inovasoft.inevolving.ms.motivation.service.client.Auth_For_MService.TokenService;
import tech.inovasoft.inevolving.ms.motivation.service.client.Auth_For_MService.dto.TokenValidateResponse;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MotivationControllerLateTasksTest {

    private static final String VALID_TOKEN = "valid-jwt-token";
    private static final String ENDPOINT = "/ms/motivation/tasks/late/{token}";

    @Mock
    private MotivationService motivationService;

    @Mock
    private TokenService tokenService;

    @Mock
    private SchedulingConfig schedulingConfig;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MotivationController controller = new MotivationController();
        ReflectionTestUtils.setField(controller, "service", motivationService);
        ReflectionTestUtils.setField(controller, "tokenService", tokenService);
        ReflectionTestUtils.setField(controller, "schedulingConfig", schedulingConfig);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void sendEmailForUsersWithLateTasks_quandoTokenValido_deveRetornar200ComContratoJson() throws Exception {
        // Given
        when(tokenService.validateToken(VALID_TOKEN))
                .thenReturn(new TokenValidateResponse("gateway-service", "motivation-service"));
        when(motivationService.sendEmailForUsersWithLateTasks())
                .thenReturn(new MessageResponseDTO("Emails enviado com sucesso"));

        // When / Then
        mockMvc.perform(get(ENDPOINT, VALID_TOKEN).accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result))
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.message").value("Emails enviado com sucesso")));

        verify(tokenService).validateToken(VALID_TOKEN);
        verify(motivationService).sendEmailForUsersWithLateTasks();
    }

    @Test
    void sendEmailForUsersWithLateTasks_quandoTokenInvalido_deveRetornar401SemCorpo() throws Exception {
        // Given
        when(tokenService.validateToken(VALID_TOKEN)).thenReturn(null);

        // When / Then
        mockMvc.perform(get(ENDPOINT, VALID_TOKEN))
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result))
                        .andExpect(status().isUnauthorized()));

        verify(tokenService).validateToken(VALID_TOKEN);
        verifyNoInteractions(motivationService);
    }

    @Test
    void sendEmailForUsersWithLateTasks_quandoTokenRejeitadoPeloAuthForMService_deveRetornar401() throws Exception {
        // Given
        when(tokenService.validateToken(VALID_TOKEN))
                .thenThrow(new RuntimeException("Invalid token"));

        // When / Then
        mockMvc.perform(get(ENDPOINT, VALID_TOKEN))
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result))
                        .andExpect(status().isUnauthorized()));

        verify(tokenService).validateToken(VALID_TOKEN);
        verifyNoInteractions(motivationService);
    }

    @Test
    void sendEmailForUsersWithLateTasks_quandoExcecaoGenericaNaValidacao_executaServicoSemBloquear() throws Exception {
        // Given
        when(tokenService.validateToken(VALID_TOKEN))
                .thenThrow(new RuntimeException("unexpected failure"));
        when(motivationService.sendEmailForUsersWithLateTasks())
                .thenReturn(new MessageResponseDTO("Emails enviado com sucesso"));

        // When / Then
        mockMvc.perform(get(ENDPOINT, VALID_TOKEN).accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.message").value("Emails enviado com sucesso")));

        verify(motivationService).sendEmailForUsersWithLateTasks();
    }
}
