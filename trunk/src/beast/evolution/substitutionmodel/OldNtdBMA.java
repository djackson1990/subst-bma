package beast.evolution.substitutionmodel;

import beast.core.parameter.RealParameter;
import beast.core.parameter.IntegerParameter;
import beast.core.Input;
import beast.core.Description;
import beast.evolution.tree.Node;
import beast.evolution.datatype.DataType;
import beast.evolution.datatype.Nucleotide;
import beast.math.matrixAlgebra.Matrix;
import beast.math.matrixAlgebra.RobustEigenDecomposition;
import beast.math.matrixAlgebra.RobustSingularValueDecomposition;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.Property;

import java.util.Arrays;

/**
 * @author Chieh-Hsi Wu
 *
 * Model averaging of nucleotide substitution model
 *
 */
@Description("Model averaging of nucleotide substitution model including HKY85, TN93 and GTR (old parameterization).")
public class OldNtdBMA extends SubstitutionModel.Base{
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
            {PRESENT, ABSENT, ABSENT,ABSENT},
            {ABSENT, PRESENT,ABSENT,ABSENT},
            {PRESENT, PRESENT,ABSENT,ABSENT},
            {PRESENT, PRESENT, PRESENT,ABSENT},
            {PRESENT, PRESENT, PRESENT,PRESENT}
    };


    int m_nStates;
    double [][] m_rateMatrix;
    protected double[] relativeRates;
    protected double[] storedRelativeRates;

    @Override
    public void initAndValidate() throws Exception {
        if(modelChoose.get().getUpper() > GTR || modelChoose.get().getUpper() < JC){
            System.err.println("The value of model choose needs to be between " + JC + " and " + GTR + "inclusive, " +
                    "where "+ JC + " and " + GTR +" represents JC and GTR repectively");
        }
        updateMatrix = true;
        m_nStates = STATE_COUNT;
        //eigenSystem = new DefaultEigenSystem(STATE_COUNT);
        m_rateMatrix = new double[STATE_COUNT][STATE_COUNT];
        relativeRates = new double[RATE_COUNT];
        storedRelativeRates = new double[RATE_COUNT];
        initialiseEigen();

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

        /*System.err.println("AC: "+relativeRates[0]);
        System.err.println("AG: "+relativeRates[1]);
        System.err.println("AT: "+relativeRates[2]);
        System.err.println("GC: "+relativeRates[3]);
        System.err.println("CT: "+relativeRates[4]);
        System.err.println("GT: "+relativeRates[5]);
        System.err.println("indicators: "+INDICATORS[getCurrModel()][K80_INDEX]);

        System.err.println(logKappa.get().getValue(0));
        System.err.println(INDICATORS[modelChoose.get().getValue(0)][TN_INDEX]);
        System.err.println(logTN.get().getValue(0));*/


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

       /* System.err.println("Part 1");
       for(i = 0; i < m_nStates; i++){
            for(j = 0; j < m_nStates; j++){
                System.err.print(m_rateMatrix[i][j]+" ");
            }
            System.err.println();
        }                        */

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

       /*System.err.println("Part 2");
       for(i = 0; i < m_nStates; i++){

                System.err.print(m_rateMatrix[i][i]+" ");

            System.err.println();
        }
        System.err.println("fSubst: "+fSubst);*/
        for (i = 0; i < m_nStates; i++) {
            for (j = 0; j < m_nStates; j++) {
            	m_rateMatrix[i][j] = m_rateMatrix[i][j] / fSubst;
            }
        }
        /*System.err.println("rate matrix");
        for(i = 0; i< m_nStates;i++){
            for(j = 0;j < m_nStates;j++){
                System.err.print(m_rateMatrix[i][j]);
            }
            System.err.println();
        }*/

        RobustEigenDecomposition eigenDecomp;
        try {
            eigenDecomp = new RobustEigenDecomposition(new DenseDoubleMatrix2D(m_rateMatrix), maxIterations);
        } catch (ArithmeticException ae) {
            System.err.println(ae.getMessage());
            wellConditioned = false;
            System.err.println("amat = \n" + new Matrix(m_rateMatrix));
            return;
        }

        DoubleMatrix2D eigenV = eigenDecomp.getV();
        DoubleMatrix1D eigenVReal = eigenDecomp.getRealEigenvalues();
        DoubleMatrix1D eigenVImag = eigenDecomp.getImagEigenvalues();
        DoubleMatrix2D eigenVInv;

        // A better (?) approach to checking diagonalizability comes from:
        //
        // J. Gentle (2007) Matrix Algebra
        //
        // Diagonalizbility Theorem: A matrix A is (complex) diagonalizable iff all distinct eigenvalues \lambda_l
        // with algebraic multiplicity m_l are semi-simple, i.e.
        //
        //          rank(A - \lambda_l I) = n - m_l
        //
        // Equivalently (?), eigenV must be non-singular.
        //
        // SVD is needed to numerically approximate the rank of a matrix, so we can check Algrebra.rank()
        // or Algebra.cond() with almost equal amounts of work.  I don't know which is more reliable. -- MAS

        if (checkConditioning) {
            RobustSingularValueDecomposition svd;
            try {
                svd = new RobustSingularValueDecomposition(eigenV, maxIterations);
            } catch (ArithmeticException ae) {
                System.err.println(ae.getMessage());
                wellConditioned = false;
                return;
            }
            if (svd.cond() > maxConditionNumber) {
                wellConditioned = false;
                return;
            }
        }

        try {
            eigenVInv = alegbra.inverse(eigenV);
        } catch (IllegalArgumentException e) {
            wellConditioned = false;
            return;
        }

        Ievc = eigenVInv.toArray();
        Evec = eigenV.toArray();
        Eval = eigenVReal.toArray();
        EvalImag = eigenVImag.toArray();

        // Check for valid decomposition
        for (i = 0; i < m_nStates; i++) {
            if (Double.isNaN(Eval[i]) || Double.isNaN(EvalImag[i]) ||
                    Double.isInfinite(Eval[i]) || Double.isInfinite(EvalImag[i])) {
                wellConditioned = false;
                return;
            }
        }

        updateMatrix = false;
        wellConditioned = true;

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

    @Override
    public double[] getFrequencies() {
        if(INDICATORS[getCurrModel()][F81_INDEX] == PRESENT){
            //System.out.println("estimate freqs");
            return frequencies.get().getFreqs();
        }else{

            return UNIF_DIST;
        }
    }

        @Override
    public void getTransitionProbabilities(Node node, double fStartTime, double fEndTime, double fRate, double[] matrix) {
    	//System.err.println("Get probs: "+updateMatrix);
        double distance = (fStartTime - fEndTime) * fRate;

        int i, j, k;
        double temp;

        // this must be synchronized to avoid being called simultaneously by
        // two different likelihood threads - AJD
        synchronized (this) {
            if (updateMatrix) {
                //System.err.println("UPDATE MATRIX");
            	setupRelativeRates();
            	setupRateMatrix();
                /*System.err.println("rate matrix just before eigen:");
                for(i = 0; i< m_nStates;i++){
                    for(j = 0;j < m_nStates;j++){
                        System.err.print(m_rateMatrix[i][j]);
                    }
                    System.err.println();
                }*/

            	updateMatrix = false;
            }
        }

        if (!wellConditioned) {
            Arrays.fill(matrix, 0.0);
            return;
        }
        double[][] iexp = new double[m_nStates][m_nStates];//popiexp();

        for (i = 0; i < m_nStates; i++) {

            if (EvalImag[i] == 0) {
                // 1x1 block
                temp = Math.exp(distance * Eval[i]);
                for (j = 0; j < m_nStates; j++) {
                    iexp[i][j] = Ievc[i][j] * temp;
                }
            } else {
                // 2x2 conjugate block
                // If A is 2x2 with complex conjugate pair eigenvalues a +/- bi, then
                // exp(At) = exp(at)*( cos(bt)I + \frac{sin(bt)}{b}(A - aI)).
                int i2 = i + 1;
                double b = EvalImag[i];
                double expat = Math.exp(distance * Eval[i]);
                double expatcosbt = expat * Math.cos(distance * b);
                double expatsinbt = expat * Math.sin(distance * b);

                for (j = 0; j < m_nStates; j++) {
                    iexp[i][j] = expatcosbt * Ievc[i][j] + expatsinbt * Ievc[i2][j];
                    iexp[i2][j] = expatcosbt * Ievc[i2][j] - expatsinbt * Ievc[i][j];
                }
                i++; // processed two conjugate rows
            }
        }

        int u = 0;
        for (i = 0; i < m_nStates; i++) {
            for (j = 0; j < m_nStates; j++) {
                temp = 0.0;
                for (k = 0; k < m_nStates; k++) {
                    temp += Evec[i][k] * iexp[k][j];
                }
                if (temp < 0.0)
                    matrix[u] = minProb;
                else
                    matrix[u] = temp;
                u++;
            }
        }
        //pushiexp(iexp);
        /*System.err.println("dist: "+distance);
        k=0;
        for(i = 0 ; i < m_nStates; i++){
            for(j = 0; j < m_nStates;j++){
                System.err.print(matrix[k++]);

            }
            System.err.println();
        }*/
    } // getTransitionProbabilities


    protected double[] Eval;
    protected double[] EvalImag;
    protected double[] storedEvalImag;
    protected double[] storedEval;
    protected double[][] Evec;
    protected double[][] storedEvec;
    protected double[][] Ievc;
    protected double[][] storedIevc;

    /**
     * allocate memory for the Eigen routines
     */
    protected void initialiseEigen() {

        Eval = new double[m_nStates];
        Evec = new double[m_nStates][m_nStates];
        Ievc = new double[m_nStates][m_nStates];

        storedEval = new double[m_nStates];
        storedEvec = new double[m_nStates][m_nStates];
        storedIevc = new double[m_nStates][m_nStates];
        storedEvalImag = new double[m_nStates];



        updateMatrix = true;
    }



    /**
     * Restore the additional stored state
     */
    @Override

    public void restore() {

        // To restore all this stuff just swap the pointers...

        double[] tmp3 = storedEvalImag;
        storedEvalImag = EvalImag;
        EvalImag = tmp3;



//        normalization = storedNormalization;

        // Inherited
        updateMatrix = storedUpdateMatrix;
        wellConditioned = storedWellConditioned;

        double[] tmp1 = storedEval;
        storedEval = Eval;
        Eval = tmp1;

        double[][] tmp2 = storedIevc;
        storedIevc = Ievc;
        Ievc = tmp2;

        tmp2 = storedEvec;
        storedEvec = Evec;
        Evec = tmp2;

        // To restore all this stuff just swap the pointers...
        double[] tmp4 = storedRelativeRates;
        storedRelativeRates = relativeRates;
        relativeRates = tmp4;

        super.restore();

    }

    public void store() {

        storedUpdateMatrix = updateMatrix;

//        if(updateMatrix)
//            System.err.println("Storing updatable state!");

        storedWellConditioned = wellConditioned;

        System.arraycopy(EvalImag, 0, storedEvalImag, 0, m_nStates);
//        storedNormalization = normalization;

        // Inherited
        System.arraycopy(Eval, 0, storedEval, 0, m_nStates);
        for (int i = 0; i < m_nStates; i++) {
            System.arraycopy(Ievc[i], 0, storedIevc[i], 0, m_nStates);
            System.arraycopy(Evec[i], 0, storedEvec[i], 0, m_nStates);
        }

        super.store();
    }

	@Override
	public boolean canHandleDataType(DataType dataType) throws Exception {
		if (dataType instanceof Nucleotide) {
			return true;
		}
		throw new Exception("Can only handle nucleotide data");
	}
    /**
     * This function returns the Eigen vectors.
     *
     * @return the array
     */
    @Override
    public EigenDecomposition getEigenDecomposition(Node node) {

        EigenSystem eigenSystem =  new DefaultEigenSystem(m_nStates);
        synchronized (this) {
            if (updateMatrix) {
            	setupRelativeRates();
                setupRateMatrix();

                updateMatrix = false;
            }
        }
        //EigenDecomposition eigenDecomposition = eigenSystem.decomposeMatrix(m_rateMatrix);
        return eigenSystem.decomposeMatrix(m_rateMatrix);
    }


    protected boolean updateMatrix = true;
    protected boolean storedUpdateMatrix = true;
    private int maxIterations = 1000;
    private double maxConditionNumber = 1000;
    private boolean checkConditioning = true;
    protected boolean wellConditioned = true;
    private boolean storedWellConditioned;
    protected static final double minProb = Property.DEFAULT.tolerance();
    private static final Algebra alegbra = new Algebra(minProb);
}
