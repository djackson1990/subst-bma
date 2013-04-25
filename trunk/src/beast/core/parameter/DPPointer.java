package beast.core.parameter;

import beast.core.Input;
import beast.core.StateNode;
import beast.core.Description;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.io.PrintStream;

import org.w3c.dom.Node;

/**
 * @author Chieh-Hsi Wu
 */
@Description("Array that points to some set of parameters")
public class DPPointer extends StateNode {
    public Input<List<QuietRealParameter>> uniqueParametersInput = new Input<List<QuietRealParameter>>(
            "uniqueParameter",
            "refrence to a parameter",
            new ArrayList<QuietRealParameter>(),
            Input.Validate.REQUIRED
    );

    public Input<IntegerParameter> assignmentInput = new Input<IntegerParameter>(
            "initialAssignment",
            "a parameter which specifies the assignment of elements to clusters",
            Input.Validate.REQUIRED
    );





    private QuietRealParameter[] parameters;
    private QuietRealParameter[] storedParameters;
    private int lastDirty = -1;
    private int[] lastSwappedSites;
    private int[] lastDirtySites;
    private int storedLastDirty = -1;
    private ChangeType changeType;

    public DPPointer(){
        
    }
    public DPPointer(int dim){
        parameters = new QuietRealParameter[dim];
        storedParameters = new QuietRealParameter[dim];
        lastSwappedSites = new int[2];

    }

    public void initAndValidate(){
        IntegerParameter initialAssignment = assignmentInput.get();
        List<QuietRealParameter> uniqueParameters = uniqueParametersInput.get();
        parameters = new QuietRealParameter[initialAssignment.getDimension()];
        storedParameters = new QuietRealParameter[initialAssignment.getDimension()];
        for(int i = 0; i < parameters.length;i++){
            parameters[i] = uniqueParameters.get(initialAssignment.getValue(i));
        }
        System.arraycopy(parameters,0,storedParameters,0,parameters.length);
        lastSwappedSites = new int[2];
    }

    public void point(int dim, QuietRealParameter parameter){
        //System.out.println("pointer: "+dim);
        startEditing(null);
        lastDirty = dim;
        parameters[dim] = parameter;
        changeType = ChangeType.POINTER_CHANGED;

    }

    public void pointQuitely(int dim, QuietRealParameter parameter){
        parameters[dim] = parameter;

    }

    public void swapPointers(int siteIndex1, int siteIndex2){
        startEditing(null);
        lastSwappedSites[0] = siteIndex1;
        lastSwappedSites[1] = siteIndex2;
        QuietRealParameter temp = parameters[siteIndex1];
        parameters[siteIndex1] = parameters[siteIndex2];
        parameters[siteIndex2] = temp;
        changeType = ChangeType.POINTERS_SWAPPED;

    }

    public void multiPointerChanges(int[] fromSites, int toSite){
        for(int fromSite: fromSites){
            parameters[fromSite] = parameters[toSite];
        }
        lastDirtySites = new int[fromSites.length];
        System.arraycopy(fromSites,0,lastDirtySites,0,fromSites.length);
        changeType = ChangeType.MULTIPLE_POINTER_CHANGED;
    }

    public ChangeType getChangeType(){
        return changeType;
    }

    public int[] getSwappedSites(){
        return Arrays.copyOf(lastSwappedSites, lastSwappedSites.length);
    }

    public int[] getLastDirtySites(){
        return Arrays.copyOf(lastDirtySites, lastDirtySites.length);
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
        //System.err.println("isDirty: "+isDirty);
    }

    public boolean isParameterDirty(int index){
        return parameters[index].somethingIsDirty();

    }

    public void store(){
        //System.out.println("store?");
        storedLastDirty = lastDirty;
        System.arraycopy(parameters,0,storedParameters,0,parameters.length);
        /*for(int i = 0; i < storedParameters.length; i++){
            System.out.println(getID()+" stored param "+i+": "+storedParameters[i]);
        }*/
    }

    @Override
	public void restore() {
        //System.err.println("restore?");
        lastDirty = storedLastDirty;
        QuietRealParameter[] temp = parameters;
        parameters = storedParameters;
        storedParameters = temp;
	}

    public int storedIndexInList(int index, ParameterList paramList){
        /*for(int i = 0; i < storedParameters.length; i++){
            System.out.println(getID()+" param "+i+": "+storedParameters[i]);
        }
        for(int i = 0; i < paramList.getDimension(); i++){
            System.out.println("list:"+(paramList.getParameter(i)));
        }
        System.out.println("target: "+storedParameters[index]);*/
        return paramList.indexOf(storedParameters[index]);

    }



    public int scale(double fScale){
        throw new RuntimeException("Scaling simply does not make sense in this case");

    }

    public int getLastDirty(){
        //System.out.println("getLastDirty: "+lastDirty);
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

    public QuietRealParameter getParameter(int dim){
        return parameters[dim];

    }

    public double getParameterValue(int dim){
        return parameters[dim].getValue();

    }

    public int indexInList(int dim, ParameterList paramList){
        /*System.err.println("parameter: "+parameters[dim]);
        System.err.println("parameterList: "+paramList.getDimension());
        for(int i = 0; i < paramList.getDimension();i++){
            System.err.print(paramList.getParameter(i)+", ");
            System.err.println(paramList.getParameter(i) == parameters[dim]);
        }
        System.err.println(paramList.indexOf(parameters[dim]));*/
        //System.out.println("dim: "+dim );
        return paramList.indexOf(parameters[dim]);
    }

    public int getParameterIDNumber(int siteIndex){
        return parameters[siteIndex].getIDNumber();

    }

    public int getStoredParameterIDNumber(int siteIndex){
        return storedParameters[siteIndex].getIDNumber();

    }


    public boolean sameParameter(int dim, RealParameter parameter){
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

    public boolean pointEqual(int index1, RealParameter param){
        return parameters[index1] == param;
    }

    public String toString(){
        final StringBuffer buf = new StringBuffer();
        int dimParam = getDimension();
        buf.append(getID()+"["+dimParam+"]:\n");
        for(int iParam = 0; iParam < dimParam; iParam++){
            buf.append(getParameter(iParam).getIDNumber()).append("\t");
        }
        return buf.toString();
    }
}
