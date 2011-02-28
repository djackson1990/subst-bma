package beast.evolution.siteModel;

import beast.core.parameter.RealParameter;
import beast.core.parameter.IntegerParameter;
import beast.core.Input;
import beast.evolution.substitutionmodel.SubstitutionModel;
import beast.evolution.sitemodel.SiteModel;
import org.apache.commons.math.distribution.GammaDistribution;

/**
 * @author Chieh-Hsi Wu
 *
 * BSSVS on Gamma site model
 *
 */
public class GammaSiteBMA extends SiteModel {

    public Input<Integer> gammaCategoryCount =
            new Input<Integer>("gammaCategoryCount", "gamma category count (default=zero for no gamma)",Input.Validate.REQUIRED);
    public Input<RealParameter> muParameter = new Input<RealParameter>("mutationRate", "mutation rate (defaults to 1.0)",Input.Validate.REQUIRED);
    public Input<RealParameter> logShape =
            new Input<RealParameter>("logShape", "shape parameter of gamma distribution. Ignored if gammaCategoryCount 1 or less",Input.Validate.REQUIRED);
    public Input<RealParameter> logitInvar =
            new Input<RealParameter>("logitInvar", "proportion of sites that is invariant: should be between 0 (default) and 1",Input.Validate.REQUIRED);
    public Input<SubstitutionModel.Base> m_pSubstModel =
            new Input<SubstitutionModel.Base>("substModel", "substitution model along branches in the beast.tree", Input.Validate.REQUIRED);
    public Input<IntegerParameter> modelChoose = new Input<IntegerParameter>("modelChoose",
            "Integer parameter that is a bit vector presenting the model", Input.Validate.REQUIRED);


    public static final int SHAPE_INDEX = 0;
    public static final int INVAR_INDEX = 1;
    public static final int PRESENT = 1;
    public static final int ABSENT = 0;

    public void initAndValidate() throws Exception {


        //seting the bound for mu
        muParameter.get().setBounds(0.0, Double.POSITIVE_INFINITY);

        //setting bounds for shape
        shapeParameter.get().setBounds(1.0E-3, 1.0E3);

        //setting bounds for invar
        invarParameter.get().setBounds(0.0, 1.0);

        //category count
        categoryCount = gammaCategoryCount.get()+1;

        
        categoryRates = new double[categoryCount];
        categoryProportions = new double[categoryCount];

        ratesKnown = false;

        addCondition(muParameter);
        addCondition(invarParameter);
        addCondition(shapeParameter);
    }

    @Override
    protected boolean requiresRecalculation() {
        boolean recalculate = false;
        // we only get here if something is dirty in its inputs

        /*if (muParameter.isDirty()) {
            ratesKnown = false;
        }
        if (muParameter.isDirty()) {
            ratesKnown = false;
        }
        if (muParameter.isDirty()) {
            ratesKnown = false;
        }
        ratesKnown = false;
        return true;*/
        //return m_pSubstModel.isDirty() || !ratesKnown;
        return recalculate;
    }

    /**
     * discretization of gamma distribution with equal proportions in each
     * category
     */
    private void calculateCategoryRates() {
        double propVariable = 1.0;
        int cat = 0;

        categoryRates[0] = 0.0;
        //back transform from logit space
        categoryProportions[0] = modelChoose.get().getValue(INVAR_INDEX)*(1/(1+Math.exp(-logitInvar.get().getValue(0))));

        propVariable = 1.0 - categoryProportions[0];
        cat = 1;
        
        //If including the gamma shape parameter.
        if (modelChoose.get().getValue(SHAPE_INDEX) == PRESENT) {

            //back transform from log-space
            final double a = Math.exp(logShape.get().getValue(0));
            double mean = 0.0;
            final int gammaCatCount = categoryCount - cat;

            for (int i = 0; i < gammaCatCount; i++) {
                try {
                    categoryRates[i + cat] = GammaDistributionQuantile((2.0 * i + 1.0) / (2.0 * gammaCatCount), a, 1.0 / a);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Something went wrong with the gamma distribution calculation");
                    System.exit(-1);
                }
                //sum of the gamma categorical rates
                mean += categoryRates[i + cat];

                categoryProportions[i + cat] = propVariable / gammaCatCount;
            }

            //mean rate over all categories.
            mean = (propVariable * mean) / gammaCatCount;

            for (int i = 0; i < gammaCatCount; i++) {
                //divide rates by the mean so that the average across all sites equals to 1.0
                categoryRates[i + cat] /= mean;

            }

        } else {
            final int gammaCatCount = categoryCount - cat;
            for (int i = 0; i < gammaCatCount; i++) {
                categoryRates[i + cat] = 1.0 / propVariable;
                categoryProportions[i + cat] = propVariable/gammaCatCount;
            }
            
        }


        ratesKnown = true;
    }

}
