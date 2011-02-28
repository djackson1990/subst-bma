package beast.evolution.siteModel;

import beast.core.parameter.RealParameter;
import beast.core.Input;
import beast.evolution.substitutionmodel.SubstitutionModel;
import beast.evolution.sitemodel.SiteModel;

/**
 * @author Chieh-Hsi Wu
 *
 * BSSVS on Gamma site model
 *
 */
public class GammaSiteBMA extends SiteModel {

    public Input<Integer> gammaCategoryCount =
            new Input<Integer>("gammaCategoryCount", "gamma category count (default=zero for no gamma)",Input.Validate.REQUIRED);
    public Input<RealParameter> muParameter = new Input<RealParameter>("mutationRate", "mutation rate (defaults to 1.0)",Input.Validate.REQUIRED);
    public Input<RealParameter> shapeParameter =
            new Input<RealParameter>("shape", "shape parameter of gamma distribution. Ignored if gammaCategoryCount 1 or less",Input.Validate.REQUIRED);
    public Input<RealParameter> invarParameter =
            new Input<RealParameter>("proportionInvariant", "proportion of sites that is invariant: should be between 0 (default) and 1",Input.Validate.REQUIRED);
    public Input<SubstitutionModel.Base> m_pSubstModel =
            new Input<SubstitutionModel.Base>("substModel", "substitution model along branches in the beast.tree", Input.Validate.REQUIRED);

    public void initAndValidate() throws Exception {


        //seting the bound for mu
        muParameter.get().setBounds(0.0, Double.POSITIVE_INFINITY);

        //setting bounds for shape
        shapeParameter.get().setBounds(1.0E-3, 1.0E3);

        //setting bounds for invar
        invarParameter.get().setBounds(0.0, 1.0);

        //category count
        categoryCount = gammaCategoryCount.get();

        
        categoryRates = new double[categoryCount];
        categoryProportions = new double[categoryCount];

        ratesKnown = false;

        addCondition(muParameter);
        addCondition(invarParameter);
        addCondition(shapeParameter);
    }

}
