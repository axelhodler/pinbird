package earth.xor;

import java.net.UnknownHostException;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import earth.xor.db.ConfigAccessor;
import earth.xor.rest.RestServer;

public class Main {

    public static void main(String args[]) throws UnknownHostException {

        MongoClientURI mongoUri = new MongoClientURI(ConfigAccessor
                .getInstance().getMongoUri());
        MongoClient client = new MongoClient(mongoUri);

        RestServer rest = new RestServer(client);
        rest.setUp();
    }
}
