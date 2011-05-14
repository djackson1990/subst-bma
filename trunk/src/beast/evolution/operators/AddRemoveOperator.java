package beast.evolution.operators;

import beast.core.Operator;
import beast.core.Input;
import beast.core.Description;
import beast.core.parameter.ParameterList;

/**
 * @author Chieh-Hsi Wu
 */
@Description("Adds or removed parameters to a list. It is purely for testing purpose.")
public class AddRemoveOperator extends Operator {
    public Input<ParameterList> parameterListInput =
                    new Input<ParameterList>("parameters", "A list of parameters.", Input.Validate.REQUIRED);
    public double proposal(){
        return 0.0;
    }


}
