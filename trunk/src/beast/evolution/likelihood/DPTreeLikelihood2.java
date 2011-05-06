package beast.evolution.likelihood;

import beast.evolution.alignment.Alignment;
import beast.evolution.alignment.AlignmentSubset;
import beast.evolution.tree.Tree;
import beast.evolution.sitemodel.SiteModel;
import beast.evolution.branchratemodel.BranchRateModel;
import beast.evolution.branchratemodel.StrictClockModel;
import beast.evolution.substitutionmodel.Frequencies;
import beast.evolution.substitutionmodel.HKY;
import beast.core.Input;
import beast.core.State;
import beast.core.Distribution;
import beast.core.Description;
import beast.core.parameter.RealParameter;
import beast.evolution.likelihood.DPTreeLikelihood;
import beast.core.util.CompoundDistribution;

import java.util.Random;
import java.util.List;

import test.beast.BEASTTestCase;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class tree likelihood computation with data partitioning - experiment in progress")
public class DPTreeLikelihood2 extends Distribution {
    public Input<Alignment> alignmentInput = new Input<Alignment>("data", "sequence data for the beast.tree", Input.Validate.REQUIRED);
    public Input<Tree> treeInput = new Input<Tree>("tree", "phylogenetic beast.tree with sequence data in the leafs", Input.Validate.REQUIRED);
    public Input<SiteModel.Base> siteModelInput = new Input<SiteModel.Base>("siteModel", "site model for leafs in the beast.tree", Input.Validate.REQUIRED);
    public Input<BranchRateModel.Base> branchRateModelInput = new Input<BranchRateModel.Base>("branchRateModel",
            "A model describing the rates on the branches of the beast.tree.");

    public Input<Boolean> m_useAmbiguities = new Input<Boolean>("useAmbiguities", "flag to indicate leafs that sites containing ambigue states should be handled instead of ignored (the default)", false);
    CompoundDistribution compoundLik;
    public void initAndValidate() throws Exception{
        Alignment alignment = alignmentInput.get();
        int siteCount = alignment.getSiteCount();
        compoundLik = new CompoundDistribution();
        for(int i = 0; i < siteCount; i++){
            AlignmentSubset site = new AlignmentSubset(alignment,i);
            TreeLikelihood treeLikelihood = new TreeLikelihood();
                treeLikelihood.initByName(
                    "data", site,
                    "tree", treeInput.get(),
                    "siteModel", siteModelInput.get(),
                    "branchRateModel", branchRateModelInput.get()
            );

            compoundLik.initByName("distribution",treeLikelihood);
        }
        //System.err.println("Number of likelihoods: "+compoundLik.getDistributions().size());
    }


    @Override
    public double calculateLogP() throws Exception {
        return compoundLik.calculateLogP();
    }

    @Override
    public void sample(State state, Random random) {
        compoundLik.sample(state, random);
    }

    @Override
    public List<String> getArguments() {
        return compoundLik.getArguments();
    }

    @Override
    public List<String> getConditions() {
        return compoundLik.getConditions();
    }


    //public boolean requiresRecalculation() {
    //  return compoundLik.compoundLiklihoodRequiresRecalculation();
    //}

    //public List<Distribution> getDistributions(){
    //    return compoundLik.getDistributions();
    //}

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
