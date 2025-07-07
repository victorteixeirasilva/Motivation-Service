package tech.inovasoft.inevolving.ms.motivation.service.client.gerador_de_vision_bord;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tech.inovasoft.inevolving.ms.motivation.config.FeignConfig;
import tech.inovasoft.inevolving.ms.motivation.service.client.gerador_de_vision_bord.dto.ImageUrl;
import tech.inovasoft.inevolving.ms.motivation.service.client.gerador_de_vision_bord.dto.RequestGeradorDeVisionBordDTO;

@FeignClient(
        name = "GeradorDeVisionBord",
        url = "${inevolving.uri.ms.GeradorDeVisionBord}",
        configuration = FeignConfig.class
)
public interface GeradorDeVisionBordClientService {

    /**
     * @description - Metodo para gerar o vision board
     * @param dto - DTO para gerar o vision board
     * @return - Imagem do vision board
     */
    @PostMapping
    ImageUrl gerador(@RequestBody RequestGeradorDeVisionBordDTO dto);

}