package beast.math.util;

/**
 * @author Chieh-Hsi Wu
 */
public class SterlingNumber {
    public static long[] getSterlingNumberOfFirstKind(int n){
        long[][] table = new long[n+1][n+1];
        table[0][0] = 1;
        for(int i = 1; i < table.length;i++){
            for(int j = 1; j <= i;j++){
                table[i][j] = (i-1)*table[i-1][j]+table[i-1][j-1];
                
            }
        }

        /*for(int i = 0; i < table.length; i++){
            for(int j = 0; j <= i;j++){
                System.err.print(table[i][j]+"\t");
            }
            System.err.println();
        }*/
        return table[n];

    }

    public static void main(String[] args){
        long[] s1 = getSterlingNumberOfFirstKind(20);
        for(Long s:s1){
            System.err.print(s+"\t");

        }

    }
}
