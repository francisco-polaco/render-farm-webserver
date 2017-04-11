package pt.ulisboa.tecnico.meic.cnv;

import BIT.highBIT.BasicBlock;
import BIT.highBIT.ClassInfo;
import BIT.highBIT.Routine;

import java.io.File;
import java.io.PrintStream;
import java.util.Enumeration;

/**
 * Created by diogo on 11-04-2017.
 */
public class Instrumentation {

    private static PrintStream out = null;

    private static int i_count = 0, b_count = 0, m_count = 0;

    /* main reads in all the files class files present in the input directory,
     * instruments them, and outputs them to the specified output directory.
     */
    public static void main(String argv[]) {
        File file_in = new File(argv[0]);
        String infilenames[] = file_in.list();

        for (int i = 0; i < infilenames.length; i++) {
            System.out.println(infilenames[i]);

            /*String infilename = infilenames[i];

            if (infilename.endsWith(".class")) {
                // create class info object
                System.out.println(argv[0]);
                ClassInfo ci = new ClassInfo(argv[0] + System.getProperty("file.separator") + infilename);

                // loop through all the routines
                // see java.util.Enumeration for more information on Enumeration class
                for (Enumeration e = ci.getRoutines().elements(); e.hasMoreElements(); ) {

                    Routine routine = (Routine) e.nextElement();

                    routine.addBefore("ICount", "mcount", new Integer(1));

                    for (Enumeration b = routine.getBasicBlocks().elements(); b.hasMoreElements(); ) {
                        BasicBlock bb = (BasicBlock) b.nextElement();
                        bb.addBefore("ICount", "count", new Integer(bb.size()));
                    }
                }
                ci.addAfter("ICount", "printICount", ci.getClassName());
                ci.write(argv[1] + System.getProperty("file.separator") + infilename);
            }*/
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
}