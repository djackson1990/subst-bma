package beast.math.distributions;

import beast.core.Description;
import beast.core.Distribution;
import beast.core.Valuable;
import beast.core.parameter.DPValuable;
import beast.core.parameter.DPPointer;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class is a distribution on clusters. " +
        "It is not implemented properly as purely serves the purpose of testing the DirichletProcessPriorGibbsSampler," +
        "so don't laugh and don't pick on it.")
public class AdHocClusterDistribution extends ParametricDistribution{
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
        if((pointers).pointEqual(0,1)){
            if((pointers.pointEqual(0,2))){
                return Math.log(0.2);
            }else{
                return Math.log(0.5);
            }

        }else{
            return Math.log(0.1);
        }


    }
    
}
