package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class Server {
    // Server Properties
    private final static int BUFFER_SIZE = 256;
    private AsynchronousServerSocketChannel server;
    private final HttpHandler handler;

    // Additional External classes
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Database database = new Database();

    public Server(HttpHandler handler) {
        this.handler = handler;
    }

    public void bootstrap() {
        try {
            server = AsynchronousServerSocketChannel.open();
            server.bind(new InetSocketAddress("127.0.0.1", 3000));

            while (true) {
                Future<AsynchronousSocketChannel> future = server.accept();
                handleClient(future);
            }
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleClient(Future<AsynchronousSocketChannel> future)
            throws ExecutionException, InterruptedException, IOException {
        System.out.println("new client connection");

        AsynchronousSocketChannel clientChannel = future.get();

        while (clientChannel != null && clientChannel.isOpen()) {
            String strRequest = parseRequest(clientChannel);

            HttpRequest request = new HttpRequest(strRequest);
            HttpResponse response = new HttpResponse();

            if (handler != null ) {
                try {
                    String requestPossibleBody = this.handler.handle(request, response, database);

                    if (requestHasBody(requestPossibleBody)) {
                        bodyHasType(response);
                        response.setBody(requestPossibleBody);
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    ExceptionResponseMaker.INTERNAL_ERROR(response);
                }
            } else {
                ExceptionResponseMaker.NOT_FOUND_ERROR(response);
            }

            ByteBuffer serverResponse = ByteBuffer.wrap(response.getBytes());
            clientChannel.write(serverResponse);

            clientChannel.close();
        }
    }

    // Checkers
    private boolean requestHasBody(String possibleBody) {
        return possibleBody != null && !possibleBody.isBlank();
    }

    private boolean bodyHasType(HttpResponse response) {
        if (response.getHeaders().get("Content-Type") == null) {
            response.addHeader("Content-Type", Config.TEXT_HTML);
            return false;
        }

        return true;
    }

    // Additional Server Methods
    private String parseRequest(AsynchronousSocketChannel clientChannel) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            StringBuilder builder = new StringBuilder();
            boolean keepReading = true;

            while (keepReading) {
                int readResult = clientChannel.read(buffer).get();

                keepReading = readResult == BUFFER_SIZE;
                buffer.flip();
                CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer);
                builder.append(charBuffer);

                buffer.clear();
            }

            return builder.toString();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void updateJson(String body) throws IOException {
        BufferedWriter writer = new BufferedWriter(
                new FileWriter("src/main/java/database.json"));
        writer.write(body);
        writer.close();
    }
}
