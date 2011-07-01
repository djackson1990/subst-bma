package beast.core.parameter;

import beast.core.Description;

/**
 * @author Chieh-Hsi Wu
 */
@Description("Wrap around a parameter but only look at one value.")
public class RealParameterWrapper extends RealParameter{
    private RealParameter parameter;
    private int index;
    public RealParameterWrapper(RealParameter parameter, int index){
        this.parameter = parameter;
        this.index = index;

    }

    @Override
    public Double getValue(){
        return parameter.getValue(index);

    }

    public boolean somethingIsDirty(){
        return parameter.isDirty(index);
    }

    @Override
    public void setValue(Double val){
        parameter.setValue(index,val);
    }

    @Override
    public Double getValue(int index){
        throw new RuntimeException("This is a one dimensional parameter");
    }

    @Override
    public void setValue(int index, Double value){
        throw new RuntimeException("This is a one dimensional parameter");
    }


}
