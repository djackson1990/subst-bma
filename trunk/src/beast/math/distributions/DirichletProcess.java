package beast.math.distributions;

import org.apache.commons.math.distribution.ContinuousDistribution;
import beast.core.parameter.*;
import beast.core.Input;
import beast.core.Description;
import beast.core.Valuable;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class that the dirichlet process.")
public class DirichletProcess extends ParametricDistribution{

    public Input<DPValuable> dpValuableInput = new Input<DPValuable>(
            "dpVal",
            "reports the counts in each cluster",
            Input.Validate.REQUIRED
    );

    public Input<ParametricDistribution> baseDistrInput = new Input<ParametricDistribution>(
            "baseDistr",
            "The base distribution of the dirichlet process",
            Input.Validate.REQUIRED
    );
    

    public Input<RealParameter> alphaInput = new Input<RealParameter>(
            "alpha",
            "The concentration parameter of the Dirichlet process prior",
            Input.Validate.REQUIRED
    );

    private ParametricDistribution baseDistribution;
    private double[] alphaPowers;
    private RealParameter alpha;
    private DPValuable dpValuable;
    private double[] denominators;
    private double[] gammas;


    public void initAndValidate(){

        alpha = alphaInput.get();
        dpValuable = dpValuableInput.get();
        baseDistribution = baseDistrInput.get();

        //Yes I know that we are only counting number of memebers is the existing clusters
        //So there won't be any clusters of size 0.
        //But for the sake of convenience for later computation I'm going to start from 0.
        refresh();
        gammas = new double[dpValuable.getPointerDimension()+1];
        gammas[0] = Double.NaN;
        gammas[1] = 1;
        for(int i = 2; i < gammas.length; i++){
            gammas[i] = gammas[i-1]*(i-1);
        }
    }

    public void refresh(){
        //Yes I know that we are only counting number of memebers is the existing clusters
        //So there won't be any clusters of size 0.
        //But for the sake of convenience for later computation I'm going to start from 0.

        alphaPowers = new double[dpValuable.getPointerDimension()+1];
        for(int i = 0; i < alphaPowers.length;i++){
            alphaPowers[i] = Math.pow(alpha.getValue(),i);
        }

        denominators = new double[dpValuable.getPointerDimension()+1];
        denominators[0] = 1;
        for(int i = 1; i < denominators.length;i++){
            denominators[i] = denominators[i-1]*(alpha.getValue()+i-1);
        }

    }
    public double calcLogP(Valuable xList) throws Exception {
        if(requiresRecalculation()){
            refresh();
        }
        int dimParam = xList.getDimension();
        double logP = 0.0;
        for(int i = 0; i < dimParam; i ++){
            logP += baseDistribution.calcLogP(((ParameterList)xList).getParameter(i));
        }

        int[] counts = dpValuable.getClusterCounts();
        
        logP+=Math.log(alphaPowers[counts.length]);

        for(int count: counts){
            logP+=Math.log(gammas[count]);
        }
        logP-=Math.log(denominators[dpValuable.getPointerDimension()]);
        return logP;


    }
    public boolean requiresRecalculation(){
        return alpha.somethingIsDirty() || baseDistribution.isDirtyCalculation();
    }

    public ContinuousDistribution getDistribution(){
        throw new RuntimeException("Dirichlet process is a discrete distribution.");
    }

    public ParametricDistribution getBaseDistribution(){
        return baseDistribution;
    }

    public double getConcParameter(){
        return alpha.getValue();
    }


}
