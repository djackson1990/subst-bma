package beast.evolution.sitemodel;

import beast.core.parameter.RealParameter;
import beast.core.parameter.IntegerParameter;
import beast.core.Input;
import beast.core.Description;
import beast.evolution.tree.Node;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class facilitates bayesian model averaging of gamma site models.")
public class GammaSiteBMA extends SiteModel {

    public Input<RealParameter> logShapeInput =
            new Input<RealParameter>("logShape", "shape parameter of gamma distribution. Ignored if gammaCategoryCount 1 or less",Input.Validate.REQUIRED);
    public Input<RealParameter> logitInvarInput =
            new Input<RealParameter>("logitInvar", "proportion of sites that is invariant: should be between 0 (default) and 1",Input.Validate.REQUIRED);
    public Input<IntegerParameter> modelChoose = new Input<IntegerParameter>("modelChoose",
            "Integer parameter that is a bit vector presenting the model", Input.Validate.REQUIRED);


    public static final int SHAPE_INDEX = 0;
    public static final int INVAR_INDEX = 1;
    public static final int PRESENT = 1;
    public static final int ABSENT = 0;
    public static final int[][] INDICATORS = {
            {ABSENT, ABSENT,},
            {ABSENT, PRESENT},
            {PRESENT, ABSENT},
            {PRESENT, PRESENT}
        };


    private RealParameter logShape;
    private RealParameter logitInvar;

    public GammaSiteBMA() {
        gammaCategoryCount.setRule(Input.Validate.REQUIRED);
    }


    @Override
    public void initAndValidate() throws Exception {

        muParameter = muParameterInput.get();
        if (muParameter == null) {
        	muParameter = new RealParameter("1.0");
        }

        logShape = logShapeInput.get();
        logitInvar = logitInvarInput.get();

        //if (muParameter != null) {
            muParameter.setBounds(0.0, Double.POSITIVE_INFINITY);
        //}

        if ((getProportianInvariant() < 0 || getProportianInvariant() > 1)) {
        	throw new Exception("proportion invariant should be between 0 and 1");
        }

        refresh();

        addCondition(muParameterInput);
        addCondition(invarParameterInput);
        addCondition(shapeParameterInput);



    }

    private int getCurrModel(){
        return modelChoose.get().getValue();

    }

    @Override
	protected void refresh() {
        if (logShape != null) {
            categoryCount = gammaCategoryCount.get();
            if (categoryCount < 1) {
            	System.out.println("SiteModel: Invalid category count (" + categoryCount + ") Setting category count to 1");
               	categoryCount = 1;
            }

            // The quantile calculator fails when the shape parameter goes much below
            // 1E-3 so we have put a hard lower bound on it. If this is not there then
            // the category rates can go to 0 and cause a -Inf likelihood (whilst this
            // is not a problem as the state will be rejected, it could mask other issues
            // and this seems the better approach.
            logShape.setBounds(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        } else {
            categoryCount = 1;
        }

        if (logitInvar != null) {
        	if (m_bPropInvariantIsCategory) {
        		categoryCount += 1;
        	}
            logitInvar.setBounds(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        }

        categoryRates = new double[categoryCount];
        categoryProportions = new double[categoryCount];
        calculateCategoryRates(null);
        //ratesKnown = false;
	}


    /*public void initAndValidate() throws Exception {

        //seting the bound for mu
        muParameter = muParameterInput.get();
        if (muParameter == null) {
        	muParameter = new RealParameter("1.0");
        }
        muParameter.setBounds(0.0, Double.POSITIVE_INFINITY);

        logShape = logShapeInput.get();
        logitInvar = logitInvarInput.get();
        if (logShape == null) {
        	logShape = new RealParameter("0.0");
        }
        //setting bounds for shape
        logShape.setBounds(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);


        if (logitInvar == null) {
        	logitInvar = new RealParameter("0.0");
        }
        //setting bounds for invar
        logitInvar.setBounds(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        //category count
        categoryCount = gammaCategoryCount.get()+1;


        categoryRates = new double[categoryCount];
        categoryProportions = new double[categoryCount];

        ratesKnown = false;

        addCondition(muParameterInput);
        addCondition(logShapeInput);
        addCondition(logitInvarInput);
    }

    public void setPropInvariantIsCategory(boolean bPropInvariantIsCategory) {
	     m_bPropInvariantIsCategory = bPropInvariantIsCategory;
	}*/

    @Override
    protected boolean requiresRecalculation() {
       // we only get here if something is dirty in its inputs
        boolean recalculate = false;
        if(m_pSubstModel.isDirty()){
            recalculate = true;

        }else if(modelChoose.get().somethingIsDirty()){


            recalculate = true;
        }else if(logShape.somethingIsDirty()){
            if(INDICATORS[getCurrModel()][SHAPE_INDEX] == PRESENT){
                recalculate = true;
            }
        }else if(logitInvar.somethingIsDirty()){
            if(INDICATORS[getCurrModel()][INVAR_INDEX] == PRESENT){
                recalculate = true;
            }
        }

        if(recalculate){
            ratesKnown = false;
        }
        //return recalculate;
        return recalculate;


    }

    /**
     * discretization of gamma distribution with equal proportions in each
     * category
     */
    protected void calculateCategoryRates(Node node) {
        double propVariable = 1.0;
        int cat = 0;

        categoryRates[0] = 0.0;
        //back transform from logit space
        categoryProportions[0] = INDICATORS[getCurrModel()][INVAR_INDEX]*(1/(1+Math.exp(-logitInvar.getValue(0))));
        propVariable = 1.0 - categoryProportions[0];
        cat = 1;

        //If including the gamma shape parameter.
        if (INDICATORS[getCurrModel()][SHAPE_INDEX] == PRESENT) {

            //back transform from log-space
            final double a = Math.exp(logShape.getValue(0));
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

	public double getProportianInvariant() {

        if (logitInvar == null) {
        	return 0.0;
        }
		return 1.0/(1.0+Math.exp(-logitInvar.getValue()))*INDICATORS[getCurrModel()][INVAR_INDEX];
	}

}
