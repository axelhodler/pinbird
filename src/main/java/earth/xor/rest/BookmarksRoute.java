package earth.xor.rest;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("bookmarks")
public class BookmarksRoute {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getId() {
        JsonObject myObject = Json.createObjectBuilder()
                .add("name", "Agamemnon")
                .add("age", 32)
                .build();
        return myObject;
    }
}
