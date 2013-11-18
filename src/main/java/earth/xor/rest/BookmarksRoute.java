package earth.xor.rest;

import java.net.UnknownHostException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import earth.xor.Bookmark;
import earth.xor.db.BookmarkDatastore;

@Path("bookmarks")
public class BookmarksRoute {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getAllBookmarks() throws UnknownHostException {
        MongoClientURI uri = new MongoClientURI(System.getenv("URI_BASE") + System.getenv("MONGO_PORT"));
        MongoClient client = new MongoClient(uri);

        BookmarkDatastore ds = new BookmarkDatastore(client);
        List<Bookmark> allBookmarks = ds.getAllBookmarks();

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        for (Bookmark bm : allBookmarks) {
            JsonObject currentBookmark = Json.createObjectBuilder()
                    .add("title", bm.getTitle())
                    .add("url", "http://www.foo.org")
                    .build();
            arrayBuilder.add(currentBookmark);
        }

        JsonObject returnObject = Json.createObjectBuilder()
                .add("bookmarks", arrayBuilder.build()).build();

        return returnObject;
    }
}
