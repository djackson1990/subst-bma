package beast.evolution.operators;

import beast.core.Operator;
import beast.core.Input;
import beast.core.Distribution;
import beast.core.Description;
import beast.core.parameter.IntegerParameter;
import beast.core.parameter.ParameterList;
import beast.core.parameter.RealParameter2;
import beast.math.distributions.DirichletProcess;
import beast.math.distributions.ParametricDistribution;
import beast.math.distributions.MultivariateDistribution;
import beast.util.Randomizer;

/**
 * @author Chieh-Hsi Wu
 *
 */
@Description("Performs gibbs sampling when a dirichlet process prior is used.")
public class DirichletProcessPriorGibbsSampler extends Operator {
    public Input<IntegerParameter> assignmentInput = new Input<IntegerParameter>(
            "assignment",
            "a parameter which species the assignment of elements to clusters",
            Input.Validate.REQUIRED
    );
    public Input<ParameterList> xListInput = new Input<ParameterList>(
            "xList",
            "points at which the density is calculated",
            Input.Validate.REQUIRED
    );
    public Input<DirichletProcess> dpInput = new Input<DirichletProcess>(
            "dirichletProcess",
            "An object of Dirichlet Process",
            Input.Validate.REQUIRED
    );
    public Input<Integer> sampleSizeInput = new Input<Integer>(
            "sampleSize",
            "The number of prelimiary proposals",
            Input.Validate.REQUIRED
    );

    public Input<Distribution> likelihoodInput = new Input<Distribution>(
            "likelihood",
            "The likelihood given the data",
            Input.Validate.REQUIRED
    );

    private DirichletProcess dp;
    private int sampleSize;
    public void initAndValidate(){
        dp = dpInput.get();
        sampleSize = sampleSizeInput.get();

    }

    public double proposal(){
        IntegerParameter assignment = assignmentInput.get(this);
        int aIndex = Randomizer.nextInt(assignment.getDimension());
        ParameterList paramList = xListInput.get(this);
        ParametricDistribution baseDistr = dp.getBaseDistribution();
        RealParameter2[] preliminaryProposals = new RealParameter2[sampleSize];
        int dimValue = paramList.getParameterDimension();
        try{
        for(int i = 0; i < sampleSize; i++){
            Double[] sample = new Double[dimValue];
            if(baseDistr instanceof MultivariateDistribution){

                double[] temp = ((MultivariateDistribution)baseDistr).sample();
                sample = new Double[temp.length];
                for(int j = 0; j < temp.length;j++){
                    sample[j] = temp[j];
                }


            }else{
                for(int j = 0;j < dimValue;j++){
                    sample[j] = baseDistr.inverseCumulativeProbability(Randomizer.nextDouble());
                }
            }

                preliminaryProposals[i] = new RealParameter2(sample);

        }




        }catch(Exception e){
                throw new RuntimeException(e);
            }
        return Double.POSITIVE_INFINITY;
    }
}
