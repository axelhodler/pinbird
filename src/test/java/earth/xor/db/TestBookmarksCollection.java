package earth.xor.db;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import earth.xor.DbProperties;

public class TestBookmarksCollection {

    @Test
    public void testAccessingTheEmbeddedMongoClient()
            throws UnknownHostException, IOException {
        launchEmbeddedMongo();

        DBObject dbo = createAndFindDocument();

        assertEquals("bar", dbo.get("foo"));
    }

    private DBObject createAndFindDocument() throws UnknownHostException {
        MongoClient c = new MongoClient("localhost", DbProperties.EMBEDDED_PORT);
        DB db = c.getDB(DbProperties.DB_NAME);
        DBCollection col = db.getCollection(DbProperties.DB_NAME);
        col.insert(new BasicDBObject("foo", "bar"));
        DBObject dbo = col.findOne(new BasicDBObject("foo", "bar"));
        return dbo;
    }

    private void launchEmbeddedMongo() throws UnknownHostException, IOException {
        EmbeddedMongo m = new EmbeddedMongo();
        m.launchEmbeddedMongo(DbProperties.EMBEDDED_PORT);
    }
}
