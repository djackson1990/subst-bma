package beast.evolution.siteModel;

import beast.core.parameter.RealParameter;
import beast.core.Input;
import beast.evolution.substitutionmodel.SubstitutionModel;
import beast.evolution.substitutionmodel.HKY;
import beast.evolution.substitutionmodel.NtdBMA;
import beast.evolution.sitemodel.SiteModel;

/**
 * @author Chieh-Hsi Wu
 *
 * BSSVS on Gamma site model
 *
 */
public class GammaSiteBMA extends SiteModel {
    public Input<RealParameter> muParameter = new Input<RealParameter>("mutationRate", "mutation rate (defaults to 1.0)");
    public Input<Integer> gammaCategoryCount =
            new Input<Integer>("gammaCategoryCount", "gamma category count (default=zero for no gamma)", 0);
    public Input<RealParameter> shapeParameter =
            new Input<RealParameter>("shape", "shape parameter of gamma distribution. Ignored if gammaCategoryCount 1 or less");
    public Input<RealParameter> invarParameter =
            new Input<RealParameter>("proportionInvariant", "proportion of sites that is invariant: should be between 0 (default) and 1");
    public Input<SubstitutionModel.Base> m_pSubstModel =
            new Input<SubstitutionModel.Base>("substModel", "substitution model along branches in the beast.tree", new HKY(), Input.Validate.REQUIRED);

    public GammaSiteBMA(){

        
    }

}
