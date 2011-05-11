package beast.evolution.substitutionmodel;

import beast.core.CalculationNode;
import beast.core.Description;

import java.util.ArrayList;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class stores a list of substitution models.")
public class SubstitutionModelList extends CalculationNode {

    ArrayList<SubstitutionModel> substModels;
    public SubstitutionModelList(ArrayList<SubstitutionModel> modelList){
        
    }

    public SubstitutionModel getModel(int mIndex){
        return substModels.get(mIndex);
    }

    //
    public int getDimension(){
        return substModels.size();
    }

}
