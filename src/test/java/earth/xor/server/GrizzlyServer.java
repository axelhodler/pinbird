package earth.xor.server;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class GrizzlyServer {

    public static GrizzlyServer instance = null;
    private HttpServer server;

    public void startServer() {
        final ResourceConfig rc = new ResourceConfig()
                .packages("earth.xor.server");

        server = GrizzlyHttpServerFactory.createHttpServer(
                UriBuilder.fromUri("http://0.0.0.0/")
                        .port(Integer.valueOf(System.getenv("PORT"))).build(),
                rc);
    }

    public HttpServer getServer() {
        return server;
    }
}
