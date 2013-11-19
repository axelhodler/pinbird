package earth.xor.server;

import static org.junit.Assert.*;

import org.junit.Test;

import earth.xor.rest.GrizzlyRestServer;

public class TestServer {

    @Test
    public void testLaunchingServer() {
        GrizzlyRestServer server = new GrizzlyRestServer();
        server.startServer();
        assertTrue(server.getServer().isStarted());
    }
}
