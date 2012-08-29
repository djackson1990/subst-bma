package beast.evolution.sitemodel;

import beast.core.CalculationNode;
import beast.core.PluginList;
import beast.core.Input;
import beast.core.parameter.ChangeType;
import beast.core.parameter.RealParameter;
import beast.evolution.substitutionmodel.DPNtdBMA;

import java.util.ArrayList;

/**
 * @author Chieh-Hsi Wu
 *
 */
public abstract class DPSiteModel extends CalculationNode implements PluginList {

    protected ArrayList<QuietSiteModel> siteModels;
    protected ArrayList<QuietSiteModel> storedSiteModels;
    protected ChangeType changeType = ChangeType.ALL;

    public void initAndValidate() throws Exception{
        siteModels = new ArrayList<QuietSiteModel>();
    }


    public int getDimension(){
        return siteModels.size();
    }

    public int getSiteModelCount(){
        return siteModels.size();
    }


    public abstract int getRemovedIndex();




    public SiteModel getSiteModel(int index){
        return siteModels.get(index);
    }

    public abstract int getLastDirtySite();

    public SiteModel getLastAdded(){
        return getSiteModel(siteModels.size()-1);
    }

    protected abstract void addSiteModel();

    protected void removeSiteModel(int index){
        siteModels.remove(index);
        //System.err.println("removeSiteModel: "+index);
    }

    protected void store(){

        storedSiteModels = new ArrayList<QuietSiteModel>();
        for(QuietSiteModel siteModel:siteModels){
            storedSiteModels.add(siteModel);
        }

        super.store();
        //System.out.println("storing");
    }


    public void restore(){

        ArrayList<QuietSiteModel> tempList = storedSiteModels;
        storedSiteModels = siteModels;
        siteModels = tempList;
        changeType = ChangeType.ALL;
        super.restore();
       //System.out.println("restoring");
    }


    protected void accept() {
        for(QuietSiteModel siteModel:siteModels){
            siteModel.makeAccept();
        }
    	super.accept();
    }

    public ChangeType getChangeType(){
        return changeType;
    }

    public abstract int[] getSwappedSites();



    public abstract int getPrevCluster(int index);
    public abstract int getCurrCluster(int index);
    public abstract int getDirtySiteModelIndex();





}