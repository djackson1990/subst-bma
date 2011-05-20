package test;

import beast.core.parameter.*;
import beast.math.distributions.Normal;
import beast.math.distributions.ParametricDistribution;
import beast.math.distributions.DirichletProcessPrior;
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

                RealParameter2 param1 = new RealParameter2(new Double[]{-0.3});
                RealParameter2 param2 = new RealParameter2(new Double[]{-0.1});
                RealParameter2 param3 = new RealParameter2(new Double[]{0.5});
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

    Instance[] all = new Instance[]{test0};

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
                        "distribution", distr
                );

                //System.err.println(dpp.calculateLogP());
                assertEquals(dpp.calculateLogP(), test.getExpectedLogP(), 5e-10);

            }

        }catch(Exception e){
            throw new RuntimeException(e);

        }
    }

    
}
