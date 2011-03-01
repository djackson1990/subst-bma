package beast.evolution.substitutionmodel;

import beast.core.parameter.RealParameter;
import beast.core.parameter.IntegerParameter;
import beast.core.Input;
import beast.core.Description;

/**
 * @author Chieh-Hsi Wu
 *
 * Model averaging of nucleotide substitution model
 *
 */
@Description("Model averaging of nucleotide substitution model including HKY85, TN93 and GTR.")
public class NtdBMA extends GeneralSubstitutionModel{
    public Input<RealParameter> logKappa = new Input<RealParameter>("logKappa", "parameter representing log of HKY kappa parameter", Input.Validate.REQUIRED);
    public Input<RealParameter> logTN = new Input<RealParameter>("logTN", "parameter representing log of TN parameter", Input.Validate.REQUIRED);
    public Input<RealParameter> logAC = new Input<RealParameter>("logAC", "parameter representing log of AC parameter", Input.Validate.REQUIRED);
    public Input<RealParameter> logAT = new Input<RealParameter>("logAT", "parameter representing log of AT parameter", Input.Validate.REQUIRED);
    public Input<RealParameter> logGC = new Input<RealParameter>("logGC", "parameter representing log of GC parameter", Input.Validate.REQUIRED);
    public Input<RealParameter> logGT = new Input<RealParameter>("logGT", "parameter representing log of GT parameter", Input.Validate.REQUIRED);
    public Input<IntegerParameter> modelChoose = new Input<IntegerParameter>("modelChoose", "Integer presenting the model", Input.Validate.REQUIRED);

    public static final int STATE_COUNT = 4;
    public static final int RATE_COUNT = 6;



    public static final int ABSENT = 0;
    public static final int PRESENT = 1;

    public static final int K80_INDEX = 0;
    public static final int F81_INDEX = 1;
    public static final int TN_INDEX = 2;
    public static final int GTR_INDEX = 3;

    public static final double[] UNIF_DIST = {1.0/STATE_COUNT,1.0/STATE_COUNT,1.0/STATE_COUNT,1.0/STATE_COUNT};

    public static final int JC = 0;
    public static final int K80 = 1;
    public static final int F81 = 2;
    public static final int HKY = 3;
    public static final int TN = 4;
    public static final int GTR = 5;

    public static final int[][] INDICATORS = {
            {ABSENT, ABSENT, ABSENT,ABSENT},
            {ABSENT, PRESENT,ABSENT,ABSENT},
            {ABSENT, PRESENT,ABSENT,ABSENT},
            {PRESENT, PRESENT,ABSENT,ABSENT},
            {PRESENT, PRESENT, PRESENT,ABSENT},
            {PRESENT, PRESENT, PRESENT,PRESENT}
    };



    public NtdBMA() {
       m_rates.setRule(Input.Validate.OPTIONAL);
    }

    @Override
    public void initAndValidate() throws Exception {
        if(modelChoose.get().getUpper() > GTR || modelChoose.get().getUpper() > JC){
            System.err.println("The value of model choose needs to be between " + JC + " and " + GTR + "inclusive, " +
                    "where "+ JC + " and " + GTR +" represents JC and GTR repectively");
        }
        updateMatrix = true;
        m_nStates = STATE_COUNT;
        eigenSystem = new DefaultEigenSystem(STATE_COUNT);
        m_rateMatrix = new double[STATE_COUNT][STATE_COUNT];
        relativeRates = new double[RATE_COUNT];
        storedRelativeRates = new double[RATE_COUNT];
        //q = new double[m_nStates][m_nStates];
    } // initAndValidate

    



    protected void setupRelativeRates() {

        
        //rate AG value
    	relativeRates[1] = Math.exp(
                INDICATORS[getCurrModel()][K80_INDEX]*logKappa.get().getValue(0)+
                        INDICATORS[modelChoose.get().getValue(0)][TN_INDEX]*logTN.get().getValue(0));

        //rate CT value
        relativeRates[4] = Math.exp(
                INDICATORS[getCurrModel()][K80_INDEX]*logKappa.get().getValue(0)-
                        INDICATORS[getCurrModel()][TN_INDEX]*logTN.get().getValue(0));

        //rate AC value
        relativeRates[0] = Math.exp(INDICATORS[getCurrModel()][GTR_INDEX]*logAC.get().getValue(0));

        //rate AT value
        relativeRates[2] = Math.exp(INDICATORS[getCurrModel()][GTR_INDEX]*logAT.get().getValue(0));

        //rate GC value
        relativeRates[3] = Math.exp(INDICATORS[getCurrModel()][GTR_INDEX]*logGC.get().getValue(0));
        
        //rate GT value
        relativeRates[5] = Math.exp(INDICATORS[getCurrModel()][GTR_INDEX]*logGT.get().getValue(0));

      
    }

    /** sets up rate matrix **/
    protected void setupRateMatrix() {
    	double [] fFreqs;

        if(INDICATORS[getCurrModel()][F81_INDEX] == PRESENT){
            fFreqs = frequencies.get().getFreqs();
        }else{
            fFreqs = UNIF_DIST;
        }

        
        int i, j, k = 0;

        // Set the instantaneous rate matrix
        for (i = 0; i < m_nStates; i++) {
            m_rateMatrix[i][i] = 0;

            for (j = i + 1; j < m_nStates; j++) {
                m_rateMatrix[i][j] = relativeRates[k] * fFreqs[j];
                m_rateMatrix[j][i] = relativeRates[k] * fFreqs[i];
                k += 1;
            }
        }

       /*for(i = 0; i < m_nStates; i++){
            for(j = 0; j < m_nStates; j++){
                System.out.print(m_rateMatrix[i][j]+" ");
            }
            System.out.println();
        }*/

        // set up diagonal
        for (i = 0; i < m_nStates; i++) {
            double fSum = 0.0;
            for (j = 0; j < m_nStates; j++) {
                if (i != j)
                    fSum += m_rateMatrix[i][j];
            }
            m_rateMatrix[i][i] = -fSum;
        }
        // normalise rate matrix to one expected substitution per unit time
        double fSubst = 0.0;
        for (i = 0; i < m_nStates; i++)
            fSubst += -m_rateMatrix[i][i] * fFreqs[i];

        for (i = 0; i < m_nStates; i++) {
            for (j = 0; j < m_nStates; j++) {
            	m_rateMatrix[i][j] = m_rateMatrix[i][j] / fSubst;
            }
        }
	} // setupRateMatrix

    public boolean requiresRecalculation(){
        boolean recalculate = false;


        if(modelChoose.get().somethingIsDirty()){
            recalculate = true;
        }else if(frequencies.get().isDirtyCalculation() &&
                INDICATORS[getCurrModel()][F81_INDEX] == PRESENT){

            recalculate = true;

        }else if(logKappa.get().somethingIsDirty() &&
                INDICATORS[getCurrModel()][K80_INDEX] == PRESENT){

            recalculate = true;

        }else if(logTN.get().somethingIsDirty() &&
                INDICATORS[getCurrModel()][TN_INDEX] == PRESENT){

            recalculate = true;

        }else if(logAC.get().somethingIsDirty() &&
               INDICATORS[getCurrModel()][GTR_INDEX] == PRESENT){

            recalculate = true;

        }else if(logAT.get().somethingIsDirty() &&
                INDICATORS[getCurrModel()][GTR_INDEX] == PRESENT){

            recalculate = true;

        }else if(logGC.get().somethingIsDirty() &&
                INDICATORS[getCurrModel()][GTR_INDEX] == PRESENT){

            recalculate = true;

        }else if(logGT.get().somethingIsDirty() &&
                INDICATORS[getCurrModel()][GTR_INDEX] == PRESENT){

            recalculate = true;

        }

        if(recalculate){
            updateMatrix = true;
        }
        return recalculate;
    }

    private int getCurrModel(){
        return modelChoose.get().getValue(0);

    }

}
