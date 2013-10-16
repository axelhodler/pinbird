package earth.xor.rest;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.List;

import org.json.simple.JSONArray;
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
        addGETAllBookmarks();
    }

    private void addGETAllBookmarks() {
        get(new Route(RestRoutes.BOOKMARKS, usedAcceptType) {

            @SuppressWarnings("unchecked")
            @Override
            public Object handle(Request arg0, Response arg1) {
                List<Bookmark> allBookmarks = ds.getAllBookmarks();

                JSONArray ja = addBookmarksToJSONArray(allBookmarks);

                JSONObject jo = new JSONObject();
                jo.put(RestRoutes.BOOKMARKS.substring(1), ja);
                return jo.toJSONString();
            }
        });
    }

    private void addBookmarkGETbyIdRoute() {
        get(new Route(RestRoutes.BOOKMARK + "/" + RestRoutes.ID_PARAM, usedAcceptType) {

            @SuppressWarnings("unchecked")
            @Override
            public Object handle(Request request, Response response) {

                Bookmark b = ds
                        .getBookmark(request.params(RestRoutes.ID_PARAM));

                JSONObject outerObject = new JSONObject();
                
                JSONObject innerObject = bookmarkToJSONObject(b);
                outerObject.put(RestRoutes.BOOKMARK.substring(1), innerObject);

                return outerObject.toJSONString();
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

    private JSONArray addBookmarksToJSONArray(
            List<Bookmark> allBookmarks) {
        JSONArray ja = new JSONArray();

        for (Bookmark b : allBookmarks) {
            ja.add(bookmarkToJSONObject(b));
        }
        return ja;
    }
}
