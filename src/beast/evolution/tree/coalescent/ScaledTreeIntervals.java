package beast.evolution.tree.coalescent;

import beast.core.Input;
import beast.evolution.tree.Scaler;

/**
 * Created by IntelliJ IDEA.
 * User: cwu080
 * Date: 9/09/12
 * Time: 9:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScaledTreeIntervals extends TreeIntervals {
    public Input<Scaler> scalerInput = new Input<Scaler>(
            "scaler",
            "The scale factor for scaling the intervals.",
            Input.Validate.REQUIRED

    );
    private Scaler scaler;
    private boolean treeChanged;
    public void initAndValidate(){
        scaler = scalerInput.get();
        scaler.getScaleFactor();
        System.out.println(m_tree.get().getNodeCount());
        super.initAndValidate();

        treeChanged = true;
        calculateTreeIntervals();
    }

    public void setMultifurcationLimit(double multifurcationLimit) {
        // invalidate only if changing anything
        super.setMultifurcationLimit(multifurcationLimit);
        calculateTreeIntervals();
    }

    @Override
    public double getInterval(int i) {

        if (i < 0 || i >= getIntervalCount()) throw new IllegalArgumentException();
        return scaledTreeIntervals[i];
    }

    @Override
    public double[] getIntervals(double[] inters) {
        if (inters == null) inters = new double[scaledTreeIntervals.length];
        System.arraycopy(scaledTreeIntervals, 0, inters, 0, scaledTreeIntervals.length);
        return inters;

    }

    @Override
    public double[] getCoalescentTimes(double[] coalescentTimes) {
        if (coalescentTimes == null) coalescentTimes = new double[getSampleCount()];

        double time = 0;
        int coalescentIndex = 0;
        for (int i = 0; i < scaledTreeIntervals.length; i++) {
            time += scaledTreeIntervals[i];
            for (int j = 0; j < getCoalescentEvents(i); j++) {
                coalescentTimes[coalescentIndex] = time;
                coalescentIndex += 1;
            }
        }
        return coalescentTimes;
    }


    @Override
    public double getTotalDuration() {
        double height = 0.0;
        for (int j = 0; j < getIntervalCount(); j++) {
            height += scaledTreeIntervals[j];
        }
        return height;
    }




    double[] scaledTreeIntervals;
    double[] storedScaledTreeIntervals;
    private void calculateTreeIntervals(){
        if(treeChanged){
            scaledTreeIntervals = super.getIntervals(null);
        }
        storedScaledTreeIntervals = new double[scaledTreeIntervals.length];
        double scaleFactor = scaler.getScaleFactor();
        for(int i = 0 ; i < scaledTreeIntervals.length; i++){
            scaledTreeIntervals[i] = scaledTreeIntervals[i]*scaleFactor;
        }

    }



    public void store(){
        super.store();
        System.arraycopy(scaledTreeIntervals, 0, storedScaledTreeIntervals, 0, scaledTreeIntervals.length);
    }

    public void restore(){
        super.restore();
        double[] tmp = storedScaledTreeIntervals;
        storedScaledTreeIntervals = scaledTreeIntervals;
        scaledTreeIntervals = tmp;

    }

    public boolean requiresRecalculation(){
        if(m_tree.get().somethingIsDirty()){

            super.requiresRecalculation();
            treeChanged = true;
        }

        return true;
    }
}
