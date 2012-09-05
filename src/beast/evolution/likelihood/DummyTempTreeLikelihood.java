package beast.evolution.likelihood;

import beast.core.parameter.RealParameter;
import beast.evolution.substitutionmodel.NtdBMA;

/**
 * Created by IntelliJ IDEA.
 * User: cwu080
 * Date: 1/09/2011
 * Time: 4:44:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class DummyTempTreeLikelihood extends TempTreeLikelihood{

    public double calculateLogP(
            RealParameter modelParameters,
            RealParameter modelCode,
            RealParameter freqs,
            int site){
        return 0.0;
    }


    public double calculateLogP(
            RealParameter modelParameters,
            RealParameter modelCode,
            RealParameter freqs,
            RealParameter rate,
            int site){

        return 0.0;
    }


    public double calculateLogP(
            RealParameter rateParameter,
            int site){

        return 0.0;
    }

}
