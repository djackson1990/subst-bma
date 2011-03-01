package Wrapper;

import beast.core.parameter.BooleanParameter;
import beast.core.Loggable;

import java.io.PrintStream;

/**
 * @author Chieh-Hsi Wu
 *
 */

import beast.core.Loggable;

import java.io.PrintStream;

/*public class NtdBMAModelChooseWrapper implements Loggable {

    
    public void log(int nSample, PrintStream out) {

    }

    public void close(PrintStream out) {
        // nothing to do
    } */

    /**
     * Loggable interface implementation follows (partly, the actual
     * logging of values happens in derived classes) *
     
    @Override
    public void init(PrintStream out) throws Exception {
        final int nValues = getDimension();
        if (nValues == 1) {
            out.print(getID() + "\t");
        } else {
            for (int iValue = 0; iValue < nValues; iValue++) {
                out.print(getID() + iValue + "\t");
            }
        }
    }
}    */
