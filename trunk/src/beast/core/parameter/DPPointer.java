package beast.core.parameter;

import beast.core.Input;
import beast.core.StateNode;
import beast.core.Description;

import java.util.List;
import java.util.ArrayList;
import java.io.PrintStream;

import org.w3c.dom.Node;

/**
 * @author Chieh-Hsi Wu
 */
@Description("Array that points to some set of parameters")
public class DPPointer extends StateNode {
    public Input<List<RealParameter2>> uniqueParametersInput = new Input<List<RealParameter2>>(
            "uniqueParameter",
            "refrence to a parameter",
            new ArrayList<RealParameter2>(),
            Input.Validate.REQUIRED
    );

    public Input<IntegerParameter> assignmentInput = new Input<IntegerParameter>(
            "initialAssignment",
            "a parameter which species the assignment of elements to clusters",
            Input.Validate.REQUIRED
    );

    private RealParameter2[] parameters;
    private RealParameter2[] storedParameters;
    private int lastDirty = -1;
    private int storedLastDirty = -1;

    public DPPointer(){
        
    }
    public DPPointer(int dim){
        parameters = new RealParameter2[dim];
        storedParameters = new RealParameter2[dim];

    }

    public void initAndValidate(){
        IntegerParameter initialAssignment = assignmentInput.get();
        List<RealParameter2> uniqueParameters = uniqueParametersInput.get();
        parameters = new RealParameter2[initialAssignment.getDimension()];
        storedParameters = new RealParameter2[initialAssignment.getDimension()];
        for(int i = 0; i < parameters.length;i++){
            parameters[i] = uniqueParameters.get(initialAssignment.getValue(i));
        }
    }

    public void point(int dim, RealParameter2 parameter){
        startEditing(null);
        lastDirty = dim;
        parameters[dim] = parameter;

    }

    protected void pointQuitely(int dim, RealParameter2 parameter){
        parameters[dim] = parameter;

    }

    public int getDimension(){
        return parameters.length;
    }

    public DPPointer copy(){
        DPPointer copy = new DPPointer(getDimension());
        for(int i = 0; i < copy.getDimension(); i++){
            copy.pointQuitely(i, parameters[i]);
        }
        return copy;
    }

    public void setEverythingDirty(boolean isDirty){
        setSomethingIsDirty(isDirty);
    }

    public void store(){
        storedLastDirty = lastDirty;
        System.arraycopy(parameters,0,storedParameters,0,parameters.length);
    }

    @Override
	public void restore() {
        lastDirty = storedLastDirty;
        RealParameter2[] temp = parameters;
        parameters = storedParameters;
        storedParameters = temp;
	}

    public int scale(double fScale){
        throw new RuntimeException("Scaling simply does not make sense in this case");

    }

    public int getLastDirty(){
        return lastDirty;
    }

    public double getArrayValue(){
        return -1;

    }

    public double getArrayValue(int dim){
        return -1;
    }

        /** other := this
     *  Assign all values of this to other **/
    public void assignTo(StateNode other){
        //todo

    }

    /** this := other
     * Assign all values of other to this
     *
     **/
    public void assignFrom(StateNode other){
        //todo

    }

    /** As assignFrom, but only those parts are assigned that
     * are variable, for instance for parameters bounds and dimension
     * do not need to be copied.
     */
    public void assignFromFragile(StateNode other){
        //todo
    }

    /** StateNode implementation **/
    @Override
    public void fromXML(Node node) {
        //todo
    }

    private RealParameter2 getParameter(int dim){
        return parameters[dim];

    }

    public double getParameterValue(int dim){
        return parameters[dim].getValue();

    }

    public int indexInList(int dim, ParameterList paramList){
        return paramList.indexOf(parameters[dim]);
    }

    public boolean sameParameter(int dim, RealParameter2 parameter){
        return parameters[dim].getValue() == parameter.getValue();
    }

        /** Loggable implementation **/
    @Override
    public void log(int nSample, PrintStream out) {
        DPPointer dPPointer = (DPPointer) getCurrent();
        int dim = dPPointer.getDimension();
        for(int i = 0; i < dim; i++){
            parameters[i].log(nSample,out);
        }
        //todo
    }

    public void close(PrintStream out){

    }

    @Override
    public void init(PrintStream out) throws Exception {
        int dimParam = getDimension();
        for (int iParam = 0; iParam < dimParam; iParam++) {
            int dimValue = getParameter(iParam).getDimension();
            for(int iValue = 0; iValue < dimValue; iValue++){
                out.print(getID()+"."+iParam +"."+ iValue + "\t");
            }
        }

    }

    public boolean pointEqual(int index1, int index2){
        return parameters[index1] == parameters[index2];
    }

    public String toString(){
        final StringBuffer buf = new StringBuffer();
        int dimParam = getDimension();
        buf.append(getID()+"["+dimParam+"]:\n");
        for(int iParam = 0; iParam < dimParam; iParam++){
            buf.append(getParameter(iParam).toString()).append("\t");
        }
        return buf.toString();
    }
}
