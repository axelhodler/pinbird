package earth.xor.rest;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class GrizzlyRestServer {

    private HttpServer server;

    public void startServer() {
        final ResourceConfig rc = new ResourceConfig(BookmarksRoute.class);

        server = GrizzlyHttpServerFactory.createHttpServer(
                UriBuilder.fromUri("http://0.0.0.0/")
                        .port(Integer.valueOf(System.getenv("PORT"))).build(),
                rc);
    }

    public HttpServer getServer() {
        return server;
    }
}
