package beast.core.parameter;

import beast.core.Input;
import beast.core.Loggable;
import beast.core.Plugin;
import beast.evolution.tree.TreeHeightValuable;

import java.io.PrintStream;
import java.util.logging.StreamHandler;

/**
 * Created by IntelliJ IDEA.
 * User: cwu080
 * Date: 15/02/12
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParameterMeanLogger extends Plugin implements Loggable {
    //ParameterList
    public Input<ParameterList> paramListInput = new Input<ParameterList>(
            "paramList",
            "A list of unique parameter values",
            Input.Validate.REQUIRED
    );

    //assignment
    public Input<DPPointer> pointersInput = new Input<DPPointer>(
            "pointers",
            "An array which points a set of unique parameter values",
            Input.Validate.REQUIRED
    );

    public Input<DPValuable> dpValInput = new Input<DPValuable>(
            "dpVal",
            "Returns the number of sites in each cluster",
            Input.Validate.REQUIRED
    );

    public Input<TreeHeightValuable> treeHeightInput = new Input<TreeHeightValuable>(
            "tree",
            "Returns the number of sites in each cluster",
            Input.Validate.REQUIRED
    );

    private DPValuable dpVal;
    private DPPointer pointers;
    private ParameterList paramList;
    private TreeHeightValuable treeHeight;

    public void initAndValidate(){
        dpVal = dpValInput.get();
        pointers = pointersInput.get();
        paramList = paramListInput.get();
        treeHeight = treeHeightInput.get();
    }

    public void init(PrintStream out){
        out.print(pointersInput.get().getID()+".mean+\t");
    }


    public void log(int nSample, PrintStream out){
        double mean = 0.0;
        int[] clusterCounts = dpVal.getClusterCounts();
        for(int i = 0; i < clusterCounts.length;i++){
            mean += clusterCounts[i]*paramList.getValue(i,0);
        }
        mean /= pointers.getDimension();
        out.print(mean*treeHeight.getArrayValue()+"\t");

    }

    public void close(PrintStream out){}
}
