package test;

import beast.math.distributions.*;
import beast.core.parameter.*;
import junit.framework.TestCase;

/**
 * @author Chieh-Hsi Wu
 */
public class NtdDirichletProcessTest extends TestCase {
    interface Instance {
        public ParametricDistribution getParamBaseDistr() throws Exception;
        public ParametricDistribution getModelBaseDistr() throws Exception;
        public ParametricDistribution getFreqBaseDistr() throws Exception;
        public ParameterList getParamList() throws Exception;
        public ParameterList getModelList() throws Exception;
        public ParameterList getFreqList() throws Exception;
        public RealParameter getAlpha() throws Exception;
        public DPValuable getDPValuable() throws Exception;
        public double getExpectedLogP();
    }

    Instance test0 = new Instance(){



        public String getMean(){

            return "1.2 2.1 1.4 1.6";
        }

        public MatrixParameter getPrecision()throws Exception{
            MatrixParameter precision = new MatrixParameter();

            RealParameter row1  =new RealParameter();
            row1.initByName(
                    "value","1.0000000 0.9529819 0.3554853 0.1695793",
                    "lower",Double.NEGATIVE_INFINITY,
                    "upper",Double.POSITIVE_INFINITY
            );

            RealParameter row2  =new RealParameter();
            row2.initByName(
                    "value","0.9529819 2.0000000 0.3658894 0.6902582",
                    "lower",Double.NEGATIVE_INFINITY,
                    "upper",Double.POSITIVE_INFINITY
            );

            RealParameter row3  =new RealParameter();
            row3.initByName(
                    "value","0.3554853 0.3658894 1.5000000 0.8099044",
                    "lower",Double.NEGATIVE_INFINITY,
                    "upper",Double.POSITIVE_INFINITY
            );

            RealParameter row4  =new RealParameter();
            row4.initByName(
                    "value","0.1695793 0.6902582 0.8099044 1.3000000",
                    "lower",Double.NEGATIVE_INFINITY,
                    "upper",Double.POSITIVE_INFINITY
            );

            precision.initByName(
                    "parameter",row1,
                    "parameter",row2,
                    "parameter",row3,
                    "parameter",row4
            );
            //System.out.println(precision==null);
            return precision;
        }


        public ParametricDistribution getParamBaseDistr() throws Exception{

            RealParameter mean = new RealParameter();

            mean.initByName(
                    "value",getMean(),
                    "lower",Double.NEGATIVE_INFINITY,
                    "upper",Double.POSITIVE_INFINITY);

            MatrixParameter precision = getPrecision();

            MultivariateNormal multiNorm = new MultivariateNormal();
            multiNorm.initByName(
                    "mean",mean,
                    "precision", precision);
            return multiNorm;
        }

        public ParametricDistribution getModelBaseDistr() throws Exception{
            IntegerUniformDistribution intergerUniform = new IntegerUniformDistribution();
            intergerUniform.initByName(
                    "lower",0.0,
                    "upper", 5.0);
            return intergerUniform;
        }

        public ParametricDistribution getFreqBaseDistr() throws Exception{

            DirichletDistribution dirichlet = new DirichletDistribution();
            RealParameter alpha = new RealParameter(new Double[]{1.0,1.0,1.0,1.0});
            dirichlet.initByName("alpha",alpha);
            return dirichlet;
        }


        public ParameterList getParamList() throws Exception {
            RealParameter param1 = new RealParameter(new Double[]{1.6,2.3,1.6,0.9});
            RealParameter param2 = new RealParameter(new Double[]{0.16926,4.51521,0.18029,0.90557});
            RealParameter param3 = new RealParameter(new Double[]{2.88483,1.31626,-0.24917,2.6704});
            ParameterList paramList = new ParameterList();
            paramList.initByName(
                    "parameter", param1,
                    "parameter", param2,
                    "parameter", param3
            );
            return paramList;
        }

        public ParameterList getModelList() throws Exception{
            ParameterList modelList = new ParameterList();
            RealParameter param1 = new RealParameter(new Double[]{3.0});
            RealParameter param2 = new RealParameter(new Double[]{4.0});
            RealParameter param3 = new RealParameter(new Double[]{5.0});
            modelList.initByName(
                    "parameter", param1,
                    "parameter", param2,
                    "parameter", param3
            );
            return modelList;
            
        }

        public ParameterList getFreqList() throws Exception{

            RealParameter param1 = new RealParameter(new Double[]{0.0927, 0.0529, 0.2344, 0.6200});
            RealParameter param2 = new RealParameter(new Double[]{0.0294, 0.6254, 0.2657, 0.0795});
            RealParameter param3 = new RealParameter(new Double[]{0.2578, 0.4553, 0.0605, 0.2264});
            ParameterList freqList = new ParameterList();
            freqList.initByName(
                    "parameter", param1,
                    "parameter", param2,
                    "parameter", param3
            );
            return freqList;

        }

        public RealParameter getAlpha () throws Exception{

            return new RealParameter(new Double[]{0.5});
        }

        public DPValuable getDPValuable() throws Exception{
            IntegerParameter assignment = new IntegerParameter();
            assignment.initByName("value","0 1 0 2 0 0 1 0");

            ParameterList paramList = getParamList();

            DPPointer dpPointer = new DPPointer();
            //System.err.println(paramList.getParameter(0));
            dpPointer.initByName(
                    "uniqueParameter",paramList.getParameter(0),
                    "uniqueParameter",paramList.getParameter(1),
                    "uniqueParameter",paramList.getParameter(2),
                    "initialAssignment",assignment
                );

            DPValuable dpValuable = new DPValuable();
            dpValuable.initByName(
                    "paramList",paramList,
                    "pointers", dpPointer
            );
            return dpValuable;

        }

        public double getExpectedLogP(){
            return -25.1271965492;
        }


    };

    Instance[] all = new Instance[]{test0};
    public void testNtdDirichletProcess(){
        try{
            for(Instance test: all){
                ParametricDistribution paramBaseDistr = test.getParamBaseDistr();
                ParametricDistribution modelBaseDistr = test.getModelBaseDistr();
                ParametricDistribution freqBaseDistr = test.getFreqBaseDistr();


                DPValuable dpVal = test.getDPValuable();
                RealParameter conc = test.getAlpha();

                NtdDP ntdDP = new NtdDP();
                ntdDP.initByName(
                        "paramBaseDistr", paramBaseDistr,
                        "modelBaseDistr", modelBaseDistr,
                        "freqBaseDistr", freqBaseDistr,
                        "dpVal",dpVal,
                        "alpha", conc
                );

                NtdParameterListPrior ntdParamListPrior = new NtdParameterListPrior();
                ntdParamListPrior.initByName(
                        "ntdDP",ntdDP,
                        "paramList", test.getParamList(),
                        "modelList", test.getModelList(),
                        "freqList", test.getFreqList()
                );

                assertEquals(ntdParamListPrior.calculateLogP(),test.getExpectedLogP(),1e-10);
            }
        }catch(Exception e){
            throw new RuntimeException(e);

        }

    }
    

   

}
