package beast.evolution.likelihood;

import beast.core.Distribution;
import beast.core.Input;
import beast.core.CalculationNode;
import beast.core.State;


import java.util.Random;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Chieh-Hsi Wu
 */

public class DummyLikelihood extends Distribution {
    public Input<List<CalculationNode>> calcNodes = new  Input<List<CalculationNode>>("calcNode","Some sort of input", new ArrayList<CalculationNode>());

	@Override
	public void initAndValidate() {}

	@Override
	public double calculateLogP() throws Exception {
		return 0.0;
	}

	@Override
	public boolean requiresRecalculation() {
		return true;
	}

	@Override public void sample(State state, Random random) {}
	@Override public List<String> getArguments() {return null;}
	@Override public List<String> getConditions() {return null;}

}
