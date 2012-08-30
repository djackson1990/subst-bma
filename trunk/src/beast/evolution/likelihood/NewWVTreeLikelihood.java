package beast.evolution.likelihood;

import beast.core.parameter.QuietRealParameter;
import beast.evolution.alignment.Alignment;
import beast.evolution.alignment.AscertainedAlignment;
import beast.evolution.branchratemodel.StrictClockModel;
import beast.evolution.sitemodel.QuietSiteModel;
import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;
import beast.evolution.substitutionmodel.SwitchingNtdBMA;
import beast.evolution.sitemodel.SiteModel;

import java.util.Arrays;

/**
 * @author Chieh-Hsi Wu
 */
public class NewWVTreeLikelihood extends TreeLikelihood {
    WVLikelihoodCore m_likelihoodCore;
   
    protected int[] patternWeights;
    protected int[] storedPatternWeights;
    protected boolean weightsChanged;
    //protected int weightSum = 0;
    //protected int storedWeightSum = 0;
    //protected boolean nonZeroPatternIncreased;
    protected double[] storedPatternLogLikelihoods;

    public NewWVTreeLikelihood(){

    }

    public NewWVTreeLikelihood(int[] patternWeights){
        this.patternWeights = patternWeights;
        storedPatternWeights = new int[patternWeights.length];


    }

    public String getSiteModelID(){
        return m_siteModel.getID();
    }





    @Override
    public void initAndValidate() throws Exception {
    	// sanity check: alignment should have same #taxa as tree

    	if (m_data.get().getNrTaxa() != m_tree.get().getLeafNodeCount()) {
    		throw new Exception("The number of nodes in the tree does not match the number of sequences");
    	}

        int nodeCount = m_tree.get().getNodeCount();
        m_siteModel = m_pSiteModel.get();
        m_siteModel.setDataType(m_data.get().getDataType());
        m_substitutionModel = m_siteModel.m_pSubstModel.get();

        if (m_pBranchRateModel.get() != null) {
        	m_branchRateModel = m_pBranchRateModel.get();
        } else {
            m_branchRateModel = new StrictClockModel();
        }
    	m_branchLengths = new double[nodeCount];
    	m_StoredBranchLengths = new double[nodeCount];

        int nStateCount = m_data.get().getMaxStateCount();
        //System.out.println("nStateCount: "+nStateCount);
        int nPatterns = m_data.get().getPatternCount();
        //System.out.println("patternWeights.length:"+patternWeights.length);

        boolean[] unmasked = new boolean[patternWeights.length];
        for(int i = 0; i < unmasked.length;i++){
                ///System.out.println(patternWeights[i]);
                unmasked[i] = patternWeights[i] > 0;
            }
        if (nStateCount == 4) {

            m_likelihoodCore = new WVLikelihoodCore4(unmasked);
        } else {
            m_likelihoodCore = new WVLikelihoodCore(nStateCount,unmasked);
        }
        //System.err.println("TreeLikelihood uses " + m_likelihoodCore.getClass().getName());

        m_fProportionInvariant = m_siteModel.getProportianInvariant();
        m_siteModel.setPropInvariantIsCategory(false);
        if (m_fProportionInvariant > 0) {
        	calcConstantPatternIndices(nPatterns, nStateCount);
        }
        addedPatternIds = new int[nPatterns];
        initCore();

        m_fPatternLogLikelihoods = new double[nPatterns];
        storedPatternLogLikelihoods = new double[nPatterns];
        m_fRootPartials = new double[nPatterns * nStateCount];
        m_nMatrixSize = (nStateCount +1)* (nStateCount+1);
        m_fProbabilities = new double[(nStateCount +1)* (nStateCount+1)];
        //System.out.println("m_fProbabilities: "+m_fProbabilities.length+" "+m_data.get());
        Arrays.fill(m_fProbabilities, 1.0);

        if (m_data.get() instanceof AscertainedAlignment) {
            m_bAscertainedSitePatterns = true;
        }
    }


    void initCore() {
        int nodeCount = m_tree.get().getNodeCount();
        m_likelihoodCore.initialize(
                nodeCount,
                m_data.get().getPatternCount(),
                m_siteModel.getCategoryCount(),
                true,
                m_useAmbiguities.get()
        );

        int extNodeCount = nodeCount / 2 + 1;
        int intNodeCount = nodeCount / 2;

        if (m_useAmbiguities.get()) {
        	setPartials(m_tree.get().getRoot(), m_data.get().getPatternCount());
        } else {
        	setStates(m_tree.get().getRoot(), m_data.get().getPatternCount());
        }
        m_nHasDirt = Tree.IS_FILTHY;
        for (int i = 0; i < intNodeCount; i++) {
            m_likelihoodCore.createNodePartials(extNodeCount + i);
        }
    }

    /** set leaf states in likelihood core **/
    void setStates(Node node, int patternCount) {
        if (node.isLeaf()) {
            int i;
            int[] states = new int[patternCount];
            for (i = 0; i < patternCount; i++) {
                states[i] = m_data.get().getPattern(node.getNr(), i);
            }
            m_likelihoodCore.setNodeStates(node.getNr(), states);

        } else {
            setStates(node.getLeft(), patternCount);
            setStates(node.getRight(), patternCount);
        }
    }

    /** set leaf partials in likelihood core **/
    void setPartials(Node node, int patternCount) {
        if (node.isLeaf()) {
        	Alignment data = m_data.get();
        	int nStates = data.getDataType().getStateCount();
            double[] partials = new double[patternCount * nStates];

            int k = 0;
            for (int iPattern = 0; iPattern < patternCount; iPattern++) {
            	int nState = data.getPattern(node.getNr(), iPattern);
            	boolean [] stateSet = data.getStateSet(nState);
        		for (int iState = 0; iState < nStates; iState++) {
        			partials[k++] = (stateSet[iState] ? 1.0 : 0.0);
            	}
            }
            m_likelihoodCore.setNodePartials(node.getNr(), partials);

        } else {
        	setPartials(node.getLeft(), patternCount);
        	setPartials(node.getRight(), patternCount);
        }
    }


    @Override
    public double calculateLogP() throws Exception {

        Tree tree = m_tree.get();
        boolean[] trueUnmasked = m_likelihoodCore.getUnmasked();
        //System.out.println("m_nHasDirt: "+m_nHasDirt);
        if(m_nHasDirt > -1){
            //System.out.println("addedPatternIdCount: "+addedPatternIdCount);
            if(addedPatternIdCount > 0){
                boolean[] tempUnmasked = new boolean[patternWeights.length];
                //System.out.println(addedPatternIdCount);
                for(int i = 0; i < addedPatternIdCount; i++){
                    tempUnmasked[addedPatternIds[i]] = true;
                    trueUnmasked[addedPatternIds[i]] = true;
                    //System.out.println("addedPatternId: "+addedPatternIds[i]);
                }
                //m_likelihoodCore.setUnmasked(trueUnmasked);
                m_likelihoodCore.setUnmasked(tempUnmasked);
            }
            /*for(int i = 0; i < trueUnmasked.length;i++){
                trueUnmasked[i] = patternWeights[i] > 0;
            } */

            /*boolean[] temp = m_likelihoodCore.getUnmasked();
            for(int i = 0; i < temp.length; i++){
                System.out.print(temp[i]+" ");
            }
            System.out.println(); */
            for(int i = 0; i < patternWeights.length; i++){
                if(patternWeights[i] == 0 && trueUnmasked[i]){
                    for(int j = 0; j < addedPatternIdCount;j++){
                        System.err.println(addedPatternIds[j]+" "+patternWeights[addedPatternIds[j]]);

                    }
                    throw new RuntimeException("what? "+i);

                }
            }
       	    traverse(tree.getRoot());
        }

        /*boolean[] U = m_likelihoodCore.getUnmasked();
        System.out.print("Unmasked: ");
        for(boolean u:U){
            System.out.print(u+" ");
        }
        System.out.println(); */

        calcLogP();

        m_nScale++;
        if (logP > 0 || (m_likelihoodCore.getUseScaling() && m_nScale > X)) {
            //System.out.println("Switch off scaling");
            m_likelihoodCore.setUseScaling(1.0);
            m_likelihoodCore.unstore();
            m_nHasDirt = Tree.IS_FILTHY;
            X *= 2;
           	traverse(tree.getRoot());
            calcLogP();
        } else if (logP == Double.NEGATIVE_INFINITY && m_fScale < 10) { // && !m_likelihoodCore.getUseScaling()) {
        	m_nScale = 0;
        	m_fScale *= 1.01;
            //System.out.println("Turning on scaling to prevent numeric instability " + m_fScale);
            m_likelihoodCore.setUseScaling(m_fScale);
            m_likelihoodCore.unstore();
            m_nHasDirt = Tree.IS_FILTHY;
           	traverse(tree.getRoot());
            calcLogP();
        }

        if(addedPatternIdCount > 0){
            m_likelihoodCore.setUnmasked(trueUnmasked);
        }


        return logP;
    }
    protected void calcLogP() throws Exception {
        logP = 0.0;
        if (m_bAscertainedSitePatterns) {
            double ascertainmentCorrection = ((AscertainedAlignment)m_data.get()).getAscertainmentCorrection(m_fPatternLogLikelihoods);
            for (int i = 0; i < m_data.get().getPatternCount(); i++) {
            	logP += (m_fPatternLogLikelihoods[i] - ascertainmentCorrection) * patternWeights[i];
            }
        } else {
            //System.err.println("Likelihood: ");
	        for (int i = 0; i < m_data.get().getPatternCount(); i++) {
                //System.out.println("logKappa: "+(((SwitchingNtdBMA)m_substitutionModel).getLogKappa()).getValue());
                //System.out.println((((SwitchingNtdBMA)m_substitutionModel).getIDNumber()));
                /*if(patternWeights[i]>0 && m_fPatternLogLikelihoods[i]==0){
                    ((SwitchingNtdBMA)m_substitutionModel).printDetails();
                    int pattern[] = m_data.get().getPattern(i);
                    for(int j = 0;j < pattern.length;j++){
                        System.out.print(pattern[j]+" ");
                    }
                    System.out.println();
                    throw new RuntimeException("WRONG!");
                }*/
                //System.out.println(m_data.get().getID()+" pattern "+i+" Likelihood: "+m_fPatternLogLikelihoods[i]+" "+patternWeights[i]);
	            logP += m_fPatternLogLikelihoods[i] * patternWeights[i];
                //System.out.println(m_fPatternLogLikelihoods[i] +" "+ patternWeights[i]);
	        }
        }
    }

    /* Assumes there IS a branch rate model as opposed to traverse() */
    int traverse(Node node) throws Exception {

        int update = (node.isDirty()| m_nHasDirt);
        //System.out.println("update node: "+update);
        int iNode = node.getNr();

        double branchRate = m_branchRateModel.getRateForBranch(node);
        double branchTime = node.getLength() * branchRate;
        m_branchLengths[iNode] = branchTime;

        // First update the transition probability matrix(ices) for this branch
        if (!node.isRoot() && (update != Tree.IS_CLEAN || branchTime != m_StoredBranchLengths[iNode])) {
            Node parent = node.getParent();
            m_likelihoodCore.setNodeMatrixForUpdate(iNode);
            for (int i = 0; i < m_siteModel.getCategoryCount(); i++) {
                double jointBranchRate = m_siteModel.getRateForCategory(i, node) * branchRate;
                //System.out.println(getID()+" mu: "+m_siteModel.getRateForCategory(i, node)+" "+branchRate);
                //System.out.println(m_data.get()+ " update node: "+jointBranchRate);
            	m_substitutionModel.getTransitionProbabilities(node, parent.getHeight(), node.getHeight(), jointBranchRate, m_fProbabilities);
                m_likelihoodCore.setNodeMatrix(iNode, i, m_fProbabilities);
            }
            update |= Tree.IS_DIRTY;
        }

        // If the node is internal, update the partial likelihoods.
        if (!node.isLeaf()) {

            // Traverse down the two child nodes
            Node child1 = node.getLeft(); //Two children
            int update1 = traverse(child1);

            Node child2 = node.getRight();
            int update2 = traverse(child2);

            // If either child node was updated then update this node too
            if (update1 != Tree.IS_CLEAN || update2 != Tree.IS_CLEAN) {

                int childNum1 = child1.getNr();
                int childNum2 = child2.getNr();
                if(addedPatternIdCount == 0){
                    m_likelihoodCore.setNodePartialsForUpdate(iNode);
                }
                update |= (update1|update2);
                if (update >= Tree.IS_FILTHY) {
                    m_likelihoodCore.setNodeStatesForUpdate(iNode);
                }

                if (m_siteModel.integrateAcrossCategories()) {

                    m_likelihoodCore.calculatePartials(childNum1, childNum2, iNode);
                } else {
                    throw new Exception("Error TreeLikelihood 201: Site categories not supported");
                    //m_pLikelihoodCore->calculatePartials(childNum1, childNum2, nodeNum, siteCategories);
                }

                if (node.isRoot()) {
                    // No parent this is the root of the beast.tree -
                    // calculate the pattern likelihoods
                    double[] frequencies = //m_pFreqs.get().
                            m_substitutionModel.getFrequencies();

                    double[] proportions = m_siteModel.getCategoryProportions(node);
                    m_likelihoodCore.integratePartials(node.getNr(), proportions, m_fRootPartials);

                    if (m_iConstantPattern != null) { // && !SiteModel.g_bUseOriginal) {
                    	m_fProportionInvariant = m_siteModel.getProportianInvariant();
                    	// some portion of sites is invariant, so adjust root partials for this
                    	for (int i : m_iConstantPattern) {
                			m_fRootPartials[i] += m_fProportionInvariant;
                    	}
                    }

                    m_likelihoodCore.calculateLogLikelihoods(m_fRootPartials, frequencies, m_fPatternLogLikelihoods);
                }

            }
        }
        return update;
    }

    public double getPatternLogLikelihood(int iPat){
        //System.out.println(iPat+" "+m_fPatternLogLikelihoods[iPat]);
        if(patternWeights[iPat] == 0){
            //throw new RuntimeException("Zero weight.");
            return Double.NaN;
        }

        return m_fPatternLogLikelihoods[iPat];
    }
    /**
     * check state for changed variables and update temp results if necessary *
     */
    @Override
    protected boolean requiresRecalculation() {

        m_nHasDirt = Tree.IS_CLEAN;

        if(weightsChanged){

            if(addedPatternIdCount > 0){
                m_nHasDirt = Tree.IS_FILTHY;
            }else{
                m_nHasDirt = -1;
            }
            //System.err.println("flag2");
            return true;
        }

        if (m_branchRateModel != null && m_branchRateModel.isDirtyCalculation()) {
            m_nHasDirt = Tree.IS_FILTHY;
            //System.err.println("flag3");
            return true;
        }

        if (m_data.get().isDirtyCalculation()) {
            m_nHasDirt = Tree.IS_FILTHY;
            //System.err.println("flag4");
            return true;
        }

        if (m_siteModel.isDirtyCalculation()) {
            m_nHasDirt = Tree.IS_DIRTY;
            //System.err.println("flag5");
            return true;
        }

        return m_tree.get().somethingIsDirty();
    }

    int[] addedPatternIds;
    int addedPatternIdCount = 0;
    public void addWeight(int patId, int dweight){
        if(patternWeights[patId] == 0 && dweight > 0){
            //System.out.println("patId: "+patId);
            addedPatternIds[addedPatternIdCount++] = patId;
        }
        patternWeights[patId] += dweight;
        //System.err.println("patId: "+patId+", patternWeights[patId]: "+patternWeights[patId]);
        weightsChanged = true;
    }

    public void removeWeight(int patId, int dweight){
        //System.out.println("patId: "+patId+", patternWeights[patId]: "+patternWeights[patId]);
        patternWeights[patId] -= dweight;

        if(patternWeights[patId] == 0){
            m_likelihoodCore.getUnmasked()[patId] = false;
        }else if(patternWeights[patId] < 0){
            for(int i = 0; i < patternWeights.length;i++){
                System.out.print(patternWeights[i]+" ");
            }
            System.out.println();
            throw new RuntimeException("NEGATIVE WEIGHTS: "+patternWeights[patId]+" "+dweight);
        }

        weightsChanged = true;
    }

    public void setWeight(int patId, int weight){
        patternWeights[patId] = weight;
        weightsChanged = true;
    }



    @Override
    public void store(){
        m_likelihoodCore.store();
        weightsChanged = false;
        addedPatternIds = new int[patternWeights.length];
        addedPatternIdCount = 0;
        System.arraycopy(patternWeights,0,storedPatternWeights,0,patternWeights.length);
        //System.err.println("m_fPatternLogLikelihoods: "+m_fPatternLogLikelihoods);
        //System.err.println("storedPatternLogLikelihoods: "+storedPatternLogLikelihoods);
        System.arraycopy(m_fPatternLogLikelihoods,0,storedPatternLogLikelihoods,0,m_fPatternLogLikelihoods.length);
        super.store();
    }

    @Override
    public void restore(){
        if(addedPatternIdCount == 0){
            m_likelihoodCore.restore();
        }else{
            m_likelihoodCore.restoreUnmaskedOnly();
        }
        int[] temp1 = patternWeights;
        patternWeights = storedPatternWeights;
        storedPatternWeights = temp1;

        double[] temp2 = m_fPatternLogLikelihoods;
        m_fPatternLogLikelihoods = storedPatternLogLikelihoods;
        storedPatternLogLikelihoods = temp2;

        weightsChanged = false;
        addedPatternIdCount = 0;
        super.restore();
    }

    public int[] getPatternWeights(){
        return patternWeights;
    }

    public void setPatternWeights(int[] newPatternWeights){
        System.arraycopy(newPatternWeights, 0, patternWeights, 0, newPatternWeights.length);
        boolean[] unmasked = new boolean[patternWeights.length];
        for(int i = 0;i < patternWeights.length;i++){
            unmasked[i] = patternWeights[i] > 0;
        }
        m_likelihoodCore.setUnmasked(unmasked);
    }

    public int weightSum(){
        int sum = 0;
        for(int weight:patternWeights){
            sum += weight;
        }
        return sum;
    }

    protected void makeAccept() {
    	super.accept();
    }

    public void printThings(){
        System.out.println("modelID: "+((SwitchingNtdBMA)m_substitutionModel).getIDNumber());
        System.out.println("modelID: " + ((QuietRealParameter) ((QuietSiteModel) m_siteModel).getRateParameter()).getIDNumber());
    }
}
