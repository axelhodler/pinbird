package earth.xor;

import java.net.UnknownHostException;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import earth.xor.rest.RestServer;

public class Main {

    public static void main(String args[]) throws UnknownHostException {

        MongoClientURI mongoUri = new MongoClientURI(System.getenv("MONGO_URI"));
        MongoClient client = new MongoClient(mongoUri);

        RestServer rest = new RestServer(client);
        rest.setUp();
    }
}
