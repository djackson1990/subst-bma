package beast.math.distributions;

import beast.core.Input;
import beast.core.Valuable;
import beast.core.parameter.ParameterList;

/**
 * @author Chieh-Hsi Wu
 */
public class NtdDP extends DirichletProcess{
    public Input<ParametricDistribution> paramBaseDistrInput = new Input<ParametricDistribution>(
            "parmaBaseDistr",
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

    public double calcLogP(Valuable[] xList) throws Exception {
        if(requiresRecalculation()){
            refresh();
        }
        int dimParam = xList[0].getDimension();
        double logP = 0.0;
        for(int i = 0; i < dimParam; i ++){
            logP += paramBaseDistr.calcLogP(((ParameterList)xList[0]).getParameter(i));
        }
        int dimModel = xList[1].getDimension();
        for(int i = 0; i < dimModel; i ++){
            logP += modelBaseDistr.calcLogP(((ParameterList)xList[1]).getParameter(i));
        }
        int dimFreq = xList[2].getDimension();
        for(int i = 0; i < dimFreq; i ++){
            logP += freqBaseDistr.calcLogP(((ParameterList)xList[2]).getParameter(i));
        }
        //System.err.println("flag1: "+logP);

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
