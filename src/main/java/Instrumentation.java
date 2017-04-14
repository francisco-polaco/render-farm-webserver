import BIT.highBIT.*;
import pt.ulisboa.tecnico.meic.cnv.RepositoryService;
import pt.ulisboa.tecnico.meic.cnv.WebServer;

import java.io.*;
import java.math.BigInteger;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Created by diogo on 11-04-2017.
 */

public class Instrumentation {

    static class Branch {
        public BigInteger taken = BigInteger.ZERO;
        public BigInteger not_taken = BigInteger.ZERO;
    }

    private static final String classPath = System.getProperty("user.dir") + "/target/classes/raytracer/";
    private static final String METRICS_FILE = "Metrics.txt";
    private static BigInteger m_count = BigInteger.ZERO;
    private static Hashtable branch = new Hashtable();
    private static int pc;


    public static void main(String argv[]) {

        RepositoryService rs = new RepositoryService();

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
                        Instruction instr = (Instruction)routine.getInstructions()[bb.getEndAddress()];
                        short instr_type = InstructionTable.InstructionTypeTable[instr.getOpcode()];
                        if (instr_type == InstructionTable.CONDITIONAL_INSTRUCTION) {
                            instr.addBefore("Instrumentation", "Offset", new Integer(instr.getOffset()));
                            instr.addBefore("Instrumentation", "Branch", new String("BranchOutcome"));
                        }
                    }
                }
                ci.addAfter(Instrumentation.class.getCanonicalName(), "writeFile", infilename.substring(0, infilename.indexOf(".class")));
                ci.write(classPath + System.getProperty("file.separator") + infilename);
            }
        }
    }

    public static synchronized void mcount(int incr) {
        m_count = m_count.add(BigInteger.ONE);
    }

    public static synchronized void writeFile(String foo){
        try {
            PrintWriter out = new PrintWriter(new FileWriter(METRICS_FILE, true));
            out.append(new Date().toString() + "\n");
            out.append("Methods: " + m_count + "\n");
            out.append(brachStatistics());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Offset(int offset) {
        pc = offset;
    }

    public static void Branch(int brOutcome) {
        Integer n = new Integer(pc);
        Branch b = (Branch) branch.get(n);
        if (b == null) {
            b = new Branch();
            branch.put(n,b);
        }
        if (brOutcome == 0)
            b.taken = b.taken.add(BigInteger.ONE);
        else
            b.not_taken = b.not_taken.add(BigInteger.ONE);
    }


    /*
    Intuition: using number of instructions as a metric is to expensive - it creates to much indirection
               basic blocks are indirectly considered in order to extract the branch metrics
               number of calls to methods - calls are expensive, the value of having the metric compensates the performance trade-off
               branches: our application heavy load falls into his cycles
                taken  & not taken -> are a good aproximation of the number of iterations (not taken particulary)
    */
    public static String brachStatistics() {
        BigInteger total = BigInteger.ZERO, taken = BigInteger.ZERO, ntaken = BigInteger.ZERO;
        for (Enumeration e = branch.keys(); e.hasMoreElements(); ) {
            Integer key = (Integer) e.nextElement();
            Branch b = (Branch) branch.get(key);
            total = total.add(b.taken).add(b.not_taken);
            taken = taken.add(b.taken);
            ntaken = ntaken.add(b.not_taken);
        }
        // reset the hashtable
        branch = new Hashtable();
        return "taken: " + taken + "\n" +
                "not taken: " + ntaken + "\n";
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
