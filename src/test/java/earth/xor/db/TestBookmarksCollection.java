package earth.xor.db;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import earth.xor.Bookmark;
import earth.xor.DbProperties;

public class TestBookmarksCollection {

    private MongoClient client;

    @Before
    public void setUpTests() throws UnknownHostException {
        this.client = new MongoClient("localhost", DbProperties.EMBEDDED_PORT);
    }

    private void launchEmbeddedMongo() throws UnknownHostException, IOException {
        EmbeddedMongo m = new EmbeddedMongo();
        m.launchEmbeddedMongo(DbProperties.EMBEDDED_PORT);
    }

    private DBObject createAndFindDocument() {
        DB db = client.getDB(DbProperties.DB_NAME);
        DBCollection col = db.getCollection(DbProperties.DB_NAME);
        col.insert(new BasicDBObject("foo", "bar"));
        DBObject dbo = col.findOne(new BasicDBObject("foo", "bar"));
        return dbo;
    }

    private Bookmark createExampleBookmark() {
        Bookmark b = new Bookmark();
        b.setTitle("foo");
        b.setUrl("http://www.foo.org");
        return b;
    }

    @Test
    public void testAccessingTheEmbeddedMongoClient()
            throws UnknownHostException, IOException {
        launchEmbeddedMongo();

        DBObject dbo = createAndFindDocument();

        assertEquals("bar", dbo.get("foo"));
    }

    @Test
    public void testSavingAndAccessingBookmark() {
        Bookmark b = createExampleBookmark();

        BookmarkDatastore ds = new BookmarkDatastore(client);
        ds.saveBookmark(b);
        assertEquals("foo", ds.getBookmark(b).getTitle());
    }
}
