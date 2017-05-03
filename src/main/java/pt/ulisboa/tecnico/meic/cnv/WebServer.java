package pt.ulisboa.tecnico.meic.cnv;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class WebServer {

    static final ArrayList<String> suffixesInUse = new ArrayList<>();
    private static int PORT = 8000;

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("p", true, "Port to listen for remote requests");
        CommandLine cmd = (new DefaultParser()).parse(options, args);

        String port = cmd.getOptionValue("p");
        if (port != null) PORT = Integer.valueOf(port);

        new Thread() {
            @Override
            public void run() {
                try {
                    WebServerOriginal.main(new String[]{String.valueOf(PORT)});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

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

    public static String getHostname() {
        String hostname = "";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.err.println("Hostname can not be resolved!");
            e.printStackTrace();
        }
        return hostname;
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            WebServerThread webServerThread = new WebServerThread(t);
            webServerThread.setName(t.getRequestURI().getQuery());
            webServerThread.start();
        }
    }
}
