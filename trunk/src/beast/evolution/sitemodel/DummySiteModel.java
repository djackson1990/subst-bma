package beast.evolution.sitemodel;

import beast.core.parameter.QuietRealParameter;

/**
 * @author Chieh-Hsi Wu
 */
public class DummySiteModel extends SiteModel {
    public void initAndValidate() throws Exception{

        super.initAndValidate();
        if(!(muParameter instanceof QuietRealParameter) ){
            throw new RuntimeException("Quiet mu parameter required");
        }

    }
    public QuietRealParameter getRateParameter(){
        return (QuietRealParameter)muParameter;
    }
}
