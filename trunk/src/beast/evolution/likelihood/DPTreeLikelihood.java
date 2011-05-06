package beast.evolution.likelihood;

import beast.core.util.CompoundDistribution;
import beast.core.*;
import beast.core.parameter.RealParameter;
import beast.evolution.alignment.Alignment;
import beast.evolution.alignment.AlignmentSubset;
import beast.evolution.tree.Tree;
import beast.evolution.sitemodel.SiteModel;
import beast.evolution.branchratemodel.BranchRateModel;
import beast.evolution.branchratemodel.StrictClockModel;
import beast.evolution.substitutionmodel.HKY;
import beast.evolution.substitutionmodel.Frequencies;
import beast.evolution.likelihood.TreeLikelihood;
import test.beast.BEASTTestCase;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class tree likelihood computation with data partitioning.")
public class DPTreeLikelihood extends Distribution {

    public Input<Alignment> alignmentInput = new Input<Alignment>("data", "sequence data for the beast.tree", Input.Validate.REQUIRED);
    public Input<Tree> treeInput = new Input<Tree>("tree", "phylogenetic beast.tree with sequence data in the leafs", Input.Validate.REQUIRED);
    public Input<SiteModel.Base> siteModelInput = new Input<SiteModel.Base>("siteModel", "site model for leafs in the beast.tree", Input.Validate.REQUIRED);
    public Input<BranchRateModel.Base> branchRateModelInput = new Input<BranchRateModel.Base>("branchRateModel",
            "A model describing the rates on the branches of the beast.tree.");

    public Input<Boolean> m_useAmbiguities = new Input<Boolean>("useAmbiguities", "flag to indicate leafs that sites containing ambigue states should be handled instead of ignored (the default)", false);
    ArrayList<TreeLikelihood> compoundLik;
    public void initAndValidate() throws Exception{
        Alignment alignment = alignmentInput.get();
        int siteCount = alignment.getSiteCount();
        compoundLik = new ArrayList<TreeLikelihood>();
        for(int i = 0; i < siteCount; i++){
            AlignmentSubset site = new AlignmentSubset(alignment,i);
            TreeLikelihood treeLikelihood = new TreeLikelihood();
                treeLikelihood.initByName(
                    "data", site,
                    "tree", treeInput.get(),
                    "siteModel", siteModelInput.get(),
                    "branchRateModel", branchRateModelInput.get()
            );

            compoundLik.add(treeLikelihood);
        }
        System.err.println("Number of likelihoods: "+compoundLik.size());
    }



    @Override
    public double calculateLogP() throws Exception {
        logP = 0;

        for(TreeLikelihood treeLik : compoundLik) {
        	//if (dists.isDirtyCalculation()) {
        		logP += treeLik.calculateLogP();
        	//} else {
        		//logP += dists.getCurrentLogP();
        	//}
            if (Double.isInfinite(logP) || Double.isNaN(logP)) {
            	return logP;
            }
        }
        //System.err.println("logP: "+logP);
        return logP;
    }


    @Override
    public void sample(State state, Random random) {
        for(Distribution distribution : compoundLik) {
            distribution.sample(state, random);
        }
    }


    @Override
    public List<String> getArguments() {
        List<String> arguments = new ArrayList<String>();
        for(Distribution distribution : compoundLik) {
            arguments.addAll(distribution.getArguments());
        }
        return arguments;
    }

    @Override
    public List<String> getConditions() {
        List<String> conditions = new ArrayList<String>();
        for(Distribution distribution : compoundLik) {
            conditions.addAll(distribution.getConditions());
        }
        return conditions;
    }

    @Override
    protected boolean requiresRecalculation() {
      for(Distribution distribution : compoundLik) {
          if( distribution.isDirtyCalculation() ) {
              return true;
          }
      }
      return false;
    }

    public List<TreeLikelihood> getDistributions(){
        return compoundLik;
    }

    public static void main(String[] args){
        try{
            Alignment data = BEASTTestCase.getAlignment();
		    Tree tree = BEASTTestCase.getTree(data);
            
            Frequencies freqs = new Frequencies();
		    freqs.initByName("data", data);

            HKY hky = new HKY();
		    hky.initByName("kappa", "29.739445", "frequencies", freqs);

		    SiteModel siteModel = new SiteModel();
		    siteModel.initByName("mutationRate", "0.0", "gammaCategoryCount", 1, "substModel", hky);

            RealParameter mu = new RealParameter();
            mu.initByName(
                    "value", "1.0",
                    "lower", 0.0
            );
            StrictClockModel clockModel = new StrictClockModel();
            clockModel.initByName(
                    "clock.rate", mu
            );
            DPTreeLikelihood likelihood = new DPTreeLikelihood();
		    likelihood.initByName(
                    "data",data,
                    "tree",tree,
                    "siteModel", siteModel);
            System.err.println(likelihood.getDistributions().size());
            System.err.println(likelihood.calculateLogP());
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}
