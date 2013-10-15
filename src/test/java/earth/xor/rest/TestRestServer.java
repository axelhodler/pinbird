package earth.xor.rest;

import static com.jayway.restassured.RestAssured.*;

import org.junit.Test;

import com.jayway.restassured.RestAssured;

public class TestRestServer {

    @Test
    public void testCreatingRestServer() {
        RestServer rs = new RestServer();
        rs.start();

        RestAssured.port = 4567;
        expect().get("/hello").body().asString().contains("hello world");
    }
}
