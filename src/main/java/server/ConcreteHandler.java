package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ConcreteHandler implements HttpHandler{

    // Additional helpful classes
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String handle(HttpRequest request, HttpResponse response, Database database) {
        PossibleRequests flag = defineRequest(request);

//        switch (flag) {
//            default -> {
//                return null;
//            }
//        }

        // Gag
        return "<html><body>" +
                "<h1>Hello!</h1>" +
                "<div>" +
                "<p>This is test server, for now there nothing to show to you!</p>" +
                "<p>You should update ConcreteHandler Class and make fake Database</p>" +
                "</div>" +
                "</body></html>";
    }

    private PossibleRequests defineRequest(HttpRequest request) {
        // Receive request, parse it and use if-else branching to choose
        // how to process this request

        return null;
    }

    // Additional helpful methods
    private void updateJson(String body) throws IOException {
        BufferedWriter writer = new BufferedWriter(
                new FileWriter("src/main/java/database.json"));
        writer.write(body);
        writer.close();
    }
}

