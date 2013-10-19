package earth.xor.rest;

import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.post;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import spark.Request;
import spark.Response;
import spark.Route;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

import earth.xor.Bookmark;
import earth.xor.db.BookmarkDatastore;
import earth.xor.db.DbProperties;

public class RestServer {

    private String usedAcceptType = "application/json";
    private BookmarkDatastore ds;

    public RestServer(MongoClient dbclient) {
        this.ds = new BookmarkDatastore(dbclient);
    }

    public void setUp() {
        createTheRestOperationRoutes();
    }

    private void createTheRestOperationRoutes() {
        addOPTIONStoRoot();
        addBookmarkPOSTroute();
        addBookmarkGETbyIdRoute();
        addGETAllBookmarks();
    }

    private void addOPTIONStoRoot() {
        options(new Route(RestRoutes.BOOKMARKS, usedAcceptType) {

            @Override
            public Object handle(Request request, Response response) {
                addAccessControlAllowOriginToHeader(response);
                response.header("Access-Control-Allow-Headers",
                        "Origin, X-Requested-With, Content-Type, Accept");
                return "root";
            }
        });
    }

    private void addGETAllBookmarks() {
        get(new Route(RestRoutes.BOOKMARKS, usedAcceptType) {

            @SuppressWarnings("unchecked")
            @Override
            public Object handle(Request request, Response response) {
                addAccessControlAllowOriginToHeader(response);
                List<Bookmark> allBookmarks = ds.getAllBookmarks();

                JSONArray ja = addBookmarksToJSONArray(allBookmarks);

                JSONObject jo = new JSONObject();
                jo.put(RestRoutes.BOOKMARKS.substring(1), ja);
                return jo.toJSONString();
            }
        });
    }

    private void addBookmarkGETbyIdRoute() {
        get(new Route(RestRoutes.BOOKMARK + "/" + RestRoutes.ID_PARAM,
                usedAcceptType) {

            @SuppressWarnings("unchecked")
            @Override
            public Object handle(Request request, Response response) {

                addAccessControlAllowOriginToHeader(response);
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
        post(new Route(RestRoutes.BOOKMARKS, usedAcceptType) {

            @Override
            public Object handle(Request request, Response response) {
                addAccessControlAllowOriginToHeader(response);
                JSONObject obj = parseRequestBodyToJson(request);

                JSONObject inner = (JSONObject) obj.get("bookmark");

                Bookmark b = new Bookmark();
                b.setTitle(inner.get(DbProperties.TITLE).toString());
                b.setUrl(inner.get(DbProperties.URL).toString());
                ds.saveBookmark(b);

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

    @SuppressWarnings("unchecked")
    private JSONArray addBookmarksToJSONArray(List<Bookmark> allBookmarks) {
        JSONArray ja = new JSONArray();

        for (Bookmark b : allBookmarks) {
            ja.add(bookmarkToJSONObject(b));
        }
        return ja;
    }

    private void addAccessControlAllowOriginToHeader(Response response) {
        response.header("Access-Control-Allow-Origin", "*");
    }
}
