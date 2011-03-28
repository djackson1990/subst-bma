package scripts;


import beast.math.matrixAlgebra.RobustEigenDecomposition;
import beast.math.matrixAlgebra.Matrix;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.Property;

/**
 * Created by IntelliJ IDEA.
 * User: jessie
 * Date: Mar 16, 2011
 * Time: 9:06:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class CheckEigen2 {

    public static void main(String[] args){
        double[][] m = new double[4][];
        m[0] = new double[]{-1.000000000000,0.328977506868,0.342044986264,0.328977506868};
        m[1] = new double[]{0.328977506868,-1.000000000000,0.328977506868,0.342044986264};
        m[2] = new double[]{0.342044986264, 0.328977506868, -1.000000000000,  0.328977506868};
        m[3] = new double[]{0.328977506868,  0.342044986264,  0.328977506868, -1.000000000000};

        /*m[0] = new double[]{-1.0,0.3289775068681328,0.34204498626373436,0.3289775068681328};
        m[1] = new double[]{0.3289775068681328,-1.0,0.3289775068681328,0.34204498626373436};
        m[2] = new double[]{0.34204498626373436,0.3289775068681328,-1.0,0.3289775068681328};
        m[3] = new double[]{0.3289775068681328,0.34204498626373436,0.3289775068681328,-1.0};*/


        RobustEigenDecomposition eigenDecomp;
        try {
            eigenDecomp = new RobustEigenDecomposition(new DenseDoubleMatrix2D(m), 1000);
        } catch (ArithmeticException ae) {
            System.err.println(ae.getMessage());

            System.err.println("m = \n" + new Matrix(m));
            return;
        }

        double minProb = Property.DEFAULT.tolerance();
    //    private static final double minProb = 1E-20;
    //    private static final double minProb = Property.ZERO.tolerance();
        Algebra alegbra = new Algebra(minProb);
        DoubleMatrix2D eigenV = eigenDecomp.getV();
        DoubleMatrix1D eigenVReal = eigenDecomp.getRealEigenvalues();
        DoubleMatrix1D eigenVImag = eigenDecomp.getImagEigenvalues();
        DoubleMatrix2D eigenVInv = alegbra.inverse(eigenV);

        double[][] Ievc = eigenVInv.toArray();
        double[][] Evec = eigenV.toArray();
        double[] Eval = eigenVReal.toArray();
        double[] EvalImag = eigenVImag.toArray();
        for(int i = 0; i < Evec.length;i++){
            for(int j = 0;j < Evec[i].length;j++){
                System.err.print(Evec[i][j]+" ");
            }
            System.err.println();
        }

        int stateCount = 4;
        double[][] iexp = new double[stateCount][stateCount];
        int i,j, k;
        double temp;
        double distance = 0.030456999999999998;
        for (i = 0; i < stateCount; i++) {

            if (EvalImag[i] == 0) {
                // 1x1 block
                temp = Math.exp(distance * Eval[i]);
                for (j = 0; j < stateCount; j++) {
                    iexp[i][j] = Ievc[i][j] * temp;
                }
            }
        }


        double[] matrix = new double[stateCount*stateCount];
        int u = 0;
        for (i = 0; i < stateCount; i++) {
            for (j = 0; j < stateCount; j++) {
                temp = 0.0;
                for (k = 0; k < stateCount; k++) {
                    temp += Evec[i][k] * iexp[k][j];
                }
                if (temp < 0.0)
                    matrix[u] = minProb;
                else
                    matrix[u] = temp;
                u++;
            }
        }

        u = 0;
        for(i = 0; i < stateCount; i++){
            for(j = 0; j <stateCount; j++){
                System.err.print(matrix[u++]+" ");
            }
            System.err.println();

        }




    }
}
