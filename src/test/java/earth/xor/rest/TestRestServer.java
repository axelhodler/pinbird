package earth.xor.rest;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.After;
import org.junit.Before;
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

    private DBCollection col;

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

    @Before
    public void setUp() {
        this.col = client.getDB(DbProperties.DB_NAME).getCollection(
                DbProperties.COL_NAME);
    }

    @Test
    public void testAccessingBookmarksHTTPoptions() {
        expect().header("Access-Control-Allow-Origin", equalTo("*")).when()
                .options(RestRoutes.BOOKMARKS);
        expect().header("Access-Control-Allow-Headers",
                equalTo("Origin, X-Requested-With, Content-Type, Accept"))
                .when().options(RestRoutes.BOOKMARKS);
    }

    @Test
    public void testAddingABookmark() throws UnknownHostException, IOException {

        given().body(TestValues.POST_BOOKMARK_1).expect()
                .contentType(JSON.toString()).and()
                .header("Access-Control-Allow-Origin", equalTo("*")).when()
                .post(RestRoutes.BOOKMARKS);

        DBObject dbo = findTheDocumentAddedViaPost();

        assertEquals("foo", dbo.get(DbProperties.TITLE).toString());
    }

    @Test
    public void testGettingABookmarkViaId() {

        col.insert(TestValues.BOOKMARK_1);

        DBObject addedDoc = col.findOne(TestValues.BOOKMARK_1);

        String id = addedDoc.get(DbProperties.ID).toString();

        String jsonResponse = expect().contentType(JSON.toString()).and()
                .header("Access-Control-Allow-Origin", equalTo("*")).when()
                .get(RestRoutes.BOOKMARK + "/" + id).asString();

        JSONObject jso = (JSONObject) JSONValue.parse(jsonResponse);

        JSONObject bookmark = (JSONObject) jso.get("bookmark");

        assertEquals(id, bookmark.get(DbProperties.ID).toString());
        assertEquals("foo", bookmark.get(DbProperties.TITLE).toString());
        assertEquals("http://www.foo.org", bookmark.get(DbProperties.URL)
                .toString());
    }

    @Test
    public void testGettingAllBookmarks() {

        col.insert(TestValues.BOOKMARK_1);
        col.insert(TestValues.BOOKMARK_2);
        col.insert(TestValues.BOOKMARK_3);

        String jsonResponse = expect().contentType(JSON.toString()).and()
                .header("Access-Control-Allow-Origin", equalTo("*")).when()
                .get(RestRoutes.BOOKMARKS).asString();

        System.out.println(jsonResponse);
        JSONObject jo = (JSONObject) JSONValue.parse(jsonResponse);

        JSONArray ja = (JSONArray) jo.get("bookmarks");

        JSONObject firstObject = (JSONObject) ja.get(0);

        assertEquals(3, ja.size());
        assertEquals("foo", firstObject.get(DbProperties.TITLE));
    }

    @After
    public void dropDatabase() {
        col.drop();
    }
}
