package beast.evolution.likelihood;

import beast.core.*;
import beast.core.parameter.RealParameter;
import beast.evolution.alignment.Alignment;
import beast.evolution.alignment.AlignmentSubset;
import beast.evolution.tree.Tree;
import beast.evolution.sitemodel.SiteModel;
import beast.evolution.sitemodel.DPSiteModelOld;
import beast.evolution.branchratemodel.BranchRateModel;
import beast.evolution.branchratemodel.StrictClockModel;
import beast.evolution.substitutionmodel.HKY;
import beast.evolution.substitutionmodel.Frequencies;
import test.beast.BEASTTestCase;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class tree likelihood computation with data partitioning.")
public class DPTreeLikelihoodOld extends Distribution {

    public Input<Alignment> alignmentInput = new Input<Alignment>("data", "sequence data for the beast.tree", Input.Validate.REQUIRED);
    public Input<Tree> treeInput = new Input<Tree>("tree", "phylogenetic beast.tree with sequence data in the leafs", Input.Validate.REQUIRED);
    //public Input<SiteModel.Base> siteModelInput = new Input<SiteModel.Base>("siteModel", "site model for leafs in the beast.tree", Input.Validate.REQUIRED);
    public Input<DPSiteModelOld> dpSiteModelInput = new Input<DPSiteModelOld>("dpSiteModelOld", "site model for leafs in the beast.tree", Input.Validate.REQUIRED);
    public Input<BranchRateModel.Base> branchRateModelInput = new Input<BranchRateModel.Base>("branchRateModel",
            "A model describing the rates on the branches of the beast.tree.");

    public Input<Boolean> m_useAmbiguities = new Input<Boolean>("useAmbiguities", "flag to indicate leafs that sites containing ambigue states should be handled instead of ignored (the default)", false);
    ArrayList<TreeLikelihood> compoundLik;
    public DPSiteModelOld dpSiteModelOld;
    boolean[] dirtyLikelihoods;
    private boolean[] allSitesDirty;

    public void initAndValidate() throws Exception{
        dpSiteModelOld = dpSiteModelInput.get();

        Alignment alignment = alignmentInput.get();
        int siteCount = alignment.getSiteCount();
        dirtyLikelihoods = new boolean[siteCount];
        compoundLik = new ArrayList<TreeLikelihood>();
        
        for(int i = 0; i < siteCount; i++){
            AlignmentSubset site = new AlignmentSubset(alignment,i);
            TreeLikelihood treeLikelihood = new TreeLikelihood();
                treeLikelihood.initByName(
                    "data", site,
                    "tree", treeInput.get(),
                    "siteModel", dpSiteModelOld.getSiteModel(i),
                    "branchRateModel", branchRateModelInput.get()
            );
            compoundLik.add(treeLikelihood);
        }
        allSitesDirty = new boolean[siteCount]; 
        Arrays.fill(allSitesDirty,true);
        //System.err.println("Number of likelihoods: "+compoundLik.size());
    }

    @Override
    /*public double calculateLogP() throws Exception {
        logP = calcLogP();
        return logP;

    }*/



    public double calculateLogP() throws Exception{
        if(isDirtyCalculation()){
            logP = calcLogP();
        }
        //System.err.println("logP: "+logP);
        return logP;

    }


    public double calcLogP() throws Exception {

        double logP = 0;
        int counter = 0;

        for(int i = 0; i < compoundLik.size();i++) {
        	if (dirtyLikelihoods[i]) {
                counter++;
                //System.err.println(i+" "+dirtyLikelihoods[i]);
        		logP += compoundLik.get(i).calculateLogP();
        	} else {
        		logP += compoundLik.get(i).getCurrentLogP();
        	}
            if (Double.isInfinite(logP) || Double.isNaN(logP)) {
            	return logP;
            }
        }
        //System.err.println("logP: "+logP);
        //System.err.println("counter: "+counter);

        return logP;
    }

    public void store(){
        for(TreeLikelihood treeLik : compoundLik) {
            treeLik.store();
        }
        super.store();
        //System.err.println("storedLogL: "+storedLogP);
    }

    public void restore(){
        for(TreeLikelihood treeLik : compoundLik) {
            treeLik.restore();
        }
        super.restore();

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
        boolean recalculate = false;
        if(treeInput.get().somethingIsDirty() || branchRateModelInput.get().isDirtyCalculation()){
            recalculate=true;
            //System.err.println("recalculated all");
            dirtyLikelihoods = allSitesDirty;
        }else if(dpSiteModelOld.isDirtyCalculation()){
            dirtyLikelihoods = dpSiteModelOld.getSiteDirtiness();
            recalculate = true;
            //System.err.println("recalculated sites");

        }

      return recalculate;
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
            DPTreeLikelihoodOld likelihood = new DPTreeLikelihoodOld();
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
