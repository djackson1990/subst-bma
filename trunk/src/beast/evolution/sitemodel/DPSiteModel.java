package beast.evolution.sitemodel;

import beast.core.parameter.ParameterList;
import beast.core.parameter.DPPointer;
import beast.core.parameter.RealParameter;
import beast.core.Input;

/**
 * @author Chieh-Hsi Wu
 */
public class DPSiteModel {
        //ParameterList
    public Input<ParameterList> paramListInput = new Input<ParameterList>(
            "paramList",
            "A list of unique parameter values",
            Input.Validate.REQUIRED
    );


    //assignment
    public Input<DPPointer> pointersInput = new Input<DPPointer>(
            "pointers",
            "array which points a set of unique parameter values",
            Input.Validate.REQUIRED
    );

    SiteModel[] siteModels;
    DPPointer pointers;
    ParameterList paramList;
    public void initAndValidate() throws Exception{
        
        pointers = pointersInput.get();
        paramList = paramListInput.get();
        int dim = pointers.getDimension();
        for(int i = 0;i < dim; i++){
            SiteModel siteModel = new SiteModel();
            siteModel.initByName(
                    "mutationRate", paramList.getParameter(pointers.indexInList(i,paramList))
            );
        }
        
        
    }

    public boolean requriesRecalculation(){
        return pointers.somethingIsDirty() || paramList.somethingIsDirty();
    }




    class RealParameterWrapper extends RealParameter{
        private int index;
        private DPPointer pointers;
        public RealParameterWrapper(DPPointer pointers, int index){
            this.index = index;
            this.pointers = pointers;
        }

        public void initAndValidate() throws Exception{}

        public Double getValue(){
            return pointers.getParameterValue(index);

        }


    }
}
