package beast.evolution.likelihood;

import beast.core.Distribution;
import beast.core.Input;
import beast.core.State;
import beast.core.parameter.RealParameter;
import beast.evolution.alignment.Alignment;
import beast.evolution.alignment.AlignmentSubset;
import beast.evolution.tree.Tree;
import beast.evolution.branchratemodel.BranchRateModel;
import beast.evolution.sitemodel.SiteModel;
import beast.evolution.substitutionmodel.NtdBMA;
import beast.evolution.substitutionmodel.DPNtdBMA;

import java.util.List;
import java.util.Random;

/**
 * @author Chieh-Hsi Wu
 */
public class TempTreeLikelihood extends Distribution {
    public Input<Alignment> dataInput = new Input<Alignment>(
            "data",
            "sequence data for the beast.tree",
            Input.Validate.REQUIRED
    );

    public Input<SiteModel> siteModelInput = new Input<SiteModel>(
            "siteModel",
            "Models the evolution of a site in an alignment",
            Input.Validate.REQUIRED
    );

    public Input<Tree> treeInput = new Input<Tree>(
            "tree",
            "phylogenetic beast.tree with sequence data in the leafs",
            Input.Validate.REQUIRED
    );

    public Input<BranchRateModel.Base> branchRateModelInput = new Input<BranchRateModel.Base>(
            "branchRateModel",
            "A model describing the rates on the branches of the beast.tree."
    );

    public Input<Boolean> useAmbiguitiesInput = new Input<Boolean>(
            "useAmbiguities",
            "flag to indicate leafs that sites containing ambigue states should be handled instead of ignored (the default)",
            false
    );

    private RealParameter defaultMu;
    private Alignment alignment;
    private TempSiteTreeLikelihood[] treeLiks;
    public void initAndValidate() throws Exception{
        defaultMu = new RealParameter(new Double[]{1.0});
        alignment = dataInput.get();
        int siteCount = alignment.getSiteCount();
        int patternCount = alignment.getPatternCount();
        treeLiks = new TempSiteTreeLikelihood[patternCount];
        int[] firstPatternOccur = new int[patternCount];
        for(int iPat = 0; iPat < firstPatternOccur.length; iPat++){
            firstPatternOccur[iPat] = -1;
        }
        for(int iSite = 0; iSite < siteCount; iSite++){

            int iPat = alignment.getPatternIndex(iSite);
            if(firstPatternOccur[iPat] == -1){
                firstPatternOccur[iPat] = iSite;
            }
        }

        for(int i = 0; i < firstPatternOccur.length; i++){
            AlignmentSubset sitePattern = new AlignmentSubset(alignment,firstPatternOccur[i]);
            TempSiteTreeLikelihood treeLik = new TempSiteTreeLikelihood();
                treeLik.initByName(
                    "data", sitePattern,
                    "tree", treeInput.get(),
                    "siteModel", siteModelInput.get(),
                    "branchRateModel", branchRateModelInput.get()
            );
            treeLiks[i] = treeLik;
        }
        

    }

    public double calculateLogP(
            RealParameter modelParameters,
            RealParameter modelCode,
            RealParameter freqs,
            int site){
        try{

            NtdBMA ntdBMA = DPNtdBMA.createNtdBMA(modelParameters,modelCode,freqs);
            SiteModel siteModel = new SiteModel();
            siteModel.initByName(
                    "substModel", ntdBMA
            );
            int iPat = alignment.getPatternIndex(site);
            treeLiks[iPat].setSiteModel(siteModel);
            logP = treeLiks[iPat].calculateLogP();
        }catch(Exception e){
            throw new RuntimeException(e);

        }
        return logP;
    }

    public List<String> getConditions(){
        return null;

    }

    public List<String> getArguments(){
        return null;

    }

    public boolean requiresRecalculation(){
        System.err.println("what?");
        return false;
    }

    public void sample(State state, Random random){
        throw new RuntimeException("Not yet implemented as it doesn't make much sense to do so in this case");
    }





}
