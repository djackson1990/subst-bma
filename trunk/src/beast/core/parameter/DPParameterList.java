package beast.core.parameter;

import beast.core.Description;
import beast.core.Input;
import beast.evolution.alignment.AlignmentBMA;

import java.util.ArrayList;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class stores a list of parameters and the size of the list can change.")
public abstract class DPParameterList extends Parameter{
    public Input<AlignmentBMA> alignmentInput = new Input<AlignmentBMA>("alignment", "full sequence data for the beast.tree", Input.Validate.REQUIRED);

    public DPParameterList(ArrayList<Parameter> parameterList){

    }
}
