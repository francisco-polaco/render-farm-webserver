package pt.ulisboa.tecnico.meic.cnv;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class WebServer {

    private static final int PORT = 8000;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/r.html", new MyHandler());
        server.setExecutor(null); // creates a default executor
        bindEnterToStop();
        System.out.println("Web Server is running on port " + PORT);
        System.out.println("Press <Enter> to stop.");
        server.start();
    }

    private static void bindEnterToStop() {
        new Thread() {
            @Override
            public void run() {
                new Scanner(System.in).nextLine();
                System.exit(0);
            }
        }.start();
    }


    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            BestThread bestThread = new BestThread(t);
            bestThread.start();
        }
    }
}
