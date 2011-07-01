package test;

import beast.core.parameter.*;
import beast.math.distributions.Normal;
import beast.math.distributions.ParametricDistribution;
import beast.math.distributions.DirichletProcessPrior;
import beast.math.distributions.MultivariateNormal;
import junit.framework.TestCase;

/**
 * @author Chieh-Hsi Wu
 */
public class DirichletProcessPriorTest extends TestCase {

    interface Instance {
        public void setup();
        public ParametricDistribution getBaseDistribution();
        public ParameterList getParameterList();
        public RealParameter getAlpha() ;
        public IntegerParameter getAssignment();
        public double getExpectedLogP();
    }

    Instance test0 = new Instance(){
        Normal normal;
        ParameterList paramList;
        RealParameter alpha;
        IntegerParameter assignment;
        public void setup(){
            normal = new Normal();
            paramList = new ParameterList();
            alpha = new RealParameter();
            assignment = new IntegerParameter();
            try{
                normal.initByName(
                        "mean", "0",
                        "sigma", "3"
                );

                QuietRealParameter param1 = new QuietRealParameter(new Double[]{-0.3});
                QuietRealParameter param2 = new QuietRealParameter(new Double[]{-0.1});
                QuietRealParameter param3 = new QuietRealParameter(new Double[]{0.5});
                paramList.initByName(
                        "parameter", param1,
                        "parameter", param2,
                        "parameter", param3
                );
                alpha.initByName("value","0.5");
                assignment.initByName("value","1 2 1 3 1 1 2 1");
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }

        public ParametricDistribution getBaseDistribution(){
            return normal;
        }

        public ParameterList getParameterList() {
            return paramList;
        }

        public RealParameter getAlpha () {
            return alpha;
        }

        public IntegerParameter getAssignment(){
            return assignment;

        }

        public double getExpectedLogP(){
            return -13.9503869358;
        }


    };

    Instance test1 = new Instance(){
        Normal normal;
        ParameterList paramList;
        RealParameter alpha;
        IntegerParameter assignment;
        public void setup(){
            normal = new Normal();
            paramList = new ParameterList();
            alpha = new RealParameter();
            assignment = new IntegerParameter();
            try{
                normal.initByName(
                        "mean", "0",
                        "sigma", "3"
                );

                QuietRealParameter param1 = new QuietRealParameter(new Double[]{-0.3,0.2,-0.1});
                QuietRealParameter param2 = new QuietRealParameter(new Double[]{-0.1,0.03,0.15});
                QuietRealParameter param3 = new QuietRealParameter(new Double[]{0.5,0.4,-0.01});
                paramList.initByName(
                        "parameter", param1,
                        "parameter", param2,
                        "parameter", param3
                );
                alpha.initByName("value","0.5");
                assignment.initByName("value","1 2 1 3 1 1 2 1");
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }

        public ParametricDistribution getBaseDistribution(){
            return normal;
        }

        public ParameterList getParameterList() {
            return paramList;
        }

        public RealParameter getAlpha () {
            return alpha;
        }

        public IntegerParameter getAssignment(){
            return assignment;

        }

        public double getExpectedLogP(){
            return -26.0686640892;
        }


    };
    Instance test2 = new Instance(){
        MultivariateNormal mvnorm;
        ParameterList paramList;
        RealParameter alpha;
        IntegerParameter assignment;
        public void setup(){
            mvnorm = new MultivariateNormal();
            paramList = new ParameterList();
            alpha = new RealParameter();
            assignment = new IntegerParameter();
            try{
                RealParameter mean = new RealParameter();
                mean.initByName("value", "0.04 -0.01 0.1");

                RealParameter precisionRow1 = new RealParameter();
                RealParameter precisionRow2 = new RealParameter();
                RealParameter precisionRow3 = new RealParameter();
                precisionRow1.initByName("value","1.0359869138495 -0.1690294438386 -0.0436205016358");
                precisionRow2.initByName("value","-0.1690294438386  0.8696837513631 -0.0981461286805");
                precisionRow3.initByName("value","-0.0436205016358 -0.0981461286805  1.2649945474373");
                MatrixParameter precisionMatrix = new MatrixParameter();
                precisionMatrix.initByName(
                        "parameter",precisionRow1,
                        "parameter",precisionRow2,
                        "parameter",precisionRow3);
                mvnorm.initByName(
                        "mean", mean,
                        "precision", precisionMatrix
                );

                QuietRealParameter param1 = new QuietRealParameter(new Double[]{-0.3,0.2,-0.1});
                QuietRealParameter param2 = new QuietRealParameter(new Double[]{-0.1,0.03,0.15});
                QuietRealParameter param3 = new QuietRealParameter(new Double[]{0.5,0.4,-0.01});
                paramList.initByName(
                        "parameter", param1,
                        "parameter", param2,
                        "parameter", param3
                );
                alpha.initByName("value","0.5");
                assignment.initByName("value","1 2 1 3 1 1 2 1");
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }

        public ParametricDistribution getBaseDistribution(){
            return mvnorm;
        }

        public ParameterList getParameterList() {
            return paramList;
        }

        public RealParameter getAlpha () {
            return alpha;
        }

        public IntegerParameter getAssignment(){
            return assignment;

        }

        public double getExpectedLogP(){
            return -16.3149436859;
        }


    };

    Instance[] all = new Instance[]{test0,test1,test2};

    public void testDirichletProcessPrior(){
        try{
            for(Instance test:all){
                test.setup();
                IntegerParameter assignment = test.getAssignment();
                RealParameter alpha = test.getAlpha();
                ParameterList paramList = test.getParameterList();
                ParametricDistribution distr = test.getBaseDistribution();
                DirichletProcessPrior dpp = new DirichletProcessPrior();

                dpp.initByName(
                        "assignment", assignment,
                        "alpha", alpha,
                        "xList", paramList,
                        "distribution", distr,
                        "applyToList", false
                );

                //System.err.println(dpp.calculateLogP());
                assertEquals(dpp.calculateLogP(), test.getExpectedLogP(), 5e-10);

            }

        }catch(Exception e){
            throw new RuntimeException(e);

        }
    }

    
}
