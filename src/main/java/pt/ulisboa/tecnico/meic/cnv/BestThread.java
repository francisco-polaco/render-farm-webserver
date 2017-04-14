package pt.ulisboa.tecnico.meic.cnv;

import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.TreeMap;

public class BestThread extends Thread {
    private HttpExchange httpExchange;
    private TreeMap<String, String> parameterMap;

    BestThread(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
        parameterMap = new TreeMap<>();
    }

    @Override
    public void run() {
        parseRequest(httpExchange.getRequestURI().getQuery(), parameterMap);
        String outputFilename = parameterMap.get("f").replace(".txt", ".bmp");

        String[] argsToRaytrace = new String[]{
                parameterMap.get("f"),
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
            dispatch(e.getMessage());
            e.printStackTrace();
        }
        dispatch(new File(outputFilename));
    }

    private void dispatch(File file) {
        try {
            httpExchange.sendResponseHeaders(200, file.length());
            writeResponse(httpExchange, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(String response) {
        try {
            httpExchange.sendResponseHeaders(200, response.length());
            writeResponse(httpExchange, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseRequest(String query, TreeMap<String, String> paramsMap) {
        String[] args = query.split("&");
        for (String arg : args) {
            String[] parameters = arg.split("=");
            if (parameters.length == 2) {
                paramsMap.put(parameters[0], parameters[1]);
            }
        }
    }

    private void writeResponse(HttpExchange t, File file) throws IOException {
        OutputStream os = t.getResponseBody();
        Files.copy(file.toPath(), os);
        os.close();
    }

    private void writeResponse(HttpExchange t, String response) throws IOException {
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}