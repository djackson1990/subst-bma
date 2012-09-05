package beast.evolution.likelihood;

import beast.core.parameter.RealParameter;
import beast.evolution.sitemodel.SiteModel;

/**
 * Created by IntelliJ IDEA.
 * User: cwu080
 * Date: 30/09/2011
 * Time: 10:40:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class DummyTempWVTreeLikelihood extends TempWVTreeLikelihood{

    public double calculateLogP(
            RealParameter paramParameter,
            RealParameter modelParameter,
            RealParameter freqsParameter,
            RealParameter ratesParameter){

        return 0.0;
    }

    public double[] calculateLogP(
            RealParameter paramParameter,
            RealParameter modelParameter,
            RealParameter freqsParameter,
            RealParameter ratesParameter,
            int[] sites,
            int except){
        double[] siteLogP = new double[sites.length-1];

        return siteLogP;
    }

    public double[] calculateLogP(
            RealParameter paramParameter,
            RealParameter modelParameter,
            RealParameter freqsParameter,
            RealParameter ratesParameter,
            int[] sites){
        double[] siteLogP = new double[sites.length];

        return siteLogP;
    }

    public double calculateLogP(
            RealParameter paramParameter,
            RealParameter modelParameter,
            RealParameter freqsParameter){

        return 0.0;
    }

    public double[] calculateLogP(
            RealParameter paramParameter,
            RealParameter modelParameter,
            RealParameter freqsParameter,
            int[] sites,
            int except){
        double[] siteLogP = new double[sites.length-1];

        return siteLogP;
    }

    public double[] calculateLogP(
            RealParameter paramParameter,
            RealParameter modelParameter,
            RealParameter freqsParameter,
            int[] sites){
        double[] siteLogP = new double[sites.length];

        return siteLogP;
    }

    public double[] calculateLogP(
            RealParameter rateParameter,
            int[] sites){
        double[] siteLogP = new double[sites.length];
        try{
            for(int i = 0; i < sites.length;i++){
                siteLogP[i] = 0.0;
            }
        }catch(Exception e){
            throw new RuntimeException(e);

        }
        return siteLogP;
    }

    public double[] calculateLogP(
            RealParameter rateParameter,
            int[] sites,
            int exceptSite){
        double[] siteLogP = new double[sites.length-1];
        try{
            int k = 0;
            for(int i = 0; i < sites.length;i++){
                if(sites[i] != exceptSite){
                    siteLogP[k++] = 0.0;
                }
            }
        }catch(Exception e){
            throw new RuntimeException(e);

        }         
        return siteLogP;
    }

}
