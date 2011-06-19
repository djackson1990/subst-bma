package beast.evolution.operators;

import beast.core.parameter.*;
import beast.core.Input;
import beast.core.Distribution;
import beast.core.Operator;
import beast.math.distributions.DirichletProcess;
import beast.math.distributions.ParametricDistribution;
import beast.math.distributions.NtdDP;
import beast.util.Randomizer;

/**
 * @author Chieh-Hsi Wu
 */
public class NtdBMADPPGibbsSampler extends Operator {
    public Input<DPPointer> parameterPointersInput = new Input<DPPointer>(
            "pointers",
            "array which points a set of unique parameter values",
            Input.Validate.REQUIRED
    );
    public Input<ParameterList> parameterListInput = new Input<ParameterList>(
            "xList",
            "points at which the density is calculated",
            Input.Validate.REQUIRED
    );

    public Input<DPPointer> modelPointersInput = new Input<DPPointer>(
            "pointers",
            "array which points a set of unique parameter values",
            Input.Validate.REQUIRED
    );
    public Input<ParameterList> modelListInput = new Input<ParameterList>(
            "xList",
            "points at which the density is calculated",
            Input.Validate.REQUIRED
    );

    public Input<DPPointer> freqPointersInput = new Input<DPPointer>(
            "pointers",
            "array which points a set of unique parameter values",
            Input.Validate.REQUIRED
    );
    public Input<ParameterList> freqListInput = new Input<ParameterList>(
            "xList",
            "points at which the density is calculated",
            Input.Validate.REQUIRED
    );    


    public Input<NtdDP> dpInput = new Input<NtdDP>(
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

    private NtdDP dp;
    private int sampleSize;
    private ParametricDistribution paramBaseDistr;
    private ParametricDistribution modelBaseDistr;
    private ParametricDistribution freqBaseDistr;
    private DPValuable dpVal;
    public void initAndValidate(){
        dp = dpInput.get();

        sampleSize = sampleSizeInput.get();
        paramBaseDistr = dp.getParamBaseDistr();
        modelBaseDistr = dp.getModelBaseDistr();
        freqBaseDistr = dp.getFreqBaseDistr();

        dpVal = dpValuableInput.get();

    }


    public double proposal(){
     

        //Get the pointer and the list of unique values
        DPPointer paramPointers = parameterPointersInput.get(this);
        DPPointer freqPointers = freqPointersInput.get(this);
        DPPointer modelPointers = modelPointersInput.get(this);

        ParameterList paramList = parameterListInput.get();
        ParameterList freqList = freqListInput.get();
        ParameterList modelList = modelListInput.get();


        //Randomly pick an index to update, gets it's current value and its position in the parameter list
        int dimPointer = paramPointers.getDimension();


        int index = Randomizer.nextInt(dimPointer);
        //System.err.println("index: "+index+" "+pointers.getParameterValue(0)+" "+pointers.getParameterValue(1));
        RealParameter currParamVal = paramList.getParameter(paramPointers.indexInList(index,paramList));
        RealParameter currModelVal = paramList.getParameter(paramPointers.indexInList(index,paramList));
        RealParameter currFreqVal = paramList.getParameter(paramPointers.indexInList(index,paramList));

        int listIndex = paramList.indexOf(currParamVal);


        Distribution lik = likelihoodInput.get();

        //Get the dimension of the parameter
        int dimValue = paramList.getParameterDimension();

        //Count the number of items in each cluster but excluding the one about to be updated
        int[] clusterCounts = dpVal.getClusterCounts();
        clusterCounts[listIndex] =  clusterCounts[listIndex]-1;

        RealParameter[] existingParamVals = new RealParameter[clusterCounts.length];
        RealParameter[] existingModelVals = new RealParameter[clusterCounts.length];
        RealParameter[] existingFreqVals = new RealParameter[clusterCounts.length];
        int counter = 0;
        int zeroCount = -1;
        for(int i = 0; i < clusterCounts.length;i++){
            if(clusterCounts[i]>0){
                clusterCounts[counter] = clusterCounts[i];
                existingParamVals[counter] = paramList.getParameter(i);
                existingModelVals[counter] = modelList.getParameter(i);
                existingFreqVals[counter] = freqList.getParameter(i);
                counter++;
                //System.err.println(i+", cluster.counts: "+clusterCounts[i]);
            }else{
                zeroCount = i;
            }
        }
        //System.err.println("paramList size1: "+paramList.getDimension());
        try{

            //Generate a sample of proposals
            RealParameter[] paramPreProposals = paramBaseDistr.sample(sampleSize);
            RealParameter[] modelPreProposals = modelBaseDistr.sample(sampleSize);
            RealParameter[] freqPreProposals = freqBaseDistr.sample(sampleSize);

            //System.err.println("zero count: "+zeroCount);
            //If the a singleton has been picked
            if(zeroCount > -1){
                paramPreProposals[0] = paramList.getParameter(zeroCount);
                modelPreProposals[0] = modelList.getParameter(zeroCount);
                freqPreProposals[0] = freqList.getParameter(zeroCount);

            }
            //int dimList = paramList.getDimension();
            int i;
            double concVal =dp.getConcParameter();

            double[] logFullCond = new double[counter+sampleSize];
            //System.err.println("pointers: "+pointers);
            //System.err.println("counter: "+counter);
            //System.err.println();
            for(i = 0; i < counter; i++){
                //n_{-index,i}/(n - 1 + alpha)
                logFullCond[i] = Math.log(clusterCounts[i]/(dimPointer - 1 + concVal));
                paramPointers.point(index, existingParamVals[i]);
                modelPointers.point(index, existingModelVals[i]);
                freqPointers.point(index, existingFreqVals[i]);
                //System.err.println("clusterCounts[i]: "+clusterCounts[i]);
                //System.err.println("lgc and lik: "+logFullCond[i]+" "+lik.calculateLogP());
                logFullCond[i] = logFullCond[i]+lik.calculateLogP();
                //System.err.println(logFullCond[i]);
                //System.err.println("fullConditional[i]: "+fullConditional[i]+", val: "+paramList.getParameter(i));
                //System.err.println("logFullCond[i]: "+logFullCond[i]+", val: "+existingVals[i]);
            }
            for(; i < logFullCond.length; i++){
                //alpha/m/(n - 1 + alpha)
                logFullCond[i] = Math.log(concVal/sampleSize/(dimPointer - 1 + concVal));
                //System.err.println("lgc and lik: "+logFullCond[i]+" "+lik.calculateLogP());
                paramPointers.point(index, paramPreProposals[i-counter]);
                paramPointers.point(index, modelPreProposals[i-counter]);
                paramPointers.point(index, freqPreProposals[i-counter]);
                logFullCond[i] = logFullCond[i]+(lik).calculateLogP();
                //System.err.println("lgc and lik: "+logFullCond[i]+" "+lik.calculateLogP());
                //System.err.println("logFullCond[i]: "+logFullCond[i]+", val: "+preliminaryProposals[i-counter]);
            }

            double smallestVal = logFullCond[0];
            for(i = 1; i < logFullCond.length;i++){
               // if(smallestVal == Double.NEGATIVE_INFINITY ||
               //         logFullCond[i] < smallestVal & logFullCond[i]!=Double.NEGATIVE_INFINITY){
                if(smallestVal > logFullCond[i])
                    smallestVal = logFullCond[i];
                //}
            }
            //System.err.println("smallestVal2: "+smallestVal);

            double[] fullConditional = new double[logFullCond.length];
            for(i = 0; i < fullConditional.length;i++){
                //if(logFullCond[i] == Double.NEGATIVE_INFINITY){
                //    fullConditional[i] = 0.0;
                //}else{
                //if(this.counter>3240)
                //    System.err.println("logfc: "+logFullCond[i]+" "+i);
                    fullConditional[i] = Math.exp(logFullCond[i]-smallestVal);
                //}
                //System.err.println("fc: "+fullConditional[i]);
            }


            int proposedIndex = Randomizer.randomChoicePDF(fullConditional);
            //System.err.println("proposedIndex: "+proposedIndex);
            if(proposedIndex < counter){
                //take up an existing value
                paramPointers.point(index, existingParamVals[proposedIndex]);
                modelPointers.point(index, existingModelVals[proposedIndex]);
                freqPointers.point(index, existingFreqVals[proposedIndex]);
                //if(this.counter>6000)
                //    System.err.println("proposedIndex: "+proposedIndex+"; existingVal: "+existingVals[proposedIndex]);
            }else{
                RealParameter paramProposal = paramPreProposals[proposedIndex-counter];
                RealParameter modelProposal = modelPreProposals[proposedIndex-counter];
                RealParameter freqProposal = freqPreProposals[proposedIndex-counter];
                if(zeroCount > -1){
                    int paramListIndex = paramPointers.indexInList(index,paramList);

                    for(i = 0; i < dimValue;i++){
                        paramList.setValue(paramListIndex,i,paramProposal.getValue(i));
                        modelList.setValue(paramListIndex,i,modelProposal.getValue(i));
                        freqList.setValue(paramListIndex,i,freqProposal.getValue(i));


                    }
                    zeroCount = -1;

                }else{
                //take up a new value
                    paramPointers.point(index, paramPreProposals[proposedIndex-counter]);
                    modelPointers.point(index, modelPreProposals[proposedIndex-counter]);
                    freqPointers.point(index, freqPreProposals[proposedIndex-counter]);

                    paramList = parameterListInput.get(this);
                    modelList = modelListInput.get(this);
                    freqList = freqListInput.get(this);
                    paramList.addParameter(paramPreProposals[proposedIndex-counter]);
                    modelList.addParameter(modelPreProposals[proposedIndex-counter]);
                    freqList.addParameter(freqPreProposals[proposedIndex-counter]);
                }
                //System.err.println("paramList size2: "+paramList.getDimension());
                //if(this.counter>6000)
                //    System.err.println("proposedIndex: "+proposedIndex+"; newVal: "+preliminaryProposals[proposedIndex-counter]);
            }

            /*for(i = 0; i < clusterCounts.length;i++){
                System.err.println(paramList.getParameter(i).getValue()+", cluster counts: "+clusterCounts[i]);
            }*/

            //If any cluster has no member then it is removed.
            if(zeroCount > -1){
                paramList = parameterListInput.get(this);
                paramList.removeParameter(zeroCount);
                modelList.removeParameter(zeroCount);
                freqList.removeParameter(zeroCount);

            }

            //System.err.println(counter);

        }catch(Exception e){
                throw new RuntimeException(e);
            }
        return Double.POSITIVE_INFINITY;
    }
}