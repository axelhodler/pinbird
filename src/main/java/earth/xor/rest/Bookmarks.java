package earth.xor.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("bookmarks")
public class Bookmarks {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getId() {
        return "Got it";
    }
}
