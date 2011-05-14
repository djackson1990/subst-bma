package beast.evolution.operators;

import beast.core.Input;
import beast.core.Description;
import beast.core.parameter.ParameterList;
import beast.util.Randomizer;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This random walk operator performs is adjusted so that it can perform" +
        " extended random-walk on a list of parameters.")
public class PLExtendedRealRandomWalkOperator extends ExtendedRealRandomWalkOperator{
     public Input<ParameterList> parameterListInput =
                new Input<ParameterList>("parameters", "the parameter to operate a random walk on.", Input.Validate.REQUIRED);

    public PLExtendedRealRandomWalkOperator(){
        super();
        cpInput.setRule(Input.Validate.OPTIONAL);
    }

    private ParameterList parameterList;

    int lastChangedParameterIndex;

    public void initAndValidate() {
        parameterList = parameterListInput.get();
        String[] windowSizesStr =  windowSizesInput.get().split("\\s+");
        windowSizes = new double[parameterList.getParameterDimension()];
        setupWindowSizes(windowSizesStr);
    }

    /**
     * override this for proposals,
     * returns log of hastingRatio, or Double.NEGATIVE_INFINITY if proposal should not be accepted *
     */
    @Override
    public double proposal() {
        //System.err.println("PLERRWO");
        parameterList = parameterListInput.get(this);
        int iParam = Randomizer.nextInt(parameterList.getDimension());
        int iValue = Randomizer.nextInt(parameterList.getParameterDimension());
        double value = parameterList.getValue(iParam,iValue);
        double newValue = getProposedVal(
                value, //original value
                iValue,     //index of the value
                m_bUseGaussian, //whether to use gaussian moves
                windowSizes);   //window sizes

        if (newValue < parameterList.getParameterLower(iParam) || newValue > parameterList.getParameterUpper(iParam)) {
        	return Double.NEGATIVE_INFINITY;
        }
        if (newValue == value) {
        	// this saves calculating the posterior
        	return Double.NEGATIVE_INFINITY;
        }

        parameterList.setValue(iParam, iValue, newValue);

        lastChangedParameterIndex = iParam;
        lastChangedValueIndex = iValue;


        return 0.0;
    }

}
