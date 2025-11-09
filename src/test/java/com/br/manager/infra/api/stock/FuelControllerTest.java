package com.br.manager.infra.api.stock;

import com.br.manager.domain.stock.dto.FuelInputDTO;
import com.br.manager.domain.stock.dto.FuelResponseDTO;
import com.br.manager.domain.stock.repository.FuelRepository;
import com.br.manager.domain.stock.service.FuelService;
import com.br.manager.infra.api.stock.factory.FuelInputDTOFactory;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FuelControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private FuelService service;

    @Autowired
    private FuelInputDTOFactory fuelInputDTOFactory;

    @Autowired
    private FuelRepository fuelRepository;

    @Autowired
    private FuelService fuelService;

    @BeforeEach
    public void beforeEach(){
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @AfterEach
    public void afterAll(){
        fuelRepository.deleteAll();
    }

    @Test
    void shouldCreateFuelWithSuccess(){
        given()
           .contentType(ContentType.JSON)
           .body(fuelInputDTOFactory.getFuelInputDTOFactory())
        .when()
           .post("/fuel")
        .then()
           .log().all()
           .statusCode(200)
        .body(containsString("Nome combustivel"))
        .body(containsString("Litros"));
    }

    @Test
    void shouldReturnExceptionCreateFuel(){
        FuelInputDTO dto = fuelInputDTOFactory.getFuelInputDTOFactory();
        dto.setName(null);
        dto.setUnit(null);

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/fuel")
                .then()
                .log().all()
                .statusCode(400)
                .body(containsString("O nome do combustível é obrigatório"))
                .body(containsString("A unidade de medida é obrigatório"));

    }

    @Test
    void shouldReturnListWithFuel(){
        for (int i = 0; i < 2; i++) {
           fuelService.save(fuelInputDTOFactory.getFuelInputDTOFactory());
        }

        TypeRef<List<FuelResponseDTO>> typeRef = new TypeRef<List<FuelResponseDTO>>() {};

        List<FuelResponseDTO> fuels = RestAssured.given()
                .contentType(ContentType.JSON)
        .when()
                .get("/fuel")
        .then()
                .log().all()
                .statusCode(200)
                .extract()
                .as(typeRef);

        Assertions.assertEquals(2, fuels.size());
    }

    @Test
    void shouldReturnOneFuel(){
        FuelResponseDTO responseDTO = fuelService.save(fuelInputDTOFactory.getFuelInputDTOFactory());

        TypeRef<FuelResponseDTO> typeRef = new TypeRef<FuelResponseDTO>() {};

        FuelResponseDTO fuel = RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/fuel/" + responseDTO.getId())
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .as(typeRef);

        Assertions.assertNotNull(fuel);
    }

    @Test
    void shouldDeleteFuel(){
        FuelResponseDTO responseDTO = fuelService.save(fuelInputDTOFactory.getFuelInputDTOFactory());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/fuel/" + responseDTO.getId())
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    void shouldReturnFuelEditSuccess(){
        TypeRef<FuelResponseDTO> typeRef = new TypeRef<FuelResponseDTO>() {};

        FuelInputDTO dto = fuelInputDTOFactory.getFuelInputDTOFactory();
        dto.setName("Menphis Depay");

        FuelResponseDTO fuel = given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .put("/fuel")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .as(typeRef);

        Assertions.assertEquals("Menphis Depay", fuel.getName());
    }

}