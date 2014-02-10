package beast.core.parameter;

import beast.core.*;

import java.util.ArrayList;
import java.util.List;
import java.io.PrintStream;

import org.w3c.dom.Node;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class stores a list of parameters and the size of the list can change.")
public class ParameterList extends StateNode implements PluginList, Recycle {
    public Input<List<QuietRealParameter>> parametersInput =
                new Input<List<QuietRealParameter>>(
                        "parameter",
                        "refrence to a parameter",
                        new ArrayList<QuietRealParameter>(),
                        Input.Validate.REQUIRED
                );

    private ArrayList<QuietRealParameter> parameterList;
    private ArrayList<QuietRealParameter> storedParameterList;
    private int changedIndex = -1;
    private int removedIndex = -1;

    ChangeType changeType = ChangeType.ALL;
    
    public ParameterList(){
        parameterList = new ArrayList<QuietRealParameter>();
        storedParameterList = new ArrayList<QuietRealParameter>();
    }



    public void initAndValidate(){
        //System.err.println("initialize parameter list");
        List<QuietRealParameter> parameterList  = parametersInput.get();
        for(QuietRealParameter parameter: parameterList){
            parameter.setIDNumber(createID());
            this.parameterList.add(parameter);
        }
    }


    public void addParameter(QuietRealParameter parameter){

        startEditing(null);
        parameter.setIDNumber(createID());
        parameterList.add(parameter);
        changeType = ChangeType.ADDED;
        //System.out.println("add parameter: "+getID()+" "+getDimension());//+" " +toString());
    }

    ArrayList<Integer> idPool = new ArrayList<Integer>();
    private int newIDCount = 0;
    private int createID(){
        //Recycle id
        if(idPool.size() > 0){
            return idPool.remove(0);
        }
        
        //If no ids available to be recycled
        int newID = newIDCount;
        newIDCount++;
        return newID;
    }

    //Stores the ids of removed objects
    private void storeID(int spareID){
        idPool.add(spareID);
    }

    //Get how many ids have been created so far
    public int getObjectCreatedCount(){
        return newIDCount;
    }



    private void addParameterQuietly(QuietRealParameter parameter){
        parameterList.add(parameter);
        changeType = ChangeType.ADDED;
        //System.err.println(getID()+":added");

    }
    public void removeParameter(int pIndex){
        startEditing(null);
        //System.out.println("size: "+parameterList.size());
        storeID(parameterList.get(pIndex).getIDNumber());
        parameterList.remove(pIndex);
        changeType = ChangeType.REMOVED;
        removedIndex = pIndex;
        //System.out.println(getID()+": removed "+getDimension()+" "+toString());
        //throw new RuntimeException("stopping fucking with my code!");
    }
    public void setValue(int pIndex, int dim, double value) {
        startEditing(null);

        parameterList.get(pIndex).setValueQuietly(dim,value);
        parameterList.get(pIndex).setEverythingDirty(true);
        changedIndex = pIndex;
        changeType = ChangeType.VALUE_CHANGED;
        //System.out.println(getID()+": changed "+changedIndex);
        //throw new RuntimeException("stopping fucking with my code!");
    }

    public void splitParameter(int pIndex, QuietRealParameter parameter){
        startEditing(null);
        parameter.setIDNumber(createID());
        parameterList.add(parameter);        
        changedIndex = pIndex;
        changeType = ChangeType.SPLIT;

    }

    public void mergeParameter(int pIndex1, int pIndex2){
        startEditing(null);
        storeID(parameterList.get(pIndex1).getIDNumber());
        parameterList.remove(pIndex1);
        changedIndex = pIndex2 < pIndex1? pIndex2:(pIndex2-1);
        removedIndex = pIndex1;
        changeType = ChangeType.MERGE;
    }

    public int getDirtyIndex(){
        //System.out.println(getID()+changedIndex);
        return changedIndex;
    }

    public int getDirtyParameterIDNumber(){
        return parameterList.get(getDirtyIndex()).getIDNumber();
    }


    public int getRemovedIndex(){
        return removedIndex;
    }

    public int getRemovedIDNumber(){
        return idPool.get(idPool.size() - 1);
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

    /*
     * Get the last parameter added.
     */
    public QuietRealParameter getLastParameter(){
        return parameterList.get(parameterList.size() - 1);
    }


    public double getParameterUpper(int iParam){
        return getParameter(iParam).getUpper();
    }

    public double getParameterLower(int iParam){
        return getParameter(iParam).getLower();
    }

    

    public QuietRealParameter getParameter(int pIndex){
        return parameterList.get(pIndex);
    }

    public int getParameterDimension(){
        return getParameter(0).getDimension();
    }

    int storedNewIDCount;
    ArrayList<Integer> storedIDPool;
    protected void store(){
        storedNewIDCount = newIDCount;
        storedParameterList = new ArrayList<QuietRealParameter>();

        for(QuietRealParameter parameter:parameterList){
            parameter.store();
            storedParameterList.add(parameter);
        }
        storedIDPool = new ArrayList<Integer>();
        for(int id:idPool){
            storedIDPool.add(id);

        }
        //System.out.println("storing "+storedParameterList.size());

    }

    public void restore(){
        //System.err.println("restore, storedListSize: "+storedParameterList.size());
        hasStartedEditing = false;
        newIDCount = storedNewIDCount;
        ArrayList<QuietRealParameter> tempList = storedParameterList;
        storedParameterList = parameterList;
        parameterList = tempList;
        for(RealParameter parameter:parameterList){
            parameter.restore();
        }
        changeType = ChangeType.ALL;
        idPool = storedIDPool;
        storedIDPool = null;


    }

    public ChangeType getChangeType(){
        //System.out.println(getID() +" "+  changeType);
        return changeType;
    }


    public ParameterList copy(){
        ParameterList copy = new ParameterList();
        for(QuietRealParameter parameter: parameterList){
            copy.addParameterQuietly(parameter.copy());
        }
        return copy;
    }

    public void setEverythingDirty(boolean isDirty){
        //System.err.println("Parameter list: "+getID()+" "+isDirty);
        setSomethingIsDirty(isDirty);
        //System.err.println("list size: "+parameterList.size());
        for(Parameter parameter:parameterList){
            ((Parameter.Base)parameter).setEverythingDirty(isDirty);
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
            buf.append(getParameter(iParam).getIDNumber()+" "+getParameter(iParam).toString()).append("\n");
        }
        return buf.toString();
    }    

    public int getDimension(){
        //System.out.println(getID()+ ": "+parameterList.size());
        return parameterList.size();
    }

    public int scale(double fScale) throws Exception{
        for(RealParameter parameter:parameterList){
            parameter.scale(fScale);
        }
        changeType = ChangeType.ALL;
        return getDimension()*getParameterDimension();
    }


}
