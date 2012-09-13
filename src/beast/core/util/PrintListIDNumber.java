package beast.core.util;

import beast.core.*;
import beast.core.parameter.ParameterList;
import beast.core.parameter.DPPointer;

import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA.
 * User: cwu080
 * Date: 13/09/2011
 * Time: 12:04:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrintListIDNumber extends CalculationNode implements Loggable {
    public Input<ParameterList> paramListInput = new Input<ParameterList>(
            "paramList",
            "list of items to be counted"
    );

    public ParameterList paramList = null;
    public void initAndValidate(){
         paramList = paramListInput.get();

    }
    @Override
	public void init(PrintStream out) throws Exception {
        out.print("IDNumber("+paramList.getID() + ")\t");
    }

    @Override
	public void log(int nSample, PrintStream out) {
        int dim = paramList.getDimension();
        for(int i = 0; i < dim; i++){
    	    out.print(paramList.getParameter(i).getIDNumber() + "\t");
        }
	}

    @Override
	public void close(PrintStream out) {
		// nothing to do
	}
}
