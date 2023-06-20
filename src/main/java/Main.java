import server.ConcreteHandler;
import server.Server;

public class Main {
    public static void main(String[] args) {
        new Server(
                (new ConcreteHandler())
        ).bootstrap();
    }

}
