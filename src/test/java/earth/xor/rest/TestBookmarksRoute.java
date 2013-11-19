package earth.xor.rest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import earth.xor.Bookmark;
import earth.xor.db.BookmarkDatastore;
import earth.xor.db.DbProperties;
import earth.xor.db.EmbeddedMongo;

public class TestBookmarksRoute extends JerseyTest {

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
    }

    @Test
    public void testGettingAllBookmarks() {
        Bookmark bm = new Bookmark();
        bm.setTitle("foo");
        bm.setUrl("http://www.foo.org");

        BookmarkDatastore ds = new BookmarkDatastore(client);
        ds.saveBookmark(bm);

        JsonObject jo = target("bookmarks").request().get(JsonObject.class);
        JsonArray ja = jo.getJsonArray("bookmarks");

        assertEquals("foo", ja.getJsonObject(0).getJsonString("title")
                .getString());
        assertEquals("http://www.foo.org",
                ja.getJsonObject(0).getJsonString("url").getString());
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

        BookmarkDatastore ds = new BookmarkDatastore(client);
        ds.saveBookmark(bm1);
        ds.saveBookmark(bm2);
        ds.saveBookmark(bm3);

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

    @After
    public void dropCollection() {
        col.drop();
    }
}
