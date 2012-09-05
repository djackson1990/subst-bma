package beast.evolution.likelihood;

/**
 * Created by IntelliJ IDEA.
 * User: cwu080
 * Date: 6/09/2011
 * Time: 2:30:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class DummyDPSepTreeLikelihood extends DPSepTreeLikelihood{
    

    @Override
    public double calculateLogP(){
        return 0.0;
    }
    public double getSiteLogLikelihood(int iCluster, int iSite){
        return 0.0;
    }

    public double getSiteLogLikelihood(int changeType, int iCluster, int iSite){
        return 0.0;
    }

    
}
