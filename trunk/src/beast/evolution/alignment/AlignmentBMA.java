package beast.evolution.alignment;

import beast.core.parameter.IntegerParameter;
import beast.core.Input;

/**
 * @author Chieh-Hsi Wu
 */
public class AlignmentBMA extends Alignment{
    public Input<IntegerParameter> partitionIndices =
            new Input<IntegerParameter>("partitionIndices", "The indiced of partitions in which the sites are allocated",Input.Validate.REQUIRED);

    private int[][] weightMatrix;
    private int[] partitionWeight;

    public void initAndValidate() throws Exception {
        super.initAndValidate();
        computeWeightsInPartitions();
    }


    public void computeWeightsInPartitions(){
        IntegerParameter partitionIndices = this.partitionIndices.get();
        int patternCount = getPatternCount();
        int siteCount = getSiteCount();
        weightMatrix = new int[siteCount][patternCount];
        partitionWeight = new int[siteCount];
        for(int i = 0; i < siteCount;i++){
            int partitionIndex = partitionIndices.getValue(i);
            weightMatrix[partitionIndex][m_nPatternIndex[i]]++;
            partitionWeight[partitionIndex]++;
        }
    }

    /** CalculationNode implementation **/
    @Override
    protected boolean requiresRecalculation() {
        boolean recalculates = false;
        if(partitionIndices.get().somethingIsDirty()){
            recalculates = true;
            computeWeightsInPartitions();
        }

    	return recalculates;
    }

    public void restore(){
        computeWeightsInPartitions();
        super.restore();
    }

    public int[] getPartitionWeight(){
        return partitionWeight;
    }

    public int[][] getWeightMatrix(){
        return weightMatrix;
    }

}
