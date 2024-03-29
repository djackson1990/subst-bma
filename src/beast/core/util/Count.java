package beast.core.util;

import beast.core.*;

import java.io.PrintStream;

/**
 * @author Chieh-Hsi Wu
 */
@Description("Reports the number of items in a list.")
public class Count extends CalculationNode implements Loggable {
    public Input<PluginList> listInput = new Input<PluginList>("list", "list of items to be counted", Input.Validate.REQUIRED);
    PluginList list;
    @Override
	public void initAndValidate() {
		list = listInput.get();
	}

    @Override
	public void init(PrintStream out) throws Exception {
        out.print("count("+((BEASTObject)list).getID() + ")\t");
    }

    @Override
	public void log(int nSample, PrintStream out) {
    	out.print(list.getDimension() + "\t");
	}

    @Override
	public void close(PrintStream out) {
		// nothing to do
	}
}
