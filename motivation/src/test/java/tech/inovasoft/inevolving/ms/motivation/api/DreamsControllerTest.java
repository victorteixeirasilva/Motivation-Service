package tech.inovasoft.inevolving.ms.motivation.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DreamsControllerTest {

    @LocalServerPort
    private int port;

    @Test
    public void addDream_ok() {
        //TODO: Desenvolver teste do End-Point
    }

    @Test
    public void updateDream_ok() {
        //TODO: Desenvolver teste do End-Point
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
