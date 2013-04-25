package beast.math.util;


import beast.util.Randomizer;

/**
 * Methods from MathUtils in BEAST1
 */
public class MathUtils  {
    /**
 * Returns sqrt(a^2 + b^2) without under/overflow.
 */
    public static double hypot(double a, double b) {
        double r;

	    if (Math.abs(a) > Math.abs(b)) {
		    r = b/a;
		    r = Math.abs(a)*Math.sqrt(1+r*r);
	    } else if (b != 0) {
		    r = a/b;
		    r = Math.abs(b)*Math.sqrt(1+r*r);
	    } else {
		    r = 0.0;
	    }
	    return r;
    }

    public static int[] sample(int size, int[] vec, boolean replace){
        int[] temp = new int[size];
        if(replace){

            for(int i = 0; i < size; i++){

                temp[i] = vec[Randomizer.nextInt(vec.length)];


            }

        }else{
            int sampledSiteIndex;
            int[] temp2 = new int[vec.length];
            System.arraycopy(vec, 0, temp2, 0, vec.length);
            for(int i = 0; i < size; i++){
                sampledSiteIndex  = Randomizer.nextInt(vec.length - i);
                temp2[vec.length - i - 1] = temp2[sampledSiteIndex];
                temp2[sampledSiteIndex] = vec[vec.length - i - 1];
            }

            System.arraycopy(temp2,vec.length-size,temp,0,size);

        }

         return temp;

    }


   
}
