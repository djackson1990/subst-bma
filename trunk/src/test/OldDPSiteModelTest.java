package test;

import junit.framework.TestCase;
import beast.core.parameter.*;
import beast.core.State;
import beast.evolution.sitemodel.DPSiteModelOld;
import beast.evolution.sitemodel.SiteModel;

/**
 * @author Chieh-Hsi Wu
 */
public class OldDPSiteModelTest extends TestCase {
    interface Instance {
        public void setup();
        public DPPointer getDPPointer();
        public ParameterList getParameterList();
        public void operation1() throws Exception;
        public double[] getOperation1Outcome();
        public void operation2() throws Exception;
        public double[] getOperation2Outcome();
        public void operation3() throws Exception;
        public double[] getOperation3Outcome();
        

    }

    Instance test0 = new Instance(){
        ParameterList paramList;
        DPPointer pointer;
        public void setup(){
            try{

                RealParameter2 parameter1 = new RealParameter2(new Double[]{0.0});
                RealParameter2 parameter2 = new RealParameter2(new Double[]{1.0});
                RealParameter2 parameter3 = new RealParameter2(new Double[]{2.0});
                paramList = new ParameterList();
                paramList.initByName(
                        "parameter", parameter1,
                        "parameter", parameter2,
                        "parameter", parameter3
                );
                paramList.setID("parameterList");
                IntegerParameter initAssign = new IntegerParameter(new Integer[]{0,2,1,1,2});
                pointer = new DPPointer();
                pointer.initByName(
                        "uniqueParameter", parameter1,
                        "uniqueParameter", parameter2,
                        "uniqueParameter", parameter3,
                        "initialAssignment", initAssign
                );
                pointer.setID("pointer");
                State state = new State();
                state.initByName(
                    "stateNode", pointer,
                    "stateNode", paramList
                );
                state.initialise();
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }


        public DPPointer getDPPointer(){
            return pointer;
        }

        public ParameterList getParameterList(){
            return paramList;
        }

        public void operation1() throws Exception{
            paramList.addParameter(new RealParameter2(new Double[]{3.0}));
            pointer.point(3,paramList.getParameter(3));
            
        }

        public double[] getOperation1Outcome(){
            return new double[]{0.0, 2.0, 1.0, 3.0, 2.0};
        }

        public void operation2() throws Exception{
            pointer.point(0,paramList.getParameter(3));
            paramList.removeParameter(0);
        }

        public double[] getOperation2Outcome(){
            return new double[]{3.0, 2.0, 1.0, 3.0, 2.0};
        }

        public void operation3() throws Exception{
            pointer.point(2,paramList.getParameter(1));
            paramList.removeParameter(0);
        }

        public double[] getOperation3Outcome(){
            return new double[]{3.0, 2.0, 2.0, 3.0, 2.0};
        }
    };



    

    Instance[] tests = new Instance[]{test0};
    public void testDPSiteModel(){
        try{
            for(Instance test: tests){
                test.setup();
                DPPointer pointers = test.getDPPointer();
                ParameterList paramList = test.getParameterList();
                DPSiteModelOld dpSiteModelOld = new DPSiteModelOld();
                dpSiteModelOld.initByName(
                        "paramList", paramList,
                        "pointers", pointers
                );
                for(int i = 0; i < dpSiteModelOld.getSiteModelCount();i++){
                    SiteModel siteModel = dpSiteModelOld.getSiteModel(i);
                    double[] rates = siteModel.getCategoryRates(null);
                    assertEquals(rates[0],pointers.getParameterValue(i));
                }

                test.operation1();
                double[] op1Outcome = test.getOperation1Outcome();
                for(int i = 0; i < dpSiteModelOld.getSiteModelCount();i++){
                    SiteModel siteModel = dpSiteModelOld.getSiteModel(i);
                    double[] rates = siteModel.getCategoryRates(null);
                    assertEquals(rates[0],op1Outcome[i]);
                }

                test.operation2();
                double[] op2Outcome = test.getOperation2Outcome();
                for(int i = 0; i < dpSiteModelOld.getSiteModelCount();i++){
                    SiteModel siteModel = dpSiteModelOld.getSiteModel(i);
                    double[] rates = siteModel.getCategoryRates(null);
                    assertEquals(rates[0],op2Outcome[i]);
                }

                test.operation3();
                double[] op3Outcome = test.getOperation3Outcome();
                for(int i = 0; i < dpSiteModelOld.getSiteModelCount();i++){
                    SiteModel siteModel = dpSiteModelOld.getSiteModel(i);
                    double[] rates = siteModel.getCategoryRates(null);
                    assertEquals(rates[0],op3Outcome[i]);
                }
                
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }

    }


}
