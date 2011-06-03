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
    public Input<DPPointer> pointersInput = new Input<DPPointer>(
            "pointers",
            "array which points a set of unique parameter values",
            Input.Validate.REQUIRED
    );

    private ParameterList paramList;
    private DPPointer pointers;
    private boolean pointersChanged;
    private int[] clusterCounts;
    private int[] storedClusterCounts;


    public void initAndValidate(){
        paramList = paramListInput.get();
        pointers = pointersInput.get();
        pointersChanged = true;
     
    }


    /** CalculationNode methods **/
    // smarter vesions to be computed
	@Override
	public void store() {

        storedClusterCounts = new int[clusterCounts.length];
        System.arraycopy(clusterCounts,0,storedClusterCounts,0,clusterCounts.length);
		super.store();
	}
	@Override
	public void restore() {

        int[] temp = clusterCounts;
        clusterCounts = storedClusterCounts;
        storedClusterCounts = temp;
        super.restore();
	}

    @Override
	public boolean requiresRecalculation() {
		return true;
	}

    /** Valuable implementation follows **/
	@Override
	public int getDimension() {
		return clusterCounts.length;
	}

    @Override
	public double getArrayValue() {

		return clusterCounts[0];
	}

    public double getArrayValue(int dim){
        return clusterCounts[dim];
    }

    public int[] getClusterCounts(){
        if(pointersChanged){
            clusterCounts = new int[paramList.getDimension()];
            for(int i = 0; i < pointers.getDimension();i++ ){
                clusterCounts[pointers.indexInList(i,paramList)]++;
            }
            pointersChanged = false;
        }
        return clusterCounts;
    }

}
