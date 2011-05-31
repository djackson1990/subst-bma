package beast.core.parameter;

import beast.core.*;

/**
 * @author Chieh-Hsi Wu
 */

@Description("Summarizes a set of valuables from a Dirichlet prior process")
public class DPValuable extends CalculationNode implements Valuable {

    //ParameterList
    public Input<ParameterList> paramListInput = new Input<ParameterList>(
            "paramList",
            "A list of unique parameter values",
            Input.Validate.REQUIRED
    );


    //assignment
    public Input<IntegerParameter> assignmentInput = new Input<IntegerParameter>(
            "assignment",
            "a parameter which species the assignment of elements to clusters",
            Input.Validate.REQUIRED
    );

    private ParameterList paramList;
    private IntegerParameter initialAssignment;
    private RealParameter2[] parameters;
    private RealParameter2[] storedParameters;
    private int[] clusterCounts;
    private int[] storedClusterCounts;
    private boolean pointersChanged;


    public void initAndValidate(){
        paramList = paramListInput.get();
        initialAssignment = assignmentInput.get();
        parameters = new RealParameter2[initialAssignment.getDimension()];
        storedParameters = new RealParameter2[initialAssignment.getDimension()];
        clusterCounts = new int[initialAssignment.getDimension()];
        for(int i = 0; i < parameters.length;i++){
            parameters[i] = paramList.getParameter(initialAssignment.getValue(i));
        }
        pointersChanged = true;
     
    }

    public void changePointers(int dim, int listIndex){
        parameters[dim] = paramList.getParameter(listIndex);
        pointersChanged = true;
    }

    /** CalculationNode methods **/
    // smarter vesions to be computed
	@Override
	public void store() {
		System.arraycopy(parameters,0,storedParameters,0,parameters.length);
        storedClusterCounts = new int[clusterCounts.length];
        System.arraycopy(clusterCounts,0,storedClusterCounts,0,clusterCounts.length);
		super.store();
	}
	@Override
	public void restore() {
        RealParameter2[] temp1 = parameters;
        parameters = storedParameters;
        storedParameters = temp1;

        int[] temp2 = clusterCounts;
        clusterCounts = storedClusterCounts;
        storedClusterCounts = temp2;
        super.restore();
	}

    @Override
	public boolean requiresRecalculation() {
		return true;
	}

    /** Valuable implementation follows **/
	@Override
	public int getDimension() {
		return parameters.length;
	}

    @Override
	public double getArrayValue() {

		return parameters[0].getArrayValue();
	}

    public double getArrayValue(int dim){
        return parameters[dim].getArrayValue();
    }

    public int[] clusterCounts(){
        if(pointersChanged){
            clusterCounts = new int[paramList.getDimension()];
            for(RealParameter2 parameter:parameters){
                clusterCounts[paramList.indexOf(parameter)]++;
            }
            pointersChanged = false;
        }
        return clusterCounts;
    }

}
