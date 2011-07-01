package beast.math.distributions;

import beast.core.parameter.RealParameter;
import beast.core.Description;
import beast.util.Randomizer;

/**
 * @author Chieh-Hsi Wu
 */
@Description("Extends Dirichlet class to add the sample method.")
public class DirichletDistribution extends Dirichlet{

    @Override
    public Double[][] sample(int size){
        RealParameter alpha = m_alpha.get();
        int dim = alpha.getDimension();
        Double[][] samples = new Double[size][];
        try{

            for(int i =0; i < size;i++){
                Double[] dirichletSample = new Double[dim];
                double sum = 0.0;
                for(int j  =0; j < dim;j++){
                    dirichletSample[j] = Randomizer.nextGamma(alpha.getValue(j),1.0);
                    sum += dirichletSample[j];
                }
                for(int j = 0; j < dim;j++){
                    dirichletSample[j] = dirichletSample[j]/sum;
                }
                samples[i] = dirichletSample;

            }
        }catch(Exception e){
            throw new RuntimeException(e);

        }
        return samples;

    }

    public static void  main(String[] args){
        try{
            RealParameter alpha = new RealParameter(new Double[]{4.0,2.0,1.0,3.0});
            DirichletDistribution dirichlet = new DirichletDistribution();
            dirichlet.initByName(
                    "alpha", alpha
            );

             Double[][] samples = dirichlet.sample(1000);

            for(int i = 0; i < 1000; i++){
                RealParameter sample = new RealParameter(samples[i]);
                sample.log(0,System.out);
                System.out.println();

            }

            //fitted alpha from VGAM
            //3.9830039 1.9792060 0.9378492 2.9612180

        }catch(Exception e){
            throw new RuntimeException(e);

        }

    }
}
