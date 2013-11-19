package earth.xor.rest;

import java.net.UnknownHostException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import earth.xor.Bookmark;
import earth.xor.db.BookmarkDatastore;

@Path("bookmarks")
public class BookmarksRoute {

    private BookmarkDatastore ds;

    public BookmarksRoute() throws UnknownHostException {
        MongoClientURI uri = new MongoClientURI(System.getenv("MONGO_URI"));
        MongoClient client = new MongoClient(uri);

        ds = new BookmarkDatastore(client);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBookmarks() {
        List<Bookmark> allBookmarks = ds.getAllBookmarks();

        JsonArrayBuilder arrayBuilder = iterateAllBookmarksAndAddToArray(allBookmarks);

        JsonObject returnObject = Json.createObjectBuilder()
                .add("bookmarks", arrayBuilder.build()).build();

        return Response.ok(returnObject)
                .header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookmarkById(@PathParam("id") String id) {
        Bookmark bm = null;
        try {
            bm = ds.getBookmarkById(id);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("The id: " + id + " is an invalid ObjectId")
                    .header("Access-Control-Allow-Origin", "*").build();
        }

        if (bm == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Resource not found for bookmarkid: " + id)
                    .header("Access-Control-Allow-Origin", "*").build();
        }
        JsonObject inner = Json.createObjectBuilder()
                .add("title", bm.getTitle()).add("url", bm.getUrl()).build();
        JsonObject outer = Json.createObjectBuilder().add("bookmark", inner)
                .build();
        return Response.ok(outer).header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postABookmark(JsonObject requestObject) {
        Bookmark bmToSave = new Bookmark();
        JsonObject inner = requestObject.getJsonObject("bookmark");

        bmToSave.setTitle(inner.getJsonString("title").getString());
        bmToSave.setUrl(inner.getJsonString("url").getString());

        ds.saveBookmark(bmToSave);
        return Response.ok().header("Access-Control-Allow-Origin", "*").build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteBookmarkById(@PathParam("id") String id) {
        ds.deleteBookmarkById(id);
        return Response.ok().header("Access-Control-Allow-Origin", "*").build();
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

    @OPTIONS
    public Response optionsBookmarks() {
        return Response
                .ok()
                .header("Access-Control-Allow-Headers",
                        "Origin, X-Requested-With, Content-Type, Accept")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE")
                .build();
    }
}
