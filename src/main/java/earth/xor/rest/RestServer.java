package earth.xor.rest;

import static spark.Spark.get;
import spark.Request;
import spark.Response;
import spark.Route;

public class RestServer {

    public void start() {

        get(new Route("/hello") {
            @Override
            public Object handle(Request request, Response response) {
                return "hello world";
            }
        });
    }
}
