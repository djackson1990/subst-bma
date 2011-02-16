package beast.evolution.substitutionmodel;

import beast.core.Description;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.parameter.RealParameter;

@Description("Substitution model that does Bayesian variable selection in order to... .")
public class FancyBSVV extends GeneralSubstitutionModel {
    public Input<RealParameter> m_logKappa = new Input<RealParameter>("logkappa", "parameter representing log of HKY kappa parameter", Validate.REQUIRED);

    @Override
    public void initAndValidate() {
        // construction and input validation goes here
    }


} // class FancyBSVV


