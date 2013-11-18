package earth.xor.rest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

public class TestBookmarksRest extends JerseyTest{
 
    @Override
    protected Application configure() {
        return new ResourceConfig(Bookmarks.class);
    }
 
    @Test
    public void test() {
        final String hello = target("bookmarks").request().get(String.class);
        assertEquals("Got it", hello);
    }
}
