package test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import org.junit.Test;

import beast.evolution.alignment.Alignment;
import beast.evolution.alignment.AscertainedAlignment;
import beast.evolution.alignment.Sequence;
import beast.evolution.likelihood.TreeLikelihood;
import beast.evolution.sitemodel.SiteModel;
import beast.evolution.substitutionmodel.Frequencies;
import beast.evolution.substitutionmodel.GeneralSubstitutionModel;
import beast.evolution.substitutionmodel.HKY;
import beast.evolution.substitutionmodel.WAG;
import beast.evolution.tree.Tree;
import beast.util.TreeParser;

/**
 * @author Chieh-Hsi Wu
 *
 * JUnit test for AlignmentBMA model
 */
public class AlignmentBMATest extends TestCase {
	final static double PRECISION = 1e-6;



	protected TreeLikelihood newTreeLikelihood() {
		return new TreeLikelihood();
	}


	static public Alignment getAlignment1() throws Exception {
		Sequence human = new Sequence("human", "       AGAAAAAGGA");
		Sequence chimp = new Sequence("chimp","        AGAAAAAGGA");
		Sequence bonobo = new Sequence("bonobo","      AGTAATATGT");
		Sequence gorilla = new Sequence("gorilla","    AGTAATATGT");
		Sequence orangutan = new Sequence("orangutan","AGAAAAATGA");
		Sequence siamang = new Sequence("siamang","    AGAAAAATGA");

		Alignment data = new Alignment();
		data.initByName("sequence", human, "sequence", chimp, "sequence", bonobo, "sequence", gorilla, "sequence", orangutan, "sequence", siamang,
						"dataType","nucleotide"
						);
		return data;
	}

    static public Alignment getAlignment2() throws Exception {
		Sequence human = new Sequence("human", "       AGAAGAGGG");
		Sequence chimp = new Sequence("chimp","        ACAAAAGAA");
		Sequence bonobo = new Sequence("bonobo","      TCTAGATGG");
		Sequence gorilla = new Sequence("gorilla","    TGTATACTT");
		Sequence orangutan = new Sequence("orangutan","TGAAAATAA");
		Sequence siamang = new Sequence("siamang","    AGAATATTT");

		Alignment data = new Alignment();
		data.initByName("sequence", human, "sequence", chimp, "sequence", bonobo, "sequence", gorilla, "sequence", orangutan, "sequence", siamang,
						"dataType","nucleotide"
						);
		return data;
	}

    static public Alignment getAlignment3() throws Exception {
		Sequence human = new Sequence("human", "       GAAAAAG");
		Sequence chimp = new Sequence("chimp","        GAGAAAG");
		Sequence bonobo = new Sequence("bonobo","      GTTATAT");
		Sequence gorilla = new Sequence("gorilla","    GTTATAT");
		Sequence orangutan = new Sequence("orangutan","GACAAAA");
		Sequence siamang = new Sequence("siamang","    GAAAAAA");
        
		Alignment data = new Alignment();
		data.initByName("sequence", human, "sequence", chimp, "sequence", bonobo, "sequence", gorilla, "sequence", orangutan, "sequence", siamang,
						"dataType","nucleotide"
						);
		return data;
	}

    public void testAlignmentBMA() throws Exception{

        Alignment align1  = getAlignment1();
        Alignment align2  = getAlignment2();
        Alignment align3  = getAlignment3();
        for(int i = 0; i < align1.getSiteCount(); i++){
            System.err.print(align1.getPatternIndex(i)+" ");
        }
        System.err.println();
        System.err.println(align2.getPatternCount());
        System.err.println(align3.getPatternCount());
        //assertEquals(1.0, 1.0, 5e-10);

    }






} // class TreeLikelihoodTest
