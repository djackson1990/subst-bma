package test;

import junit.framework.TestCase;
import beast.core.parameter.DPPointer;
import beast.core.parameter.ParameterList;
import beast.core.parameter.RealParameter2;
import beast.core.parameter.IntegerParameter;
import beast.evolution.sitemodel.DPSiteModel;
import beast.evolution.sitemodel.SiteModel;

/**
 * @author Chieh-Hsi Wu
 */
public class DPSiteModelTest extends TestCase {
    interface Instance {
        public void setup();
        public DPPointer getDPPointer();
        public ParameterList getParameterList();

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




    };

    Instance[] tests = new Instance[]{test0};
    public void testDPSiteModel(){
        try{
            for(Instance test: tests){
                test.setup();
                DPPointer pointers = test.getDPPointer();
                ParameterList paramList = test.getParameterList();
                DPSiteModel dpSiteModel = new DPSiteModel();
                dpSiteModel.initByName(
                        "paramList", paramList,
                        "pointers", pointers
                );
                for(int i = 0; i < dpSiteModel.getSiteModelCount();i++){
                    SiteModel siteModel = dpSiteModel.getSiteModel(i);
                    double[] rates = siteModel.getCategoryRates(null);
                    assertEquals(rates[0],pointers.getParameterValue(i));
                }
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }

    }


}
