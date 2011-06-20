package beast.math.distributions;

import beast.core.Input;
import beast.core.parameter.ParameterList;

/**
 * @author Chieh-Hsi Wu
 */
public class NtdDP extends DirichletProcess{
    public Input<ParametricDistribution> paramBaseDistrInput = new Input<ParametricDistribution>(
            "paramBaseDistr",
            "The base distribution of the dirichlet process",
            Input.Validate.REQUIRED
    );
    public Input<ParametricDistribution> modelBaseDistrInput = new Input<ParametricDistribution>(
            "modelBaseDistr",
            "The base distribution of the dirichlet process",
            Input.Validate.REQUIRED
    );
    public Input<ParametricDistribution> freqBaseDistrInput = new Input<ParametricDistribution>(
            "freqBaseDistr",
            "The base distribution of the dirichlet process",
            Input.Validate.REQUIRED
    );


    private ParametricDistribution paramBaseDistr;
    private ParametricDistribution modelBaseDistr;
    private ParametricDistribution freqBaseDistr;

    public NtdDP(){
        baseDistrInput.setRule(Input.Validate.OPTIONAL);
    }
    public void initAndValidate(){
        paramBaseDistr = paramBaseDistrInput.get();
        modelBaseDistr = modelBaseDistrInput.get();
        freqBaseDistr = freqBaseDistrInput.get();

        alpha = alphaInput.get();
        dpValuable = dpValuableInput.get();
        initialise();

    }

    public double calcLogP(
            ParameterList paramList,
            ParameterList modelList,
            ParameterList freqList) throws Exception {
        if(requiresRecalculation()){
            refresh();
        }
        int dimParam = paramList.getDimension();
        double logP = 0.0;
        for(int i = 0; i < dimParam; i ++){
            logP += paramBaseDistr.calcLogP(paramList.getParameter(i));
        }
        //System.err.println("flag1: "+logP);
        int dimModel = modelList.getDimension();
        for(int i = 0; i < dimModel; i ++){
            logP += modelBaseDistr.calcLogP(modelList.getParameter(i));
        }
        //System.err.println("flag2: "+logP);
        int dimFreq = freqList.getDimension();
        for(int i = 0; i < dimFreq; i ++){
            logP += freqBaseDistr.calcLogP(freqList.getParameter(i));
        }
        //System.err.println("flag3: "+logP);

        int[] counts = dpValuable.getClusterCounts();

        logP+=Math.log(alphaPowers[counts.length]);
        //System.err.println("flag2: "+logP);

        for(int count: counts){
            logP+=gammas[count];
        }
        //System.err.println("flag3: "+logP);
        logP-=denominators[dpValuable.getPointerDimension()];
        //System.err.println("flag4: "+logP);
        //if(Double.isNaN(logP))
        //    throw new RuntimeException("wrong!!!");
        return logP;


    }

    public boolean requiresRecalculation(){
        return alpha.somethingIsDirty()
                || paramBaseDistr.isDirtyCalculation()
                || modelBaseDistr.isDirtyCalculation()
                || freqBaseDistr.isDirtyCalculation();
    }
    public ParametricDistribution getBaseDistribution(){
        return null;
    }
    public ParametricDistribution getParamBaseDistr(){
        return paramBaseDistr;
    }

    public ParametricDistribution getModelBaseDistr(){
        return modelBaseDistr;
    }

    public ParametricDistribution getFreqBaseDistr(){
        return freqBaseDistr;
    }

}
