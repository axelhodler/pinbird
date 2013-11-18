package earth.xor.rest;

import static org.junit.Assert.*;

import javax.json.JsonObject;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

public class TestBookmarksRest extends JerseyTest{
 
    @Override
    protected Application configure() {
        return new ResourceConfig(BookmarksRoute.class);
    }
 
    @Test
    public void test() {
        final JsonObject jo = target("bookmarks").request().get(JsonObject.class);
        assertEquals("Agamemnon", jo.getJsonString("name").getString());
    }
}
