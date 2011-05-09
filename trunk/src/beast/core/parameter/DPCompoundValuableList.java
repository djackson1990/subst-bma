package beast.core.parameter;

import beast.core.Input;
import beast.evolution.alignment.AlignmentBMA;


import java.util.ArrayList;

/**
 * @author Chieh-Hsi Wu
 */
public class DPCompoundValuableList extends CompoundValuable{

    public Input<AlignmentBMA> alignmentBMAInput = new Input<AlignmentBMA>("data", "sequence data for the beast.tree", Input.Validate.REQUIRED);
    public Input<CompoundValuable> initParamInput = new Input<CompoundValuable>("initParameter", "parameter to initiate all the elements in the parameter list", Input.Validate.REQUIRED);

    private ArrayList<DPCompoundValuable> parameters;
    public void initAndValidate() throws Exception{
        AlignmentBMA alignment = alignmentBMAInput.get();
        int siteCount = alignment.getSiteCount();
        parameters = new ArrayList<DPCompoundValuable>();

        for(int i = 0; i < siteCount; i++){

        }

    }

}
