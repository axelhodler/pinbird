package earth.xor;

import java.io.IOException;

import earth.xor.rest.GrizzlyRestServer;

public class Main {

    public static void main(String args[]) throws IOException {
        GrizzlyRestServer server = new GrizzlyRestServer();
        server.startServer();

        while(true) {
            System.in.read();
        }
    }
}
