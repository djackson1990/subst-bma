package beast.math.distributions;

import beast.core.parameter.ParameterList;
import beast.core.Input;
/**
 * @author Chieh-Hsi Wu
 */

public class NtdParameterListPrior extends ParameterListPrior{
    public Input<NtdDP> ntdDPInput =
            new Input<NtdDP>("ntdDP","Dirichlet process for nucleotide model parameters", Input.Validate.REQUIRED);

    public Input<ParameterList> paramListInput = new Input<ParameterList>(
            "paramList",
            "points at which the density is calculated",
            Input.Validate.REQUIRED
    );
    public Input<ParameterList> modelListInput = new Input<ParameterList>(
            "modelList",
            "points at which the density is calculated",
            Input.Validate.REQUIRED
    );
   public Input<ParameterList> freqListInput = new Input<ParameterList>(
            "freqList",
            "points at which the density is calculated",
            Input.Validate.REQUIRED
    );

    public NtdParameterListPrior(){
        m_distInput.setRule(Input.Validate.OPTIONAL);
        xListInput.setRule(Input.Validate.OPTIONAL);
    }

    private NtdDP ntdDP;
    public void initAndValidate(){
        ntdDP = ntdDPInput.get();

    }

    @Override
	public double calculateLogP() throws Exception {

        logP = ntdDP.calcLogP(
                paramListInput.get(),
                modelListInput.get(),
                freqListInput.get());
		return logP;
	}

}
