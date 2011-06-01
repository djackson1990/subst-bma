package test;

import beast.core.parameter.*;
import beast.core.State;
import beast.math.distributions.ParametricDistribution;
import junit.framework.TestCase;

/**
 * @author Chieh-Hsi Wu
 */
public class DPPointerTest extends TestCase {

    interface Instance {
        public void setup();
        public DPPointer getDPPointer();
        public ParameterList getParameterList();

    }

    Instance test0 = new Instance(){
        ParameterList paramList;
        DPPointer pointer;
        public void setup(){
            try{

                RealParameter2 parameter1 = new RealParameter2(new Double[]{0.0});
                RealParameter2 parameter2 = new RealParameter2(new Double[]{1.0});
                RealParameter2 parameter3 = new RealParameter2(new Double[]{2.0});
                paramList = new ParameterList();
                paramList.initByName(
                        "parameter", parameter1,
                        "parameter", parameter2,
                        "parameter", parameter3
                );
                paramList.setID("parameterList");
                IntegerParameter initAssign = new IntegerParameter(new Integer[]{0,2,1,1,2});
                pointer = new DPPointer();
                pointer.initByName(
                        "uniqueParameters", parameter1,
                        "uniqueParameters", parameter2,
                        "uniqueParameters", parameter3,
                        "initialAssignment", initAssign
                );
                pointer.setID("pointer");
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }
        public DPPointer getDPPointer(){
            return pointer;
        }

        public ParameterList getParameterList(){
            return paramList;
        }


        

    };



    Instance[] tests = new Instance[]{test0};
    public void testDDPointer(){
        try{
        for(Instance test: tests){
            test.setup();
            DPPointer pointer = test.getDPPointer();
            ParameterList paramList =  test.getParameterList();
            State state = new State();

        state.initByName(
                "stateNode", pointer,
                "stateNode", paramList
        );
        state.initialise();

            operationEx1(pointer,paramList);
        }
        }catch(Exception e){
            throw new RuntimeException(e);

        }

    }

    public void operationEx1(DPPointer pointer,ParameterList paramList){
        try{

            RealParameter2 newVal = new RealParameter2(new Double[]{3.0});
            int index = 3;
            int[] assignment = new int[]{0,2,1,1,2};

            RealParameter2[] expectedPointer= new RealParameter2[assignment.length];
            RealParameter2[] expectedList = new RealParameter2[assignment.length];
            


            operation1(pointer, paramList, index, newVal,assignment,expectedPointer,expectedList);
            for(int i = 0; i < expectedPointer.length; i++)
                assertTrue(pointer.sameParameter(i,expectedPointer[i]));

            for(int i = 0; i < paramList.getDimension(); i++)
                assertTrue(paramList.getParameter(i)==expectedList[i]);

            index = 1;
            int listIndex = 2;
            assignment = new int[]{0,2,1,3,2};
            operation2(pointer, paramList, index, listIndex,assignment,expectedPointer);
            for(int i = 0; i < expectedPointer.length; i++){
                //System.err.println(expectedPointer[i]);
                assertTrue(pointer.sameParameter(i,expectedPointer[i]));
            }

            for(int i = 0; i < paramList.getDimension(); i++)
                assertTrue(paramList.getParameter(i)==expectedList[i]);

            index = 0;
            newVal = new RealParameter2(new Double[]{4.0});
            operation3(pointer, paramList, index, newVal,assignment,expectedPointer,expectedList);



        }catch(Exception e){
            throw new RuntimeException(e);

        }

    }

    private void operation1(
            DPPointer pointer,
            ParameterList paramList,
            int index,
            RealParameter2 newVal,
            int[] assignment,
            RealParameter2[] expectedPointer,
            RealParameter2[] expectedList) throws Exception{


        for(int i = 0; i < expectedPointer.length; i++)
            expectedPointer[i] = paramList.getParameter(assignment[i]);
        expectedPointer[index] = newVal;

        for(int i = 0; i < paramList.getDimension(); i++)
            expectedList[i] = paramList.getParameter(i);
        expectedList[paramList.getDimension()] = newVal;

        pointer.point(index,newVal);
        paramList.addParameter(newVal);

    }
    
    private void operation2(
            DPPointer pointer,
            ParameterList paramList,
            int index,
            int existingValIndex,
            int[] assignment,
            RealParameter2[] expectedPointer) throws Exception{

        for(int i = 0; i < pointer.getDimension();i++){
            expectedPointer[i] = paramList.getParameter(assignment[i]);
        }        

        int listIndex = paramList.indexOf(expectedPointer[existingValIndex]);
        expectedPointer[index] = expectedPointer[existingValIndex];

        pointer.point(index,paramList.getParameter(listIndex));


    }

    private void operation3(
            DPPointer pointers,
            ParameterList paramList,
            int index,
            RealParameter2 newVal,
            int[] assignment,
            RealParameter2[] expectedPointer,
            RealParameter2[] expectedList) throws Exception{


        for(int i = 0; i < expectedPointer.length; i++)
            expectedPointer[i] = paramList.getParameter(assignment[i]);
        expectedPointer[index] = newVal;

        int counter = 0;
        for(int i = 0; i < paramList.getDimension(); i++){
            if(index != i){
                expectedList[counter++] = paramList.getParameter(i);
            }
        }
        expectedList[paramList.getDimension()] = newVal;

        DPValuable dpValuable = new DPValuable();
        dpValuable.initByName(
                "paramList", paramList,
                "pointers", pointers
        );

        pointers.point(index,newVal);
        paramList.addParameter(newVal);
        int[] counts = dpValuable.clusterCounts();
        for(int i = 0; i < counts.length;i++){
            if(counts[i] == 0)
                paramList.removeParameter(i);
        }
    }
}
