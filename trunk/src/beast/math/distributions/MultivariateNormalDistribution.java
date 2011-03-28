package beast.math.distributions;


import beast.core.Input;
import beast.core.parameter.RealParameter;

import org.apache.commons.math.distribution.ContinuousDistribution;
import org.apache.commons.math.distribution.Distribution;

/**
 * Created by IntelliJ IDEA.
 * User: jessie
 * Date: Mar 14, 2011
 * Time: 2:10:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class MultivariateNormalDistribution extends ParametricDistribution{
    public Input<RealParameter> m_meanVec = new Input<RealParameter>("m_meanVec","Mean vector of the multivariate normal distribution", Input.Validate.REQUIRED);


    @Override
	public Distribution getDistribution() {
		return null;
	}


}
