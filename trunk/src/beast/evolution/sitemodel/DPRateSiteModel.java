package beast.evolution.sitemodel;

import beast.core.Description;
import beast.core.Input;
import beast.core.parameter.*;
import beast.evolution.substitutionmodel.JC69;

import java.util.ArrayList;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class facilitates partition selection by DPP on site rates.")

public class DPRateSiteModel {

    private ArrayList<SiteModel> siteModels;
    private ArrayList<SiteModel> storedSiteModels;
    private DPPointer pointers;
    private ParameterList paramList;
    private boolean[] ratesDirty;
    private JC69 jc;
    private ChangeType changeType = ChangeType.ALL;
    private double[] pointerIndices;
    private double[] storedPointerIndices;


            //ParameterList
    public Input<ParameterList> paramListInput = new Input<ParameterList>(
            "paramList",
            "A list of unique parameter values",
            Input.Validate.REQUIRED
    );


    //assignment
    public Input<DPPointer> pointersInput = new Input<DPPointer>(
            "pointers",
            "array which points a set of unique parameter values",
            Input.Validate.REQUIRED
    );


    public void initAndValidate() throws Exception{
        jc = new JC69();
        pointers = pointersInput.get();
        pointerIndices = new double[pointers.getDimension()];
        storedPointerIndices = new double[pointers.getDimension()];
        setupPointerIndices();

        paramList = paramListInput.get();

        int dim = paramList.getDimension();
        siteModels = new ArrayList<SiteModel>();
        for(int i = 0;i < dim; i++){
            SiteModel siteModel = new SiteModel();
            RealParameter muParameter = paramList.getParameter(pointers.indexInList(i,paramList));
            siteModel.initByName(
                    "substModel", jc,
                    "mutationRate", muParameter
            );
            siteModels.add(siteModel);
        }
        ratesDirty = new boolean [dim];


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
            RealParameter muParameter = paramList.getParameter(paramList.getDimension()-1);
            siteModel.initByName(
                    "substModel", jc,
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
        }catch(Exception e){
            throw new RuntimeException(e);
        }

    }

    public ChangeType getChangeType(){
        return changeType;
    }


    public void setupPointerIndices(){
        for(int i = 0; i < ratesDirty.length;i++){
            //System.err.println("dirty1: "+i);
            pointerIndices[i] = pointers.indexInList(i,paramList);
        }

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

            }else {
                changeType = ChangeType.ALL;
            }
            recalculate = true;
            //System.err.println("dirty1");

        }else if (pointers.somethingIsDirty()){
            recalculate = true;
            //System.err.println("dirty2");
            ratesDirty = new boolean[ratesDirty.length];
            ratesDirty[pointers.getLastDirty()] = true;
        }

        return recalculate;
    }





    protected void store(){

        storedSiteModels = new ArrayList<SiteModel>();
        for(SiteModel siteModel:siteModels){
            storedSiteModels.add(siteModel);
        }

        System.arraycopy(pointerIndices,0,storedPointerIndices,0,pointerIndices.length);
        //System.out.println("storing");
    }

    public void restore(){

        ArrayList<SiteModel> tempList = storedSiteModels;
        storedSiteModels = siteModels;
        siteModels = tempList;
        changeType = ChangeType.ALL;

        double[] temp = pointerIndices;
        pointerIndices = storedPointerIndices;
        storedPointerIndices = temp;



        //System.out.println("restoring");

    }

}