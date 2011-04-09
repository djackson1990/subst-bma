package test;

import beast.evolution.sitemodel.GammaSiteBMA;
import beast.evolution.substitutionmodel.SubstitutionModel;
import beast.evolution.substitutionmodel.HKY;
import beast.evolution.substitutionmodel.Frequencies;
import beast.core.parameter.IntegerParameter;
import beast.core.parameter.RealParameter;
import junit.framework.TestCase;

/**
 * @author Chieh-Hsi Wu
 */
public class GammaSiteBMATest extends TestCase {

interface Instance {
        SubstitutionModel getSubstModel () throws Exception;

        double getMu();

        double getLogitInvar();

        double getLogShape();

        int getCategoryCount();

        IntegerParameter getModelChoose() throws Exception;

        double[] getCategoryRates();

        double[] getCategoryProportions();
    }

    //Neither gamma shape nor site invariant parameters is included.
    Instance test0 = new Instance() {
        public SubstitutionModel getSubstModel ()throws Exception{


            HKY jc = new HKY();
            Frequencies freqs = new Frequencies();
		    freqs.initByName(
                    "data", null,
                    "estimate", false,
                    "frequencies", "0.25 0.25 0.25 0.25");
            
		    jc.initByName("kappa", "1.0",
				       "frequencies",freqs);
            return jc;
        }

        public double getMu(){
            return 1.5;
        }


        public double getLogitInvar(){
            //The invariant site proportion, p = 0.2.
            return -1.38629436112;
        }

        public double getLogShape(){
            //Gamma shape parameter, alpha = 2.
            return 0.69314718056;
        }

        public int getCategoryCount(){
            return 4;
        }

        public IntegerParameter getModelChoose() throws Exception{
            IntegerParameter modelChoose = new IntegerParameter();
            modelChoose.init(0, 1, "0 0",2);
            return modelChoose;
        }

        public double[] getCategoryRates(){
            return new double[]{0.0, 1.5, 1.5, 1.5, 1.5};
        }

        public double[] getCategoryProportions(){
            return new double[]{0, 0.25, 0.25, 0.25, 0.25};
        }

    };

    //Neither gamma shape nor site invariant parameters is included.
    Instance test1 = new Instance(){
        public SubstitutionModel getSubstModel ()throws Exception{


            HKY jc = new HKY();
            Frequencies freqs = new Frequencies();
		    freqs.initByName(
                    "data", null,
                    "estimate", false,
                    "frequencies", "0.25 0.25 0.25 0.25");

		    jc.initByName("kappa", "1.0",
				       "frequencies",freqs);
            return jc;
        }

        public double getMu(){
            return 1.5;
        }


        public double getLogitInvar(){
            //The invariant site proportion, p = 0.2.
            return -1.38629436112;
        }

        public double getLogShape(){
            //Gamma shape parameter, alpha = 2.
            return 0.69314718056;
        }

        public int getCategoryCount(){
            return 4;
        }

        public IntegerParameter getModelChoose() throws Exception{
            IntegerParameter modelChoose = new IntegerParameter();
            modelChoose.init(0, 1, "0 1",2);
            return modelChoose;
        }

        public double[] getCategoryRates(){
            return new double[]{0.0, 1.875, 1.875, 1.875, 1.875};
        }

        public double[] getCategoryProportions(){
            return new double[]{0.2, 0.2, 0.2, 0.2, 0.2};
        }


    };

    //Neither gamma shape nor site invariant parameters is included.
    Instance test2 = new Instance(){
        public SubstitutionModel getSubstModel ()throws Exception{

            HKY jc = new HKY();
            Frequencies freqs = new Frequencies();
		    freqs.initByName(
                    "data", null,
                    "estimate", false,
                    "frequencies", "0.25 0.25 0.25 0.25");

		    jc.initByName("kappa", "1.0",
				       "frequencies",freqs);
            return jc;
        }

        public double getMu(){
            return 1.5;
        }


        public double getLogitInvar(){
            //The invariant site proportion, p = 0.2.
            return -1.38629436112;
        }

        public double getLogShape(){
            //Gamma shape parameter, alpha = 2.
            return 0.69314718056;
        }

        public int getCategoryCount(){
            return 4;
        }

        public IntegerParameter getModelChoose() throws Exception{
            IntegerParameter modelChoose = new IntegerParameter();
            modelChoose.init(0, 1, "1 1",2);
            return modelChoose;
        }

        public double[] getCategoryRates(){
            return new double[]{0.0, 0.59824693778, 1.28130225333, 2.07933195980, 3.54111884909};
        }

        public double[] getCategoryProportions(){
            return new double[]{0.2, 0.2, 0.2, 0.2, 0.2};
        }
    };



    Instance test3 = new Instance(){
        public SubstitutionModel getSubstModel ()throws Exception{

            HKY jc = new HKY();
            Frequencies freqs = new Frequencies();
		    freqs.initByName(
                    "data", null,
                    "estimate", false,
                    "frequencies", "0.25 0.25 0.25 0.25");

		    jc.initByName("kappa", "1.0",
				       "frequencies",freqs);
            return jc;
        }

        public double getMu(){
            return 1.5;
        }


        public double getLogitInvar(){
            //The invariant site proportion, p = 0.2.
            return -1.38629436112;
        }

        public double getLogShape(){
            //Gamma shape parameter, alpha = 2.
            return 0.69314718056;
        }

        public int getCategoryCount(){
            return 8;
        }

        public IntegerParameter getModelChoose() throws Exception{
            IntegerParameter modelChoose = new IntegerParameter();
            modelChoose.init(0, 1, "1 1",2);
            return modelChoose;
        }

        public double[] getCategoryRates(){
            return new double[]{0.0,
                    0.387224876120, 0.757770190732, 1.085824675833, 1.426031312891,
                    1.810616868095, 2.285251426765, 2.955667305794, 4.291613343768};
        }

        public double[] getCategoryProportions(){
            return new double[]{0.2, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
        }
    };

    Instance[] all = {test0,test1,test2,test3};
    public void testGammaSiteBMA() throws Exception{

        for(Instance test: all){
            SubstitutionModel substModel = test.getSubstModel();
            RealParameter mu = new RealParameter();
            RealParameter logitInvar = new RealParameter();
            RealParameter logShape = new RealParameter();

            mu.init(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, ""+test.getMu(),1);
            logitInvar.init(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, ""+test.getLogitInvar(),1);
            logShape.init(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, ""+test.getLogShape(),1);

            int catCount = test.getCategoryCount();
            IntegerParameter modelChoose = test.getModelChoose();

            GammaSiteBMA gammaSiteBMA = new GammaSiteBMA();
            gammaSiteBMA.initByName(
                    "gammaCategoryCount",catCount,
                    "mutationRate",mu,
                    "logitInvar",logitInvar,
                    "logShape",logShape,
                    "substModel",substModel,
                    "modelChoose",modelChoose
            );

            double[] catRates = gammaSiteBMA.getCategoryRates(null);
            double[] expectedCatRates = test.getCategoryRates();

            for(int i = 0; i < catRates.length; i++){
                assertEquals(catRates[i], expectedCatRates[i], 8e-10);
            }

            double[] catProps = gammaSiteBMA.getCategoryProportions(null);
            double[] expectedCatProps = test.getCategoryProportions();
            for(int i = 0; i < catProps.length; i++){
                assertEquals(catProps[i], expectedCatProps[i], 1e-10);
            }

        }


    }

}
