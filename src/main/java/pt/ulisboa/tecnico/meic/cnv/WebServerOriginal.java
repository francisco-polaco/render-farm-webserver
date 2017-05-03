package pt.ulisboa.tecnico.meic.cnv;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class WebServerOriginal {

    public static void main(String[] args) throws Exception {
        if(args.length != 1) {
            System.err.println("Ups!");
            return;
        }
        HttpServer server = HttpServer.create(new InetSocketAddress(Integer.valueOf(args[0])), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "Page OK!";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}
