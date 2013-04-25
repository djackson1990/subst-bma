package beast.evolution.operators;

import beast.core.Input;
import beast.core.Operator;
import beast.core.parameter.DPPointer;
import beast.core.parameter.DPValuable;
import beast.math.util.MathUtils;
import beast.util.Randomizer;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: cwu080
 * Date: 25/04/13
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class CategoryDeltaExchangeOperator extends Operator {
    public Input<Double> deltaInput = new Input<Double>("delta", "Magnitude of change for two randomly picked categories.", Input.Validate.REQUIRED);
    public Input<DPValuable> dpValInput = new Input<DPValuable>("dpVal", "The Dirichlet process valuable object that records information on the clusters.", Input.Validate.REQUIRED);
    public Input<List<DPPointer>> pointersInput = new Input<List<DPPointer>>("pointers", "The Dirichlet process valuable object that records information on the clusters.", Input.Validate.REQUIRED);


    private double delta;
    private DPValuable dpVal;
    private List<DPPointer> pointers;

    public void initAndValidate(){
        delta = deltaInput.get();
        dpVal = dpValInput.get();
        pointers = pointersInput.get(this);

    }

    public double proposal(){
        int categoryCount = dpVal.getCategoryCount();

        //Randomly pick two categories
        int icat = Randomizer.nextInt(categoryCount);
        int jcat = 0;
        while(icat == jcat){
            jcat = Randomizer.nextInt(categoryCount);
        }


        //Pick the number of sites to be moved
        int size = Randomizer.nextInt((int)Math.round(delta));

        //The number of the sites to be moved is greater or equal to number of sites in the clusters.
        //As a the (log) prior probability on this clustering structure is 0 (-infinity).
        if(size >= dpVal.getClusterSize(icat)){
            return Double.NEGATIVE_INFINITY;
        }


        int[] sites = dpVal.getClusterSites(icat);
        int[] sitesToBeMoved = MathUtils.sample(size, sites, false);
        dpVal.getOneClusterSite(jcat);
        for(DPPointer pointer: pointers){
            pointer.multiPointerChanges(sitesToBeMoved, jcat);
        }

        return 0.0;

    }

    @Override
    public double getCoercableParameterValue() {
        return delta;
    }

    @Override
    public void setCoercableParameterValue(final double fValue) {
        delta = fValue;
    }

    /**
     * called after every invocation of this operator to see whether
     * a parameter can be optimised for better acceptance hence faster
     * mixing
     *
     * @param logAlpha difference in posterior between previous state & proposed state + hasting ratio
     */
    @Override
    public void optimize(final double logAlpha) {
        // must be overridden by operator implementation to have an effect
        //if (autoOptimize) {
            double fDelta = calcDelta(logAlpha);
            fDelta += Math.log(delta);
            delta = Math.exp(fDelta);
        //}

    }

    @Override
    public final String getPerformanceSuggestion() {
        final double prob = m_nNrAccepted / (m_nNrAccepted + m_nNrRejected + 0.0);
        final double targetProb = getTargetAcceptanceProbability();

        double ratio = prob / targetProb;
        if (ratio > 2.0) ratio = 2.0;
        if (ratio < 0.5) ratio = 0.5;

        // new scale factor
        final double newDelta = delta * ratio;

        final DecimalFormat formatter = new DecimalFormat("#.###");
        if (prob < 0.10) {
            return "Try setting delta to about " + formatter.format(newDelta);
        } else if (prob > 0.40) {
            return "Try setting delta to about " + formatter.format(newDelta);
        } else return "";
    }
}
