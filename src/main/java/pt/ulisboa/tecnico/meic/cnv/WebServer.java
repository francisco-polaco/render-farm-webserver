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
import java.util.Scanner;

public class WebServer {

    private static int PORT = 8000;

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("p", true, "Port to listen for remote requests");
        CommandLine cmd = (new DefaultParser()).parse(options, args);

        String port = cmd.getOptionValue("p");
        System.out.println(port);
        if(port != null) PORT = Integer.valueOf(port);

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
            bestThread.setName(t.getRequestURI().getQuery());
            bestThread.start();
        }
    }

    public static String getHostname() {
        String hostname = "";
        try
        {
            hostname = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
            System.err.println("Hostname can not be resolved!");
            e.printStackTrace();
        }
        return hostname;
    }
}
