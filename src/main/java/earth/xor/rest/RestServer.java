package earth.xor.rest;

import static spark.Spark.get;
import static spark.Spark.post;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import spark.Request;
import spark.Response;
import spark.Route;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import earth.xor.Bookmark;
import earth.xor.DbProperties;
import earth.xor.db.BookmarkDatastore;

public class RestServer {

    private String usedAcceptType = "application/json";
    private MongoClient dbclient;
    private DBCollection col;

    public RestServer(MongoClient dbclient) {
        this.dbclient = dbclient;
        this.col = dbclient.getDB(DbProperties.DB_NAME).getCollection(
                DbProperties.COL_NAME);
    }

    public void start() {
        addBookmarkPOSTroute();

        get(new Route(RestRoutes.BOOKMARK + "/:id") {

            @Override
            public Object handle(Request request, Response response) {
                BookmarkDatastore ds = new BookmarkDatastore(dbclient);

                Bookmark b = ds.getBookmark(request.params(":id"));

                JSONObject obj = new JSONObject();
                obj.put(DbProperties.ID, b.getId());
                obj.put(DbProperties.TITLE, b.getTitle());
                obj.put(DbProperties.URL, b.getUrl());

                return obj.toJSONString();
            }
        });
    }

    private void addBookmarkPOSTroute() {
        post(new Route(RestRoutes.BOOKMARK, usedAcceptType) {

            @Override
            public Object handle(Request request, Response response) {
                JSONObject obj = parseRequestBodyToJson(request);

                BasicDBObject dbo = jsonObjectToBasicDBObject(obj);

                col.insert(dbo);

                return obj.toJSONString();
            }
        });
    }

    private BasicDBObject jsonObjectToBasicDBObject(JSONObject obj) {
        return new BasicDBObject(DbProperties.TITLE,
                obj.get(DbProperties.TITLE)).append(DbProperties.URL,
                obj.get(DbProperties.URL));
    }

    private JSONObject parseRequestBodyToJson(Request request) {
        return (JSONObject) JSONValue.parse(request.body());
    }
}
