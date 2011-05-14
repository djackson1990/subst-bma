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
    	super(fValues);
    }

    /** Constructor used by Input.setValue(String) **/
    public RealParameter2(String sValue) throws Exception {
    	init(0.0, 0.0, sValue, 1);
    }

    @Override
    public void initAndValidate() throws Exception {
        super.initAndValidate();
    }

    public void setValueQuietly(int dim, double value){
        values[dim] = value;
        m_bIsDirty[dim] = true;
        m_nLastDirty = dim;
    }

}


