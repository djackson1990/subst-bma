package beast.math.distributions;

import beast.core.Description;
import beast.core.Input;
import beast.core.parameter.ParameterList;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class is a wrapper that provides prior to parameter list.")
public class ParameterListPrior extends Prior{

    public Input<List<ParameterList>> xListsInput = new Input<List<ParameterList>>(
            "xList",
            "points at which the density is calculated",
            new ArrayList<ParameterList>(),
            Input.Validate.REQUIRED
    );

    public Input<Boolean> applyToListInput = new Input<Boolean>(
            "applyToList",
            "Whether the prior is applied to the entire list",
            Input.Validate.REQUIRED
    );


    public ParameterListPrior(){
        m_x.setRule(Input.Validate.OPTIONAL);
    }

    boolean applyToList;
    public void initAndValidate(){
        super.initAndValidate();
        applyToList = applyToListInput.get();

    }

    @Override
	public double calculateLogP() throws Exception {
        List<ParameterList> parameterLists = xListsInput.get();
        if(applyToList){
            //System.err.println("logP: "+logP);
            logP = ((DirichletProcess)m_dist).calcLogP(parameterLists);
        }else{
            logP = 0.0;

            int listCount = parameterLists.size();
            for(int i = 0; i < listCount; i++){
                int dimParam = parameterLists.get(i).getDimension();
                for(int j = 0; j < dimParam; j ++){
                    logP += m_dist.calcLogP(parameterLists.get(i).getParameter(j));
                }
            }

        }

		return logP;
	}


 
}
