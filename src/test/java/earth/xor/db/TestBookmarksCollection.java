package earth.xor.db;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Test;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class TestBookmarksCollection {

    @Test
    public void testAccessingTheEmbeddedMongoClient()
            throws UnknownHostException, IOException {
        EmbeddedMongo m = new EmbeddedMongo();
        m.launchEmbeddedMongo(12345);

        MongoClient c = new MongoClient("localhost", 12345);
        assertTrue(c.getDB("test") instanceof DB);
    }
}
