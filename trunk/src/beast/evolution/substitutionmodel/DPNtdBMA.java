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

    public Input<ParameterList> freqsListInput = new Input<ParameterList>(
            "freqsList",
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
    private ParameterList freqsList;
    private DPPointer pointers;
    //private DPPointer modelPointers;
    //private DPPointer freqPointers;

    private int[] pointerIndices;
    private int removedIndex = -1;

    private ChangeType changeType;
    public void initAndValidate(){
        paramList = paramListInput.get();
        modelList = modelListInput.get();
        freqsList = freqsListInput.get();
        pointers = freqPointersInput.get();

        int dimParamList = paramList.getDimension();
        for(int i = 0; i < dimParamList;i++){
            ntdBMAs.add(
                    createNtdBMA(
                            paramList.getParameter(i),
                            modelList.getParameter(i),
                            freqsList.getParameter(i)
                    )
            );

        }
        //System.err.println("model counts: "+ntdBMAs.size());
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

    public int[] getPointerIndices(){
        return pointerIndices;
    }

    private void addModel(){


        RealParameter parameter = paramList.getParameter(paramList.getDimension()-1);
        RealParameter model = modelList.getParameter(modelList.getDimension()-1);
        RealParameter freqs = freqsList.getParameter(freqsList.getDimension()-1);
        Frequencies frequencies = new Frequencies();
        try{
            frequencies.initByName("frequencies",freqs);
        }catch(Exception e){
            throw new RuntimeException(e);
        }

        ntdBMAs.add(createNtdBMA(parameter,model,freqs));


    }

    private void removeModel(int index){
        ntdBMAs.remove(index);
        removedIndex = index;
        //System.err.println("removeNtdModel: "+index);
    }

    public int getLastDirtySite(){
        return pointers.getLastDirty();
    }

    public int getRemovedIndex(){
        return removedIndex;
    }

    public ChangeType getChangeType(){
        return changeType;
    }

    int prevCluster = -1;
    public void setupPointerIndices(){
        //System.out.println("changeType: "+changeType);
        if(changeType == ChangeType.ADDED || changeType == ChangeType.POINTER_CHANGED){
            int changedIndex = pointers.getLastDirty();
            prevCluster = pointerIndices[changedIndex];
            pointerIndices[changedIndex] = pointers.indexInList(changedIndex,freqsList);
        }else if (changeType == ChangeType.REMOVED){
            resetAllPointerIndices();
        }else if (changeType == ChangeType.ALL){
            resetAllPointerIndices();
        }
    }

    public int getPrevCluster(int index){
        return pointers.storedIndexInList(index,freqsList);
    }

    public int getCurrCluster(int index){
        return pointers.indexInList(index,freqsList);
    }

 

    public void resetAllPointerIndices(){
        for(int i = 0; i < pointerIndices.length;i++){
            pointerIndices[i] = pointers.indexInList(i,freqsList);
        }
    }







    public static NtdBMA createNtdBMA(
            RealParameter modelParameters,
            RealParameter modelCode,
            RealParameter frequencies){
        RealParameter logKappa = new RealParameterWrapper(modelParameters, 0);
        RealParameter logTN = new RealParameterWrapper(modelParameters,1);
        RealParameter logAC = new RealParameterWrapper(modelParameters,2);
        RealParameter logAT = new RealParameterWrapper(modelParameters,3);
        RealParameter logGC = new RealParameterWrapper(modelParameters,4);
        RealParameter logGT = new RealParameterWrapper(modelParameters,5);
       
        //System.err.println();
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

    public void store(){
        for(NtdBMA ntdBMA:ntdBMAs){
            ntdBMA.store();
        }
        prevCluster = -1;
        super.store();
    }

    public void restore(){
        for(NtdBMA ntdBMA:ntdBMAs){
            ntdBMA.restore();
        }
        prevCluster = -1;
        super.restore();
        //System.out.println("ntd restoring");
    }



    public boolean requiresRecalculation(){

        boolean recalculate = false;
        //System.err.println("dirty0");
        if(paramList.somethingIsDirty()||modelList.somethingIsDirty()||freqsList.somethingIsDirty()){
            //System.out.println("dirty0: "+freqsList.getChangeType());
            ChangeType changeType;
            if(paramList.somethingIsDirty()){
                changeType = paramList.getChangeType();

            }else if(modelList.somethingIsDirty()){
                changeType = modelList.getChangeType();

            }else{
                changeType = freqsList.getChangeType();

            }

            //System.err.println("dirty0: "+changeType);
            if(changeType == ChangeType.ADDED){
                //System.err.println(getID()+"model added");
                addModel();

                this.changeType = ChangeType.ADDED;
                setupPointerIndices();

            }else if(changeType == ChangeType.REMOVED){
                //System.out.println(getID()+"model removed, "+paramList.getRemovedIndex());
                removeModel(freqsList.getRemovedIndex());

                this.changeType = ChangeType.REMOVED;
                setupPointerIndices();

            }else if(changeType == ChangeType.VALUE_CHANGED){
                //System.err.println(getID()+": model changed");
                this.changeType = ChangeType.VALUE_CHANGED;
                for(NtdBMA ntdBMA:ntdBMAs){
                    ntdBMA.checkDirtiness();
                }

            }else{
                this.changeType = ChangeType.ALL;
                setupPointerIndices();
                for(NtdBMA ntdBMA:ntdBMAs){
                    ntdBMA.checkDirtiness();
                }
            }
            recalculate = true;
            //System.err.println("dirty1");

        }else if (pointers.somethingIsDirty()){
            recalculate = true;

            this.changeType = ChangeType.POINTER_CHANGED;
            setupPointerIndices();
        }

        //System.out.println("recalculate: "+recalculate);

        return recalculate;
    }


}
