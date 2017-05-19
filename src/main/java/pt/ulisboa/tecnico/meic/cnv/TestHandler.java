package pt.ulisboa.tecnico.meic.cnv;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class TestHandler implements HttpHandler{

    public static BigInteger workPerformed = BigInteger.ZERO;

    public static synchronized void addToWorkPerformed(BigInteger increment) {
        workPerformed = workPerformed.add(increment);
    }

    @Override
    public void handle(final HttpExchange t) throws IOException {
        new Thread(){
            @Override
            public void run() {
                String response = "Page OK!";
                String performed = "performed=" + workPerformed.toString();
                response = response + " " + performed;
                try {
                    t.sendResponseHeaders(200, response.length());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try (BufferedOutputStream os = new BufferedOutputStream(t.getResponseBody())) {
                    os.write(response.getBytes("UTF-8"));
                    workPerformed = BigInteger.ZERO;
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
