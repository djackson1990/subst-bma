package beast.evolution.operators;

import beast.core.Operator;
import beast.core.Input;
import beast.core.Description;
import beast.core.parameter.ParameterList;
import beast.core.parameter.RealParameter2;
import beast.util.Randomizer;
import beast.math.distributions.ParametricDistribution;
import beast.math.distributions.MultivariateDistribution;
import beast.math.distributions.Normal;

/**
 * @author Chieh-Hsi Wu
 */
@Description("Adds or removed parameters to a list. It is purely for testing purpose.")
public class AddRemoveOperator extends Operator {
    public Input<ParameterList> parameterListInput =
                    new Input<ParameterList>("parameters", "A list of parameters.", Input.Validate.REQUIRED);
    public Input<ParametricDistribution> distInput =
            new Input<ParametricDistribution>("distribution","The distribution from which an added parameter is to be sampled from", Input.Validate.REQUIRED);
    public Input<Integer> maxInput = new Input<Integer>("max","The maximum number of elements in a list.");
    private int maxElement;
    public void initAndValidate() {
        maxElement = maxInput.get();
    }
    //private int count = 0;
    public AddRemoveOperator(){
        System.err.println("This operator is purely for testing purpose. It cannot be used for real MCMC analysis");
    }

    public double proposal(){
        //System.err.println("propose: "+ ++count);
        ParameterList paramList = parameterListInput.get(this);
        ParametricDistribution dist = distInput.get();

        boolean addParameter = Randomizer.nextBoolean();
        if(paramList.getDimension()>=maxElement){
            addParameter = false;
        }else if(paramList.getDimension()<=1){
            addParameter = true;
        }
        if(addParameter){
            int valueDim = paramList.getParameterDimension();
            Double[] vals = new Double[valueDim];
            try{
                if(dist instanceof MultivariateDistribution){
                    double[] tempVals = ((MultivariateDistribution)dist).sample();
                    for(int i = 0; i < valueDim; i++){
                        vals[i] = tempVals[i];
                    }

                }else{
                    for(int i = 0; i < valueDim; i++){
                        vals[i] = dist.inverseCumulativeProbability(Randomizer.nextDouble());
                    }

                }
                RealParameter2 param = new RealParameter2(vals);
                paramList.addParameter(param);
            }catch(Exception e){
                throw new RuntimeException(e);

            }


        }else{

            int dim = Randomizer.nextInt(paramList.getDimension());
            paramList.removeParameter(dim);

        }
        //This hastings ratio is entirely WRONG!
        return Double.POSITIVE_INFINITY;
    }

    public static void main(String[] args){
        Normal normalDist = new Normal();
        try{
            normalDist.initByName("mean", "0", "sigma", "3");
            System.err.println(normalDist.inverseCumulativeProbability(0.5));
            System.err.println(normalDist.inverseCumulativeProbability(0.975));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }




}
