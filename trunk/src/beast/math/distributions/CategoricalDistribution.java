package beast.math.distributions;

import beast.core.Input;
import beast.core.Description;
import beast.core.parameter.RealParameter;
import org.apache.commons.math.distribution.*;
import org.apache.commons.math.MathException;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class represents a discrete distirbution on integers.")
public class CategoricalDistribution extends ParametricDistribution {
    public Input<RealParameter> m_probs = new Input<RealParameter>("probs","Probabilities of each integer value", Input.Validate.REQUIRED);
    CategorialImpl m_dist = new CategorialImpl();

    public void initAndValidate(){
        refresh();
    }

    public void refresh(){
        Double[] probs = m_probs.get().getValues();
        double sumP = 0;
        for(Double prob: probs){
            sumP +=prob;
        }
        if(sumP != 1.0){
            throw new RuntimeException("Probabilities don't sum to one");
        }
        m_dist.setProbabilityVector(probs);

    }

    class CategorialImpl extends AbstractIntegerDistribution{
        private Double[] probs;

        public void setProbabilityVector(Double[] probs){
            this.probs = probs;
        }

		@Override
        public double probability(int x){
            return probs[x];
        }
		public double cumulativeProbability(int x) throws MathException {
            double cumProb = 0.0;
            for(int i = 0; i <= x; i++){
                cumProb +=probs[i];
            }
            return cumProb;
		}

		@Override
		public double cumulativeProbability(int x0, int x1) throws MathException {
            double cumProb = 0.0;
            for(int i = x0; i <= x1; i++){
                cumProb +=probs[i];
            }
            return cumProb;
		}

		@Override
		public int inverseCumulativeProbability(double p) throws MathException {
            double cumProb = 0.0;
            for(int i = 0; i < probs.length;i++){
                cumProb+=probs[i];
                if(cumProb> p){
                    return i;
                }
            }
            throw new MathException("Specified probability p exceeds total probability");
		}

        public int getDomainLowerBound(double p){
            return -1;
        }

        public int getDomainUpperBound(double p){
            return -1;
        }

	}

    public double inverseCumulativeProbability(double p) throws MathException {
    	    org.apache.commons.math.distribution.Distribution dist = getDistribution();
    		return ((IntegerDistribution)dist).inverseCumulativeProbability(p);
    }

    @Override
	public Distribution getDistribution() {
        if(m_probs.get().somethingIsDirty()){
            refresh();
        }
		return m_dist;
	}

    


}
