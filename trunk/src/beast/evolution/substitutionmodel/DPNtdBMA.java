package beast.evolution.substitutionmodel;

import beast.core.CalculationNode;
import beast.core.Input;
import beast.core.parameter.*;
import beast.evolution.sitemodel.SiteModel;

import java.util.ArrayList;

/**
 * @author Chieh-Hsi Wu
 */
public class DPNtdBMA extends CalculationNode {
    ArrayList<NtdBMA> ntdBMAs = new ArrayList<NtdBMA>();
    ArrayList<NtdBMA> storedNtdBMAs = new ArrayList<NtdBMA>();


    public Input<ParameterList> paramListInput = new Input<ParameterList>(
            "paramList",
            "A list of unique parameter values",
            Input.Validate.REQUIRED
    );

    public Input<ParameterList> freqListInput = new Input<ParameterList>(
            "paramList",
            "A list of unique parameter values",
            Input.Validate.REQUIRED
    );

    public Input<ParameterList> modelListInput = new Input<ParameterList>(
            "modelList",
            "A list of unique model values",
            Input.Validate.REQUIRED
    );


    //assignment
    public Input<DPPointer> pointersInput = new Input<DPPointer>(
            "pointers",
            "array which points a set of unique parameter values",
            Input.Validate.REQUIRED
    );

    public Input<DPPointer> modelPointersInput = new Input<DPPointer>(
            "pointers",
            "array which points a set of unique model",
            Input.Validate.REQUIRED
    );

    public Input<DPPointer> freqPointersInput = new Input<DPPointer>(
            "pointers",
            "array which points a set of unique model",
            Input.Validate.REQUIRED
    );


    private ParameterList paramList;
    private ParameterList modelList;
    private ParameterList freqList;
    private DPPointer pointers;
    private DPPointer modelPointers;
    private DPPointer freqPointers;

    private int[] pointerIndices;

    private ChangeType changeType;
    public void initAndValidate(){
        paramList = paramListInput.get();
        modelList = modelListInput.get();
        freqList = freqListInput.get();
        pointers = pointersInput.get();

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
        pointerIndices = new int[pointers.getDimension()];
        
    }

    public NtdBMA getSiteModel(int index){
        return ntdBMAs.get(index);
    }

    public int getSiteModelCount(){
        return ntdBMAs.size();
    }

    private void addSiteModel(){

        NtdBMA ntdBMA = new NtdBMA();
        RealParameter parameter = paramList.getParameter(paramList.getDimension()-1);
        RealParameter model = modelList.getParameter(modelList.getDimension()-1);
        RealParameter freqs = freqList.getParameter(freqList.getDimension()-1);
        Frequencies frequencies = new Frequencies();
        try{
            frequencies.initByName("frequencies",freqs);
        }catch(Exception e ){
            new RuntimeException(e);
        }

        ntdBMAs.add(createNtdBMA(parameter,model,freqs));


    }

    private void removeSiteModel(int index){
        ntdBMAs.remove(index);
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
            new RuntimeException(e);
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
            if(changeType == ChangeType.ADDED){
                addSiteModel();
                setupPointerIndices();
                changeType = ChangeType.ADDED;

            }else if(changeType == ChangeType.REMOVED){
                removeSiteModel(paramList.getRemovedIndex());
                setupPointerIndices();
                changeType = ChangeType.REMOVED;
            }else if(changeType == ChangeType.VALUE_CHANGED){
                changeType = ChangeType.VALUE_CHANGED;

            }else{
                changeType = ChangeType.ALL;
            }
            recalculate = true;
            //System.err.println("dirty1");

        }else if (pointers.somethingIsDirty()){
            recalculate = true;
        }

        return recalculate;
    }

    
}
