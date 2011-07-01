package beast.evolution.likelihood;

import beast.evolution.tree.Tree;
import beast.evolution.sitemodel.SiteModel;
import beast.evolution.substitutionmodel.SubstitutionModel;
import beast.core.Description;

/**
 * @author Chieh-Hsi Wu
 */
@Description("Calculates tree likelihood of a particular site.")
public class TempSiteTreeLikelihood extends TreeLikelihood{

    @Override
    public double calculateLogP() throws Exception{
        m_nHasDirt = Tree.IS_FILTHY;
        return super.calculateLogP();

    }

    public void setSiteModel(SiteModel siteModel){
        m_siteModel = siteModel;
        m_substitutionModel = (SubstitutionModel.Base) siteModel.getSubstitutionModel();
    }

}
