package tech.inovasoft.inevolving.ms.motivation.integration;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.swagger.v3.oas.models.links.Link;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import tech.inovasoft.inevolving.ms.motivation.service.client.dto.RequestGeradorDeVisionBordDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GeradorDeVisionBordClientServiceTest {

    @Test
    public void integrationTest_Ok() {

        UUID idUser = UUID.randomUUID();

        List<String> images = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            images.add("https://inovasoft.tech/wp-content/webp-express/webp-images/uploads/2025/03/LogoTipoBorda-512x319px.png.webp");
        }

        var request = new RequestGeradorDeVisionBordDTO(idUser.toString(), images);

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        ValidatableResponse response = requestSpecification
                .body(request)
                .when()
                .post("http://0.0.0.0:5000/generate-vision-board")
                .then();

        response.assertThat().statusCode(200);

    }
}
