package beast.math.distributions;

import beast.core.Input;
import beast.core.Valuable;
import beast.core.parameter.RealParameter;
import org.apache.commons.math.distribution.Distribution;

/**
 * @author Chieh-Hsi Wu
 */
public class DiscreteDistribution extends ParametricDistribution {
    public Input<RealParameter> m_probs = new Input<RealParameter>("probs","Probabilities of each integer value", Input.Validate.REQUIRED);

    private Double[] probs;
    private int offset = 0;



    public void initAndValidate(){
        probs = m_probs.get().getValues();
        //check that probs sum up to 1
        double sumP = 0;
        for(Double prob: probs){
            sumP +=prob;
        }
        if(sumP != 1.0){
            throw new RuntimeException("Probabilities don't sum to one");
        }

        if(m_offset.get() != null){
            offset = (int)(double)m_offset.get();
        }

    }


    @Override
	public Distribution getDistribution() {
		return null;
	}

    @Override
	public double calcLogP(Valuable x) throws Exception {

		return Math.log(probs[(int)x.getArrayValue() - offset]);
	}



}
