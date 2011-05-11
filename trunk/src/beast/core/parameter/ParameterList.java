package beast.core.parameter;

import beast.core.Description;
import beast.core.StateNode;

import java.util.ArrayList;
import java.io.PrintStream;

import org.w3c.dom.Node;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class stores a list of parameters and the size of the list can change.")
public class ParameterList extends StateNode {

    private ArrayList<Parameter> parameterList;
    private ArrayList<Parameter> storedParameterList;
    private int lastParameterChanged = -1;
    
    public ParameterList(){
    }

    public void addParameter(Parameter parameter){
        startEditing(null);
        parameterList.add(parameter);
    }

    public void setValue(int pIndex, int dim, double value) {
        startEditing(null);
        parameterList.get(pIndex).setValue(dim,value);
        lastParameterChanged = pIndex;
    }

    

    private Parameter getParameter(int pIndex){
        return parameterList.get(pIndex);
    }
    

    protected void removeParameter(int pIndex){
        startEditing(null);
        parameterList.remove(pIndex);
    }

    public void store(){
        storedParameterList = new ArrayList<Parameter>();
        for(Parameter parameter:parameterList){
            storedParameterList.add(parameter);
        }
    }

    public void restore(){
        ArrayList<Parameter> tempList = storedParameterList;
        storedParameterList = parameterList;
        parameterList = tempList;
    }

    public ParameterList copy(){
        ParameterList copy = new ParameterList();
        for(Parameter parameter: parameterList){
            copy.addParameter(parameter.copy());
        }
        return copy;
    }

    public void setEverythingDirty(boolean isDirty){
        for(Parameter parameter:parameterList){
            parameter.setEverythingDirty(isDirty);
        }

    }

    public double getArrayValue(){
        throw new RuntimeException("As suggested by the name, this class represents a list, so it's not appropriate to return a single value");

    }

    public double getArrayValue(int dim){
        throw new RuntimeException("As suggested by the name, this class represents a list, so it's not appropriate to return a single value");

    }

    /** other := this
     *  Assign all values of this to other **/
    public void assignTo(StateNode other){

    }

    /** this := other
     * Assign all values of other to this
     *
     **/
    public void assignFrom(StateNode other){

    }

    /** As assignFrom, but only those parts are assigned that
     * are variable, for instance for parameters bounds and dimension
     * do not need to be copied.
     */
    public void assignFromFragile(StateNode other){
    }

    /** StateNode implementation **/
    @Override
    public void fromXML(Node node) {
        //todo
    }

    /** Loggable implementation **/
    @Override
    public void log(int nSample, PrintStream out) {
        ParameterList paramList = (ParameterList) getCurrent();
        int dim = paramList.getDimension();
        for(int i = 0; i < dim; i++){
            paramList.getParameter(i).log(nSample,out);
        }
        //todo
    }

    public void close(PrintStream out){

    }

    public void init(PrintStream out) throws Exception {
        //todo
    }

    public int getDimension(){
        return parameterList.size();
    }

    public int scale(double fScale){
        throw new RuntimeException("Not appropriate for a list!");
    }
}
