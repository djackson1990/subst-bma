package beast.evolution.sitemodel;

import beast.core.Description;
import beast.core.Input;
import beast.core.CalculationNode;
import beast.core.PluginList;
import beast.core.parameter.*;
import beast.evolution.substitutionmodel.DPNtdBMA;

import java.util.ArrayList;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This site model class facilitates partition selection by DPP on nucleotide substitution models.")

public class DPNtdSiteModel extends CalculationNode implements PluginList {

    private ArrayList<SiteModel> siteModels;
    private ArrayList<SiteModel> storedSiteModels;
    private DPNtdBMA dpNtdBMA;
    private boolean[] ratesDirty;
    private ChangeType changeType = ChangeType.ALL;
    private double[] pointerIndices;
    private double[] storedPointerIndices;

    private RealParameter muParameter;

    //private int removedIndex = -1;

    public Input<DPNtdBMA> dpNtdBMAInput = new Input<DPNtdBMA>(
            "ntdBMAList",
            "array which points a set of unique parameter values",
            Input.Validate.REQUIRED
    );


    public Input<RealParameter> muParameterInput = new Input<RealParameter>(
            "mu",
            "subsitution rate for all partitions in an alignment",
            Input.Validate.REQUIRED
    );



    public void initAndValidate() throws Exception{
        dpNtdBMA = dpNtdBMAInput.get();
        muParameter = muParameterInput.get();
        int ntdBMACount = dpNtdBMA.getDimension();


        //Setting up site models
        siteModels = new ArrayList<SiteModel>();
        for(int i = 0;i < ntdBMACount; i++){
            SiteModel siteModel = new SiteModel();
            siteModel.initByName(
                    "substModel", dpNtdBMA.getModel(i),
                    "mutationRate", muParameter
            );
            siteModels.add(siteModel);
        }
    }

    public int getDimension(){
        return siteModels.size(); 
    }

    public SiteModel getSiteModel(int index){
        return siteModels.get(index);
    }

    public int getSiteModelCount(){
        return siteModels.size();
    }

    public boolean[] getSiteDirtiness(){
        return ratesDirty;
    }

    private void addSiteModel(){
        try{
            SiteModel siteModel = new SiteModel();
            siteModel.initByName(
                    "substModel", dpNtdBMA.getModel(dpNtdBMA.getDimension()-1),
                    "mutationRate", muParameter
            );
            siteModels.add(siteModel);
        }catch(Exception e){
            throw new RuntimeException(e);
        }

    }

    private void removeSiteModel(int index){
        try{

            siteModels.remove(index);
            //System.err.println("removeSiteModel: "+index);
        }catch(Exception e){
            throw new RuntimeException(e);
        }

    }

    public ChangeType getChangeType(){
        return changeType;
    }


    public void setupPointerIndices(){
        /*for(int i = 0; i < ratesDirty.length;i++){
            //System.err.println("dirty1: "+i);
            pointerIndices[i] = pointers.indexInList(i,paramList);
        }*/
    }


    public boolean requiresRecalculation(){

        boolean recalculate = false;
        if(dpNtdBMA.isDirtyCalculation()){
            ChangeType changeType = dpNtdBMA.getChangeType();
            //System.err.println("dpNtd: "+changeType);
            if(changeType == ChangeType.ADDED){
                addSiteModel();
                changeType = ChangeType.ADDED;

            }else if(changeType == ChangeType.REMOVED){
                removeSiteModel(dpNtdBMA.getRemovedIndex());
                changeType = ChangeType.REMOVED;
            }else if(changeType == ChangeType.VALUE_CHANGED){
                changeType = ChangeType.VALUE_CHANGED;

            }else{
                changeType = ChangeType.ALL;
            }
            recalculate = true;

        }

        return recalculate;
    }

    protected void store(){
        storedSiteModels = new ArrayList<SiteModel>();
        for(SiteModel siteModel:siteModels){
            storedSiteModels.add(siteModel);
        }
        //System.arraycopy(pointerIndices,0,storedPointerIndices,0,pointerIndices.length);
        //System.out.println("storing");
    }

    public void restore(){

        ArrayList<SiteModel> tempList = storedSiteModels;
        storedSiteModels = siteModels;
        siteModels = tempList;
        changeType = ChangeType.ALL;

        //double[] temp = pointerIndices;
        //pointerIndices = storedPointerIndices;
        //storedPointerIndices = temp;

       //System.out.println("restoring");
    }

}
