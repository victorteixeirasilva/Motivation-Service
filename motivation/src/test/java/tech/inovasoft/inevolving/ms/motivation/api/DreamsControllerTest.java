package tech.inovasoft.inevolving.ms.motivation.api;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tech.inovasoft.inevolving.ms.motivation.domain.dto.request.DreamRequestDTO;
import tech.inovasoft.inevolving.ms.motivation.domain.model.Dreams;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DreamsControllerTest {

    @LocalServerPort
    private int port;

    private static final UUID idUser = UUID.randomUUID();

    @Test
    public void addDream_ok() {
        DreamRequestDTO request = new DreamRequestDTO(
                "Nome do Sonho",
                "Descrição do Sonho",
                "https://inovasoft.tech/wp-content/webp-express/webp-images/uploads/2025/03/LogoTipoBorda-512x319px.png.webp",
                idUser
        );

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        ValidatableResponse response = requestSpecification
                .body(request)
                .when()
                .post("http://localhost:" + port + "/ms/motivation/dreams")
                .then();

        response.assertThat().statusCode(200).and()
                .body("id", notNullValue()).and()
                .body("name", equalTo(request.name())).and()
                .body("description", equalTo(request.description())).and()
                .body("urlImage", equalTo(request.urlImage())).and()
                .body("idUser", equalTo(idUser.toString()));
    }

    private UUID addDream(){
        DreamRequestDTO request = new DreamRequestDTO(
                "Nome do Sonho",
                "Descrição do Sonho",
                "https://inovasoft.tech/wp-content/webp-express/webp-images/uploads/2025/03/LogoTipoBorda-512x319px.png.webp",
                idUser
        );

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        ValidatableResponse response = requestSpecification
                .body(request)
                .when()
                .post("http://localhost:" + port + "/ms/motivation/dreams")
                .then();

        response.assertThat().statusCode(200).and()
                .body("id", notNullValue()).and()
                .body("name", equalTo(request.name())).and()
                .body("description", equalTo(request.description())).and()
                .body("urlImage", equalTo(request.urlImage())).and()
                .body("idUser", equalTo(idUser.toString()));

        return UUID.fromString(response.extract().body().jsonPath().get("id"));
    }

    @Test
    public void updateDream_ok() {

        UUID idDream = addDream();

        var request = new Dreams(
                idDream,
                "Novo Nome do Sonho",
                "Novo Descrição do Sonho",
                "https://inovasoft.tech/wp-content/webp-express/webp-images/uploads/2025/03/LogoTipoBorda-512x319px.png.webp",
                idUser
        );

        RequestSpecification requestSpecification = given()
                .contentType(ContentType.JSON);

        ValidatableResponse response = requestSpecification
                .body(request)
                .when()
                .patch("http://localhost:" + port + "/ms/motivation/dreams")
                .then();

        response.assertThat().statusCode(200).and()
                .body("id", equalTo(request.getId().toString())).and()
                .body("name", equalTo(request.getName())).and()
                .body("description", equalTo(request.getDescription())).and()
                .body("urlImage", equalTo(request.getUrlImage())).and()
                .body("idUser", equalTo(idUser.toString()));
    }

    @Test
    public void deleteDream_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void getDreamsByUserId_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void getDreamByID_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void generateVisionBordByUserId_ok() {
        //TODO: Desenvolver teste do End-Point
    }

}
