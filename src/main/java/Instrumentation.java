import BIT.highBIT.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import pt.ulisboa.tecnico.meic.cnv.RepositoryService;
import pt.ulisboa.tecnico.meic.cnv.WebServer;
import pt.ulisboa.tecnico.meic.cnv.dto.Metric;
import raytracer.RayTracer;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

class Branch {
    BigInteger taken = BigInteger.ZERO;
    BigInteger not_taken = BigInteger.ZERO;
}

public class Instrumentation {

    private static final String PACKAGE_OF_INSTRUMENTATION = "raytracer/";
    private static final String A_INSTRUMENTING_CLASS = "RayTracer.class";
    private static String classPath;
    private static final RepositoryService repositoryService = new RepositoryService();

    /* ThreadLocal gives us local context for each thread in static variables */
    private static ThreadLocal<BigInteger> m_count = new ThreadLocal<BigInteger>() {
        @Override
        protected BigInteger initialValue()
        {
            return BigInteger.ZERO;
        }
    };

    private static ThreadLocal<Hashtable> branch = new ThreadLocal<Hashtable>() {
        @Override
        protected Hashtable initialValue()
        {
            return new Hashtable();
        }
    };

    private static ThreadLocal<Integer> pc = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue()
        {
            return new Integer(0);
        }
    };

    public static void main(String[] args) throws ParseException {
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));

        Options options = new Options();
        options.addOption("i", false, "Instrument code");
        options.addOption("p", false, "Instrument code");
        CommandLine cmd = (new DefaultParser()).parse(options, args);

        ClassLoader classLoader = RayTracer.class.getClassLoader();
        URL url = classLoader.getResource(PACKAGE_OF_INSTRUMENTATION + A_INSTRUMENTING_CLASS);

        if(cmd.hasOption("i")) {
             /*
            String jarPath = null;
            if(url.getPath().contains(".jar")){
               try {
                    jarPath = url.getPath().substring("file:".length(),url.getPath().indexOf("!"));
                    extractJar(jarPath);
                    classPath = jarPath.substring(0,jarPath.lastIndexOf("/") + 1).concat(PACKAGE_OF_INSTRUMENTATION);

                } catch (IOException e) {
                    System.err.println("Error finding the classes to instrument!");
                    return;
                }
            }
            else*/

            classPath = url.getPath().substring(0,url.getPath().lastIndexOf("/") + 1);


            File file_in = new File(classPath);
            if (!file_in.exists()) {
                System.err.println("You need to compile the classes first!");
                return;
            }
            instrumentRayTracer(file_in.list());
        }

        arguments.remove("-i");

        try {
            WebServer.main(arguments.toArray(new String[arguments.size()]));
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
        m_count.set(m_count.get().add(BigInteger.ONE));
    }

    public static synchronized void writeFile(String foo){
        Branch data = branchStats();
        String url = Thread.currentThread().getName();

        Metric metric = new Metric(
                WebServer.getHostname(),
                url,
                m_count.get(),
                data.taken,
                data.not_taken);

        repositoryService.addMetric(String.valueOf(url.hashCode()), metric);
    }

    private static void reset() {
        m_count.set(BigInteger.ZERO);
        branch.set(new Hashtable());
    }

    public static void Offset(int offset) {
        pc.set(new Integer(offset));
    }

    public static void Branch(int brOutcome) {
        Integer n = pc.get();
        Branch b = (Branch) branch.get().get(n);
        if (b == null) {
            b = new Branch();
            branch.get().put(n, b);
        }
        if (brOutcome == 0)
            b.taken = b.taken.add(BigInteger.ONE);
        else
            b.not_taken = b.not_taken.add(BigInteger.ONE);
    }

    /* Just for compatibility with 1st delivery */
    public static String brachStatistics() {
        BigInteger total = BigInteger.ZERO, taken = BigInteger.ZERO, ntaken = BigInteger.ZERO;
        for (Enumeration e = branch.get().keys(); e.hasMoreElements(); ) {
            Integer key = (Integer) e.nextElement();
            Branch b = (Branch) branch.get().get(key);
            total = total.add(b.taken).add(b.not_taken);
            taken = taken.add(b.taken);
            ntaken = ntaken.add(b.not_taken);
        }
        return "taken: " + taken + "\n" +
                "not taken: " + ntaken + "\n";
    }

    /*
    Intuition: using number of instructions as a metric is to expensive - it creates to much indirection
            basic blocks are indirectly considered in order to extract the branch metrics
            number of calls to methods - calls are expensive, the value of having the metric compensates the performance trade-off
            branches: our application heavy load falls into his cycles
             taken  & not taken -> are a good aproximation of the number of iterations (not taken particulary)
    */
    private static Branch branchStats() {
        Branch accumulate = new Branch();
        accumulate.taken = accumulate.not_taken = BigInteger.ZERO;
        for (Enumeration e = branch.get().keys(); e.hasMoreElements(); ) {
            Integer key = (Integer) e.nextElement();
            Branch b = (Branch) branch.get().get(key);
            accumulate.taken = accumulate.taken.add(b.taken);
            accumulate.not_taken = accumulate.not_taken.add(b.not_taken);
        }
        return accumulate;
    }

    private static void extractJar(String path) throws IOException {
        JarFile jar = new JarFile(path);
        Enumeration<JarEntry> enumEntries = jar.entries();
        while (enumEntries.hasMoreElements()) {
            JarEntry file = (JarEntry) enumEntries.nextElement();
            if(file.getName().startsWith("raytracer")) {
                File f = new File(path.substring(0, path.lastIndexOf("/")) + File.separator + file.getName());
                if (file.isDirectory()) {
                    f.mkdir();
                    continue;
                }
                InputStream is = jar.getInputStream(file);
                FileOutputStream fos = new FileOutputStream(f);
                while (is.available() > 0) {
                    fos.write(is.read());
                }
                fos.close();
                is.close();
            }
        }
        jar.close();
    }


}
