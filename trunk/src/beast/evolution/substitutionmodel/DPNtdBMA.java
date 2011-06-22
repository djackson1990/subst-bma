package beast.evolution.substitutionmodel;

import beast.core.CalculationNode;
import beast.core.Input;
import beast.core.PluginList;
import beast.core.Description;
import beast.core.parameter.*;
import java.util.ArrayList;

/**
 * @author Chieh-Hsi Wu
 */
@Description("A list of NtdBMA classes for DPP.")
public class DPNtdBMA extends CalculationNode implements PluginList {
    ArrayList<NtdBMA> ntdBMAs = new ArrayList<NtdBMA>();
    ArrayList<NtdBMA> storedNtdBMAs = new ArrayList<NtdBMA>();


    public Input<ParameterList> paramListInput = new Input<ParameterList>(
            "paramList",
            "A list of unique parameter values",
            Input.Validate.REQUIRED
    );

    public Input<ParameterList> freqListInput = new Input<ParameterList>(
            "freqList",
            "A list of unique parameter values",
            Input.Validate.REQUIRED
    );

    public Input<ParameterList> modelListInput = new Input<ParameterList>(
            "modelList",
            "A list of unique model values",
            Input.Validate.REQUIRED
    );


    //assignment
    public Input<DPPointer> paramPointersInput = new Input<DPPointer>(
            "paramPointers",
            "array which points a set of unique parameter values",
            Input.Validate.REQUIRED
    );

    public Input<DPPointer> modelPointersInput = new Input<DPPointer>(
            "modelPointers",
            "array which points a set of unique model",
            Input.Validate.REQUIRED
    );

    public Input<DPPointer> freqPointersInput = new Input<DPPointer>(
            "freqPointers",
            "array which points a set of unique model",
            Input.Validate.REQUIRED
    );


    private ParameterList paramList;
    private ParameterList modelList;
    private ParameterList freqList;
    private DPPointer pointers;
    //private DPPointer modelPointers;
    //private DPPointer freqPointers;

    private int[] pointerIndices;
    private int removedIndex = -1;

    private ChangeType changeType;
    public void initAndValidate(){
        paramList = paramListInput.get();
        modelList = modelListInput.get();
        freqList = freqListInput.get();
        pointers = freqPointersInput.get();

        int dimParamList = paramList.getDimension();
        for(int i = 0; i < dimParamList;i++){
            ntdBMAs.add(
                    createNtdBMA(
                            paramList.getParameter(i),
                            modelList.getParameter(i),
                            freqList.getParameter(i)
                    )
            );
            
        }
        System.err.println("model counts: "+ntdBMAs.size());
        pointerIndices = new int[pointers.getDimension()];
        
    }

    public int getDimension(){
        return ntdBMAs.size();
    }

    public NtdBMA getModel(int index){
        return ntdBMAs.get(index);
    }

    public int getSiteModelCount(){
        return ntdBMAs.size();
    }

    private void addSiteModel(){


        RealParameter parameter = paramList.getParameter(paramList.getDimension()-1);
        RealParameter model = modelList.getParameter(modelList.getDimension()-1);
        RealParameter freqs = freqList.getParameter(freqList.getDimension()-1);
        Frequencies frequencies = new Frequencies();
        try{
            frequencies.initByName("frequencies",freqs);
        }catch(Exception e){
            throw new RuntimeException(e);
        }

        ntdBMAs.add(createNtdBMA(parameter,model,freqs));


    }

    private void removeSiteModel(int index){
        ntdBMAs.remove(index);
        removedIndex = index;
        //System.err.println("removeNtdModel: "+index);
    }

    public int getRemovedIndex(){
        return removedIndex;
    }

    public ChangeType getChangeType(){
        return changeType;
    }

    public void setupPointerIndices(){
        for(int i = 0; i < pointerIndices.length;i++){
            pointerIndices[i] = pointers.indexInList(i,paramList);
        }
    }




    public NtdBMA createNtdBMA(RealParameter modelParameters, RealParameter modelCode, RealParameter freqs){
        RealParameter logKappa = new RealParameterWrapper(modelParameters, 0);
        RealParameter logTN = new RealParameterWrapper(modelParameters,1);
        RealParameter logAC = new RealParameterWrapper(modelParameters,2);
        RealParameter logAT = new RealParameterWrapper(modelParameters,3);
        RealParameter logGC = new RealParameterWrapper(modelParameters,4);
        RealParameter logGT = new RealParameterWrapper(modelParameters,5);
        Frequencies frequencies = new Frequencies();
        try{
            frequencies.initByName("frequencies",freqs);

        }catch(Exception e ){
            throw new RuntimeException(e);
        }

        NtdBMA  ntdBMA = new NtdBMA(
                logKappa,
                logTN,
                logAC,
                logAT,
                logGC,
                logGT,
                modelCode,
                frequencies
        );
        return ntdBMA;

        
    }



        public boolean requiresRecalculation(){

        boolean recalculate = false;
        //System.err.println("dirty0");
        if(paramList.somethingIsDirty()){
            ChangeType changeType = paramList.getChangeType();
            //System.err.println("dirty0: "+changeType);
            if(changeType == ChangeType.ADDED){
                //System.err.println(getID()+"model added");
                addSiteModel();
                //setupPointerIndices();
                this.changeType = ChangeType.ADDED;

            }else if(changeType == ChangeType.REMOVED){
                //System.err.println(getID()+"model removed, "+paramList.getRemovedIndex());
                removeSiteModel(paramList.getRemovedIndex());
                //setupPointerIndices();
                this.changeType = ChangeType.REMOVED;

            }else if(changeType == ChangeType.VALUE_CHANGED){
                //System.err.println(getID()+": model changed");
                this.changeType = ChangeType.VALUE_CHANGED;

            }else if(changeType == ChangeType.POINTER_CHANGED){
                this.changeType = ChangeType.POINTER_CHANGED;

            }else{
                this.changeType = ChangeType.ALL;
            }
            recalculate = true;
            //System.err.println("dirty1");

        }else if (pointers.somethingIsDirty()){
            recalculate = true;
            this.changeType = ChangeType.POINTER_CHANGED;
        }

        return recalculate;
    }

    
}
