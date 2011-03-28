package scripts;

import beast.evolution.substitutionmodel.DefaultEigenSystem;
import beast.evolution.substitutionmodel.EigenDecomposition;
import beast.evolution.substitutionmodel.EigenSystem;

/**
 * Created by IntelliJ IDEA.
 * User: jessie
 * Date: Mar 16, 2011
 * Time: 7:42:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class CheckEigen {

    public static void main(String[] args){
        double[][] m = new double[4][];
        m[0] = new double[]{-1.0,0.3289775068681328,0.34204498626373436,0.3289775068681328};
        m[1] = new double[]{0.3289775068681328,-1.0,0.3289775068681328,0.34204498626373436};
        m[2] = new double[]{0.34204498626373436,0.3289775068681328,-1.0,0.3289775068681328};
        m[3] = new double[]{0.3289775068681328,0.34204498626373436,0.3289775068681328,-1.0};

        /*m[0] = new double[]{-1.0,0.328977506868,0.342044986264,0.328977506868};
        m[1] = new double[]{0.328977506868,-1.0,0.328977506868,0.342044986264};
        m[2] = new double[]{0.342044986264,0.328977506868, -1.0, 0.328977506868};
        m[3] = new double[]{0.328977506868, 0.342044986264, 0.328977506868, 1.0};*/

        
        EigenSystem eigenSystem = new DefaultEigenSystem(m.length);
        EigenDecomposition eigenDecomposition = eigenSystem.decomposeMatrix(m);

        double[] iexp = new double[m.length];
        // Eigen vectors
        double[] Evec = eigenDecomposition.getEigenVectors();
        // inverse Eigen vectors
        double[] Ievc = eigenDecomposition.getInverseEigenVectors();
        // Eigen values
        double[] Eval = eigenDecomposition.getEigenValues();


        int k = 0;

        double[][] evec = new double[m.length][m.length];
        for(int i = 0;i < evec.length;i++){
            for(int j = 0; j < evec[i].length;j++){
                evec[i][j] =Evec[k++];
            }
        }

        k = 0;

        double[][] ievc = new double[m.length][m.length];
        for(int i = 0;i < ievc.length;i++){
            for(int j = 0; j < ievc[i].length;j++){
                ievc[i][j] = Ievc[k++];
            }
        }


        k =0;
        for(int i = 0; i < Eval.length;i++){
            for(int j = 0;j < Eval.length;j++){
                System.err.print(Evec[k++]+" ");
            }
            System.err.println();
        }


        double distance =0.030457;
        for(int i = 0; i < iexp.length; i++){
            iexp[i] = Math.exp(distance*Eval[i]);
            System.err.println(iexp[i]);
        }

        double[][] probs = new double[m.length][m.length];
        for(int i = 0; i < probs.length;i++){
            for(int j = 0;j < probs.length;j++){
                probs[i][j] = iexp[i]*ievc[i][j];
            }
        }

        for(int i = 0; i < probs.length;i++){
            double temp =0;
            for(int j = 0;j < probs.length;j++){
                for(int l = 0; l < probs.length; l++){
                    temp += evec[j][l]*probs[j][l];
                }
                probs[i][j]=temp;
            }
          
        }




        for(int i = 0;i < probs.length;i++){
            for(int j = 0;j < probs.length;j++){
                System.err.print(probs[i][j]+" ");
            }
            System.err.println();
        }

        int i, j;
        double temp;
        double[] matrix = new double[16];
        iexp = new double[16];
        int m_nStates = 4;

        for (i = 0; i < m_nStates; i++) {
            temp = Math.exp(distance * Eval[i]);
            for (j = 0; j < m_nStates; j++) {
                iexp[i * m_nStates + j] = Ievc[i * m_nStates + j] * temp;
            }
        }

        int u = 0;
        for (i = 0; i < m_nStates; i++) {
            for (j = 0; j < m_nStates; j++) {
                temp = 0.0;
                for (k = 0; k < m_nStates; k++) {
                    temp += Evec[i * m_nStates + k] * iexp[k * m_nStates + j];
                }

                matrix[u] = Math.abs(temp);
                u++;
            }
        }

        k = 0;
        for(i = 0;i < probs.length;i++){
            for(j = 0;j < probs.length;j++){
                System.err.print(matrix[k++]+" ");
            }
            System.err.println();
        }

        



    }
}
