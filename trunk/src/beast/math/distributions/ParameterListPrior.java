package beast.math.distributions;

import beast.core.Description;
import beast.core.Input;
import beast.core.parameter.ParameterList;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class is a wrapper that provides prior to parameter list.")
public class ParameterListPrior extends Prior{

    public Input<ParameterList> xList = new Input<ParameterList>("xList","points at which the density is calculated", Input.Validate.REQUIRED);

    public ParameterListPrior(){
        m_x.setRule(Input.Validate.OPTIONAL);
    }

    @Override
	public double calculateLogP() throws Exception {
        logP = 0.0;
		ParameterList parameterList = xList.get();
        int dimParam = parameterList.getDimension();
        for(int i = 0; i < dimParam; i ++){
            logP += m_dist.calcLogP(parameterList.getParameter(i));
        }

		return logP;
	}


}
