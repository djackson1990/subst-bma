package beast.evolution.substitutionmodel;

import beast.evolution.datatype.DataType;
import beast.evolution.datatype.Nucleotide;
import beast.evolution.tree.Node;

/**
 * @author Chieh-Hsi Wu
 */
public class JC69 extends SubstitutionModel.Base{

    int STATE_COUNT = 4;
    @Override

    public void initAndValidate(){}
	public boolean canHandleDataType(DataType dataType) throws Exception {
		if (dataType instanceof Nucleotide) {
			return true;
		}
		throw new Exception("Can only handle nucleotide data");
	}

    /**
     * This function returns the Eigen vectors.
     *
     * @return the array
     */
    @Override
    public EigenDecomposition getEigenDecomposition(Node node) {
        return null;//Don't need no eigen decimposition for JC
    }

    public void getTransitionProbabilities(
            Node node,
            double fStartTime,
            double fEndTime,
            double fRate,
            double[] matrix) {

        int k = 0;
        for(int i = 0; i < STATE_COUNT;i++){
            for(int j = 0; j < STATE_COUNT;j++){
                if(i==j){
                    matrix[k++] = (1.0+3.0*Math.exp(-4.0/3.0*(fEndTime-fStartTime)*fRate))/4.0;
                }else{
                    matrix[k++] = (1.0-Math.exp(-4.0/3.0*(fEndTime-fStartTime)*fRate))/4.0;
                }

            }
        }
    }
}