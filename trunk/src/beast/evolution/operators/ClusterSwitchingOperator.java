package beast.evolution.operators;

import beast.core.parameter.DPPointer;
import beast.core.parameter.ParameterList;
import beast.core.Input;
import beast.core.Operator;
import beast.util.Randomizer;

/**
 * @author Chieh-Hsi Wu
 * 
 */
public class ClusterSwitchingOperator extends Operator {
    public Input<DPPointer> pointersInput = new Input<DPPointer>(
            "pointers",
            "array which points a set of unique parameter values",
            Input.Validate.REQUIRED
    );
    public Input<ParameterList> xListInput = new Input<ParameterList>(
            "xList",
            "points at which the density is calculated",
            Input.Validate.REQUIRED
    );

    public void initAndValidate(){}

    public double proposal(){
        DPPointer pointers = pointersInput.get(this);
        ParameterList paramList = xListInput.get(this);
        int index = Randomizer.nextInt(pointers.getDimension());
        int currIndex = pointers.indexInList(index,paramList);
        int dimList = paramList.getDimension();
        int proposalIndex = Randomizer.nextInt(dimList-1);
        if(proposalIndex>=currIndex){
            proposalIndex = proposalIndex +1;
        }
        pointers.point(index,paramList.getParameter(proposalIndex));
        return 0.0;
    }

}
