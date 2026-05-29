package tech.inovasoft.inevolving.ms.motivation.unit;

import org.junit.jupiter.api.Test;
import tech.inovasoft.inevolving.ms.motivation.service.client.tasks_service.TasksServiceConstants;

import static org.assertj.core.api.Assertions.assertThat;

class TasksServiceConstantsTest {

    @Test
    void deveExporHeaderDeTimezoneConformeContratoDoTasksService() {
        assertThat(TasksServiceConstants.USER_TIMEZONE_HEADER).isEqualTo("X-User-Timezone");
    }

    @Test
    void deveExporTimezonePadraoAmericaSaoPaulo() {
        assertThat(TasksServiceConstants.DEFAULT_TIMEZONE).isEqualTo("America/Sao_Paulo");
    }
}
