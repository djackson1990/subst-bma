package beast.evolution.operators;

import beast.core.Operator;
import beast.core.Input;
import beast.core.Distribution;
import beast.core.Description;
import beast.core.parameter.*;
import beast.math.distributions.DirichletProcess;
import beast.math.distributions.ParametricDistribution;
import beast.util.Randomizer;

import java.util.ArrayList;

/**
 * @author Chieh-Hsi Wu
 */
@Description("Gibbs sampler with a Dirichlet process prior.")
public class DirichletProcessPriorGibbsSampler extends Operator {
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

    public Input<DPValuable> dpValuableInput = new Input<DPValuable>(
            "dpVal",
            "reports the counts in each cluster",
            Input.Validate.REQUIRED
    );

    private DirichletProcess dp;
    private int sampleSize;
    private ParametricDistribution baseDistr;
    private DPValuable dpVal;
    public void initAndValidate(){
        dp = dpInput.get();
        sampleSize = sampleSizeInput.get();
        baseDistr = dp.getBaseDistribution();
        dpVal = dpValuableInput.get();

    }

    public double proposal(){
        //Get the pointer and the list of unique values
        DPPointer pointers = pointersInput.get(this);
        ParameterList paramList = xListInput.get(this);

        //Randomly pick an index to update, gets it's current value and its position in the parameter list
        int dimPointer = pointers.getDimension();
        int index = Randomizer.nextInt(dimPointer);
        //System.err.println("index: "+index);
        RealParameter2 currVal = paramList.getParameter(pointers.indexInList(index,paramList));
        int listIndex = paramList.indexOf(currVal);


        Distribution lik = likelihoodInput.get();

        //Get the dimension of the parameter
        int dimValue = paramList.getParameterDimension();

        //Count the number of items in each cluster but excluding the one about to be updated
        int[] clusterCounts = dpVal.getClusterCounts();
        clusterCounts[listIndex] =  clusterCounts[listIndex]-1;

        RealParameter2[] existingVals = new RealParameter2[clusterCounts.length];
        int counter = 0;
        int zeroCount = -1;
        for(int i = 0; i < clusterCounts.length;i++){
            if(clusterCounts[i]>0){
                clusterCounts[counter] = clusterCounts[i];
                existingVals[counter++] = paramList.getParameter(i);
                //System.err.println(i+", cluster.counts: "+clusterCounts[i]);


            }else{
                zeroCount = i;

            }
        }

        try{

            //Generate a sample of proposals
            RealParameter2[] preliminaryProposals = sampleFromBaseDistribution(dimValue);
            int dimList = paramList.getDimension();
            int i;
            double concVal =dp.getConcParameter();

            double[] logFullCond = new double[counter+sampleSize];
            //System.err.println("pointers: "+pointers);
            //System.err.println("counter: "+counter);
            //System.err.println();
            for(i = 0; i < counter; i++){
                //n_{-index,i}/(n - 1 + alpha)
                logFullCond[i] = Math.log(clusterCounts[i]/(dimPointer - 1 + concVal));
                pointers.point(index, existingVals[i]);
                //System.err.println("clusterCounts[i]: "+clusterCounts[i]);
                //System.err.println("lgc and lik: "+logFullCond[i]+" "+lik.calculateLogP());
                logFullCond[i] = logFullCond[i]+lik.calculateLogP();
                //System.err.println(logFullCond[i]);
                //System.err.println("fullConditional[i]: "+fullConditional[i]+", val: "+paramList.getParameter(i));

            }
            for(; i < logFullCond.length; i++){
                //alpha/m/(n - 1 + alpha)
                logFullCond[i] = Math.log(concVal/sampleSize/(dimPointer - 1 + concVal));
                //System.err.println("lgc and lik: "+logFullCond[i]+" "+lik.calculateLogP());
                pointers.point(index, preliminaryProposals[i-counter]);
                logFullCond[i] = logFullCond[i]+lik.calculateLogP();
                //System.err.println("lgc and lik: "+logFullCond[i]+" "+lik.calculateLogP());
                //System.err.println("fullConditional[i]: "+fullConditional[i]+", val: "+i);
            }

            double smallestVal = logFullCond[0];
            for(i = 1; i < logFullCond.length;i++){
               // if(smallestVal == Double.NEGATIVE_INFINITY ||
               //         logFullCond[i] < smallestVal & logFullCond[i]!=Double.NEGATIVE_INFINITY){
                    smallestVal = logFullCond[i];
                //}
            }
            //System.err.println("smallestVal2: "+smallestVal);

            double[] fullConditional = new double[logFullCond.length];
            for(i = 0; i < fullConditional.length;i++){
                //if(logFullCond[i] == Double.NEGATIVE_INFINITY){
                //    fullConditional[i] = 0.0;
                //}else{
                    fullConditional[i] = Math.exp(logFullCond[i]-smallestVal);
                //}
                //System.err.println("fc: "+fullConditional[i]);
            }


            int proposedIndex = Randomizer.randomChoicePDF(fullConditional);
            //System.err.println("proposedIndex: "+proposedIndex);
            if(proposedIndex < counter){
                //take up an existing value
                pointers.point(index, existingVals[proposedIndex]);
                //System.err.println("proposedIndex: "+proposedIndex+"; existingVal: "+existingVals[proposedIndex]);
            }else{
                //take up a new value
                pointers.point(index, preliminaryProposals[proposedIndex-counter]);
                paramList.addParameter(preliminaryProposals[proposedIndex-counter]);
                //System.err.println("proposedIndex: "+proposedIndex+"; newVal: "+preliminaryProposals[proposedIndex-counter]);
            }

            /*for(i = 0; i < clusterCounts.length;i++){
                System.err.println(paramList.getParameter(i).getValue()+", cluster counts: "+clusterCounts[i]);
            }*/

            //If any cluster has no member then it is removed.
            if(zeroCount > -1){
               paramList.removeParameter(zeroCount);

            }

        }catch(Exception e){
                throw new RuntimeException(e);
            }
        return Double.POSITIVE_INFINITY;
    }

    public RealParameter2[] sampleFromBaseDistribution(int dimValue) throws Exception{
        RealParameter2[] preliminaryProposals = new RealParameter2[sampleSize];

        for(int i = 0; i < sampleSize; i++){

            Double[] sample = new Double[dimValue];
            for(int j = 0;j < dimValue;j++){

                sample[j] = baseDistr.inverseCumulativeProbability(Randomizer.nextDouble());
                //System.err.print(sample[j]+" ");
            }
            preliminaryProposals[i] = new RealParameter2(sample);

        }
        //System.err.println();
        return preliminaryProposals;

    }
}
