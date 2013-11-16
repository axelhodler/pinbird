package earth.xor.server;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestServer {

    @Test
    public void testLaunchingServer() {
        GrizzlyServer server = new GrizzlyServer();
        server.startServer();
        assertTrue(server.getServer().isStarted());
    }
}
