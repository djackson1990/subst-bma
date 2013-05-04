package beast.evolution.likelihood;

import beast.core.Distribution;
import beast.core.Input;
import beast.core.State;
import beast.evolution.alignment.Alignment;
import beast.evolution.branchratemodel.BranchRateModel;
import beast.evolution.sitemodel.DummySiteModel;
import beast.evolution.tree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Chieh-Hsi Wu
 */
public class MultiTreesTempTreeLikelihood extends Distribution {
    public Input<List<Alignment>> alignmentsInput = new Input<List<Alignment>>(
            "alignment",
            "A list of alignments used as data.",
            new ArrayList<Alignment>(),
            Input.Validate.REQUIRED
    );

    public Input<List<Tree>> treesInput = new Input<List<Tree>>(
            "tree",
            "A list of trees representing the underlying phylogenies that generated the data.",
            new ArrayList<Tree>(),
            Input.Validate.REQUIRED
    );
    public Input<Boolean> useAmbiguitiesInput = new Input<Boolean>(
            "useAmbiguities",
            "flag to indicate leafs that sites containing ambigue states should be handled instead of ignored (the default)",
            false
    );

    public Input<BranchRateModel.Base> branchRateModelInput = new Input<BranchRateModel.Base>(
            "branchRateModel",
            "A model describing the rates on the branches of the beast.tree."
    );

    public Input<List<DummySiteModel>> siteModelsInput = new Input<List<DummySiteModel>>(
            "siteModel",
            "Models the evolution of a site in an alignment",
            Input.Validate.REQUIRED
    );

    private List<Alignment> alignments;
    private List<Tree> trees;
    private TempTreeLikelihood[] tempTreeLikelihoods;
    public void initAndValidate(){
        alignments = alignmentsInput.get();
        trees = treesInput.get();
        tempTreeLikelihoods = new TempTreeLikelihood[alignments.size()];
        for(int i = 0; i < tempTreeLikelihoods.length; i++){
            tempTreeLikelihoods[i] = new TempTreeLikelihood(
                    alignments.get(i),
                    trees.get(i),
                    useAmbiguitiesInput.get(),
                    siteModelsInput.get().get(i),
                    branchRateModelInput.get()

            );
        }



    }

    public List<String> getConditions(){
        return null;
    }

    public List<String> getArguments(){
        return null;

    }

    public void sample(State state, Random random){
        throw new RuntimeException("Not yet implemented as it doesn't make much sense to do so in this case");
    }
}
