package beast.evolution.likelihood;

import beast.evolution.sitemodel.SiteModel;
import beast.evolution.sitemodel.DPNtdRateSepSiteModel;
import beast.evolution.substitutionmodel.SubstitutionModel;
import beast.evolution.substitutionmodel.NtdBMA;
import beast.core.Input;
import beast.core.parameter.RealParameter;

/**
 * @author Chieh-Hsi Wu
 */
public class SepTempTreeLikelihood extends TempTreeLikelihood{
    public Input<DPNtdRateSepSiteModel> dpNtdRateSepSiteModelInput = new Input<DPNtdRateSepSiteModel>(
            "NtdRateSepSiteModel",
            "A site model that records separate clustering of the substitution model and rates",
            Input.Validate.REQUIRED
    );

    private DPNtdRateSepSiteModel dpNtdRateSepSiteModel;
    public void initAndValidate() throws Exception{
        dpNtdRateSepSiteModel = dpNtdRateSepSiteModelInput.get();
        super.initAndValidate();
    }

    public double calculateLogP(
            RealParameter rateParameter,
            int siteIndex){

        try{


            //Retrive the substModel of the given site
            SubstitutionModel substModel = dpNtdRateSepSiteModel.getModel(siteIndex);
            //Set the substitution model of the siteModel to the substitution model retrieved
            siteModelInput.get().m_pSubstModel.setValue(substModel,siteModelInput.get());
            //Set the rate to the specified rate
            siteModelInput.get().getRateParameter().setValueQuietly(0,rateParameter.getValue());
            //Retrieve the pattern
            int iPat = alignment.getPatternIndex(siteIndex);
            //System.out.println("recompute:");
            logP = treeLiks[iPat].calculateLogP();
        }catch(Exception e){
            throw new RuntimeException(e);

        }
        return logP;

    }

    public double calculateLogP(
            RealParameter modelParameters,
            RealParameter modelCode,
            RealParameter freqs,
            int siteIndex){
        try{

            //Retrive the rate of the given site
            RealParameter muParameter = dpNtdRateSepSiteModel.getRate(siteIndex);
            //Set the mutational of the siteModel to the retrieved rate
            siteModelInput.get().getRateParameter().setValueQuietly(0,muParameter.getValue());

            //Set the subsitution model parameters to the specified values.
            if(substModel instanceof NtdBMA){
                ((NtdBMA)substModel).getLogKappa().setValueQuietly(0,modelParameters.getValue(0));
                ((NtdBMA)substModel).getLogTN().setValueQuietly(0,modelParameters.getValue(1));
                ((NtdBMA)substModel).getLogAC().setValueQuietly(0,modelParameters.getValue(2));
                ((NtdBMA)substModel).getLogAT().setValueQuietly(0,modelParameters.getValue(3));
                ((NtdBMA)substModel).getLogGC().setValueQuietly(0,modelParameters.getValue(4));
                ((NtdBMA)substModel).getModelChoose().setValueQuietly(0,modelCode.getValue());
                ((NtdBMA)substModel).getFreqs().setValueQuietly(0,freqs.getValue(0));
                ((NtdBMA)substModel).getFreqs().setValueQuietly(1,freqs.getValue(1));
                ((NtdBMA)substModel).getFreqs().setValueQuietly(2,freqs.getValue(2));
                ((NtdBMA)substModel).getFreqs().setValueQuietly(3,freqs.getValue(3));

            }else{
                throw new RuntimeException("Need NtdBMA");
            }
            ((NtdBMA)substModel).setUpdateMatrix(true);
            siteModelInput.get().m_pSubstModel.setValue(substModel,siteModelInput.get());

            int iPat = alignment.getPatternIndex(siteIndex);
            logP = treeLiks[iPat].calculateLogP();
        }catch(Exception e){
            throw new RuntimeException(e);

        }
        return logP;
    }



}
