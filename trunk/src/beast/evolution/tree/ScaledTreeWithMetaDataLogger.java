package beast.evolution.tree;

import beast.core.*;
import beast.core.parameter.RealParameter;
import beast.evolution.branchratemodel.BranchRateModel;

import java.io.PrintStream;

/**
 * @author Chieh-Hsi Wu
 */
@Description("Scales the tree with the provided scaler and then log it with metadata.")
public class ScaledTreeWithMetaDataLogger  extends TreeWithMetaDataLogger implements Loggable {
    public Input<Scaler> m_scaler = new Input<Scaler>("scaler","To scale the branch lengths by some number.", Input.Validate.REQUIRED);
    public Input<RealParameter> scalerParameterInput = new Input<RealParameter>("scalerParameter", "Some parameter that scales the tree branches", Input.Validate.XOR, m_scaler);
    Scaler scaler;
    RealParameter scaleParameter;
    private double scaleFactor;

	@Override
	public void initAndValidate() throws Exception{
		super.initAndValidate();
        scaler = m_scaler.get();
        scaleParameter = scalerParameterInput.get();
	}

    @Override
	public void log(int nSample, PrintStream out) {
		// make sure we get the current version of the inputs
        Tree tree = (Tree) treeInput.get().getCurrent();
        Function metadata = parameterInput.get();
        if (metadata instanceof StateNode) {
        	metadata = ((StateNode) metadata).getCurrent();
        }

        BranchRateModel.Base branchRateModel = clockModelInput.get();
        // write out the log tree with meta data
        out.print("tree STATE_" + nSample + " = ");
		tree.getRoot().sort();
        if(scaler != null){
            scaleFactor = scaler.getScaleFactor();
        }else{
            scaleFactor = scaleParameter.getValue();
        }
		out.print(toNewick(tree.getRoot(), metadata, branchRateModel, scaleFactor));
        //out.print(tree.getRoot().toShortNewick(false));
        out.print(";");
	}



    String toNewick(Node node, Function metadata, BranchRateModel.Base branchRateModel, double scaleFactor) {
        StringBuffer buf = new StringBuffer();
        if (node.getLeft() != null) {
            buf.append("(");
            buf.append(toNewick(node.getLeft(), metadata, branchRateModel, scaleFactor));
            if (node.getRight() != null) {
                buf.append(',');
                buf.append(toNewick(node.getRight(), metadata, branchRateModel, scaleFactor));
            }
            buf.append(")");
        } else {
            buf.append(node.labelNr + 1);
        }
        if (someMetaDataNeedsLogging) {
	        buf.append("[");
	        if (metadata != null) {
	            buf.append(metaDataLabel);
	            buf.append(metadata.getArrayValue(node.labelNr));
	            if (branchRateModel != null) {
	                buf.append(",");
	            }
	        }
	        if (branchRateModel != null) {
	            buf.append("rate=");
	            buf.append(branchRateModel.getRateForBranch(node));
	        }
	        buf.append(']');
        }
        buf.append(":").append(node.getLength()*scaleFactor);
        return buf.toString();
    }

}
