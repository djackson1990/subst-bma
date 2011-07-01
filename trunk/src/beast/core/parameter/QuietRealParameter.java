package beast.core.parameter;

import beast.core.Description;

/**
 * @author Chieh-Hsi Wu
 */
@Description("A real parameter class that allows values to be set quietly. It's convenience is beyond imagination.")
public class QuietRealParameter extends RealParameter{
    public QuietRealParameter(){
        
    }
    public void initAndValidate() throws Exception{
        super.initAndValidate();
    }

    public QuietRealParameter(Double[] values) throws Exception{
        super(values);

    }


    public void setValueQuietly(int dim, Double value){
        values[dim] = value;
        m_bIsDirty[dim] = true;
        m_nLastDirty = dim;
    }
}
