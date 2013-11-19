package earth.xor.db;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

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

public class TestBookmarksCollection {

    private MongoClient client;
    private BookmarkDatastore ds;

    @BeforeClass
    public static void before() throws UnknownHostException, IOException {
        EmbeddedMongo.startEmbeddedMongo(DbProperties.EMBEDDED_PORT);
    }

    @Before
    public void setUpTests() throws UnknownHostException {
        this.client = new MongoClient("localhost", DbProperties.EMBEDDED_PORT);
        this.ds = new BookmarkDatastore(client);
    }

    private Bookmark createExampleBookmark() {
        Bookmark b = new Bookmark();
        b.setTitle("foo");
        b.setUrl("http://www.foo.org");
        return b;
    }

    private DBCollection getBookmarksCollection() {
        return client.getDB(System.getenv("DB_NAME")).getCollection(
                DbProperties.COL_NAME);
    }

    private void saveSomeExampleBookmarks() {
        for (int i = 0; i < 3; i++) {
            Bookmark b = createExampleBookmark();
            ds.saveBookmark(b);
        }
    }

    private void dropBookmarksCollection() {
        getBookmarksCollection()
                .drop();
    }

    private String saveBookmarkAndGetItsId() {
        Bookmark b = createExampleBookmark();
        ds.saveBookmark(b);
        
        DBCollection col = getBookmarksCollection();
        DBObject dbo = col.findOne(new BasicDBObject(DbProperties.TITLE, "foo"));
        String id = dbo.get(DbProperties.ID).toString();
        return id;
    }

    @Test
    public void testSaveAndGetBookmarkById() {
        String id = saveBookmarkAndGetItsId();

        assertEquals(id, ds.getBookmarkById(id).getId());
    }


    @Test
    public void testGettingAllBookmarks() {

        saveSomeExampleBookmarks();

        List<Bookmark> allBookmarks = ds.getAllBookmarks();
        assertEquals(3, allBookmarks.size());
    }

    @Test
    public void testDeletingABookmark() {
        String id = saveBookmarkAndGetItsId();

        ds.deleteBookmarkById(id);

        assertNull(ds.getBookmarkById(id));
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
