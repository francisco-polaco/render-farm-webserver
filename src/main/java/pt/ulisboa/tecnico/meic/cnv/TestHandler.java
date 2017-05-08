package pt.ulisboa.tecnico.meic.cnv;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class TestHandler implements HttpHandler{

    @Override
    public void handle(final HttpExchange t) throws IOException {
        new Thread(){
            @Override
            public void run() {
                String response = "Page OK!";
                try {
                    t.sendResponseHeaders(200, response.length());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try (OutputStream os = t.getResponseBody()){
                    os.write(response.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
