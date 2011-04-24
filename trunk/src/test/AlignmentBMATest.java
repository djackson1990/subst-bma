package test;

import java.util.ArrayList;

import junit.framework.TestCase;

import beast.evolution.alignment.Sequence;
import beast.evolution.alignment.AlignmentBMA;
import beast.core.parameter.IntegerParameter;

/**
 * @author Chieh-Hsi Wu
 *
 * JUnit test for AlignmentBMA model
 */
public class AlignmentBMATest extends TestCase {



    static public ArrayList<Sequence> getSequences() throws Exception {
		Sequence human = new Sequence("human", "AGAAAAAGGAAGAAGAGGGGAAAAAG");
		Sequence chimp = new Sequence("chimp","AGAAAAAGGAACAAAAGAAGAGAAAG");
		Sequence bonobo = new Sequence("bonobo","AGTAATATGTTCTAGATGGGTTATAT");
		Sequence gorilla = new Sequence("gorilla","AGTAATATGTTGTATACTTGTTATAT");
		Sequence orangutan = new Sequence("orangutan","AGAAAAATGATGAAAATAAGACAAAA");
		Sequence siamang = new Sequence("siamang","AGAAAAATGAAGAATATTTGAAAAAA");
        ArrayList<Sequence> seqList = new ArrayList<Sequence>();

        seqList.add(human);
        seqList.add(chimp);
        seqList.add(bonobo);
        seqList.add(gorilla);
        seqList.add(orangutan);
        seqList.add(siamang);

		return seqList;
	}

    public int[] getPartitionWeights(){
        int[] getPartitionWeights = new int[26];
        getPartitionWeights[0] = 10;
        getPartitionWeights[1] = 10;
        getPartitionWeights[2] = 6;

        return getPartitionWeights;
    }

    public int[][] getWeightMatrix(){
        int[][] weightMatrix = new int[26][10];
        weightMatrix[0] = new int []{4,3,0,0,0,0,2,0,0,1};
        weightMatrix[1] = new int []{2,1,1,0,3,1,1,1,0,0};
        weightMatrix[2] = new int []{2,2,0,1,0,0,0,0,1,0};
        return weightMatrix;
    }

    public int[] getPatternIndices(){
        return new int[]{0,6,1,0,0,1,0,9,6,1,2,5,1,0,4,0,7,4,4,6,1,3,0,1,0,8};
    }

    public void testAlignmentBMA() throws Exception{

        IntegerParameter partitionIndices = new IntegerParameter();
        partitionIndices.initByName(
                "value", "0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1 1 1 2 2 2 2 2 2",
                "dimension", 26,
                "upper", 25,
                "lower", 0
        );
        ArrayList<Sequence> seqList  = getSequences();


        AlignmentBMA data = new AlignmentBMA();
		data.initByName(
                "partitionIndices", partitionIndices,
                "sequence", seqList.get(0),
                "sequence", seqList.get(1),
                "sequence", seqList.get(2),
                "sequence", seqList.get(3),
                "sequence", seqList.get(4),
                "sequence", seqList.get(5),
                "dataType","nucleotide"
        );

        int[] partitionWeight = data.getPartitionWeight();
        int[] expectedParitionWeight = getPartitionWeights();
        int[][] expectedWeightMatrix = getWeightMatrix();
        int[] expectedPatternIndices = getPatternIndices();
        int[] patternIndices = data.getPatternIndices();

        for(int i = 0; i < partitionWeight.length;i++){
            assertEquals(partitionWeight[i], expectedParitionWeight[i]);
        }

        int[][] weightMatrix = data.getWeightMatrix();

        for(int i = 0;i < weightMatrix.length; i ++){
            for(int j = 0; j < weightMatrix[i].length;j++){
                assertEquals(weightMatrix[i][j], expectedWeightMatrix[i][j]);
            }
        }

        for(int i = 0; i < patternIndices.length;i++){
            assertEquals(patternIndices[i], expectedPatternIndices[i]);
        }


    }


//                               0123456789
//AGAAAAAGGAAGAAGAGGGGAAAAAG     AAAAGGGGGG
//AGAAAAAGGAACAAAAGAAGAGAAAG     AAAGACGGGG
//AGTAATATGTTCTAGATGGGTTATAT     ATTTGCGTTT
//AGTAATATGTTGTATACTTGTTATAT     ATTTTGGCTT
//AGAAAAATGATGAAAATAAGACAAAA     AATCAGGTAT
//AGAAAAATGAAGAATATTTGAAAAAA     AAAATGGTAT
//06100109612510407446130108
//00000000111111234445666789
//8611313111

//0610010961 2510407446 130108
//0000111669 0012444567 001138

//int[] patFreq1 = {4,3,0,0,0,0,2,0,0,1}
//int[] patFreq2 = {2,1,1,0,3,1,1,1,0,0}
//int[] patFreq3 = {2,2,0,1,0,0,0,0,1,0}    
} 
