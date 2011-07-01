package beast.evolution.operators;

import beast.core.Operator;
import beast.core.Input;
import beast.core.Description;
import beast.core.parameter.*;
import beast.math.distributions.DirichletProcess;
import beast.math.distributions.ParametricDistribution;
import beast.util.Randomizer;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Chieh-Hsi Wu
 *
 */
@Description("Performs gibbs sampling when a dirichlet process prior is used.")
public class DirichletProcessPriorSampler extends Operator {
    //assignment
    public Input<List<DPPointer>> pointersInput = new Input<List<DPPointer>>(
            "pointers",
            "array which points a set of unique parameter values",
            new ArrayList<DPPointer>(),
            Input.Validate.REQUIRED
    );
    public Input<List<ParameterList>> xListsInput = new Input<List<ParameterList>>(
            "xList",
            "points at which the density is calculated",
            new ArrayList<ParameterList>(),
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

    /*public Input<Distribution> likelihoodInput = new Input<Distribution>(
            "likelihood",
            "The likelihood given the data",
            Input.Validate.REQUIRED
    );*/

    public Input<DPValuable> dpValuableInput = new Input<DPValuable>(
            "dpVal",
            "reports the counts in each cluster",
            Input.Validate.REQUIRED
    );

    private DirichletProcess dp;
    private int sampleSize;
    private List<ParametricDistribution> baseDistrs;
    private DPValuable dpVal;
    int dimPointer;
    public void initAndValidate(){
        dp = dpInput.get();
        sampleSize = sampleSizeInput.get();
        baseDistrs = dp.getBaseDistribution();
        dpVal = dpValuableInput.get();

        //check if the all pointers have the same dimension
        int dimPointer = pointersInput.get().get(0).getDimension();
        int pointerCount = pointersInput.get().size();
        for(int i=1; i < pointerCount;i++){
            int anotherDimPointer = pointersInput.get().get(i).getDimension();
            if(dimPointer != anotherDimPointer){
                throw new RuntimeException("All pointers must have the same dimension.");
            }

        }
        this.dimPointer = dimPointer;

    }

    public double proposal(){
        //Get the pointer and the list of unique values
        List<DPPointer> pointers = pointersInput.get();
        for(DPPointer pointer:pointers){
            pointer.getCurrentEditable(this);

        }
        List<ParameterList> paramLists = xListsInput.get();
        for(ParameterList paramList:paramLists){
            paramList.getCurrentEditable(this);

        }

        //Randomly pick an index to update, gets it's current value and its position in the parameter list

        int index = Randomizer.nextInt(dimPointer);

        RealParameter[] currVals = new RealParameter[paramLists.size()];
        int listIndex = pointers.get(0).indexInList(index,paramLists.get(0));
        currVals[0] = paramLists.get(0).getParameter(listIndex);
        for(int i = 1; i < currVals.length;i++){
            //System.err.println(pointers.get(i)+" "+paramLists.get(i));
            currVals[i] = paramLists.get(i).getParameter(pointers.get(i).indexInList(index,paramLists.get(i)));


        }

        //Distribution lik = likelihoodInput.get();
        //Count the number of items in each cluster but excluding the one about to be updated
        int[] clusterCounts = dpVal.getClusterCounts();
        clusterCounts[listIndex] =  clusterCounts[listIndex]-1;

        try{

            //Generate a sample of proposals
            //Get the dimension of the parameter
            //int[] dimValues = new int[paramLists.size()];
            QuietRealParameter[][] preliminaryProposals = new QuietRealParameter[paramLists.size()][];
            for(int iList = 0; iList < preliminaryProposals.length; iList++){
                preliminaryProposals[iList] = sampleFromBaseDistribution(iList,paramLists.get(iList).getParameterDimension());

            }


            int dimList = paramLists.get(0).getDimension();
            int i;
            double concVal =dp.getConcParameter();
            
            double[] fullConditional = new double[dimList+sampleSize];

            for(i = 0; i < dimList; i++){
                //n_{-index,i}/(n - 1 + alpha)
                fullConditional[i] = clusterCounts[i]/(dimPointer - 1 + concVal);
                //System.err.println("fullConditional[i]: "+fullConditional[i]+", val: "+paramList.getParameter(i));

            }
            for(; i < fullConditional.length; i++){
                //alpha/m/(n - 1 + alpha)
                fullConditional[i] = concVal/sampleSize/(dimPointer - 1 + concVal);
                //System.err.println("fullConditional[i]: "+fullConditional[i]+", val: "+i);
            }

            int proposedIndex = Randomizer.randomChoicePDF(fullConditional);
            //System.err.println("proposedIndex: "+proposedIndex);
            if(proposedIndex < dimList){
                //take up an existing value
                for(int iPointer = 0; iPointer < pointers.size(); iPointer++){
                    pointers.get(iPointer).point(index, paramLists.get(iPointer).getParameter(proposedIndex));
                }
            }else{
                //take up a new value
                for(int iPointer = 0; iPointer < pointers.size(); iPointer++){                    
                    pointers.get(iPointer).point(index, preliminaryProposals[iPointer][proposedIndex-dimList]);
                    paramLists.get(iPointer).addParameter(preliminaryProposals[iPointer][proposedIndex-dimList]);
                }


            }

            /*for(i = 0; i < clusterCounts.length;i++){
                System.err.println(paramList.getParameter(i).getValue()+", cluster counts: "+clusterCounts[i]);
            }*/

            //If any cluster has no member then it is removed.
            for(i = 0; i < clusterCounts.length;i++){
                if(clusterCounts[i] ==0){
                    for(int iList = 0; iList < paramLists.size(); iList++)
                        paramLists.get(iList).removeParameter(i);
                    break;
                }

            }

        }catch(Exception e){
                throw new RuntimeException(e);
            }
        return Double.POSITIVE_INFINITY;
    }

    public QuietRealParameter[] sampleFromBaseDistribution(int iDistr,int dimValue) throws Exception{
        QuietRealParameter[] preliminaryProposals = new QuietRealParameter[sampleSize];

        for(int i = 0; i < sampleSize; i++){

            Double[] sample = new Double[dimValue];
            for(int j = 0;j < dimValue;j++){

                sample[j] = baseDistrs.get(iDistr).inverseCumulativeProbability(Randomizer.nextDouble());
                //System.err.print(sample[j]+" ");
            }
            preliminaryProposals[i] = new QuietRealParameter(sample);

        }
        //System.err.println();
        return preliminaryProposals;

    }
}
