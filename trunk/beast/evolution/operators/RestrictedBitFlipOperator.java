/*
* File BitFlipOperator.java
*
* Copyright (C) 2010 Joseph Heled jheled@gmail.com
* This file is part of BEAST2.
* See the NOTICE file distributed with this work for additional
* information regarding copyright ownership and licensing.
*
* BEAST is free software; you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
*  BEAST is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with BEAST; if not, write to the
* Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
* Boston, MA  02110-1301  USA
*/

package beast.evolution.operators;

import beast.core.Operator;
import beast.core.Description;
import beast.core.Input;
import beast.core.parameter.IntegerParameter;
import beast.util.Randomizer;

/**
 * @author Chieh-Hsi Wu
 *
 */

@Description("Flip one bit in an array of boolean bits. Allow dependencies so that the certain bit vectors are forbidden.")
public class RestrictedBitFlipOperator extends Operator {
    public static final int PRESENT = 1;
    public static final int ABSENT = 0;
    public static final int INDEPENDENT = -1;
    public static final String MODEL_CHOOSE = "modelChoose";
    public static final String DEPENDENCIES = "dependencies";
    public Input<IntegerParameter> modelChoose = new Input<IntegerParameter>(MODEL_CHOOSE, "the parameter to operate a flip on.");
    public Input<IntegerParameter> dependencies = new Input<IntegerParameter>(DEPENDENCIES, "Rules which defines the set of bit vector are allowed.");


    private int[] depends;

    public void initAndValidate() {
        int bitVectorLength = dependencies.get().getDimension();
        if(bitVectorLength != modelChoose.get().getDimension()){
            throw new RuntimeException("The bit vector and the dependencies vector should have the same length");
        }

        for(int i = 0; i< bitVectorLength;i++){
            depends[i]  =  dependencies.get().getValue(i);
        }
      
    }

    /**
     * Change the parameter and return the hastings ratio.
     * Flip (Switch a 0 to 1 or 1 to 0) for a random bit in a bit vector.
     * Return the hastings ratio which makes all subsets of vectors with the same number of 1 bits
     * equiprobable, unless usesPriorOnSum = false then all configurations are equiprobable
     */

    @Override
    public double proposal() {

        IntegerParameter parameter = modelChoose.get(this);

        double logq = 0.0;

        int[] bitVec = new int[parameter.getDimension()];
        for(int i = 0; i < bitVec.length; i++){
            bitVec[i] = parameter.getValue(i);
        }

        int index = Randomizer.nextInt(parameter.getDimension());

        int oldVal  = parameter.getValue(index);
        int newVal = -1;
        if(oldVal == ABSENT){
            newVal = PRESENT;
        }else if(oldVal == PRESENT){
           newVal = ABSENT;
        }else{
            throw new RuntimeException("The parameter can only take values 0 or 1.");
        }
        bitVec[index] = newVal;

        boolean isNewVectorPermitted = true;
        for(int i = 0; i < bitVec.length; i++){
            int dependentInd = depends[i];
            if(dependentInd > INDEPENDENT){
                if(bitVec[dependentInd] == ABSENT && bitVec[i]==PRESENT){
                    isNewVectorPermitted = false;
                    break;
                }
            }

        }

        if(isNewVectorPermitted){
            parameter.setValue(index, newVal);
        }

        return logq;
    }
}

