package beast.evolution.tree;

import beast.core.Input;
import beast.core.Loggable;
import beast.evolution.tree.Scaler;
import beast.evolution.tree.Tree;
import beast.evolution.tree.TreeHeightLogger;

import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA.
 * User: cwu080
 * Date: 6/12/11
 * Time: 10:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScaledTreeHeightLogger extends TreeHeightLogger {
    public Input<Scaler> m_scaler = new Input<Scaler>("scaler","To scale the branch lengths by some number.", Input.Validate.REQUIRED);
    Scaler scaler;

	public void initAndValidate() {
        super.initAndValidate();
        scaler = m_scaler.get();
	}



    public void log (int nSample, PrintStream out){
        final Tree tree = m_tree.get();
		out.print(tree.getRoot().getHeight()*scaler.getScaleFactor() + "\t");
    }



}
