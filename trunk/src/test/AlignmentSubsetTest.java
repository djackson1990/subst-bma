package test;

import junit.framework.TestCase;
import beast.evolution.alignment.Sequence;
import beast.evolution.alignment.Alignment;
import beast.evolution.alignment.AlignmentSubset;

/**
 * @author Chieh-Hsi Wu
 *
 * This is a Junit test to check whether the wrapper class of the alignment works properly.
 */
public class AlignmentSubsetTest extends TestCase {
    private Alignment data;
    public Alignment getAlignment() throws Exception {
		Sequence human = new Sequence("human", "AGAAAAAGGAAGAAGAGGGGAAAAAG");
		Sequence chimp = new Sequence("chimp","AGAAAAAGGAACAAAAGAAGAGAAAG");
		Sequence bonobo = new Sequence("bonobo","AGTAATATGTTCTAGATGGGTTATAT");
		Sequence gorilla = new Sequence("gorilla","AGTAATATGTTGTATACTTGTTATAT");
		Sequence orangutan = new Sequence("orangutan","AGAAAAATGATGAAAATAAGACAAAA");
		Sequence siamang = new Sequence("siamang","AGAAAAATGAAGAATATTTGAAAAAA");


        data = new Alignment();
        data.initByName("sequence", human, "sequence", chimp, "sequence", bonobo, "sequence", gorilla, "sequence", orangutan, "sequence", siamang,
                "dataType", "nucleotide"
        );
        return data;
	}

    public int getExpectedNrTaxa(){
        return data.getNrTaxa();
    }

    public int getExpectedPatternCount(){
        return 1;
    }

    public int getExpectedTaxonIndex(String sID) {
        return data.getTaxonIndex(sID);
    }

    public int[] getExpectedPattern(int siteIndex) {
        return data.getPattern(data.getPatternIndex(siteIndex)); //todo assuming a subset only has a single column
    }

    public int getExpectedPattern(int iTaxon, int siteIndex) {
        int[] patterns = data.getPattern(data.getPatternIndex(siteIndex));  //todo assuming a subset only has a single column
        return patterns[iTaxon];
    }

    public int getExpectedPatternWeight() {
        return 1; //todo assuming a subset only has a single column
    }

    public int getExpectedMaxStateCount() {
        return data.getMaxStateCount();
    }

    public int getPatternIndex(int iSite) {
        throw new RuntimeException("Does not make much sense to retrive the pattern index as the colume of interest has already been defined");
    }

    public int getExpectedSiteCount() {
        return 1; //todo assuming a subset only has a single column
    }

    public int [] getExpectedWeights() {
        return new int[]{1};
    }



    public void testAlignmentBMA() throws Exception{
        Alignment alignment = getAlignment();
        int siteCount = alignment.getSiteCount();
        String[] taxaNames = new String[] {"human","chimp","bonobo","gorilla","orangutan","siamang"};
        for(int i = 0; i < siteCount; i++){
            AlignmentSubset subset = new AlignmentSubset();
		    subset.initByName(
                "data", alignment,
                "siteIndex", i
            );

            int taxaCount = subset.getNrTaxa();
            assertEquals(taxaCount, getExpectedNrTaxa());
            assertEquals(subset.getPatternCount(), getExpectedPatternCount());

            for(int taxai = 0; taxai < taxaCount; taxai++){
                assertEquals(subset.getTaxonIndex(taxaNames[taxai]), getExpectedTaxonIndex(taxaNames[taxai]));
            }

            int[] pats = subset.getPattern(0);
            int[] expectedPats = getExpectedPattern(i);
            for(int j = 0; j < pats.length; j++){
                assertEquals(pats[j], expectedPats[j]);
            }

            for(int j = 0; j < pats.length; j++)
                assertEquals(subset.getPattern(j,0), getExpectedPattern(j,i));


            assertEquals(subset.getPatternWeight(0), getExpectedPatternWeight());

            assertEquals(subset.getMaxStateCount(), getExpectedMaxStateCount());

            assertEquals(subset.getSiteCount(), getExpectedSiteCount());

            assertEquals(subset.getWeights()[0], getExpectedWeights()[0]);




        }






    }
}
