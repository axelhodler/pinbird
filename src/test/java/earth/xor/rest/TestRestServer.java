package earth.xor.rest;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import earth.xor.DbProperties;
import earth.xor.db.EmbeddedMongo;

public class TestRestServer {

    private static RestServer rs;
    private static MongoClient client;

    private static void startEmbeddedAndClient() throws UnknownHostException,
    IOException {
        EmbeddedMongo.startEmbeddedMongo(DbProperties.EMBEDDED_PORT);
        client = new MongoClient("localhost", DbProperties.EMBEDDED_PORT);
    }

    private DBObject findTheDocumentAddedViaPost() {
        DBObject dbo = client.getDB(DbProperties.DB_NAME)
                .getCollection(DbProperties.COL_NAME)
                .findOne(new BasicDBObject(DbProperties.TITLE, "foo"));
        return dbo;
    }

    @BeforeClass
    public static void setUpMongoAndServer() throws UnknownHostException, IOException {
        startEmbeddedAndClient();

        rs = new RestServer();
        rs.start();

        RestAssured.port = 4567;
    }

    @Test
    public void testAddingABookmark() throws UnknownHostException, IOException {

        given().body(TestJSONValues.POST_BOOKMARK_1)
                .expect().contentType(JSON.toString()).when()
                .post(RestRoutes.BOOKMARK);

        DBObject dbo = findTheDocumentAddedViaPost();

        assertEquals("foo", dbo.get(DbProperties.TITLE).toString());
    }
}
