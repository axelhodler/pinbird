package earth.xor.rest;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.UnknownHostException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import earth.xor.db.BookmarkDatastore;
import earth.xor.db.DbProperties;
import earth.xor.db.EmbeddedMongo;

public class TestRestServer {

    private static RestServer rs;
    private static MongoClient client;
    private DBCollection col;

    private static void startEmbeddedAndClient() throws UnknownHostException,
            IOException {
        EmbeddedMongo.startEmbeddedMongo(Integer.valueOf(System
                .getenv("MONGO_PORT")));
        MongoClientURI uri = new MongoClientURI(System.getenv("URI_BASE")
                + System.getenv("MONGO_PORT"));
        client = new MongoClient(uri);
    }

    private DBObject findTheDocumentAddedViaPost() {
        DBObject dbo = client.getDB(DbProperties.DB_NAME)
                .getCollection(DbProperties.COL_NAME)
                .findOne(new BasicDBObject(DbProperties.TITLE, "foo"));
        return dbo;
    }

    private void checkIfSameOriginPolicyAllowed(String route) {
        expect().header(HttpHeaders.ACAOrigin, equalTo("*")).when()
        .options(route);
    }

    private void insertThreeBookmarks() {
        col.insert(TestValues.BOOKMARK_1);
        col.insert(TestValues.BOOKMARK_2);
        col.insert(TestValues.BOOKMARK_3);
    }

    private JSONObject createBookmarkFromResponse(String jsonResponse) {
        JSONObject jsonObj = (JSONObject) JSONValue.parse(jsonResponse);
        
        JSONObject bookmark = (JSONObject) extractTheBookmarkObject(jsonObj);
        return bookmark;
    }
    
    private Object extractTheBookmarkObject(JSONObject jsonObj) {
        return jsonObj.get(RestRoutes.BOOKMARK.substring(1));
    }

    private JSONArray extractTheArrayOfBookmarks(JSONObject jsonObj) {
        return (JSONArray) jsonObj.get(RestRoutes.BOOKMARKS.substring(1));
    }

    private String getIdOfSavedBookmark() {
        col.insert(TestValues.BOOKMARK_1);
        DBObject addedDoc = col.findOne(TestValues.BOOKMARK_1);
        String idOfJustAddedDoc = addedDoc.get(DbProperties.ID).toString();
        return idOfJustAddedDoc;
    }

    @BeforeClass
    public static void setUpMongoAndServer() throws UnknownHostException,
            IOException {
        startEmbeddedAndClient();

        rs = new RestServer(client);
        rs.setUp();

//        RestAssured.port = 4567;
    }

    @Before
    public void setUpTheCollection() {
        this.col = client.getDB(DbProperties.DB_NAME).getCollection(
                DbProperties.COL_NAME);
    }

    @Ignore
    @Test
    public void testBookmarksOPTIONS() {
        checkIfSameOriginPolicyAllowed(RestRoutes.BOOKMARKS);
        expect().header(HttpHeaders.ACAHeaders,
                equalTo("Origin, X-Requested-With, Content-Type, Accept"))
                .when().options(RestRoutes.BOOKMARKS);
    }

    @Ignore
    @Test
    public void testBookmarksOPTIONSwithIdInUrl() {
        String idOfJustAddedDoc = getIdOfSavedBookmark();

        String route = RestRoutes.BOOKMARKS + "/" + idOfJustAddedDoc;
        checkIfSameOriginPolicyAllowed(route);

        expect().header(HttpHeaders.ACAMethods, equalTo("DELETE")).when()
        .options(route);
    }

    @Ignore
    @Test
    public void testBookmarksPOST() throws UnknownHostException, IOException {
        given().body(TestValues.POST_BOOKMARK_1).expect()
                .contentType(JSON.toString()).and()
                .header(HttpHeaders.ACAOrigin, equalTo("*")).when()
                .post(RestRoutes.BOOKMARKS);

        DBObject dbo = findTheDocumentAddedViaPost();

        assertEquals("foo", dbo.get(DbProperties.TITLE).toString());
    }

    @Ignore
    @Test
    public void testGettingABookmarkViaId() {
        String idOfJustAddedDoc = getIdOfSavedBookmark();

        String jsonResponse = expect().contentType(JSON.toString()).and()
                .header(HttpHeaders.ACAOrigin, equalTo("*")).when()
                .get(RestRoutes.BOOKMARK + "/" + idOfJustAddedDoc).asString();

        JSONObject bookmark = createBookmarkFromResponse(jsonResponse);

        assertEquals(idOfJustAddedDoc, bookmark.get(DbProperties.ID).toString());
        assertEquals("foo", bookmark.get(DbProperties.TITLE).toString());
        assertEquals("http://www.foo.org", bookmark.get(DbProperties.URL)
                .toString());
    }

    @Ignore
    @Test
    public void testDeletingABookMarkViaId() {
        String idOfJustAddedDoc = getIdOfSavedBookmark();

        expect().contentType(JSON.toString()).and()
                .header(HttpHeaders.ACAOrigin, equalTo("*")).when()
                .delete(RestRoutes.BOOKMARKS + "/" + idOfJustAddedDoc);
        try {
            BookmarkDatastore ds = new BookmarkDatastore(client);
            ds.getBookmarkById(idOfJustAddedDoc);
            fail("This Bookmark should have been deleted");
        } catch (NullPointerException e) {}
    }

    @Ignore
    @Test
    public void testGettingAllBookmarks() {
        insertThreeBookmarks();

        String jsonResponse = expect().contentType(JSON.toString()).and()
                .header(HttpHeaders.ACAOrigin, equalTo("*")).when()
                .get(RestRoutes.BOOKMARKS).asString();

        JSONObject jsonObj = (JSONObject) JSONValue.parse(jsonResponse);
        JSONArray jsonArr = extractTheArrayOfBookmarks(jsonObj);

        JSONObject firstObject = (JSONObject) jsonArr.get(0);

        assertEquals(3, jsonArr.size());
        assertEquals("foo", firstObject.get(DbProperties.TITLE));
    }

    @After
    public void dropCollection() {
        col.drop();
    }
}
