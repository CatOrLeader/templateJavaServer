package server;

import java.io.IOException;

interface HttpHandler {
    String handle(HttpRequest request, HttpResponse response, Database database) throws IOException;
}
