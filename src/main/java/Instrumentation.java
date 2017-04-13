import BIT.highBIT.*;
import BIT.lowBIT.Attribute_Info;
import BIT.lowBIT.Cp_Info;
import BIT.lowBIT.Field_Info;
import pt.ulisboa.tecnico.meic.cnv.BestThread;
import pt.ulisboa.tecnico.meic.cnv.WebServer;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by diogo on 11-04-2017.
 */
public class Instrumentation {


    private static final String classPath = System.getProperty("user.dir") + "/target/classes/raytracer/";
    private static final String METRICS_FILE = "Metrics.txt";
    private static BigInteger i_count = BigInteger.ZERO, b_count = BigInteger.ZERO, m_count = BigInteger.ZERO;

    public static void main(String argv[]) {

        File file_in = new File(classPath);
        if(!file_in.exists()) {
            System.err.println("You need to compile the classes first!");
            return;
        }

        createMetricsFile();

        instrumentRayTracer(file_in.list());

        try {
            WebServer.main(new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error calling the webserver!");
        }
    }

    private static void instrumentRayTracer(String[] infilenames) {
        for (int i = 0; i < infilenames.length; i++) {
            String infilename = infilenames[i];
            if (infilename.endsWith(".class")) {
                System.out.println("Instrumenting " + infilenames[i] + "...");

                ClassInfo ci = new ClassInfo(classPath + System.getProperty("file.separator") + infilename);

                for (Enumeration e = ci.getRoutines().elements(); e.hasMoreElements(); ) {

                    Routine routine = (Routine) e.nextElement();

                    routine.addBefore(Instrumentation.class.getCanonicalName(), "mcount", new Integer(1));

                    for (Enumeration b = routine.getBasicBlocks().elements(); b.hasMoreElements(); ) {
                        BasicBlock bb = (BasicBlock) b.nextElement();
                        bb.addBefore(Instrumentation.class.getCanonicalName(), "count", new Integer(bb.size()));
                    }
                }
                ci.addAfter(Instrumentation.class.getCanonicalName(), "printICount", ci.getClassName());
                ci.addAfter(Instrumentation.class.getCanonicalName(), "writeFile", infilename.substring(0, infilename.indexOf(".class")));
                ci.write(classPath + System.getProperty("file.separator") + infilename);
            }
        }
    }

    public static synchronized void printICount(String foo) {
        System.out.println(i_count + " instructions in " + b_count + " basic blocks were executed in " + m_count + " methods.");
    }

    public static synchronized void count(int incr) {
        i_count = i_count.add(BigInteger.valueOf(incr));
        b_count = b_count.add(BigInteger.ONE);
    }

    public static synchronized void mcount(int incr) {
        m_count = m_count.add(BigInteger.ONE);
    }

    public static synchronized void writeFile(String foo){
        try {
            PrintWriter out = new PrintWriter(new FileWriter(METRICS_FILE, true));
            out.append(new Date().toString() + "\n");
            out.append("Instruction count: " + i_count + "\n");
            out.append("Basic blocks: " + b_count + "\n");
            out.append("Methods: " + m_count + "\n");
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        i_count = b_count = m_count = BigInteger.ZERO;
    }


    // aux functions

    private static void createMetricsFile() {
        System.out.println("Creating the metrics file!");
        File metricsFIle = new File(METRICS_FILE);
        if(metricsFIle.exists())
            metricsFIle.delete();
        try {
            metricsFIle.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
