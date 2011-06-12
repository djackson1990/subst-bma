package beast.math.distributions;

import beast.core.Valuable;
import beast.core.parameter.DPPointer;

/**
 * @author Chieh-Hsi Wu
 *
 */
public class ContinuousCategoricalDistribution extends ParametricDistribution{
    public void initAndValidate(){

    }
    public ParametricDistribution getDistribution(){
        return null;

    }

    public double calcLogP(Valuable val){
        //System.err.println("logDensity");
        if(!(val instanceof DPPointer)){
            throw new RuntimeException("This distriubtion only applies to DPValuable");
        }
        DPPointer pointers = ((DPPointer)val);
        if(pointers.getLastDirty() == -1){
        double logL = 0.0;
        for(int i = 0; i < pointers.getDimension();i++){
            if(pointers.getParameterValue(i)<0.5){
                logL+=Math.log(0.2);
            }else{
                logL+=Math.log(0.8);
            }
        }
        return logL;
        }
        if(pointers.getParameterValue(pointers.getLastDirty())<0.5){
                return Math.log(0.2);
            }else{
                return Math.log(0.8);
            }



    }
}
