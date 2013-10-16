package earth.xor.rest;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import earth.xor.db.DbProperties;
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
    public static void setUpMongoAndServer() throws UnknownHostException,
            IOException {
        startEmbeddedAndClient();

        rs = new RestServer(client);
        rs.start();

        RestAssured.port = 4567;
    }

    @Test
    public void testAddingABookmark() throws UnknownHostException, IOException {

        given().body(TestValues.POST_BOOKMARK_1).expect()
                .contentType(JSON.toString()).when().post(RestRoutes.BOOKMARK);

        DBObject dbo = findTheDocumentAddedViaPost();

        assertEquals("foo", dbo.get(DbProperties.TITLE).toString());
    }

    @Test
    public void testGettingABookmarkViaId() {

        BasicDBObject dbo = new BasicDBObject(DbProperties.TITLE, "foo")
                .append(DbProperties.URL, "http://www.foo.org");
        DBCollection col = client.getDB(DbProperties.DB_NAME).getCollection(
                DbProperties.COL_NAME);

        col.insert(dbo);

        DBObject addedDoc = col.findOne(dbo);

        String id = addedDoc.get(DbProperties.ID).toString();

        String jsonResponse = expect().contentType(JSON.toString()).when()
                .get(RestRoutes.BOOKMARK + "/" + id).asString();

        JSONObject jso = (JSONObject) JSONValue.parse(jsonResponse);

        assertEquals("foo", jso.get(DbProperties.TITLE).toString());
    }
}
