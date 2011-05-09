package beast.core.parameter;

import beast.core.Valuable;
import beast.core.Description;

import java.util.ArrayList;

/**
 * @author Chieh-Hsi Wu
 */
@Description("Summarizes a set of valuables so that for example a rate matrix can be " +
		"specified that uses a parameter in various places in the matrix - designed for data partitioning.")
public class DPCompoundValuable extends CompoundValuable{

    public DPCompoundValuable(ArrayList<Valuable> valuables){
        // determine dimension
		int nDimension = 0;
		for (Valuable valuable : valuables) {
			nDimension += valuable.getDimension();
		}
		m_fValues = new double[nDimension];
    }
}
