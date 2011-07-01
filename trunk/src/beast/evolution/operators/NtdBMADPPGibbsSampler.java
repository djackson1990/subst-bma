package beast.evolution.operators;

import beast.core.parameter.*;
import beast.core.Input;
import beast.core.Operator;
import beast.core.Description;
import beast.math.distributions.ParametricDistribution;
import beast.math.distributions.NtdDP;
import beast.util.Randomizer;
import beast.evolution.likelihood.TempTreeLikelihood;
import beast.evolution.likelihood.DPTreeLikelihood;

/**
 * @author Chieh-Hsi Wu
 */
@Description("Gibbs sampler with DPP for NtdBMA.")
public class NtdBMADPPGibbsSampler extends Operator {
    public Input<DPPointer> parameterPointersInput = new Input<DPPointer>(
            "parameterPointers",
            "array which points a set of unique parameter values",
            Input.Validate.REQUIRED
    );
    public Input<ParameterList> parameterListInput = new Input<ParameterList>(
            "parameterList",
            "points at which the density is calculated",
            Input.Validate.REQUIRED
    );

    public Input<DPPointer> modelPointersInput = new Input<DPPointer>(
            "modelPointers",
            "array which points a set of unique parameter values",
            Input.Validate.REQUIRED
    );
    public Input<ParameterList> modelListInput = new Input<ParameterList>(
            "modelList",
            "points at which the density is calculated",
            Input.Validate.REQUIRED
    );

    public Input<DPPointer> freqPointersInput = new Input<DPPointer>(
            "freqPointers",
            "array which points a set of unique parameter values",
            Input.Validate.REQUIRED
    );
    public Input<ParameterList> freqsListInput = new Input<ParameterList>(
            "freqsList",
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

    /*public Input<Distribution> likelihoodInput = new Input<Distribution>(
            "likelihood",
            "The likelihood given the data",
            Input.Validate.REQUIRED
    );*/

    public Input<TempTreeLikelihood> tempLikelihoodInput = new Input<TempTreeLikelihood>(
            "tempLikelihood",
            "The temporary likelihood given the data at site i",
            Input.Validate.REQUIRED
    );

    public Input<DPValuable> dpValuableInput = new Input<DPValuable>(
            "dpVal",
            "reports the counts in each cluster",
            Input.Validate.REQUIRED
    );

    public Input<DPTreeLikelihood> dpTreeLikelihoodInput = new Input<DPTreeLikelihood>(
            "dpTreeLik",
            "Tree likelihood that handle DPP",
            Input.Validate.REQUIRED
    );


    private NtdDP dp;
    private int sampleSize;
    private ParametricDistribution paramBaseDistr;
    private ParametricDistribution modelBaseDistr;
    private ParametricDistribution freqsBaseDistr;
    private DPValuable dpVal;
    private DPTreeLikelihood dpTreeLikelihood;
    public void initAndValidate(){
        dp = dpInput.get();

        sampleSize = sampleSizeInput.get();
        paramBaseDistr = dp.getParamBaseDistr();
        modelBaseDistr = dp.getModelBaseDistr();
        freqsBaseDistr = dp.getFreqBaseDistr();

        dpVal = dpValuableInput.get();
        dpTreeLikelihood = dpTreeLikelihoodInput.get();

    }


    public double proposal(){
        //Get the pointer and the list of unique values
        //DPPointer paramPointers = parameterPointersInput.get(this);
        //DPPointer freqPointers = freqPointersInput.get(this);
        //DPPointer modelPointers = modelPointersInput.get(this);
        DPPointer paramPointers = parameterPointersInput.get();
        DPPointer freqPointers = freqPointersInput.get();
        DPPointer modelPointers = modelPointersInput.get();

        ParameterList paramList = parameterListInput.get();
        ParameterList freqsList = freqsListInput.get();
        ParameterList modelList = modelListInput.get();


        //Randomly pick an index to update, gets it's current value and its position in the parameter list
        int dimPointer = paramPointers.getDimension();
        int index = Randomizer.nextInt(dimPointer);



        int listIndex = paramPointers.indexInList(index,paramList);
        RealParameter currParamVal = paramList.getParameter(listIndex);
        RealParameter currModelVal = modelList.getParameter(listIndex);
        RealParameter currFreqsVal = freqsList.getParameter(listIndex);




        TempTreeLikelihood tempLik = tempLikelihoodInput.get();

        //Get the dimension of the parameter
        int paramDimValue = paramList.getParameterDimension();
        int modelDimValue = modelList.getParameterDimension();
        int freqDimValue = freqsList.getParameterDimension();

        RealParameter curr = freqPointers.getParameter(index);

        //Count the number of items in each cluster but excluding the one about to be updated
        int[] clusterCounts = dpVal.getClusterCounts();
        clusterCounts[listIndex] =  clusterCounts[listIndex]-1;

        QuietRealParameter[] existingParamVals = new QuietRealParameter[clusterCounts.length];
        QuietRealParameter[] existingModelVals = new QuietRealParameter[clusterCounts.length];
        QuietRealParameter[] existingFreqsVals = new QuietRealParameter[clusterCounts.length];
        int[] existingCluster = new int[clusterCounts.length];

        int counter = 0;
        int zeroCount = -1;

        for(int i = 0; i < clusterCounts.length;i++){
            if(clusterCounts[i]>0){
                clusterCounts[counter] = clusterCounts[i];
                existingParamVals[counter] = paramList.getParameter(i);
                existingModelVals[counter] = modelList.getParameter(i);
                existingFreqsVals[counter] = freqsList.getParameter(i);
                existingCluster[counter] = i;
                counter++;

            }else{
                zeroCount = i;
            }
        }

        try{

            //Generate a sample of proposals
            QuietRealParameter[] paramPreProposals = getSamples(paramBaseDistr,currParamVal);
            QuietRealParameter[] modelPreProposals = getSamples(modelBaseDistr,currModelVal);
            QuietRealParameter[] freqsPreProposals = getSamples(freqsBaseDistr,currFreqsVal);

            //System.err.println("zero count: "+zeroCount);
            //If the a singleton has been picked
            if(zeroCount > -1){
                paramPreProposals[0] = paramList.getParameter(zeroCount);
                modelPreProposals[0] = modelList.getParameter(zeroCount);
                freqsPreProposals[0] = freqsList.getParameter(zeroCount);

            }
            //int dimList = paramList.getDimension();
            int i;
            double concVal =dp.getConcParameter();

            double[] logFullCond = new double[counter+sampleSize];

            for(i = 0; i < counter; i++){

                logFullCond[i] = Math.log(clusterCounts[i]/(dimPointer - 1 + concVal));
                //paramPointers.point(index, existingParamVals[i]);
                //modelPointers.point(index, existingModelVals[i]);
                //freqPointers.point(index, existingFreqsVals[i]);
                //logFullCond[i] = logFullCond[i]+lik.calculateLogP();
                //System.out.println("here?");

                /*logFullCond[i] = logFullCond[i]+
                        tempLik.calculateLogP(
                                paramPointers.getParameter(i),
                                modelPointers.getParameter(i),
                                freqPointers.getParameter(i),
                                index
                        );*/

                /*double temp1= tempLik.calculateLogP(
                                existingParamVals[i],
                                existingModelVals[i],
                                existingFreqsVals[i],
                                index);
                double temp2= dpTreeLikelihood.getSiteLogLikelihood(existingCluster[i],index);
                if(temp1 != temp2){
                    //System.err.println(temp1+" "+temp2);
                    System.err.println(paramPointers.getParameter(i));
                    System.err.println(modelPointers.getParameter(i));
                    System.err.println(freqPointers.getParameter(i));
                    throw new RuntimeException(temp1+" "+temp2);
                } */
                logFullCond[i] = logFullCond[i]+ dpTreeLikelihood.getSiteLogLikelihood(existingCluster[i],index);


            }

            if(zeroCount > -1){
                /*double temp1 = tempLik.calculateLogP(
                                paramPreProposals[i-counter],
                                modelPreProposals[i-counter],
                                freqsPreProposals[i-counter],
                                index
                        );
                double temp2 = logFullCond[i]+ dpTreeLikelihood.getSiteLogLikelihood(zeroCount,index);
                if(temp1 != temp2){
                    System.err.println(paramPreProposals[i-counter]+" "+paramList.getParameter(zeroCount));
                    throw new RuntimeException(temp1+" "+temp2);
                }*/
                logFullCond[i] = Math.log(concVal/sampleSize/(dimPointer - 1 + concVal));
                logFullCond[i] = logFullCond[i]+ dpTreeLikelihood.getSiteLogLikelihood(zeroCount,index);
                i++;
            }
            for(; i < logFullCond.length; i++){

                logFullCond[i] = Math.log(concVal/sampleSize/(dimPointer - 1 + concVal));

                //paramPointers.point(index, paramPreProposals[i-counter]);
                //modelPointers.point(index, modelPreProposals[i-counter]);
                //freqPointers.point(index, freqPreProposals[i-counter]);
                //logFullCond[i] = logFullCond[i]+(lik).calculateLogP();

                logFullCond[i] = logFullCond[i]+
                        tempLik.calculateLogP(
                                paramPreProposals[i-counter],
                                modelPreProposals[i-counter],
                                freqsPreProposals[i-counter],
                                index
                        );

            }

            double smallestVal = logFullCond[0];
            //double smallestIndex = 0;
            for(i = 1; i < logFullCond.length;i++){
               // if(smallestVal == Double.NEGATIVE_INFINITY ||
               //         logFullCond[i] < smallestVal & logFullCond[i]!=Double.NEGATIVE_INFINITY){
                if(smallestVal > logFullCond[i]){
                    smallestVal = logFullCond[i];
                    //smallestIndex = i;
                }
                //}
            }
            //System.err.println("smallestVal2: "+smallestVal+" "+smallestIndex+" "+logFullCond.length);

            double[] fullConditional = new double[logFullCond.length];
            for(i = 0; i < fullConditional.length;i++){
                fullConditional[i] = Math.exp(logFullCond[i]-smallestVal);
                //System.err.println("fullConditional[i]: "+fullConditional[i]);
            }


            int proposedIndex = Randomizer.randomChoicePDF(fullConditional);
            //System.err.println("proposedIndex: "+proposedIndex);
            QuietRealParameter paramProposal;
            QuietRealParameter modelProposal;
            QuietRealParameter freqsProposal;

            if(proposedIndex < counter){
                paramProposal = existingParamVals[proposedIndex];
                modelProposal = existingModelVals[proposedIndex];
                freqsProposal = existingFreqsVals[proposedIndex];
            }else{
                paramProposal = paramPreProposals[proposedIndex-counter];
                modelProposal = modelPreProposals[proposedIndex-counter];
                freqsProposal = freqsPreProposals[proposedIndex-counter];

            }

            if(curr != freqsProposal){//Takes a different value from the current

                if(proposedIndex >= counter && zeroCount > -1){//Singleton takes new value
                    int paramListIndex = paramPointers.indexInList(index,paramList);
                    //System.err.println("paramListIndex: "+paramListIndex);
                    for(i = 0; i < paramDimValue;i++){
                        paramList.setValue(paramListIndex,i,paramProposal.getValue(i));
                    }

                    modelList.setValue(paramListIndex,0,modelProposal.getValue());

                    for(i = 0; i < freqDimValue; i++){
                        freqsList.setValue(paramListIndex,i,freqsProposal.getValue(i));
                    }

                    zeroCount = -1;

                }else{
                    //Singleton takes existing value or
                    //non-singleton takes new or existing value
                    paramPointers = parameterPointersInput.get(this);
                    freqPointers = freqPointersInput.get(this);
                    modelPointers = modelPointersInput.get(this);
                    paramPointers.point(index, paramProposal);
                    modelPointers.point(index, modelProposal);
                    freqPointers.point(index, freqsProposal);

                    //Non singleton takes new value
                    if(proposedIndex >= counter){
                        paramList = parameterListInput.get(this);
                        modelList = modelListInput.get(this);
                        freqsList = freqsListInput.get(this);
                        paramList.addParameter(paramProposal);
                        modelList.addParameter(modelProposal);
                        freqsList.addParameter(freqsProposal);
                    }

                }

                //If any cluster has no member then it is removed.
                if(zeroCount > -1){
                    paramList = parameterListInput.get(this);
                    modelList = modelListInput.get(this);
                    freqsList = freqsListInput.get(this);
                    paramList.removeParameter(zeroCount);
                    modelList.removeParameter(zeroCount);
                    freqsList.removeParameter(zeroCount);

                }

            }

            /*for(i = 0; i < clusterCounts.length;i++){
                System.err.println(paramList.getParameter(i).getValue()+", cluster counts: "+clusterCounts[i]);
            }*/



            //System.err.println(counter);

        }catch(Exception e){
                throw new RuntimeException(e);
            }
        return Double.POSITIVE_INFINITY;
    }


    public QuietRealParameter[] getSamples(ParametricDistribution distr, RealParameter example) throws Exception{
        QuietRealParameter[] samples = new QuietRealParameter[sampleSize];
        Double[][] sampleVals = distr.sample(sampleSize);
        for(int i = 0; i < samples.length;i++){
            samples[i] = new QuietRealParameter(sampleVals[i]);
            samples[i].setUpper(example.getUpper());
            samples[i].setLower(example.getLower());
        }
        return samples;
    }

 
}