package earth.xor.rest;

import java.net.UnknownHostException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import earth.xor.Bookmark;
import earth.xor.db.BookmarkDatastore;

@Path("bookmarks")
public class BookmarksRoute {

    private BookmarkDatastore ds;

    public BookmarksRoute() throws UnknownHostException {
        MongoClientURI uri = new MongoClientURI(System.getenv("URI_BASE")
                + System.getenv("MONGO_PORT"));
        MongoClient client = new MongoClient(uri);

        ds = new BookmarkDatastore(client);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAllBookmarks() {
        List<Bookmark> allBookmarks = ds.getAllBookmarks();

        JsonArrayBuilder arrayBuilder = iterateAllBookmarksAndAddToArray(allBookmarks);

        JsonObject returnObject = Json.createObjectBuilder()
                .add("bookmarks", arrayBuilder.build()).build();

        return returnObject;
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getBookmarkById(@PathParam("id") String id) {
        Bookmark bm = ds.getBookmarkById(id);
        JsonObject inner = Json.createObjectBuilder()
                .add("title", bm.getTitle()).add("url", bm.getUrl()).build();
        JsonObject outer = Json.createObjectBuilder().add("bookmark", inner)
                .build();
        return outer;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void postABookmark(JsonObject requestObject) {
        Bookmark bmToSave = new Bookmark();
        JsonObject inner = requestObject.getJsonObject("bookmark");

        bmToSave.setTitle(inner.getJsonString("title").getString());
        bmToSave.setUrl(inner.getJsonString("url").getString());

        ds.saveBookmark(bmToSave);
    }

    private JsonArrayBuilder iterateAllBookmarksAndAddToArray(
            List<Bookmark> allBookmarks) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        for (Bookmark bm : allBookmarks) {
            JsonObject currentBookmark = Json.createObjectBuilder()
                    .add("title", bm.getTitle()).add("url", bm.getUrl())
                    .build();
            arrayBuilder.add(currentBookmark);
        }
        return arrayBuilder;
    }
}
