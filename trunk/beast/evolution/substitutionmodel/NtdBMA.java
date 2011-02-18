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
    public Input<IntegerParameter> modelChoose = new Input<IntegerParameter>("modelChoose", "parameter representing log of GT parameter", Input.Validate.REQUIRED);

    public static final int STATE_COUNT = 4;
    public static final int RATE_COUNT = 6;

    public static final int TN_INDEX = 0;
    public static final int GTR_INDEX = 1;

    public static final int ABSENT = 0;
    public static final int PRESENT = 1;



    public NtdBMA() {
       m_rates.setRule(Input.Validate.OPTIONAL);
    }

    @Override
    public void initAndValidate() throws Exception {
        updateMatrix = true;
        m_nStates = STATE_COUNT;
        eigenSystem = new DefaultEigenSystem(STATE_COUNT);
        m_rateMatrix = new double[STATE_COUNT][STATE_COUNT];
        relativeRates = new double[RATE_COUNT];
        storedRelativeRates = new double[RATE_COUNT];
        //q = new double[m_nStates][m_nStates];
    } // initAndValidate

    



    protected void setupRelativeRates() {
    	relativeRates[1] = Math.exp(logKappa.get().getValue(0)+modelChoose.get().getValue(TN_INDEX)*logTN.get().getValue(0));
        //rate CT value
        relativeRates[4] = Math.exp(logKappa.get().getValue(0)-modelChoose.get().getValue(TN_INDEX)*logTN.get().getValue(0));
        //rate AC value
        relativeRates[0] = Math.exp(modelChoose.get().getValue(GTR_INDEX)*logAC.get().getValue(0));
        //rate AT value
        relativeRates[2] = Math.exp(modelChoose.get().getValue(GTR_INDEX)*logAT.get().getValue(0));
        //rate GC value
        relativeRates[3] = Math.exp(modelChoose.get().getValue(GTR_INDEX)*logGC.get().getValue(0));
        //rate GT value
        relativeRates[5] = Math.exp(modelChoose.get().getValue(GTR_INDEX)*logGT.get().getValue(0));
    }

    /** sets up rate matrix **/
    protected void setupRateMatrix() {
        //System.out.println("setup rate matrix");
    	double [] fFreqs = frequencies.get().getFreqs();
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

}
