package beast.core.parameter;

import beast.core.Description;
import beast.core.StateNode;
import beast.core.Input;
import beast.core.PluginList;

import java.util.ArrayList;
import java.util.List;
import java.io.PrintStream;

import org.w3c.dom.Node;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class stores a list of parameters and the size of the list can change.")
public class ParameterList extends StateNode implements PluginList {
    public Input<List<RealParameter>> parametersInput =
                new Input<List<RealParameter>>("parameter", "refrence to a parameter", new ArrayList<RealParameter>(), Input.Validate.REQUIRED);

    private ArrayList<RealParameter> parameterList;
    private ArrayList<RealParameter> storedParameterList;
    private int changedIndex = -1;
    private int removedIndex = -1;

    ChangeType changeType = ChangeType.ALL;
    
    public ParameterList(){
        parameterList = new ArrayList<RealParameter>();
        storedParameterList = new ArrayList<RealParameter>();
    }



    public void initAndValidate(){
        //System.err.println("initialize parameter list");
        List<RealParameter> parameterList  = parametersInput.get();
        for(RealParameter parameter: parameterList){
            this.parameterList.add(parameter);
        }
    }


    public void addParameter(RealParameter parameter){
        //System.err.println("add parameter");
        startEditing(null);
        parameterList.add(parameter);
        changeType = ChangeType.ADDED;
        //throw new RuntimeException("stopping fucking with my code!");
    }

    public void addParameterQuietly(RealParameter parameter){
        parameterList.add(parameter);
        changeType = ChangeType.ADDED;
        //System.err.println(getID()+":added");

    }
    public void removeParameter(int pIndex){
        startEditing(null);
        //System.err.println("size: "+parameterList.size());
        parameterList.remove(pIndex);
        changeType = ChangeType.REMOVED;
        removedIndex = pIndex;
        //System.err.println(getID()+": removed");
        //throw new RuntimeException("stopping fucking with my code!");
    }
    public void setValue(int pIndex, int dim, double value) {
        startEditing(null);

        parameterList.get(pIndex).setValueQuietly(dim,value);
        parameterList.get(pIndex).setEverythingDirty(true);
        changedIndex = pIndex;
        changeType = ChangeType.VALUE_CHANGED;
        //System.err.println(getID()+": changed");
        //throw new RuntimeException("stopping fucking with my code!");
    }

    public int getChangedIndex(){
        return changedIndex;
    }

    public int getRemovedIndex(){
        return removedIndex;
    }

    public double getValue(int pIndex, int dim) {
        return parameterList.get(pIndex).getValue(dim);

    }

    public double getUpper(){
        return getParameter(0).getUpper();
    }

    public double getLower(){
        return getParameter(0).getLower();
    }

    public double getParameterUpper(int iParam){
        return getParameter(iParam).getUpper();
    }

    public double getParameterLower(int iParam){
        return getParameter(iParam).getLower();
    }

    

    public RealParameter getParameter(int pIndex){
        return parameterList.get(pIndex);
    }

    public int getParameterDimension(){
        return getParameter(0).getDimension();
    }
    



    protected void store(){
        storedParameterList = new ArrayList<RealParameter>();
        for(RealParameter parameter:parameterList){
            parameter.store();
            storedParameterList.add(parameter);
        }
        //System.out.println("storing");
    }

    public void restore(){
        m_bHasStartedEditing = false;
        ArrayList<RealParameter> tempList = storedParameterList;
        storedParameterList = parameterList;
        parameterList = tempList;
        for(RealParameter parameter:parameterList){
            parameter.restore();
        }
        changeType = ChangeType.ALL;
        //System.out.println("restoring");

    }

    public ChangeType getChangeType(){
        return changeType;
    }

    public ParameterList copy(){
        ParameterList copy = new ParameterList();
        for(RealParameter parameter: parameterList){
            copy.addParameterQuietly((RealParameter)parameter.copy());
        }
        return copy;
    }

    public void setEverythingDirty(boolean isDirty){
        //System.err.println("Parameter list: "+isDirty);
        setSomethingIsDirty(isDirty);
        //System.err.println("list size: "+parameterList.size());
        for(Parameter parameter:parameterList){
            parameter.setEverythingDirty(isDirty);
        }
        changeType = ChangeType.ALL;

    }

    public int indexOf(RealParameter param){
        return parameterList.indexOf(param);
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

    /** Note that changing toString means fromXML needs to be changed as well,
     * since it parses the output of toString back into a parameter.
     */
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        int dimParam = getDimension();
        buf.append(getID()+"["+dimParam+"]:\n");
        for(int iParam = 0; iParam < dimParam; iParam++){
            buf.append(getParameter(iParam).toString()).append("\n");
        }
        return buf.toString();
    }    

    public int getDimension(){
        return parameterList.size();
    }

    public int scale(double fScale){
        throw new RuntimeException("Not appropriate for a list!");
    }


}
