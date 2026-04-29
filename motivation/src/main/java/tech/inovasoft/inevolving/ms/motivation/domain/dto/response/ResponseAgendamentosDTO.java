package tech.inovasoft.inevolving.ms.motivation.domain.dto.response;

import java.util.List;

public record ResponseAgendamentosDTO(
        int quantidadeAgendamentos,
        List<AgendamentoInfoDTO> agendamentos,
        int agendamentosAtivos
) {
}
