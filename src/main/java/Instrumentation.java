import BIT.highBIT.BasicBlock;
import BIT.highBIT.ClassInfo;
import BIT.highBIT.Routine;
import pt.ulisboa.tecnico.meic.cnv.WebServer;

import java.io.*;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by diogo on 11-04-2017.
 */
public class Instrumentation {

    private static final String classPath = System.getProperty("user.dir") + "/target/classes/raytracer/";

    private static PrintWriter out = null;
    private static final String METRICS_FILE = "Metrics.txt";

    private static int i_count = 0, b_count = 0, m_count = 0;

    /* main reads in all the files class files present in the input directory,
     * instruments them, and outputs them to the specified output directory.
     */
    public static void main(String argv[]) {
        System.out.println(classPath);

        File file_in = new File(classPath);
        if(!file_in.exists()) {
            System.err.println("You need to compile the classes first!");
            return;
        }

        System.out.println("Creating the metrics file!");
        File metricsFIle = new File(METRICS_FILE);
        if(metricsFIle.exists())
            metricsFIle.delete();
        try {
            metricsFIle.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        String infilenames[] = file_in.list();

        for (int i = 0; i < infilenames.length; i++) {
            String infilename = infilenames[i];
            if (infilename.endsWith(".class")) {
                System.out.println("Instrumenting " + infilenames[i] + "...");

                ClassInfo ci = new ClassInfo(classPath + System.getProperty("file.separator") + infilename);

                // loop through all the routines
                // see java.util.Enumeration for more information on Enumeration class
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
        try {
            WebServer.main(new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void printICount(String foo) {
        System.out.println(i_count + " instructions in " + b_count + " basic blocks were executed in " + m_count + " methods.");
    }


    public static synchronized void count(int incr) {
        i_count += incr;
        b_count++;
    }

    public static synchronized void mcount(int incr) {
        m_count++;
    }

    public static synchronized void writeFile(String foo){
        try {
            out = new PrintWriter(new FileWriter(METRICS_FILE, true));
            out.append(new Date().toString() + "\n");
            out.append("Instruction count: " + i_count + "\n");
            out.append("Basic blocks: " + b_count + "\n");
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        i_count = 0;
        b_count = 0;
    }


}
