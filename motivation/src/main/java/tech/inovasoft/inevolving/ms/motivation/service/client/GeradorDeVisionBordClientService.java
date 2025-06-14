package tech.inovasoft.inevolving.ms.motivation.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tech.inovasoft.inevolving.ms.motivation.service.client.dto.ImageUrl;
import tech.inovasoft.inevolving.ms.motivation.service.client.dto.RequestGeradorDeVisionBordDTO;

import java.util.List;

@FeignClient(name = "GeradorDeVisionBord", url = "http://0.0.0.0:5000/generate-vision-board")
public interface GeradorDeVisionBordClientService {

    /**
     * @description - Metodo para gerar o vision board
     * @param dto - DTO para gerar o vision board
     * @return - Imagem do vision board
     */
    @PostMapping
    ImageUrl gerador(@RequestBody RequestGeradorDeVisionBordDTO dto);

}
