package pt.ulisboa.tecnico.meic.cnv;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
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

    static class BestThread extends Thread {
        private HttpExchange httpExchange;
        private TreeMap<String, String> parameterMap;

        BestThread(HttpExchange httpExchange) {
            this.httpExchange = httpExchange;
            parameterMap = new TreeMap<>();
        }

        @Override
        public void run() {
            String query = httpExchange.getRequestURI().getQuery();
            String[] args = query.split("&");
            for (String arg : args) {
                String[] parameters = arg.split("=");
                if (parameters.length == 2) {
                    parameterMap.put(parameters[0], parameters[1]);
                }
            }

            String outputFilename = parameterMap.get("f").replace(".txt", ".bmp");

            String[] argsToRaytrace = new String[]{parameterMap.get("f"),
                    outputFilename,
                    parameterMap.get("sc"),
                    parameterMap.get("sr"),
                    parameterMap.get("wc"),
                    parameterMap.get("wr"),
                    parameterMap.get("coff"),
                    parameterMap.get("roff")};
            try {
                raytracer.Main.main(argsToRaytrace);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            File file = new File(outputFilename);
            try {
                httpExchange.sendResponseHeaders(200, file.length());
                writeResponse(httpExchange, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void writeResponse(HttpExchange t, File file) throws IOException {
            OutputStream os = t.getResponseBody();
            Files.copy(file.toPath(), os);
            //os.write(response.getBytes());
            os.close();
        }
    }

}
