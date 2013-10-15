package earth.xor.db;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import earth.xor.Bookmark;
import earth.xor.DbProperties;

public class TestBookmarksCollection {

    private MongoClient client;

    @BeforeClass
    public static void before() throws UnknownHostException, IOException {
        EmbeddedMongo.startEmbeddedMongo(DbProperties.EMBEDDED_PORT);
    }

    @Before
    public void setUpTests() throws UnknownHostException {
        this.client = new MongoClient("localhost", DbProperties.EMBEDDED_PORT);
    }

    private Bookmark createExampleBookmark() {
        Bookmark b = new Bookmark();
        b.setTitle("foo");
        b.setUrl("http://www.foo.org");
        return b;
    }

    private DBCollection getBookmarksCollection() {
        return client.getDB(DbProperties.DB_NAME).getCollection(
                DbProperties.COL_NAME);
    }

    private void dropBookmarksCollection() {
        getBookmarksCollection()
                .drop();
    }

    @Test
    public void testSaveAndGetBookmarkById() {
        Bookmark b = createExampleBookmark();
        BookmarkDatastore ds = new BookmarkDatastore(client);
        ds.saveBookmark(b);

        DBCollection col = getBookmarksCollection();
        DBObject dbo = col.findOne(new BasicDBObject(DbProperties.TITLE, "foo"));
        String id = dbo.get(DbProperties.ID).toString();

        assertEquals(id, ds.getBookmark(id).getId());
    }

    @After
    public void cleanUp() {
        dropBookmarksCollection();
    }

    @AfterClass
    public static void after() {
        EmbeddedMongo.stopEmbeddedMongo();
    }
}
