package beast.evolution.alignment;

import beast.core.Input;
import beast.core.Description;
import beast.evolution.tree.Tree;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class represents an alignment with variable weights.")
public class WVAlignment extends Alignment{
    public Input<Alignment> alignmentInput = new Input<Alignment>("data", "sequence data for the beast.tree", Input.Validate.REQUIRED);
    public Input<Alignment> m_data = new Input<Alignment>("data", "sequence data for the beast.tree", Input.Validate.REQUIRED);
        public Input<Tree> m_tree = new Input<Tree>("tree", "phylogenetic beast.tree with sequence data in the leafs", Input.Validate.REQUIRED);
        
    public boolean weightsChanged = false;
    public int[] storedWeight;
    public void initAndValidate(){
        this.alignment = alignmentInput.get();

    }

    private Alignment alignment;
    

    public WVAlignment(Alignment alignment){
        this(alignment,alignment.getWeights());

    }


    public WVAlignment(Alignment alignment, int[] weights){
        this.alignment = alignment;
        m_nWeight = weights;
        storedWeight = new int[weights.length];
    }

     public int getNrTaxa() {
        return alignment.getNrTaxa();
    }

    public int getTaxonIndex(String sID) {
        return alignment.getTaxonIndex(sID);
    }

    public int getPatternCount() {
        return alignment.getPatternCount();
    }

    public int[] getPattern(int id) {
        return alignment.getPattern(id);
    }

    public int getPattern(int iTaxon, int id) {
        return alignment.getPattern(iTaxon,id);
    }

    public int getPatternWeight(int id) {
        return alignment.getPatternWeight(id);
    }

    public int getMaxStateCount() {
        return alignment.getMaxStateCount();
    }

    public int getPatternIndex(int iSite) {
        return alignment.getPatternIndex(iSite);
    }

    public int getSiteCount() {
        return alignment.getSiteCount();
    }

    public int [] getWeights() {
        return m_nWeight; //TODO TO BE ONVERRIDEN!
    }

    public boolean[] getStateSet(int iState) {
    	return alignment.getStateSet(iState);
    }

    public void setWeight(int patId, int weight){
        m_nWeight[patId] = weight;
        weightsChanged = true;
    }

    @Override
    public void store(){
        weightsChanged = false;
        System.arraycopy(m_nWeight,0,storedWeight,0,m_nWeight.length);
        super.store();
    }

    @Override
    public void restore(){
        weightsChanged = false;
        
        int[] temp = m_nWeight;
        m_nWeight = storedWeight;
        storedWeight = temp;

        super.restore();
    }

    @Override
    public boolean requiresRecalculation(){
        return weightsChanged; 
    }
   
}
