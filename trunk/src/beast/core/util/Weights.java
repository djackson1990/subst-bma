package beast.core.util;

import beast.core.Plugin;
import beast.core.Loggable;
import beast.core.Input;
import beast.core.Description;
import beast.evolution.likelihood.DPTreeLikelihood;

import java.io.PrintStream;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class reports the pattern weights for each likelihood, which is rather handy for debugging.")
public class Weights extends Plugin implements Loggable {
    public Input<DPTreeLikelihood> dpTreeLikInput = new Input<DPTreeLikelihood>(
            "dpTreeLik",
            "The dptreeLikelihood whose weight are to be reported",
            Input.Validate.REQUIRED
    );

    DPTreeLikelihood dpTreeLik;
    @Override
	public void initAndValidate() {
		dpTreeLik = dpTreeLikInput.get();
	}

    @Override
	public void init(PrintStream out) throws Exception {
        out.print("count("+dpTreeLik.getID() + ")\t");
    }


    @Override
	public void log(int nSample, PrintStream out) {
        int[][] clusterWeights = dpTreeLik.getClusterWeights();
        for(int[] weights:clusterWeights){
            for(int weight:weights){
                out.print(weight + "\t");
            }

        }

	}

    @Override
	public void close(PrintStream out) {
		// nothing to do
	}    

}
