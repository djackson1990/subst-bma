package beast.core.util;

import beast.core.parameter.DPPointer;
import beast.core.parameter.ParameterList;
import beast.core.Input;

import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA.
 * User: cwu080
 * Date: 13/09/2011
 * Time: 12:22:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrintPointerIDNumber extends PrintListIDNumber{

    public Input<DPPointer> pointersInput = new Input<DPPointer>("pointers", "list of items to be counted", Input.Validate.REQUIRED);

    public DPPointer pointers = null;
    public void initAndValidate(){
         pointers = pointersInput.get();

    }
    @Override
	public void init(PrintStream out) throws Exception {
        for(int i = 0; i < pointers.getDimension(); i++){
            out.print(pointers.getID() + ".idNum." + i + "\t");
        }
    }

    @Override
	public void log(int nSample, PrintStream out) {
        int dim = pointers.getDimension();
        for(int i = 0; i < dim; i++){
    	    out.print(pointers.getParameter(i).getIDNumber() + "\t");
        }
	}

}
