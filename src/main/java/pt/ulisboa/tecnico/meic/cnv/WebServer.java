package pt.ulisboa.tecnico.meic.cnv;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.TreeMap;

public class WebServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/r.html", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            BestThread bestThread = new BestThread(t);
            bestThread.start();
        }
    }

    private static void writeResponse(HttpExchange t, String response) throws IOException {
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    static class BestThread extends Thread{
        private HttpExchange httpExchange;
        private TreeMap<String, String> parameterMap;

        BestThread(HttpExchange httpExchange){
            this.httpExchange = httpExchange;
            parameterMap = new TreeMap<>();
        }

        @Override
        public void run() {
            String query = httpExchange.getRequestURI().getQuery();
            String[] args = query.split("&");
            for(String arg : args){
                String[] parameters = arg.split("=");
                if(parameters.length == 2){
                    parameterMap.put(parameters[0], parameters[1]);
                }
            }
            String response = "Query Map :\n" + parameterMap.toString()
                    + "##";
            try {
                httpExchange.sendResponseHeaders(200, response.length());
                writeResponse(httpExchange, response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
