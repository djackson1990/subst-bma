package beast.core;

/**
 * Created by IntelliJ IDEA.
 * User: cwu080
 * Date: 22/08/12
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class MCMCNodeFactory {

    public static void checkDirtiness(CalculationNode calcNode){
        calcNode.checkDirtiness();

    }

    public static void makeAccept(CalculationNode calcNode){
        calcNode.accept();

    }
}
