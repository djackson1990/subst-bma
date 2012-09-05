package beast.evolution.tree;

import beast.core.CalculationNode;
import beast.core.Input;
import beast.core.Plugin;
import beast.core.Valuable;

/**
 * @author Chieh-Hsi Wu
 *
 */
public class TreeHeightValuable extends CalculationNode implements Valuable {
    public Input<Tree> treeInput = new Input<Tree>("tree", "The tree of interest, where the root height of which is of interest.", Input.Validate.REQUIRED);


    private Tree tree;
    public void initAndValidate(){
        tree = treeInput.get();
    }


    public int getDimension(){
        return 1;
    }

    public double getArrayValue(){
        return tree.getRoot().getHeight();
    }

    public double getArrayValue(int dim){
        if(dim < 1){
            return getArrayValue();

        }else {
            throw new RuntimeException("This is a unidimensional valuable, so please use getArrayValue() instead to retrieve the root height.");
        }
    }

}
