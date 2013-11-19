package earth.xor.rest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Request;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import earth.xor.Bookmark;
import earth.xor.db.BookmarkDatastore;
import earth.xor.db.DbProperties;
import earth.xor.db.EmbeddedMongo;

public class TestBookmarksRoute extends JerseyTest {

    private static MongoClient client;
    private DBCollection col;
    private BookmarkDatastore ds;

    private static void startEmbeddedAndClient() throws UnknownHostException,
            IOException {
        EmbeddedMongo.startEmbeddedMongo(Integer.valueOf(System
                .getenv("MONGO_PORT")));
        MongoClientURI uri = new MongoClientURI(System.getenv("URI_BASE")
                + System.getenv("MONGO_PORT"));
        client = new MongoClient(uri);
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(BookmarksRoute.class);
    }

    @BeforeClass
    public static void setUpMongoAndServer() throws UnknownHostException,
            IOException {
        startEmbeddedAndClient();
    }

    @Before
    public void setUpTheCollection() {
        this.col = client.getDB(DbProperties.DB_NAME).getCollection(
                DbProperties.COL_NAME);
        this.ds = new BookmarkDatastore(client);
    }

    @Test
    public void testGettingAllBookmarksWhenMultipleAdded() {
        Bookmark bm1 = new Bookmark();
        bm1.setTitle("foo");
        bm1.setUrl("http://www.foo.org");

        Bookmark bm2 = new Bookmark();
        bm2.setTitle("bar");
        bm2.setUrl("http://www.bar.org");

        Bookmark bm3 = new Bookmark();
        bm3.setTitle("baz");
        bm3.setUrl("http://www.baz.org");

        ds.saveBookmark(bm1);
        ds.saveBookmark(bm2);
        ds.saveBookmark(bm3);

        assertEquals(
                "*",
                target("bookmarks").request().get()
                        .getHeaderString("Access-Control-Allow-Origin"));

        JsonObject jo = target("bookmarks").request().get(JsonObject.class);
        JsonArray ja = jo.getJsonArray("bookmarks");

        assertEquals("foo", ja.getJsonObject(0).getJsonString("title")
                .getString());
        assertEquals("http://www.foo.org",
                ja.getJsonObject(0).getJsonString("url").getString());

        assertEquals("bar", ja.getJsonObject(1).getJsonString("title")
                .getString());
        assertEquals("http://www.bar.org",
                ja.getJsonObject(1).getJsonString("url").getString());

        assertEquals("baz", ja.getJsonObject(2).getJsonString("title")
                .getString());
        assertEquals("http://www.baz.org",
                ja.getJsonObject(2).getJsonString("url").getString());
    }

    @Test
    public void testAddingBookmarkViaPost() {
        JsonObject inner = Json.createObjectBuilder().add("title", "foo")
                .add("url", "http://www.foo.org").build();
        JsonObject outer = Json.createObjectBuilder().add("bookmark", inner)
                .build();

        assertEquals(
                "*",
                target("bookmarks").request()
                        .post(Entity.entity(outer, "application/json"))
                        .getHeaderString("Access-Control-Allow-Origin"));

        JsonObject jo = target("bookmarks").request().get(JsonObject.class);
        JsonArray ja = jo.getJsonArray("bookmarks");

        assertEquals("foo", ja.getJsonObject(0).getJsonString("title")
                .getString());
        assertEquals("http://www.foo.org",
                ja.getJsonObject(0).getJsonString("url").getString());
    }

    @Test
    public void testGettingBookmarkById() {
        createAndSaveTestBookmark();

        DBObject addedDoc = col.findOne(new BasicDBObject("title", "foo"));
        String idOfJustAddedBm = addedDoc.get(DbProperties.ID).toString();

        JsonObject jo = target("bookmarks").path("/" + idOfJustAddedBm)
                .request().get(JsonObject.class);

        JsonObject innerOb = jo.getJsonObject("bookmark");
        assertEquals("foo", innerOb.getJsonString("title").getString());
        assertEquals("http://www.foo.org", innerOb.getJsonString("url")
                .getString());
    }

    private void createAndSaveTestBookmark() {
        Bookmark bm1 = new Bookmark();
        bm1.setTitle("foo");
        bm1.setUrl("http://www.foo.org");

        ds.saveBookmark(bm1);
    }

    @Test
    public void testGettingBookmarkByInvalidId() {
        assertEquals("ObjectId does not exist", 404,
                target("bookmarks").path("/507f1f77bcf86cd799439011").request()
                        .get().getStatus());
        assertEquals("Invalid ObjectId", 400,
                target("bookmarks").path("/foobarbaz123").request().get()
                        .getStatus());
    }

    @Test
    public void testDeletingABookmarkById() {
        createAndSaveTestBookmark();

        DBObject addedDoc = col.findOne(new BasicDBObject("title", "foo"));
        String idOfJustAddedBm = addedDoc.get(DbProperties.ID).toString();

        target("bookmarks").path("/" + idOfJustAddedBm).request().delete();

        assertEquals("*",
                target("bookmarks").path("/" + idOfJustAddedBm).request()
                        .delete()
                        .getHeaderString("Access-Control-Allow-Origin"));

        assertEquals(404, target("bookmarks").path("/" + idOfJustAddedBm)
                .request().get().getStatus());
    }

    @After
    public void dropCollection() {
        col.drop();
    }
}
