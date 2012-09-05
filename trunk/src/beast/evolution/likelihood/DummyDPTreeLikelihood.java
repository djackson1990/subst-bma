package beast.evolution.likelihood;

/**
 * Created by IntelliJ IDEA.
 * User: cwu080
 * Date: 1/09/2011
 * Time: 4:52:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class DummyDPTreeLikelihood extends DPTreeLikelihood{
    public void initAndValidate() throws Exception{
        super.initAndValidate();
    }

    @Override
    public double calculateLogP(){
        double sum =0.0;
        for(NewWVTreeLikelihood treeLik:treeLiks){
            sum+=treeLik.weightSum();
        }
        if(sum != alignment.getSiteCount()){
          throw new RuntimeException("weights do not add up correctly");
        }
        /*else{
            System.out.println("good");
        }*/
        logP = 0.0;
        return logP;
    }
    public double getSiteLogLikelihood(int iCluster, int iSite){
        return 0.0;
    }

    /*public void addTreeLikelihood(){
        //super.addTreeLikelihood();
    }
    public void removeTreeLikelihood(int index){

    }

    protected void updateWeights(){

    } */

    /*public boolean requiresRecalculation(){
        return true;
    }*/
}
