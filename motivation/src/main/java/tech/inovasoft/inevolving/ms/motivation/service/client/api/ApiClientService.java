package tech.inovasoft.inevolving.ms.motivation.service.client.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tech.inovasoft.inevolving.ms.motivation.service.client.api.dto.UserEmailDTO;
import tech.inovasoft.inevolving.ms.motivation.service.client.gerador_de_vision_bord.dto.ImageUrl;
import tech.inovasoft.inevolving.ms.motivation.service.client.gerador_de_vision_bord.dto.RequestGeradorDeVisionBordDTO;

import java.util.List;

@FeignClient(name = "api-service", url = "http://localhost:8090/api/user")
public interface ApiClientService {

    @GetMapping("/verified/active")
    ResponseEntity<List<UserEmailDTO>> getUsersIsVerifiedAndActive();

    @GetMapping("/disconnected")
    ResponseEntity<List<UserEmailDTO>> getUsersDisconnectedAndActive();

}
