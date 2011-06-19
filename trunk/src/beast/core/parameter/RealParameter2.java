package beast.core.parameter;



import beast.core.Description;
/**
 * @author Chieh-Hsi Wu
 */

@Description("RealParameter that has a method which allows values to be set quietly.")
public class RealParameter2 extends RealParameter {
    public RealParameter2() {
    }

    public RealParameter2(Double [] fValues) throws Exception {
    	this(fValues, Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
    }

    public RealParameter2(Double [] fValues,double lower, double upper) throws Exception {
    	super(fValues);
        setUpper(upper);
        setLower(lower);
        m_bIsDirty = new boolean[fValues.length];
        storedValues = new java.lang.Double[fValues.length];
    }

    /** Constructor used by Input.setValue(String)
     * @param sValue values of parameter
     * @throws Exception Will throw some exception.
     * **/
    public RealParameter2(String sValue) throws Exception {
    	init(0.0, 0.0, sValue, 1);
    }

    @Override
    public void initAndValidate() throws Exception {
        super.initAndValidate();
    }

  
}


