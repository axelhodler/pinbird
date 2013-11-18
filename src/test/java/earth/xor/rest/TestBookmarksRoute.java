package earth.xor.rest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.json.JsonObject;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import earth.xor.db.DbProperties;
import earth.xor.db.EmbeddedMongo;

public class TestBookmarksRoute extends JerseyTest{

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
    public void test() {
        final JsonObject jo = target("bookmarks").request().get(JsonObject.class);
        assertEquals("Agamemnon", jo.getJsonString("name").getString());
    }
}
