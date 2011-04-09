package test;

import beast.core.parameter.IntegerParameter;
import beast.core.parameter.RealParameter;
import beast.evolution.substitutionmodel.NtdBMA;
import beast.evolution.substitutionmodel.Frequencies;
import junit.framework.TestCase;

/**
 * @author Chieh-Hsi Wu
 *
 * JUnit test for NtdBMA model
 */
public class NtdBMATest extends TestCase {
    interface Instance {
        String getPi();

        double getLogKappa();

        double getLogTN();

        double getLogAC();

        double getLogAT();
        double getLogGC();
        double getLogGT();
        IntegerParameter getModelChoose() throws Exception;

        double getDistance();

        double[] getExpectedResult();
    }

    
    //A HKY model
    Instance test0 = new Instance() {
        public String getPi() {
            return "0.25 0.25 0.25 0.25";
        }

        public double getLogKappa() {
            return Math.log(2);
        }

        public double getLogTN(){
            return Math.log(1.2);
        }

        public double getLogAC(){
            return Math.log(0.5);
        }

        public double getLogAT(){
            return Math.log(0.5);
        }

        public double getLogGC(){
            return Math.log(0.5);
        }
        public double getLogGT(){
            return Math.log(0.5);
        }

        public IntegerParameter getModelChoose() throws Exception{
            IntegerParameter modelChoose = new IntegerParameter();
            modelChoose.init(0, 5, "3",1);
            return modelChoose;
        }

        public double getDistance() {
            return 0.1;
        }

        public double[] getExpectedResult() {
            return new double[]{
                    0.906563342722, 0.023790645491, 0.045855366296, 0.023790645491,
                    0.023790645491, 0.906563342722, 0.023790645491, 0.045855366296,
                    0.045855366296, 0.023790645491, 0.906563342722, 0.023790645491,
                    0.023790645491, 0.045855366296, 0.023790645491, 0.906563342722
            };
        }
    };

    //A TN93 model
    Instance test1 = new Instance() {
        public String getPi() {
            return "0.1 0.2 0.3 0.4";
        }

        public double getLogKappa() {
            return Math.log(3)-0.5*Math.log(1.5);
        }

        public double getLogTN(){
            return -0.5*Math.log(1.5);
        }

        public double getLogAC(){
            return Math.log(0.5);
        }

        public double getLogAT(){
            return Math.log(0.5);
        }

        public double getLogGC(){
            return Math.log(0.5);
        }
        public double getLogGT(){
            return Math.log(0.5);
        }

        public IntegerParameter getModelChoose() throws Exception{
            IntegerParameter modelChoose = new IntegerParameter();
            modelChoose.init(0, 5, "4",1);
            return modelChoose;
        }


        public double getDistance() {
            return 0.1;
        }

        public double[] getExpectedResult() {
            return new double[]{
                    0.895550254199242, 0.017687039418335, 0.051388627545752, 0.035374078836670,
                    0.008843519709168, 0.865344657365451, 0.026530559127503, 0.099281263797879,
                    0.017129542515251, 0.017687039418335, 0.929809339229744, 0.035374078836670,
                    0.008843519709168, 0.049640631898940, 0.026530559127503, 0.914985289264390
            };
        }
    };

    //GTR example
    Instance test2 = new Instance() {
        public String getPi() {
            return "0.20 0.30 0.25 0.25";
        }


        public double getLogKappa() {
            return Math.log(3)-0.5*Math.log(1.5);
        }

        public double getLogTN(){
            return -0.5*Math.log(1.5);
        }

        public double getLogAC(){
            return Math.log(1.2);
        }

        public double getLogAT(){
            return Math.log(0.6);
        }

        public double getLogGC(){
            return Math.log(0.5);
        }
        public double getLogGT(){
            return Math.log(0.8);
        }

        public IntegerParameter getModelChoose() throws Exception{
            IntegerParameter modelChoose = new IntegerParameter();
            modelChoose.init(0, 5, "5", 1);
            return modelChoose;
        }


        public double getDistance() {
            return 0.1;
        }

        public double[] getExpectedResult() {
            return new double[]{
                    0.9078362845301, 0.0325116185198, 0.0449673267333, 0.0146847702168,
                    0.0216744123465, 0.9006273487178, 0.0122790622489, 0.0654191766868,
                    0.0359738613867, 0.0147348746987, 0.9308468616493, 0.0184444022654,
                    0.0117478161734, 0.0785030120241, 0.0184444022654, 0.8913047695370
            };
        }
    };

    Instance test3 = new Instance() {
        public String getPi() {
            return "0.25 0.25 0.25 0.25";
        }


        public double getLogKappa() {
            return Math.log(2);
        }

        public double getLogTN(){
            return Math.log(1.2);
        }

        public double getLogAC(){
            return Math.log(0.5);
        }

        public double getLogAT(){
            return Math.log(0.5);
        }

        public double getLogGC(){
            return Math.log(0.5);
        }
        public double getLogGT(){
            return Math.log(0.5);
        }

        public IntegerParameter getModelChoose() throws Exception{
            IntegerParameter modelChoose = new IntegerParameter();
            modelChoose.init(0, 5, "3", 1);
            return modelChoose;
        }


        public double getDistance() {
            return 1.8;
        }

        public double[] getExpectedResult() {
            return new double[]{
                    0.324927478425, 0.208675277945, 0.257721965686, 0.208675277945,
                    0.208675277945, 0.324927478425, 0.208675277945, 0.257721965686,
                    0.257721965686, 0.208675277945, 0.324927478425, 0.208675277945,
                    0.208675277945, 0.257721965686, 0.208675277945, 0.324927478425
            };
        }
    };

    Instance test4 = new Instance() {

        public String getPi() {
            return "0.1 0.2 0.3 0.4";
        }


        public double getLogKappa() {
            return Math.log(3)-0.5*Math.log(1.5);
        }

        public double getLogTN(){
            return -0.5*Math.log(1.5);
        }

        public double getLogAC(){
            return Math.log(0.5);
        }

        public double getLogAT(){
            return Math.log(0.5);
        }

        public double getLogGC(){
            return Math.log(0.5);
        }
        public double getLogGT(){
            return Math.log(0.5);
        }

        public IntegerParameter getModelChoose() throws Exception{
            IntegerParameter modelChoose = new IntegerParameter();
            modelChoose.init(0, 5, "4", 1);
            return modelChoose;
        }



        public double getDistance() {
            return 2.5;
        }

        public double[] getExpectedResult() {
            return new double[]{
                    0.144168843021, 0.180243104854, 0.315101842417, 0.360486209708,
                    0.090121552427, 0.217265980316, 0.270364657281, 0.422247809976,
                    0.105033947472, 0.180243104854, 0.354236737965, 0.360486209708,
                    0.090121552427, 0.211123904988, 0.270364657281, 0.428389885304
            };
        }
    };

    //GTR example
    Instance test5 = new Instance() {
        public String getPi() {
            return "0.20 0.30 0.25 0.25";
        }


        public double getLogKappa() {
            return Math.log(3)-0.5*Math.log(1.5);
        }

        public double getLogTN(){
            return -0.5*Math.log(1.5);
        }

        public double getLogAC(){
            return Math.log(1.2);
        }

        public double getLogAT(){
            return Math.log(0.6);
        }

        public double getLogGC(){
            return Math.log(0.5);
        }
        public double getLogGT(){
            return Math.log(0.8);
        }

        public IntegerParameter getModelChoose() throws Exception{
            IntegerParameter modelChoose = new IntegerParameter();
            modelChoose.init(0, 5, "5", 1);
            return modelChoose;
        }
       

        public double getDistance() {
            return 2.5;
        }

        public double[] getExpectedResult() {
            return new double[]{
                    0.246055801088, 0.266163561908, 0.273492078437, 0.214288558567,
                    0.177442374606, 0.341245862774, 0.202456669039, 0.278855093581,
                    0.218793662750, 0.242948002847, 0.333259562500, 0.204998771904,
                    0.171430846853, 0.334626112297, 0.204998771904, 0.288944268946
            };
        }
    };

    Instance[] all = {test0,test1,test2,test3,test4,test5};

    public void testNtdBMA() throws Exception{
        for (Instance test : all) {

            RealParameter logKappa = new RealParameter();
            RealParameter logTN = new RealParameter();
            RealParameter logAC = new RealParameter();
            RealParameter logAT = new RealParameter();
            RealParameter logGC = new RealParameter();
            RealParameter logGT = new RealParameter();

            logKappa.init(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, ""+test.getLogKappa(),1);
            logTN.init(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, ""+test.getLogTN(),1);
            logAC.init(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, ""+test.getLogAC(),1);
            logAT.init(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, ""+test.getLogAT(),1);
            logGC.init(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, ""+test.getLogGC(),1);
            logGT.init(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, ""+test.getLogGT(),1);
            IntegerParameter modelChoose = test.getModelChoose();
           
            Frequencies f = new Frequencies();
            f.init(null, true,test.getPi());
            
            NtdBMA ntdBMA = new NtdBMA();
            ntdBMA.init(
                    logKappa,
                    logTN,
                    logAC,
                    logAT,
                    logGC,
                    logGT,
                    modelChoose,
                    f
            );

            double distance = test.getDistance();

            double[] mat = new double[4 * 4];
            ntdBMA.getTransitionProbabilities(null,distance, 0,1.0, mat);
            final double[] result = test.getExpectedResult();

            for (int k = 0; k < mat.length; ++k) {
                assertEquals(mat[k], result[k], 5e-10);
            }
        }
    }
}
