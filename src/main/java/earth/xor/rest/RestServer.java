package earth.xor.rest;

import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.post;
import static spark.Spark.delete;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import spark.Request;
import spark.Response;
import spark.Route;

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
        addOptionsToBookmarks();
        addBookmarkPOSTroute();
        addBookmarkGETbyIdRoute();
        addDeleteBookmarkByIdRoute();
        addGETAllBookmarks();
    }

    private void addOptionsToBookmarks() {
        options(new Route(RestRoutes.BOOKMARKS, usedAcceptType) {

            @Override
            public Object handle(Request request, Response response) {
                dealWithSameOriginPolicy(response);
                response.header("Access-Control-Allow-Headers",
                        "Origin, X-Requested-With, Content-Type, Accept");
                return "";
            }
        });
    }

    private void addGETAllBookmarks() {
        get(new Route(RestRoutes.BOOKMARKS, usedAcceptType) {

            @Override
            public Object handle(Request request, Response response) {
                dealWithSameOriginPolicy(response);

                return putAllBookmarksArrayIntoJSONObject().toJSONString();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private JSONObject putAllBookmarksArrayIntoJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put(RestRoutes.BOOKMARKS.substring(1),
                addBookmarksToJSONArray(ds.getAllBookmarks()));
        return jo;
    }

    private void addBookmarkGETbyIdRoute() {
        get(new Route(RestRoutes.BOOKMARK + "/" + RestRoutes.ID_PARAM,
                usedAcceptType) {
            @Override
            public Object handle(Request request, Response response) {
                dealWithSameOriginPolicy(response);

                return putRetrievedBookmarkIntoJSONObject(request).toJSONString();
            }
        });
    }

    private void addDeleteBookmarkByIdRoute() {
        delete(new Route(RestRoutes.BOOKMARK + "/" + RestRoutes.ID_PARAM, usedAcceptType) {
            @Override
            public Object handle(Request request, Response response) {
                dealWithSameOriginPolicy(response);
                ds.deleteBookmarkById(request.params(RestRoutes.ID_PARAM));
                return "deleted";
            }
        });
    }

    @SuppressWarnings("unchecked")
    private JSONObject putRetrievedBookmarkIntoJSONObject(
            Request request) {
        JSONObject jo = new JSONObject();

        jo.put(RestRoutes.BOOKMARK.substring(1),
                bookmarkToJSONObject(ds.getBookmarkById(request
                        .params(RestRoutes.ID_PARAM))));
        return jo;
    }

    private void addBookmarkPOSTroute() {
        post(new Route(RestRoutes.BOOKMARKS, usedAcceptType) {

            @Override
            public Object handle(Request request, Response response) {
                dealWithSameOriginPolicy(response);
                JSONObject main = parseRequestBodyToJson(request);

                JSONObject inner = (JSONObject) main.get("bookmark");

                ds.saveBookmark(jsonObjectToBookmark(inner));

                return main.toJSONString();
            }
        });
    }

    private Bookmark jsonObjectToBookmark(JSONObject inner) {
        Bookmark b = new Bookmark();
        b.setTitle(inner.get(DbProperties.TITLE).toString());
        b.setUrl(inner.get(DbProperties.URL).toString());
        return b;
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

    private JSONArray addBookmarksToJSONArray(List<Bookmark> allBookmarks) {
        JSONArray ja = new JSONArray();

        iterateBookmarksAndAddToJSONArray(allBookmarks, ja);
        return ja;
    }

    @SuppressWarnings("unchecked")
    private void iterateBookmarksAndAddToJSONArray(List<Bookmark> allBookmarks,
            JSONArray ja) {
        for (Bookmark b : allBookmarks)
            ja.add(bookmarkToJSONObject(b));
    }

    private void dealWithSameOriginPolicy(Response response) {
        response.header("Access-Control-Allow-Origin", "*");
    }
}
