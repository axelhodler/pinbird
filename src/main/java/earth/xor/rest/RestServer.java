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
import earth.xor.db.BookmarkDatastore;
import earth.xor.db.DbProperties;

public class RestServer {

    private String usedAcceptType = "application/json";
    private DBCollection col;
    private BookmarkDatastore ds;

    public RestServer(MongoClient dbclient) {
        this.col = dbclient.getDB(DbProperties.DB_NAME).getCollection(
                DbProperties.COL_NAME);
        this.ds = new BookmarkDatastore(dbclient);
    }

    public void start() {
        addBookmarkPOSTroute();
        addBookmarkGETbyIdRoute();
    }

    private void addBookmarkGETbyIdRoute() {
        get(new Route(RestRoutes.BOOKMARK + "/" + RestRoutes.ID_PARAM) {

            @Override
            public Object handle(Request request, Response response) {

                Bookmark b = ds
                        .getBookmark(request.params(RestRoutes.ID_PARAM));

                JSONObject obj = bookmarkToJSONObject(b);

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

    @SuppressWarnings("unchecked")
    private JSONObject bookmarkToJSONObject(Bookmark b) {
        JSONObject obj = new JSONObject();

        obj.put(DbProperties.ID, b.getId());
        obj.put(DbProperties.TITLE, b.getTitle());
        obj.put(DbProperties.URL, b.getUrl());
        return obj;
    }
}
