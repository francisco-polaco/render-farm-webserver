package pt.ulisboa.tecnico.meic.cnv;

import com.sun.net.httpserver.HttpExchange;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
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
        parseRequest();
        String outputFilename = parameterMap.get("f").replace(".txt", ".bmp");
        try {
            File f = new File(outputFilename);
            if (f.exists()) {
                System.out.println("File exists!");
                BufferedImage bimg = ImageIO.read(f);
                if (canBeResized(bimg)) {
                    System.out.println("I have a bigger file in cache, let's resize it.");
                    int type = bimg.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : bimg.getType();
                    BufferedImage bufferedImage =
                            resizeImage(bimg, type, Integer.valueOf(parameterMap.get("sc")), Integer.valueOf(parameterMap.get("sr")));
                    String newFilename = parameterMap.get("sc") + parameterMap.get("sr") + outputFilename;
                    ImageIO.write(bufferedImage, "bmp", new File(newFilename));
                    dispatch(new File(newFilename));
                    return;
                } else if (hasSameSize(bimg)) {
                    System.out.println("I've found the file in cache.");
                    dispatch(f);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            e.printStackTrace();
        }
        dispatch(new File(outputFilename));
    }

    private boolean hasSameSize(BufferedImage bimg) {
        return bimg.getWidth() == Integer.valueOf(parameterMap.get("sc")) &&
                bimg.getHeight() == Integer.valueOf(parameterMap.get("sr"));
    }

    private boolean canBeResized(BufferedImage bimg) {
        return (bimg.getWidth() > Integer.valueOf(parameterMap.get("sc")) &&
                bimg.getHeight() > Integer.valueOf(parameterMap.get("sr")) ||
                bimg.getWidth() > Integer.valueOf(parameterMap.get("wc")) &&
                bimg.getHeight() > Integer.valueOf(parameterMap.get("wr")))  &&
                bimg.getWidth() / bimg.getHeight() ==
                        Integer.valueOf(parameterMap.get("sc")) / Integer.valueOf(parameterMap.get("sr"));
    }

    private void dispatch(File file) {
        try {
            httpExchange.sendResponseHeaders(200, file.length());
            writeResponse(httpExchange, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseRequest() {
        String query = httpExchange.getRequestURI().getQuery();
        String[] args = query.split("&");
        for (String arg : args) {
            String[] parameters = arg.split("=");
            if (parameters.length == 2) {
                parameterMap.put(parameters[0], parameters[1]);
            }
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int type, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    private void writeResponse(HttpExchange t, File file) throws IOException {
        OutputStream os = t.getResponseBody();
        Files.copy(file.toPath(), os);
        //os.write(response.getBytes());
        os.close();
    }
}