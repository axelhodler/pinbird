package earth.xor.rest;

import static spark.Spark.get;
import static spark.Spark.post;

import java.net.UnknownHostException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import spark.Request;
import spark.Response;
import spark.Route;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

import earth.xor.DbProperties;

public class RestServer {

    public void start() {

        post(new Route("/bookmark", "application/json") {

            @Override
            public Object handle(Request request, Response response) {
                JSONObject obj = (JSONObject) JSONValue.parse(request.body());

                MongoClient client = null;
                try {
                    client = new MongoClient("localhost", 12345);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                BasicDBObject dbo = new BasicDBObject(DbProperties.TITLE,
                        obj.get(DbProperties.TITLE)).append(DbProperties.URL,
                        obj.get(DbProperties.URL));
                client.getDB(DbProperties.DB_NAME)
                        .getCollection(DbProperties.COL_NAME).insert(dbo);
                return obj.toJSONString();
            }

        });
    }
}
