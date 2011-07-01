package beast.evolution.likelihood;

import beast.core.*;
import beast.core.parameter.ChangeType;
import beast.evolution.sitemodel.DPNtdSiteModel;
import beast.evolution.sitemodel.SiteModel;
import beast.evolution.alignment.Alignment;
import beast.evolution.tree.Tree;
import beast.evolution.branchratemodel.BranchRateModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Chieh-Hsi Wu
 */
@Description("A tree likelihood class that can handle data partitiioning.")
public class DPTreeLikelihood extends Distribution implements PluginList {

    private DPNtdSiteModel dpNtdSiteModel = new DPNtdSiteModel();
    private ArrayList<WVTreeLikelihood> treeLiks = new ArrayList<WVTreeLikelihood>();
    private ChangeType changeType = ChangeType.ALL;

    public Input<DPNtdSiteModel> dpNtdSiteModelInput = new Input<DPNtdSiteModel>(
            "siteModelList",
            "array which points a set of unique parameter values",
            Input.Validate.REQUIRED
    );

    public Input<Alignment> alignmentInput = new Input<Alignment>(
            "data",
            "sequence data for the beast.tree",
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

    /** calculation engine **/

    //private ArrayList<int[]> clusterWeights;

    private Alignment alignment;

    public void initAndValidate() throws Exception{

        dpNtdSiteModel = dpNtdSiteModelInput.get();


        alignment = alignmentInput.get();
        int patternCount = alignment.getPatternCount();



        int[][] clusterWeights = new int[dpNtdSiteModel.getDimension()][patternCount];

        int siteModelCount = dpNtdSiteModel.getSiteModelCount();

        int siteCount = alignment.getSiteCount();
        for(int i = 0; i < siteCount; i++){
            //System.err.println("substModelIndices[i]: "+dpNtdSiteModel.getSubstCurrCluster(i));
            clusterWeights[dpNtdSiteModel.getSubstCurrCluster(i)][alignment.getPatternIndex(i)]++;
        }

        for(int i = 0; i < siteModelCount;i++){
            WVTreeLikelihood treeLik = new WVTreeLikelihood(clusterWeights[i]);
            treeLik.initByName(
                    "data", alignment,
                    "tree", treeInput.get(),
                    "siteModel", dpNtdSiteModel.getSiteModel(i),
                    "branchRateModel", branchRateModelInput.get(),
                    "useAmbiguities",useAmbiguitiesInput.get()
            );
            treeLiks.add(treeLik);

            
        }

    }

    public int getDimension(){
        return treeLiks.size();
    }

    @Override
    public double calculateLogP() throws Exception{
        logP = 0.0;
        //System.out.println("hello: "+treeLiks.size());
        for(WVTreeLikelihood treeLik : treeLiks) {
            //System.err.println("hello?"+treeLik.isDirtyCalculation());
        	if (treeLik.isDirtyCalculation()) {
                //System.err.println("hello?");
        		logP += treeLik.calculateLogP();
                //System.out.println("calcLogP: "+treeLik.calculateLogP());
        	} else {
        		logP += treeLik.getCurrentLogP();
                //System.out.println("currLogP: "+treeLik.calculateLogP());
        	}
            if (Double.isInfinite(logP) || Double.isNaN(logP)) {
            	return logP;
            }
        }
        //System.out.println("logP: "+logP);
        return logP;
    }

    public double getSiteLogLikelihood(int iCluster, int iSite){
        return treeLiks.get(iCluster).getPatternLogLikelihood(alignment.getPatternIndex(iSite));
    }


    public void store(){
        for(TreeLikelihood treeLik : treeLiks) {
            treeLik.store();
        }

        super.store();
        //System.out.println("storedLogP: "+logP);
    }

    public void restore(){
        for(TreeLikelihood treeLik : treeLiks) {
            treeLik.restore();
        }
        super.restore();
        //System.out.println("restoredLogP: "+logP);

    }

    @Override
    public List<String> getConditions() {
        List<String> conditions = new ArrayList<String>();
        for(TreeLikelihood treeLik : treeLiks) {
            conditions.addAll(treeLik.getConditions());
        }
        return conditions;
    }

    public void addTreeLikelihood(){
        SiteModel siteModel = dpNtdSiteModel.getLastAdded();

        int[] patternWeights = new int[alignment.getPatternCount()];
        patternWeights[alignment.getPatternIndex(dpNtdSiteModel.getSubstLastDirtySite())] +=1;

        //WVAlignment wvalign = new WVAlignment(alignment, patternWeights);
        WVTreeLikelihood treeLik = new WVTreeLikelihood(patternWeights);
        try{
            treeLik.initByName(
                    "data", alignment,
                    "tree", treeInput.get(),
                    "siteModel", siteModel,
                    "branchRateModel", branchRateModelInput.get(),
                    "useAmbiguities",useAmbiguitiesInput.get()
            );
            treeLik.calculateLogP();
            treeLik.store();
            treeLiks.add(treeLik);
        }catch(Exception e){
            throw new RuntimeException(e);
        }

    }

    public void removeTreeLikelihood(int removedIndex){
        treeLiks.remove(removedIndex);
    }

    private void updateWeights(){
        int dirtySite = dpNtdSiteModel.getSubstLastDirtySite();
        //System.err.println("changeType: "+changeType);
        if(changeType == ChangeType.ADDED){
            int prevCluster = dpNtdSiteModel.getSubstPrevCluster(dirtySite);
            treeLiks.get(prevCluster).lessWeight(alignment.getPatternIndex(dirtySite),1);
        }else if(changeType==ChangeType.POINTER_CHANGED){
            int prevCluster = dpNtdSiteModel.getSubstPrevCluster(dirtySite);
            treeLiks.get(prevCluster).lessWeight(alignment.getPatternIndex(dirtySite),1);

            int currCluster = dpNtdSiteModel.getSubstCurrCluster(dirtySite);
            treeLiks.get(currCluster).addWeight(alignment.getPatternIndex(dirtySite),1);
            //System.out.println(prevCluster+" "+currCluster);
        }else if(changeType == ChangeType.REMOVED){
            int currCluster = dpNtdSiteModel.getSubstCurrCluster(dirtySite);
            treeLiks.get(currCluster).addWeight(alignment.getPatternIndex(dirtySite),1);
        }
       

    }


    @Override
    protected boolean requiresRecalculation() {
        //System.err.println("hello?");
        boolean recalculate = false;
        if(dpNtdSiteModel.isDirtyCalculation()){

            ChangeType changeType = dpNtdSiteModel.getChangeType();
            //System.out.println("treeLik requires recal!!"+changeType);
            if(changeType == ChangeType.ADDED){
                //System.out.println("added!!");
                addTreeLikelihood();
                this.changeType = ChangeType.ADDED;
                updateWeights();

            }else if(changeType == ChangeType.REMOVED){
                //System.out.println("removed!!");
                removeTreeLikelihood(dpNtdSiteModel.getRemovedIndex());
                this.changeType = ChangeType.REMOVED;
                updateWeights();
            }else if (changeType == ChangeType.POINTER_CHANGED){
                this.changeType = ChangeType.POINTER_CHANGED;
                updateWeights();
            }else if(changeType == ChangeType.VALUE_CHANGED){
                this.changeType = ChangeType.VALUE_CHANGED;

            }else{
                this.changeType = ChangeType.ALL;
            }


            
            recalculate = true;
        }else if(treeInput.get().somethingIsDirty()){
            recalculate = true;

        }
        if(recalculate){
            for(TreeLikelihood treeLik:treeLiks){
                treeLik.checkDirtiness();
            }
        }
        return recalculate;
    }

    @Override
    public void sample(State state, Random random) {
        throw new UnsupportedOperationException("Can't sample a fixed alignment!");
    }

    @Override
    public List<String> getArguments() {
        List<String> arguments = new ArrayList<String>();
        for(TreeLikelihood treeLik : treeLiks) {
            arguments.addAll(treeLik.getArguments());
        }
        return arguments;
    }

    public int[][] getClusterWeights(){
        int[][] clusterWeights = new int[treeLiks.size()][];
        for(int i = 0; i < clusterWeights.length;i++){
            clusterWeights[i] = treeLiks.get(i).getPatternWeights();
        }
        return clusterWeights;
    }


}
