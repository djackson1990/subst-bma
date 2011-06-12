package test;

import junit.framework.TestCase;
import beast.app.BeastMCMC;

/**
 * @author Chieh-Hsi Wu
 */
public class XMLTest extends TestCase {
    public void testXMls(){
        try{
            String sDir = System.getProperty("user.dir");
            System.err.println(sDir);
            String xml = "C:\\Users\\Jessie Wu\\Documents\\Jessie\\BEAST\\SubstBMA\\xml\\testOperators\\testDirichletProcessPriorSampler_2.xml";
            BeastMCMC.main(new String[]{xml});

        }catch(Exception e){

        }
    }

}
