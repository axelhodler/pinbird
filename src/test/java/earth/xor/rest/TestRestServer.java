package earth.xor.rest;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import earth.xor.DbProperties;
import earth.xor.db.EmbeddedMongo;

public class TestRestServer {

    @Test
    public void testCreatingRestServer() {
        RestServer rs = new RestServer();
        rs.start();

        RestAssured.port = 4567;
        expect().get("/hello").body().asString().contains("hello world");
    }

    @Test
    public void testAddingABookmark() throws UnknownHostException, IOException {
        RestServer rs = new RestServer();
        rs.start();

        EmbeddedMongo.startEmbeddedMongo(12345);
        RestAssured.port = 4567;
        given().body("{\"title\":\"foo\", \"url\":\"http://www.foo.org\"}")
                .expect().contentType("application/json").when()
                .post("/bookmark");

        MongoClient client = new MongoClient("localhost", 12345);
        DBObject dbo = client.getDB(DbProperties.DB_NAME)
                .getCollection(DbProperties.COL_NAME)
                .findOne(new BasicDBObject("title", "foo"));

        assertEquals("foo", dbo.get("title").toString());
    }
}
