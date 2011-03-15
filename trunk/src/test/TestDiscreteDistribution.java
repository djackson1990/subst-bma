package test;

import beast.core.parameter.IntegerParameter;
import beast.core.parameter.RealParameter;
import beast.math.distributions.DiscreteDistribution;
import junit.framework.TestCase;

/**
 * @author Chieh-Hsi Wu
 */
public class TestDiscreteDistribution extends TestCase {

    interface Instance {

        String getProbs();
        int[] getXs();
        double[] getFLogX();

    }

    Instance test0 = new Instance() {
        public String getProbs (){

            return "0.25 0.25 0.25 0.25";
        }

        public int[] getXs(){
            return new int[]{0, 2, 1, 3};
        }

        public double[] getFLogX(){
            return new double[]{
                    -1.38629436112,
                    -1.38629436112,
                    -1.38629436112,
                    -1.38629436112
            };
        }

    };


    Instance test1 = new Instance() {
        public String getProbs (){

            return "0.15 0.23 0.14 0.07 0.25 0.16";
        }

        public int[] getXs(){
            return new int[]{3, 2, 1, 5, 4, 0};
        }

        public double[] getFLogX(){
            return new double[]{
                    -2.65926003693,
                    -1.96611285637,
                    -1.46967597006,
                    -1.83258146375,
                    -1.38629436112,
                    -1.89711998489
            };
        }

    };

    Instance test2 = new Instance() {
        public String getProbs (){

            return "0.11 0.22 0.12 0.24 0.3 0.01";
        }

        public int[] getXs(){
            return new int[]{1, 2, 4, 3, 5, 0};
        }

        public double[] getFLogX(){
            return new double[]{
                    -1.51412773263,
                    -2.12026353620,
                    -1.20397280433,
                    -1.42711635564,
                    -4.60517018599,
                    -2.20727491319
            };
        }

    };

    Instance test3 = new Instance() {
        public String getProbs (){

            return "0.11 0.18 0.16 0.08 0.21 0.04 0.04 0.04 0.02 0.12";
        }

        public int[] getXs(){
            return new int[]{1, 8, 4, 0, 2, 3, 5, 6, 7, 9};
        }

        public double[] getFLogX(){
            return new double[]{
                    -1.71479842809,
                    -3.91202300543,
                    -1.56064774826,
                    -2.20727491319,
                    -1.83258146375,
                    -2.52572864431,
                    -3.21887582487,
                    -3.21887582487,
                    -3.21887582487,
                    -2.12026353620

            };
        }

    };

    Instance[] all = new Instance[]{test0,test1,test2,test3};

    public void testMultinomial() throws Exception{
        for(Instance test: all){

            RealParameter probs = new RealParameter();

            probs.initByName(
                    "value",test.getProbs(),
                    "lower","0",
                    "upper","1");

            int[] xVals = test.getXs();
            for(int i = 0; i < xVals.length; i++){

                IntegerParameter x  = new IntegerParameter();
                x.initByName("value", ""+xVals[i],
                        "lower", "0",
                        "upper", "100000000");
                DiscreteDistribution dist = new DiscreteDistribution();
                dist.initByName("probs", probs);
                double fLogX = dist.calcLogP(x);
                assertEquals(fLogX, test.getFLogX()[i], 1e-10);
            }

        }
    }







}
