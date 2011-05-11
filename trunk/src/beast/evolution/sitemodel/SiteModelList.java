package beast.evolution.sitemodel;

import beast.evolution.alignment.AlignmentBMA;
import beast.evolution.substitutionmodel.SubstitutionModelList;
import beast.core.Input;
import beast.core.CalculationNode;
import beast.core.Description;

import java.util.ArrayList;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class stores a list of siteModels.")
public class SiteModelList extends CalculationNode {
    public Input<AlignmentBMA> alignmentBMAInput = new Input<AlignmentBMA>("data", "sequence data for the beast.tree", Input.Validate.REQUIRED);
    public Input<SubstitutionModelList> substitutionModelListInput =
            new Input<SubstitutionModelList>("substitutionModelList","A list of substitution model",Input.Validate.REQUIRED);

    private ArrayList<SiteModel> siteModelList = new ArrayList<SiteModel>();
    public void initAndValidate () throws Exception {
        SubstitutionModelList substModelList = substitutionModelListInput.get();
        int dim = substModelList.getDimension();
        for(int i = 0; i < dim; i++){
            SiteModel siteModel = new SiteModel();
            siteModel.initByName(
                    "substModel", substModelList.getModel(i));
            siteModelList.add(siteModel);
        }
    }

}
